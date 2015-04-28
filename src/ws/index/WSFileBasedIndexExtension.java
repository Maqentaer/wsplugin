package ws.index;

import com.intellij.lang.javascript.psi.JSArgumentList;
import com.intellij.lang.javascript.psi.JSExpression;
import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.indexing.*;
import com.intellij.util.io.DataExternalizer;
import com.intellij.util.io.EnumeratorStringDescriptor;
import com.intellij.util.io.KeyDescriptor;
import org.jetbrains.annotations.NotNull;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WSFileBasedIndexExtension extends FileBasedIndexExtension<String, Void> {
    private static final int INDEX_VERSION = 22;
    public static final ID<String, Void> WS_PATH_INDEX = ID.create("wsPathIndex");
    private DataIndexer<String, Void, FileContent> myDataIndexer = new MyDataIndexer();

    public static Collection<VirtualFile> getFileByComponentName(@NotNull final Project project, @NotNull final String name) {
        return  FileBasedIndex.getInstance().getContainingFiles(WS_PATH_INDEX, name, GlobalSearchScope.projectScope(project));
    }

    private static class MyDataIndexer implements DataIndexer<String, Void, FileContent> {
        @Override
        @NotNull
        public Map<String, Void> map(@NotNull final FileContent inputData) {
            String key = null;
            Collection<JSArgumentList> argList = PsiTreeUtil.findChildrenOfType(inputData.getPsiFile(), JSArgumentList.class);

            if(argList.iterator().hasNext()){
                JSExpression[] arg = argList.iterator().next().getArguments();
                if(arg.length > 0){
                    Pattern pattern = Pattern.compile("^'|\"js!SBIS3\\.(\\w+\\.\\w+)");
                    Matcher matcher = pattern.matcher(arg[0].getText());

                    if (matcher.find()) {
                        String controlName = matcher.group(1);
                        if(controlName != null){
                            key = controlName;
                        }
                    }
                }
            }
            if(key == null || key.isEmpty()){
                return Collections.emptyMap();
            }

            return Collections.singletonMap(key, null);
        }
    }

    @NotNull
    @Override
    public ID<String, Void> getName() {
        return WS_PATH_INDEX;
    }

    @Override
    public int getVersion() {
        return INDEX_VERSION;
    }

    @NotNull
    @Override
    public DataIndexer<String, Void, FileContent> getIndexer() {
        return myDataIndexer;
    }

    @NotNull
    @Override
    public KeyDescriptor<String> getKeyDescriptor() {
        return new EnumeratorStringDescriptor();
    }

    @NotNull
    @Override
    public DataExternalizer<Void> getValueExternalizer() {
        return ScalarIndexExtension.VOID_DATA_EXTERNALIZER;
    }

    @NotNull
    @Override
    public FileBasedIndex.InputFilter getInputFilter() {
        return new WSInputFilter();
    }

    public class WSInputFilter extends DefaultFileTypeSpecificInputFilter {
        @Override
        public boolean acceptInput(@NotNull VirtualFile file) {
            boolean accepts = super.acceptInput(file);
            if (accepts && file.getFileType() == StdFileTypes.JS) {
                accepts = (file.getName().contains(".module.js"));
            }
            return accepts;
        }

        public WSInputFilter() {
            super(StdFileTypes.JS);
        }
    }

    @Override
    public boolean dependsOnFileContent() {
        return true;
    }
}
