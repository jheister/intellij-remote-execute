package jheister.idearemoteexecute;

import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.execution.configurations.ConfigurationTypeBase;
import com.intellij.execution.configurations.ConfigurationTypeUtil;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class RemoteExecutionConfigType extends ConfigurationTypeBase implements ConfigurationType {
    @NotNull
    public static RemoteExecutionConfigType getInstance() {
        return ConfigurationTypeUtil.findConfigurationType(RemoteExecutionConfigType.class);
    }

    protected RemoteExecutionConfigType() {
        super("jheister.idearemoteexecute.runconfig", "Remote execute", "Execute app on remote host", AllIcons.RunConfigurations.Application);

        addFactory(new ConfigurationFactory(this) {
            @NotNull
            @Override
            public RunConfiguration createTemplateConfiguration(@NotNull Project project) {
                return new RemoteExecutionConfig(project, this, "");
            }
        });
    }
}
