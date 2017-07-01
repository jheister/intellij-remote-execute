package jheister.idearemoteexecute;

import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.execution.actions.RunConfigurationProducer;
import com.intellij.execution.application.ApplicationConfigurationType;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.util.Ref;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;

public class RemoteExecutionConfigProducer extends RunConfigurationProducer<RemoteExecutionConfig> {
    protected RemoteExecutionConfigProducer() {
        super(RemoteExecutionConfigType.getInstance());
    }

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
        remoteExecutionConfig.setName(mainClass.getName() + ".main() in DC");
        remoteExecutionConfig.setClassToRun(mainClass.getQualifiedName());

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
}
