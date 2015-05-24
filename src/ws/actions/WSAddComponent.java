package ws.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;

public class WSAddComponent extends com.intellij.openapi.actionSystem.AnAction {
    public static String jsFileString = "" +
            "define('js!%1$s', [\n" +
            "    'js!SBIS3.CORE.CompoundControl'\n" +
            "], function( CompoundControl ) {\n" +
            "\n" +
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
    public void actionPerformed(final AnActionEvent anActionEvent) {
        //anActionEvent.getData();

        new WSAddComponentDialog(anActionEvent.getProject());

    }
}
