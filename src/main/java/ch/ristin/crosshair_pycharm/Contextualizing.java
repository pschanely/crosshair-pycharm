// Copyright (c) 2021 Marko Ristin <marko@ristin.ch>

package ch.ristin.crosshair_pycharm;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileSystemItem;
import com.intellij.psi.PsiManager;
import com.jetbrains.python.PythonFileType;
import com.jetbrains.python.psi.PyFile;
import com.jetbrains.python.psi.PyFunction;
import com.jetbrains.python.psi.resolve.QualifiedNameFinder;

import javax.annotation.Nullable;

public class Contextualizing {
    /**
     * Infer the context from the caret.
     *
     * @param event event defining the context
     * @return context of the caret or null if the context could not be inferred (*e.g.*, if not editing a Python file)
     */
    static public @Nullable
    Context infer(AnActionEvent event) {
        final var dataContext = event.getDataContext();
        final VirtualFile virtualFile = dataContext.getData(CommonDataKeys.VIRTUAL_FILE);

        if (virtualFile == null) {
            return null;
        }

        if (!(virtualFile.getFileType() instanceof PythonFileType) && !virtualFile.isDirectory()) {
            return null;
        }

        final Project project = event.getProject();
        if (project == null) {
            return null;
        }

        // Try to find the module
        final PsiFileSystemItem psiFileSystemItem = virtualFile.isDirectory()
                ? PsiManager.getInstance(project).findDirectory(virtualFile)
                : PsiManager.getInstance(project).findFile(virtualFile);

        if (psiFileSystemItem == null) {
            return null;
        }

        @Nullable String module = QualifiedNameFinder.findShortestImportableName(psiFileSystemItem, virtualFile);

        final EditorEx editor = (EditorEx) CommonDataKeys.EDITOR.getData(event.getDataContext());
        if (editor == null) {
            // We can only retrieve the information about the module.
            return new Context(null, psiFileSystemItem, module, project);
        }

        if (!(psiFileSystemItem instanceof PsiFile)) {
            throw new AssertionError(
                    String.format(
                            "Unexpected psiFileSystemItem which is not PsiFile in editor: %s. " +
                                    "Expected a directory to be resolved before. " +
                                    "if the item is not a directory, we expected it to be a Python file.",
                            psiFileSystemItem));
        }

        final PsiFile psiFile = (PsiFile) psiFileSystemItem;

        final int offset = editor.getExpectedCaretOffset();

        var element = psiFile.findElementAt(offset);

        PyFunction pyFunction = null;  // a global function corresponding to the caret
        while (element != null && !(element instanceof PyFile)) {
            if (element instanceof PyFunction) {
                pyFunction = (PyFunction) element;
            }

            element = element.getParent();
        }

        if (element == null) {
            // We couldn't infer the global function, so we can only test the whole file.
            return new Context(null, psiFile, module, project);
        }

        return new Context(pyFunction, psiFile, module, project);
    }

    /**
     * Compute the path as view of a context.
     *
     * @param context of an action
     * @return system-dependent path (with base path replaced by ".", if prefixed)
     */
    static public String path(Context context) {
        final @Nullable String basePath = context.project.getBasePath();
        final String path = context.psiFileSystemItem.getVirtualFile().getPath();

        return Pathing.systemDependentPath(Pathing.stripBaseBath(path, basePath));
    }
}
