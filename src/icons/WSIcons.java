package icons;

import com.intellij.openapi.util.IconLoader;

import javax.swing.*;

public class WSIcons {
    private static Icon load(String path) {
        return IconLoader.getIcon(path, WSIcons.class);
    }

    public static final Icon ws = load("/icons/ws.png");
}
