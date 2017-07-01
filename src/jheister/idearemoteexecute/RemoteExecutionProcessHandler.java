package jheister.idearemoteexecute;

import com.intellij.execution.process.ProcessHandler;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.util.Key;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.intellij.execution.process.ProcessOutputTypes.STDERR;
import static com.intellij.execution.process.ProcessOutputTypes.STDOUT;
import static com.intellij.execution.process.ProcessOutputTypes.SYSTEM;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

public class RemoteExecutionProcessHandler extends ProcessHandler {
    private final RemoteExecutionConfig config;
    private final String hostName;
    private final String javaExec;

    public RemoteExecutionProcessHandler(RemoteExecutionConfig config) {
        this.config = config;
        hostName = PropertiesComponent.getInstance().getValue(RemoteExecutionSettingsDialog.HOSTNAME_PROPERTY, "");
        javaExec = PropertiesComponent.getInstance().getValue(RemoteExecutionSettingsDialog.JAVA_EXEC_PROPERTY, "");
    }

    @Override
    public void startNotify() {
        super.startNotify();

        if (hostName.isEmpty() || javaExec.isEmpty()) {
            notifyTextAvailable("Host / Java exec not configured\n", STDERR);
            notifyProcessTerminated(-1);
            return;
        }

        Executors.newSingleThreadExecutor().execute(() -> {
            notifyTextAvailable("Going to run on " + hostName + " with " + javaExec + "\n", STDERR);
            if (executeCommand(syncCommand(), SYSTEM) != 0) {
                //todo: handle failure
            }
            notifyProcessTerminated(executeCommand(javaCommand(), STDOUT));
        });
    }

    private String[] javaCommand() {
        String classpath = config.requiredFiles().stream().map(f -> "idea-remote-execution/" + f.getName()).collect(toSet()).stream().collect(joining(":"));
        return new String[] {
                "ssh",
                hostName,
                javaExec + " -cp " + classpath + " " + config.getClassToRun()
        };
    }

    @NotNull
    private String[] syncCommand() {
        List<String> cmdLine = config.requiredFiles().stream().map(f -> {
            if (!f.isInLocalFileSystem()) {
                throw new RuntimeException("Cannot sync file " + f.getPath() + " it is not local");
            }

            return f.getPath();
        }).collect(toList());

        cmdLine.add(0, "rsync");
        cmdLine.add(1, "-avh");
        cmdLine.add(hostName + ":idea-remote-execution");
        cmdLine.add("--delete");

        return cmdLine.toArray(new String[cmdLine.size()]);
    }

    private int executeCommand(String[] cmd, Key outType) {
        notifyTextAvailable(asList(cmd).stream().collect(joining(" ")) + "\n", SYSTEM);

        try {
            Process syncProcess = new ProcessBuilder(cmd).start();

            ExecutorService io = Executors.newCachedThreadPool();
            io.execute(textNotifierOf(syncProcess.getInputStream(), outType));
            io.execute(textNotifierOf(syncProcess.getErrorStream(), STDERR));

            return syncProcess.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private Runnable textNotifierOf(InputStream in, Key outputType) {
        return () -> {
            InputStreamReader reader = new InputStreamReader(in);
            try {
                char[] buffer = new char[1024];
                int read;
                while ((read = reader.read(buffer)) != -1) {
                    String chars = new String(buffer, 0, read);
                    notifyTextAvailable(chars, outputType);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };
    }

    @Override
    protected void destroyProcessImpl() {
        notifyTextAvailable("Destroy is not implemented!\n", STDERR);
    }

    @Override
    protected void detachProcessImpl() {
        notifyTextAvailable("Detatch is not implemented!\n", STDERR);
    }

    @Override
    public boolean detachIsDefault() {
        return false;
    }

    @Nullable
    @Override
    public OutputStream getProcessInput() {
        return null;
    }
}
