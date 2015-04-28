package ws.settings;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.TextBrowseFolderListener;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

import com.intellij.openapi.ui.TextFieldWithBrowseButton;

public class WSSettingsPage implements Configurable {
    public PropertiesComponent properties;

    private JPanel panel;
    private TextFieldWithBrowseButton file;

    public WSSettingsPage(@NotNull final Project project) {
        this.properties = PropertiesComponent.getInstance(project);
        FileChooserDescriptor descriptor = new FileChooserDescriptor(false, true, false, false, false, false);
        TextBrowseFolderListener listener = new TextBrowseFolderListener(descriptor);
        file.addBrowseFolderListener(listener);
    }

    @Nls
    @Override
    public String getDisplayName() {
        return "WS Plugin";
    }

    @Nullable
    @Override
    public String getHelpTopic() {
        return null;
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        file.setText(properties.getValue("PathToWs", ""));
        return panel;
    }

    @Override
    public boolean isModified() {
        return !file.getText().equals(properties.getValue("PathToWs"));
    }

    @Override
    public void apply() throws ConfigurationException {
        properties.setValue("PathToWs", file.getText());
    }

    @Override
    public void reset() {

    }

    @Override
    public void disposeUIResources() {

    }
}
