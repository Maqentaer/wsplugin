package ws.reference;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import net.sf.cglib.core.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import ws.WSUtil;
import ws.index.WSFileBasedIndexExtension;

import java.util.Collection;
import java.util.HashSet;

public class WSControlReference extends WSPsiReference {

    public WSControlReference(PsiElement psiElement) {
        super(psiElement);
    }

    @NotNull
    public Object[] getVariants() {
        Collection<String> result = WSUtil.getVariantsByName(parseResult, project);
        return result.toArray();
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean b) {
        String controlName = parseResult[0];
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

        return PsiElementResolveResult.createResults(resultFilesCollection);
    }

}
