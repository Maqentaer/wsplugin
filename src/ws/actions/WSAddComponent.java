package ws.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import org.jetbrains.annotations.NotNull;

import java.io.FileWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WSAddComponent extends AnAction {
    public static String jsFileString = "" +
            "define('js!%1$s', [\n" +
            "    'js!SBIS3.CORE.CompoundControl'\n" +
            "], function( CompoundControl ) {\n" +
            "    'use strict';\n" +
            "\n" +
            "    /**\n" +
            "    *\n" +
            "    * @class %1$s\n" +
            "    * @extends $ws.proto.CompoundControl\n" +
            "    */\n" +
            "    var %2$s = CompoundControl.extend(/** @lends %1$s.prototype */{\n" +
            "        $protected : {\n" +
            "            _options: {}\n" +
            "        },\n" +
            "        $constructor: function (){},\n" +
            "        init: function (){\n" +
            "            %2$s.superclass.init.call(this);\n" +
            "        }\n" +
            "    });\n" +
            "    return %2$s;\n" +
            "});";
    public static String xhtmlFileString = "<div class=\"%s\"></div>";
    public static String cssFileString = ".%s{}";

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        VirtualFile file = anActionEvent.getData(CommonDataKeys.VIRTUAL_FILE);
        Project project = anActionEvent.getData(CommonDataKeys.PROJECT);

        if (file == null || project == null) {
            return;
        }

        if (!file.isDirectory()) {
            file = file.getParent();
        }

        final PsiDirectory directory = PsiManager.getInstance(project).findDirectory(file);

        new WSAddComponentDialog() {
            @Override
            public void onOK() {

                if (directory != null && !getControlName().isEmpty()) {
                    Pattern pattern = Pattern.compile("^(SBIS3)\\.(\\w+)\\.(\\w+)");
                    Matcher matcher = pattern.matcher(getControlName());

                    if (matcher.find()) {
                        final String prName = matcher.group(2);
                        final String controlName = matcher.group(3);
                        WriteCommandAction.runWriteCommandAction(null, new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    PsiDirectory controlDirectory = directory.createSubdirectory(controlName);
                                    if (needJsFile()) {
                                        PsiFile jsFile = controlDirectory.createFile(controlName + ".module.js");

                                        FileWriter fileWriter = new FileWriter(jsFile.getVirtualFile().getPath());
                                        fileWriter.write(String.format(WSAddComponent.jsFileString, getControlName(), controlName));
                                        fileWriter.close();

                                    }
                                    if (needXhtmlFile()) {
                                        PsiFile xhtmlFile = controlDirectory.createFile(controlName + ".xhtml");

                                        FileWriter fileWriter = new FileWriter(xhtmlFile.getVirtualFile().getPath());
                                        fileWriter.write(String.format(WSAddComponent.xhtmlFileString, prName.toLowerCase() + "-" + controlName.toLowerCase()));
                                        fileWriter.close();
                                    }
                                    if (needCssFile()) {
                                        PsiFile checkCss = controlDirectory.createFile(controlName + ".css");
                                        FileWriter fileWriter = new FileWriter(checkCss.getVirtualFile().getPath());
                                        fileWriter.write(String.format(WSAddComponent.cssFileString, prName.toLowerCase() + "-" + controlName.toLowerCase()));
                                        fileWriter.close();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                }

                dispose();
            }
        };

    }
}
