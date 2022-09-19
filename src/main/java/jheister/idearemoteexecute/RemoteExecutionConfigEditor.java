package jheister.idearemoteexecute;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

class RemoteExecutionConfigEditor extends SettingsEditor<RemoteExecutionConfig> {
    private JTextField classToRun = new JTextField("");
    private JTextField module = new JTextField("");
    private JTextField commandArgs = new JTextField();
    private JTextField jvmArgs = new JTextField();
    private JTextField remoteHost = new JTextField();
    private JTextField remoteJavaExec = new JTextField();
    private JTextField remoteUser = new JTextField();

    @Override
    protected void resetEditorFrom(@NotNull RemoteExecutionConfig o) {
        classToRun.setText(o.getClassToRun());
        if (o.getModules().length != 0) {
            module.setText(o.getModule().getName());
        } else {
            module.setText("");
        }
        commandArgs.setText(o.getCommandArgs());
        jvmArgs.setText(o.getJvmArgs());
        remoteHost.setText(o.getRemoteHost());
        remoteJavaExec.setText(o.getRemoteJavaExec());
        remoteUser.setText(o.getRemoteUser());

    }

    @Override
    protected void applyEditorTo(@NotNull RemoteExecutionConfig o) throws ConfigurationException {
        o.setClassToRun(classToRun.getText());
        o.setModuleName(module.getText());
        o.setCommandArgs(commandArgs.getText());
        o.setJvmArgs(jvmArgs.getText());
        o.setRemoteHost(remoteHost.getText());
        o.setRemoteJavaExec(remoteJavaExec.getText());
        o.setRemoteUser(remoteUser.getText());
    }

    @NotNull
    @Override
    protected JComponent createEditor() {
        JPanel panel = new JPanel(new GridLayout(4, 2));
        panel.add(new JLabel("Module:"));
        panel.add(module);
        panel.add(new JLabel("Main class:"));
        panel.add(classToRun);
        panel.add(new JLabel("VM options:"));
        panel.add(jvmArgs);
        panel.add(new JLabel("Program arguments:"));
        panel.add(commandArgs);
        panel.add(new JLabel("Remote host:"));
        panel.add(remoteHost);
        panel.add(new JLabel("Remote Java exec:"));
        panel.add(remoteJavaExec);
        panel.add(new JLabel("Remote user:"));
        panel.add(remoteUser);
        return panel;
    }
}
