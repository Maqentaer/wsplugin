package ws.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.ListPopup;
import com.intellij.openapi.ui.popup.PopupStep;
import com.intellij.openapi.ui.popup.SpeedSearchFilter;
import com.intellij.openapi.ui.popup.util.BaseListPopupStep;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import ws.index.WSFileBasedIndexExtension;

import java.util.Collection;

public class WSGenerateComponent extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        PsiElement elem = anActionEvent.getData(CommonDataKeys.PSI_ELEMENT);
        VirtualFile file = anActionEvent.getData(CommonDataKeys.VIRTUAL_FILE);
        Caret caret = anActionEvent.getData(CommonDataKeys.CARET);

        Project project = anActionEvent.getData(CommonDataKeys.PROJECT);
        Editor editor = anActionEvent.getData(CommonDataKeys.EDITOR);

        if (project == null || editor == null) {
            return;
        }

        Collection<String> variants = WSFileBasedIndexExtension.getAllComponentNames(project);

        BaseListPopupStep<String> popupStep = new BaseListPopupStep<String>("Выберите компонент", variants.toArray(new String[variants.size()])){
            @Override
            public PopupStep onChosen(String selectedValue, boolean finalChoice) {
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
