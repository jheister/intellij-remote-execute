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
import java.util.concurrent.Future;

import static com.intellij.execution.process.ProcessOutputTypes.STDERR;
import static com.intellij.execution.process.ProcessOutputTypes.STDOUT;
import static com.intellij.execution.process.ProcessOutputTypes.SYSTEM;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

public class RemoteExecutionProcessHandler extends ProcessHandler {
    public static final String REMOTE_DIR = "idea-remote-execution";
    private final RemoteExecutionConfig config;
    private final String hostName;
    private final String javaExec;
    private Future<?> runningProcess;

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

        runningProcess = Executors.newSingleThreadExecutor().submit(() -> {
            notifyTextAvailable("Going to run on " + hostName + " with " + javaExec + "\n", STDERR);
            int result = executeCommand(syncCommand(), SYSTEM);
            if (result != 0) {
                notifyTextAvailable("Sync failed: " + result + "\n", STDERR);
                throw new RuntimeException("Sync failed");
            }
            notifyProcessTerminated(executeCommand(javaCommand(), STDOUT));
        });
    }

    private String[] javaCommand() {
        String classpath = config.requiredFiles().stream().map(f -> REMOTE_DIR + "/" + f.getName()).collect(toSet()).stream().collect(joining(":"));
        return new String[] {
                "ssh",
                "-tt",
                hostName,
                javaExec + " -cp " + classpath + " " + config.getJvmArgs() + config.getClassToRun() + " " + config.getCommandArgs()
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
        cmdLine.add(hostName + ":" + REMOTE_DIR);
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

            try {
                return syncProcess.waitFor();
            } catch (InterruptedException e) {
                return syncProcess.destroyForcibly().waitFor();
            }
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
        runningProcess.cancel(true);
    }

    @Override
    protected void detachProcessImpl() {
        notifyTextAvailable("Detatch is not implemented, destroying instead\n", STDERR);
        destroyProcessImpl();
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
