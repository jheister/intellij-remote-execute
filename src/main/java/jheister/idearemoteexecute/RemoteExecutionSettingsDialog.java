package jheister.idearemoteexecute;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.*;
import java.util.Objects;

public class RemoteExecutionSettingsDialog implements Configurable  {
    private final PropertiesComponent propertiesComponent = PropertiesComponent.getInstance();

    public static final String JVM_ARGS_PROPERTY = "jheister.idearemoteexecute.jvm_args";
    public static final String COMMAND_ARGS_PROPERTY = "jheister.idearemoteexecute.program_args";
    public static final String HOSTNAME_PROPERTY = "jheister.idearemoteexecute.hostname";
    public static final String USER_PROPERTY = "jheister.idearemoteexecute.username";
    public static final String JAVA_EXEC_PROPERTY = "jheister.idearemoteexecute.javaexec";

    private JTextField jvmArgsField;
    private JTextField commandArgsField;
    private JTextField hostNameField;
    private JTextField remoteUserField;
    private JTextField javaExecField;

    @Nls
    @Override
    public String getDisplayName() {
        return "Remote execution";
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        JPanel container = new JPanel(new BorderLayout());

        container.add(new JLabel("Default values that will be applied to all new Remote Execute run configurations"), BorderLayout.NORTH);

        JPanel panel = new JPanel(new GridLayout(5, 2));
        panel.add(new JLabel("JVM arguments"));
        jvmArgsField = new JTextField();
        panel.add(jvmArgsField);

        panel.add(new JLabel("Command arguments"));
        commandArgsField = new JTextField();
        panel.add(commandArgsField);

        panel.add(new JLabel("Remote host"));
        hostNameField = new JTextField();
        panel.add(hostNameField);

        panel.add(new JLabel("Remote user"));
        remoteUserField = new JTextField();
        panel.add(remoteUserField);

        panel.add(new JLabel("Remote Java executable"));
        javaExecField = new JTextField();
        panel.add(javaExecField);

        container.add(panel, BorderLayout.CENTER);

        jvmArgsField.setText(propertiesComponent.getValue(JVM_ARGS_PROPERTY, ""));
        commandArgsField.setText(propertiesComponent.getValue(COMMAND_ARGS_PROPERTY, ""));
        hostNameField.setText(propertiesComponent.getValue(HOSTNAME_PROPERTY, ""));
        remoteUserField.setText(propertiesComponent.getValue(USER_PROPERTY, ""));
        javaExecField.setText(propertiesComponent.getValue(JAVA_EXEC_PROPERTY, ""));

        return container;
    }

    @Override
    public boolean isModified() {
        return !(
                Objects.equals(jvmArgsField.getText(), propertiesComponent.getValue(JVM_ARGS_PROPERTY, ""))
                && Objects.equals(commandArgsField.getText(), propertiesComponent.getValue(COMMAND_ARGS_PROPERTY, ""))
                && Objects.equals(hostNameField.getText(), propertiesComponent.getValue(HOSTNAME_PROPERTY, ""))
                && Objects.equals(javaExecField.getText(), propertiesComponent.getValue(JAVA_EXEC_PROPERTY, ""))
                && Objects.equals(remoteUserField.getText(), propertiesComponent.getValue(USER_PROPERTY, "")));
    }

    @Override
    public void apply() throws ConfigurationException {
        propertiesComponent.setValue(JVM_ARGS_PROPERTY, jvmArgsField.getText());
        propertiesComponent.setValue(COMMAND_ARGS_PROPERTY, commandArgsField.getText());
        propertiesComponent.setValue(HOSTNAME_PROPERTY, hostNameField.getText());
        propertiesComponent.setValue(USER_PROPERTY, remoteUserField.getText());
        propertiesComponent.setValue(JAVA_EXEC_PROPERTY, javaExecField.getText());
    }
}
