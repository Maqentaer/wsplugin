package ws.reference;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import ws.WSUtil;
import ws.index.WSFileBasedIndexExtension;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

public class WSJSReference extends WSPsiReference {

    public WSJSReference(PsiElement psiElement) {
        super(psiElement);
    }

    @NotNull
    public Object[] getVariants() {
        Collection<String> variants = WSUtil.getVariantsByName(parseResult, project);
        Object[] result = new Object[variants.size()];

        int i = 0;
        for (Object variant : variants) {
            result[i++] = "js!" + variant;
        }

        return result;
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean b) {
        /*String controlName = parseResult[0];
        String path = parseResult[1];

        if (controlName == null || controlName.isEmpty()) {
            return new ResolveResult[0];
        }

        Collection<VirtualFile> files = WSFileBasedIndexExtension.getFileByComponentName(project, controlName);

        if (files.size() == 0) {
            return new ResolveResult[0];
        }

        final PsiManager instance = PsiManager.getInstance(project);

        Collection<PsiElement> resultFilesCollection = new HashSet<PsiElement>();


        for (VirtualFile file : files) {
            if (path != null && !path.isEmpty()) {
                VirtualFile pathFile = file.getParent().findFileByRelativePath(path + ".js");
                if (pathFile != null) {
                    file = pathFile;
                } else {
                    continue;
                }
            }
            try {
                PsiFile psiFile = instance.findFile(file);
                if (psiFile != null) {
                    resultFilesCollection.add(instance.findFile(file));
                }
            } catch (Exception ignore) {
            }
        }
*/
        return PsiElementResolveResult.createResults(WSUtil.getFilesByNameWithPrefix(parseResult, project));
    }
}
