package jheister.idearemoteexecute;

import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.execution.actions.RunConfigurationProducer;
import com.intellij.execution.application.ApplicationConfigurationType;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.util.Ref;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;

import java.util.Optional;

import static jheister.idearemoteexecute.RemoteExecutionSettingsDialog.COMMAND_ARGS_PROPERTY;
import static jheister.idearemoteexecute.RemoteExecutionSettingsDialog.HOSTNAME_PROPERTY;
import static jheister.idearemoteexecute.RemoteExecutionSettingsDialog.JAVA_EXEC_PROPERTY;
import static jheister.idearemoteexecute.RemoteExecutionSettingsDialog.JVM_ARGS_PROPERTY;
import static jheister.idearemoteexecute.RemoteExecutionSettingsDialog.USER_PROPERTY;

public class RemoteExecutionConfigProducer extends RunConfigurationProducer<RemoteExecutionConfig> {
    protected RemoteExecutionConfigProducer() {
        super(RemoteExecutionConfigType.getInstance());
    }

    private final PropertiesComponent propertiesComponent = PropertiesComponent.getInstance();

    @Override
    protected boolean setupConfigurationFromContext(RemoteExecutionConfig remoteExecutionConfig,
                                                    ConfigurationContext configurationContext,
                                                    Ref<PsiElement> ref) {
        if (!(configurationContext.getPsiLocation() instanceof PsiClass)) {
            return false;
        }

        PsiClass mainClass = ApplicationConfigurationType.getMainClass(configurationContext.getPsiLocation());

        if (mainClass == null) {
            return false;
        }

        Module module = configurationContext.getModule();

        remoteExecutionConfig.setModule(module);
        remoteExecutionConfig.setName("Remote: " + mainClass.getName() + ".main()");
        remoteExecutionConfig.setClassToRun(mainClass.getQualifiedName());
        remoteExecutionConfig.setJvmArgs(propertiesComponent.getValue(JVM_ARGS_PROPERTY, ""));
        remoteExecutionConfig.setCommandArgs(propertiesComponent.getValue(COMMAND_ARGS_PROPERTY, ""));
        remoteExecutionConfig.setRemoteUser(getTrimmedUserName());
        remoteExecutionConfig.setRemoteJavaExec(propertiesComponent.getValue(JAVA_EXEC_PROPERTY, ""));
        remoteExecutionConfig.setRemoteHost(propertiesComponent.getValue(HOSTNAME_PROPERTY, ""));

        return true;
    }


    @Override
    public boolean isConfigurationFromContext(RemoteExecutionConfig remoteExecutionConfig, ConfigurationContext configurationContext) {
        PsiClass mainClass = ApplicationConfigurationType.getMainClass(configurationContext.getPsiLocation());

        return mainClass != null
                && configurationContext.getPsiLocation() instanceof PsiClass
                && configurationContext.getModule().equals(remoteExecutionConfig.getModule())
                && mainClass.getQualifiedName().equals(remoteExecutionConfig.getClassToRun());
    }


    private String getTrimmedUserName() {
        return Optional.of(propertiesComponent.getValue(USER_PROPERTY, "")).map(String::trim).orElse("");
    }

}
