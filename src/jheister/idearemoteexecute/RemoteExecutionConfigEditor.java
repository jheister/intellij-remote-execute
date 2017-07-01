package jheister.idearemoteexecute;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

class RemoteExecutionConfigEditor extends SettingsEditor<RemoteExecutionConfig> {
    private JLabel classToRun;
    private JLabel module;

    @Override
    protected void resetEditorFrom(@NotNull RemoteExecutionConfig o) {
        classToRun.setText("Class: " + o.getClassToRun());
        module.setText("Module: " + o.getModule().getName());
    }

    @Override
    protected void applyEditorTo(@NotNull RemoteExecutionConfig o) throws ConfigurationException {

    }

    @NotNull
    @Override
    protected JComponent createEditor() {
        JPanel panel = new JPanel(new GridLayout(2, 1));
        module = new JLabel("");
        panel.add(module);
        classToRun = new JLabel("");
        panel.add(classToRun);
        return panel;
    }
}
