package ws;

import com.intellij.openapi.vfs.VirtualFile;

import java.util.Collection;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WSUtil {

    protected static String REGEX_PATTERN = "(SBIS3\\.\\w+\\.\\w+)(.*)?";
    protected static String REGEX_PATTERN_WITH_PREFIX = "^['\"]js!(SBIS3\\.\\w+\\.\\w+)(.*)";

    public static String[] parseComponentName(String text) {
        String[] result = {"", "", null};
        Pattern pattern = Pattern.compile(REGEX_PATTERN);
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            //fixme: IntellijIdeaRulezzz
            result[0] = matcher.group(1).replace("IntellijIdeaRulezzz", "");
            result[1] = matcher.group(2) != null ? matcher.group(2).replace("IntellijIdeaRulezzz", "") : "";

            String[] str = result[1].split(":");
            if (str.length > 1) {
                result[1] = str[0];
                result[2] = str[1];
            }
        }
        return result;
    }

    public static Collection<String> getChildFiles(VirtualFile file, String key) {
        Collection<String> result = new HashSet<String>();
        String fileName = file.getName();
        String filePath = file.getPath();
        String fileShortPath = filePath.replace(fileName, "");

        VirtualFile[] childFiles = file.isDirectory() ? file.getChildren() : file.getParent().getChildren();
        for (VirtualFile childFile : childFiles) {
            String path = childFile.getPath();
            path = path.replace(fileShortPath, "");
            if (childFile.isDirectory()) {
                result.addAll(getChildFiles(childFile, key));
            } else if (!path.isEmpty() && !path.equals(fileName) && path.endsWith(".js") && !path.endsWith(".module.js")) {
                result.add(key + "/" + path.replace(".js", ""));
            }
        }
        return result;
    }
}
