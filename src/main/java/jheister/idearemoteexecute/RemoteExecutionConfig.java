package jheister.idearemoteexecute;

import com.intellij.execution.DefaultExecutionResult;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.JavaParameters;
import com.intellij.execution.configurations.ModuleBasedConfiguration;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.configurations.RunConfigurationModule;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.configurations.RunProfileWithCompileBeforeLaunchOption;
import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.JDOMExternalizerUtil;
import com.intellij.openapi.util.WriteExternalException;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.search.GlobalSearchScope;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class RemoteExecutionConfig extends ModuleBasedConfiguration implements RunProfileWithCompileBeforeLaunchOption {
    private String classToRun = "";
    private String commandArgs = "";
    private String jvmArgs;

    protected RemoteExecutionConfig(@NotNull Project project, @NotNull ConfigurationFactory factory, String name) {
        super(name, new RunConfigurationModule(project), factory);
    }

    @NotNull
    @Override
    public SettingsEditor<? extends RunConfiguration> getConfigurationEditor() {
        return new RemoteExecutionConfigEditor();
    }

    public List<VirtualFile> requiredFiles() {
        try {
            JavaParameters params = new JavaParameters();
            params.configureByModule(getModule(), JavaParameters.CLASSES_AND_TESTS);

            return params.getClassPath().getVirtualFiles();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Nullable
    @Override
    public RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment executionEnvironment) throws ExecutionException {
        return new RemoteExecutionRunProfileState(this);
    }

    @Nullable
    @Override
    public String suggestedName() {
        return "MyConfig";
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

    @Override
    public void readExternal(Element element) throws InvalidDataException {
        readModule(element);
        classToRun = JDOMExternalizerUtil.readField(element, "classToRun", "");
        commandArgs = JDOMExternalizerUtil.readField(element, "commandArgs", "");
        jvmArgs = JDOMExternalizerUtil.readField(element, "jvmArgs", "");
    }

    @Override
    public void writeExternal(Element element) throws WriteExternalException {
        super.writeExternal(element);
        writeModule(element);
        JDOMExternalizerUtil.writeField(element, "classToRun", classToRun);
        JDOMExternalizerUtil.writeField(element, "commandArgs", commandArgs);
        JDOMExternalizerUtil.writeField(element, "jvmArgs", jvmArgs);
    }

    @Override
    public Collection<Module> getValidModules() {
        return getAllModules();
    }

    @Nullable
    @Override
    public GlobalSearchScope getSearchScope() {
        return null;
    }

    public Module getModule() {
        if (getModules().length == 0) {
            return null;
        }
        return getModules()[0];
    }

    public String getJvmArgs() {
        return jvmArgs;
    }

    public void setJvmArgs(String jvmArgs) {
        this.jvmArgs = jvmArgs;
    }

    public String getHostName() {
        return PropertiesComponent.getInstance().getValue(RemoteExecutionSettingsDialog.HOSTNAME_PROPERTY, "");
    }

    public String getJavaExec() {
        return PropertiesComponent.getInstance().getValue(RemoteExecutionSettingsDialog.JAVA_EXEC_PROPERTY, "");
    }

    public Optional<String> getUserName() {
        return Optional.of(PropertiesComponent.getInstance().getValue(RemoteExecutionSettingsDialog.USER_PROPERTY, ""))
                .map(String::trim)
                .filter(u -> !u.isEmpty());
    }


    public static class RemoteExecutionRunProfileState implements RunProfileState {
        private final RemoteExecutionConfig config;
        private String additionalJvmArgs = "";

        public RemoteExecutionRunProfileState(RemoteExecutionConfig config) {
            this.config = config;
        }

        @Nullable
        @Override
        public ExecutionResult execute(Executor executor, @NotNull ProgramRunner programRunner) throws ExecutionException {
            ConsoleView console = TextConsoleBuilderFactory.getInstance().createBuilder(config.getProject()).getConsole();
            ProcessHandler processHandler = new RemoteExecutionProcessHandler(config, additionalJvmArgs);
            DefaultExecutionResult result = new DefaultExecutionResult(console, processHandler);
            console.attachToProcess(processHandler);
            processHandler.startNotify();

            return result;
        }

        public void setAdditionalJvmArgs(String args) {
            additionalJvmArgs = args;
        }

        public String hostName() {
            return config.getHostName();
        }
    }
}
