package ws.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;

public class WSAddComponent extends com.intellij.openapi.actionSystem.AnAction {
    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        //anActionEvent.getData();
        new WSAddComponentDialog(){
            private void onOK() {
                dispose();
            }
        };

    }
}
