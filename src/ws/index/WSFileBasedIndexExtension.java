package ws.index;

import com.intellij.lang.javascript.psi.JSArgumentList;
import com.intellij.lang.javascript.psi.JSCallExpression;
import com.intellij.lang.javascript.psi.JSExpression;
import com.intellij.lang.javascript.psi.JSReferenceExpression;
import com.intellij.lang.javascript.psi.impl.JSCallExpressionImpl;
import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiElementProcessor;
import com.intellij.psi.util.PsiElementFilter;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.indexing.*;
import com.intellij.util.io.DataExternalizer;
import com.intellij.util.io.EnumeratorStringDescriptor;
import com.intellij.util.io.KeyDescriptor;
import org.jetbrains.annotations.NotNull;
import ws.WSUtil;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WSFileBasedIndexExtension extends FileBasedIndexExtension<String, Void> {

    private static final int INDEX_VERSION = 132; // !!!ВАЖНО!!! при изменении логики построения индекса необходимо увеличить версию индекса

    public static final ID<String, Void> WS_PATH_INDEX = ID.create("wsPathIndex");
    private DataIndexer<String, Void, FileContent> myDataIndexer = new MyDataIndexer();

    public static Collection<VirtualFile> getFileByComponentName(@NotNull final Project project, @NotNull final String name) {
        return FileBasedIndex.getInstance().getContainingFiles(WS_PATH_INDEX, name, GlobalSearchScope.projectScope(project));
    }

    public static Collection<String> getAllComponentNames(@NotNull final Project project) {
        return FileBasedIndex.getInstance().getAllKeys(WS_PATH_INDEX, project);
    }

    private static class MyDataIndexer implements DataIndexer<String, Void, FileContent> {
        @Override
        @NotNull
        public Map<String, Void> map(@NotNull final FileContent inputData) {
            String key = null;
            try {
                PsiElementFilter filter = new PsiElementFilter() {
                    @Override
                    public boolean isAccepted(PsiElement psiElement) {

                        if (PsiTreeUtil.instanceOf(psiElement, JSArgumentList.class)) {
                            try {
                                String functionName = ((JSCallExpressionImpl) psiElement.getParent()).getMethodExpression().getText();
                                return functionName.equals("define");
                            } catch (Exception ignore) {
                                return false; // значит что то не кастуется, ну и пусть
                            }
                        }
                        return false;
                    }
                };

                PsiElement[] argListCollection = PsiTreeUtil.collectElements(inputData.getPsiFile(), filter);

                if (argListCollection.length > 0) {
                    JSExpression[] arg = ((JSArgumentList) argListCollection[0]).getArguments();
                    if (arg.length > 0) {
                        Pattern pattern = Pattern.compile(WSUtil.INDEX_REGEX_PATTERN);
                        Matcher matcher = pattern.matcher(arg[0].getText());

                        if (matcher.find()) {
                            String controlName = matcher.group(1);
                            if (controlName != null) {
                                key = controlName;
                            }
                        }
                    }
                }
            } catch (Exception ignore) {
            }


            if (key == null || key.isEmpty()) {
                return Collections.emptyMap();
            }

            return Collections.singletonMap(key, null);
        }
    }

    public class WSInputFilter extends DefaultFileTypeSpecificInputFilter {
        @Override
        public boolean acceptInput(@NotNull VirtualFile file) {
            boolean accepts = super.acceptInput(file);
            accepts = accepts && file.getFileType() == StdFileTypes.JS;
            /*if (accepts && file.getFileType() == StdFileTypes.JS) {
                accepts = (file.getName().endsWith(".module.js") || file.getName().endsWith("-plugin.js"));
            }*/
            return accepts;
        }

        public WSInputFilter() {
            super(StdFileTypes.JS);
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

    @Override
    public boolean dependsOnFileContent() {
        return true;
    }
}
