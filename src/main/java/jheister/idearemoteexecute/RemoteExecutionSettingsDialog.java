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
import java.awt.GridLayout;
import java.util.Objects;

public class RemoteExecutionSettingsDialog implements Configurable  {
    public static final String HOSTNAME_PROPERTY = "jheister.idearemoteexecute.hostname";
    public static final String USER_PROPERTY = "jheister.idearemoteexecute.username";
    public static final String JAVA_EXEC_PROPERTY = "jheister.idearemoteexecute.javaexec";
    private PropertiesComponent propertiesComponent = PropertiesComponent.getInstance();

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
        JPanel panel = new JPanel(new GridLayout(3, 2));
        panel.add(new JLabel("hostname"));
        hostNameField = new JTextField();
        panel.add(hostNameField);
        panel.add(new JLabel("user"));
        remoteUserField = new JTextField();
        panel.add(remoteUserField);
        panel.add(new JLabel("Java executable"));
        javaExecField = new JTextField();
        panel.add(javaExecField);

        hostNameField.setText(propertiesComponent.getValue(HOSTNAME_PROPERTY, ""));
        remoteUserField.setText(propertiesComponent.getValue(USER_PROPERTY, ""));
        javaExecField.setText(propertiesComponent.getValue(JAVA_EXEC_PROPERTY, ""));

        return panel;
    }

    @Override
    public boolean isModified() {
        return !(Objects.equals(hostNameField.getText(), propertiesComponent.getValue(HOSTNAME_PROPERTY, ""))
                && Objects.equals(javaExecField.getText(), propertiesComponent.getValue(JAVA_EXEC_PROPERTY, ""))
                && Objects.equals(remoteUserField.getText(), propertiesComponent.getValue(USER_PROPERTY, "")));
    }

    @Override
    public void apply() throws ConfigurationException {
        propertiesComponent.setValue(HOSTNAME_PROPERTY, hostNameField.getText());
        propertiesComponent.setValue(USER_PROPERTY, remoteUserField.getText());
        propertiesComponent.setValue(JAVA_EXEC_PROPERTY, javaExecField.getText());
    }
}
