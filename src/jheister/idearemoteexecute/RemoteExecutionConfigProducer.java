package jheister.idearemoteexecute;

import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.execution.actions.RunConfigurationProducer;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.vfs.VirtualFile;
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

        PsiClass psiClass = (PsiClass) configurationContext.getPsiLocation();

        VirtualFile file = psiClass.getContainingFile().getVirtualFile();
        Module module = configurationContext.getModule();

        remoteExecutionConfig.setModule(module);
        remoteExecutionConfig.setName(file.getName() + " in DC");
        remoteExecutionConfig.setClassToRun(psiClass.getQualifiedName());

        return true;
    }

    @Override
    public boolean isConfigurationFromContext(RemoteExecutionConfig remoteExecutionConfig, ConfigurationContext configurationContext) {
        return configurationContext.getPsiLocation() instanceof PsiClass
                && configurationContext.getModule().equals(remoteExecutionConfig.getModule())
                && ((PsiClass) configurationContext.getPsiLocation()).getQualifiedName().equals(remoteExecutionConfig.getClassToRun());
    }

}
