package jheister.idearemoteexecute;

import com.intellij.execution.DefaultExecutionResult;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.JavaParameters;
import com.intellij.execution.configurations.LocatableConfigurationBase;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.configurations.RunProfileWithCompileBeforeLaunchOption;
import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RemoteExecutionConfig extends LocatableConfigurationBase implements RunProfileWithCompileBeforeLaunchOption {
    private Module module;
    private String classToRun;
    private String commandArgs = "";

    protected RemoteExecutionConfig(@NotNull Project project, @NotNull ConfigurationFactory factory, String name) {
        super(project, factory, name);
    }

    @NotNull
    @Override
    public SettingsEditor<? extends RunConfiguration> getConfigurationEditor() {
        return new RemoteExecutionConfigEditor();
    }

    public List<VirtualFile> requiredFiles() {
        try {
            JavaParameters params = new JavaParameters();
            params.configureByModule(module, JavaParameters.CLASSES_ONLY);

            return params.getClassPath().getVirtualFiles();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Nullable
    @Override
    public RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment executionEnvironment) throws ExecutionException {
        return new RunProfileState() {
            @Nullable
            @Override
            public ExecutionResult execute(Executor executor, @NotNull ProgramRunner programRunner) throws ExecutionException {
                ConsoleView console = TextConsoleBuilderFactory.getInstance().createBuilder(getProject()).getConsole();
                ProcessHandler processHandler = new RemoteExecutionProcessHandler(RemoteExecutionConfig.this);
                DefaultExecutionResult result = new DefaultExecutionResult(console, processHandler);
                console.attachToProcess(processHandler);
                processHandler.startNotify();

                return result;
            }
        };
    }

    @Nullable
    @Override
    public String suggestedName() {
        return "MyConfig";
    }

    public void setModule(Module module) {
        this.module = module;
    }

    public Module getModule() {
        return module;
    }

    @NotNull
    @Override
    public Module[] getModules() {
        return new Module[] { module };
    }

    public void setClassToRun(String classToRun) {
        this.classToRun = classToRun;
    }

    public String getClassToRun() {
        return classToRun;
    }

    public String getCommandArgs() {
        return commandArgs;
    }

    public void setCommandArgs(String commandArgs) {
        this.commandArgs = commandArgs;
    }
}
