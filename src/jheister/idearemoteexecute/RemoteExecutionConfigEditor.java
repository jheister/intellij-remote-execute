package jheister.idearemoteexecute;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

class RemoteExecutionConfigEditor extends SettingsEditor<RemoteExecutionConfig> {
    private JLabel classToRun = new JLabel("");
    private JLabel module = new JLabel("");
    private JTextField commandArgs = new JTextField();

    @Override
    protected void resetEditorFrom(@NotNull RemoteExecutionConfig o) {
        classToRun.setText(o.getClassToRun());
        if (o.getModules().length != 0) {
            module.setText(o.getModule().getName());
        } else {
            module.setText("");
        }
        commandArgs.setText(o.getCommandArgs());
    }

    @Override
    protected void applyEditorTo(@NotNull RemoteExecutionConfig o) throws ConfigurationException {
        o.setCommandArgs(commandArgs.getText());
    }

    @NotNull
    @Override
    protected JComponent createEditor() {
        JPanel panel = new JPanel(new GridLayout(3, 2));
        panel.add(new JLabel("Module:"));
        panel.add(module);
        panel.add(new JLabel("Class:"));
        panel.add(classToRun);
        panel.add(new JLabel("Program Arguments:"));
        panel.add(commandArgs);
        return panel;
    }
}
