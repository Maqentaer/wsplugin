package ws.actions;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.*;

public abstract class WSAddComponentDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField componentName;
    private JCheckBox checkJs;
    private JCheckBox checkXhtml;
    private JCheckBox checkCss;

    public WSAddComponentDialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setSize(400, 100);
        setResizable(false);
        pack();
        setLocationRelativeTo(null);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

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

        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        componentName.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                checkEnabled();
            }
            public void removeUpdate(DocumentEvent e) {
                checkEnabled();
            }
            public void insertUpdate(DocumentEvent e) {
                checkEnabled();
            }
        });

        checkJs.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                checkEnabled();
            }
        });
        checkXhtml.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                checkEnabled();
            }
        });
        checkCss.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                checkEnabled();
            }
        });

        setVisible(true);
    }

    abstract void onOK();

    private void checkEnabled() {
        buttonOK.setEnabled(
            componentName.getText().matches("^(SBIS3)\\.(\\w+)\\.(\\w+)") &&
            (needJsFile() || needXhtmlFile() || needCssFile())
        );
    }

    private void onCancel() {
        dispose();
    }

    public String getControlName() {
        return componentName.getText();
    }

    public boolean needJsFile() {
        return checkJs.isSelected();
    }

    public boolean needXhtmlFile() {
        return checkXhtml.isSelected();
    }

    public boolean needCssFile() {
        return checkCss.isSelected();
    }

}
