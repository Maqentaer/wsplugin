package ws.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.ListPopup;
import com.intellij.openapi.ui.popup.PopupStep;
import com.intellij.openapi.ui.popup.util.BaseListPopupStep;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import ws.index.WSFileBasedIndexExtension;

import java.util.Collection;

public class WSGenerateComponent extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        final PsiElement elem = anActionEvent.getData(CommonDataKeys.PSI_ELEMENT);
        final VirtualFile file = anActionEvent.getData(CommonDataKeys.VIRTUAL_FILE);
        final Caret caret = anActionEvent.getData(CommonDataKeys.CARET);
        final Project project = anActionEvent.getData(CommonDataKeys.PROJECT);
        final Editor editor = anActionEvent.getData(CommonDataKeys.EDITOR);

        if (project == null || editor == null || caret == null) {
            return;
        }

        Collection<String> variants = WSFileBasedIndexExtension.getAllComponentNames(project);

        BaseListPopupStep<String> popupStep = new BaseListPopupStep<String>("Выберите компонент", variants.toArray(new String[variants.size()])) {
            @Override
            public PopupStep onChosen(final String selectedValue, boolean finalChoice) {
                WriteCommandAction.runWriteCommandAction(null, new Runnable() {
                    @Override
                    public void run() {
                        try {
                            int offset = caret.getOffset();
                            editor.getDocument().insertString(
                                    offset,
                                    "<component data-component=\""+ selectedValue + "\" name=\"\"></component>"
                            );
                            caret.moveToOffset(offset + selectedValue.length() + 35);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                return super.onChosen(selectedValue, finalChoice);
            }

            @Override
            public boolean isSpeedSearchEnabled() {
                return true;
            }
        };

        ListPopup popup = JBPopupFactory.getInstance().createListPopup(popupStep);
        popup.showInBestPositionFor(editor);
    }
}
