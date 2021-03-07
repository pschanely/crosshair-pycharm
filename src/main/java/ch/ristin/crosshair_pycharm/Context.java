// Copyright (c) 2021 Marko Ristin <marko@ristin.ch>

package ch.ristin.crosshair_pycharm;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFileSystemItem;
import com.jetbrains.python.psi.PyFunction;

import javax.annotation.Nullable;

/**
 * Represent the context inferred from the caret.
 */
public class Context {
    /**
     * A global function corresponding to the caret as we go up the AST
     */
    @Nullable
    final PyFunction pyFunction;

    /**
     * Python file which is currently edited
     */
    final PsiFileSystemItem psiFileSystemItem;

    /**
     * Python module corresponding to the virtualFile, if possible to infer
     */
    @Nullable
    final String module;

    final Project project;

    public Context(
            @Nullable PyFunction pyFunction,
            PsiFileSystemItem psiFileSystemItem,
            @Nullable String module,
            Project project) {
        this.pyFunction = pyFunction;
        this.psiFileSystemItem = psiFileSystemItem;
        this.module = module;
        this.project = project;
    }
}
