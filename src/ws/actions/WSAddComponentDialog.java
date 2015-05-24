package ws.actions;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;

import javax.swing.*;
import java.awt.event.*;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WSAddComponentDialog extends JDialog {
    private final Project project;
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField componentName;
    private JCheckBox checkJs;
    private JCheckBox checkXhtml;
    private JCheckBox checkCss;

    public WSAddComponentDialog(Project project) {
        this.project = project;
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);


        setSize(400, 100);
        pack();
        setVisible(true);
    }

    private void onOK() {
        PsiDirectory directory = PsiManager.getInstance(project).findDirectory(project.getBaseDir());
        if (directory != null && !getControlName().isEmpty()) {
            Pattern pattern = Pattern.compile("^(SBIS3)\\.(\\w+)\\.(\\w+)");
            Matcher matcher = pattern.matcher(getControlName());

            if (matcher.find()) {
                String prName = matcher.group(2);
                String controlName = matcher.group(3);
                try {
                    PsiDirectory controlDirectory = directory.createSubdirectory(controlName);
                    if (checkJs.isSelected()) {
                        PsiFile jsFile = controlDirectory.createFile(controlName + ".module.js");

                        FileWriter fileWriter = new FileWriter(jsFile.getVirtualFile().getPath());
                        fileWriter.write(String.format(WSAddComponent.jsFileString, getControlName(), controlName));
                        fileWriter.close();

                    }
                    if (checkXhtml.isSelected()) {
                        PsiFile xhtmlFile = controlDirectory.createFile(controlName + ".xhtml");

                        FileWriter fileWriter = new FileWriter(xhtmlFile.getVirtualFile().getPath());
                        fileWriter.write(String.format(WSAddComponent.xhtmlFileString, prName.toLowerCase() + "-" + controlName.toLowerCase()));
                        fileWriter.close();
                    }
                    if (checkJs.isSelected()) {
                        PsiFile checkCss = controlDirectory.createFile(controlName + ".css");
                        FileWriter fileWriter = new FileWriter(checkCss.getVirtualFile().getPath());
                        fileWriter.write(String.format(WSAddComponent.cssFileString, prName.toLowerCase() + "-" + controlName.toLowerCase()));
                        fileWriter.close();
                    }
                } catch (IOException e) {
                }
            }
        }

        dispose();
    }

    private void onCancel() {
        dispose();
    }

    public String getControlName() {
        return componentName.getText();
    }
}
