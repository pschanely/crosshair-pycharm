// Copyright (c) 2021 Marko Ristin <marko@ristin.ch>

package ch.ristin.crosshair_pycharm;

import com.intellij.execution.RunManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Check with crosshair-tool.
 */
public class CheckAction extends AnAction {
    /**
     * Specify and run the "crosshair check" command as a run configuration.
     *
     * @param event Event received when the associated menu item is chosen.
     */
    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        final Project project = event.getProject();
        assert project != null;

        if (!Checking.crosshairToolInstalled(project)) {
            return;
        }

        Context context = Contextualizing.infer(event);

        assert context != null;

        // Java 11 does not support switch expressions, so we have to use if-else here.
        String target;
        if (context.pyFunction != null && context.module != null) {
            target = String.format("%s.%s", context.module, context.pyFunction.getName());
        } else if (context.module != null) {
            target = context.module;
        } else {
            target = Contextualizing.path(context);
        }

        final String runName = String.format("crosshair check %s", target);

        var scriptParameters = new ArrayList<String>();
        scriptParameters.add("check");
        scriptParameters.add(target);

        final var runManager = RunManager.getInstance(project);
        final var runner = Running.FindOrCreateRunner(
                runManager, runName, scriptParameters,
                Pathing.systemDependentPath(context.project.getBasePath()));

        Running.execute(runManager, runner);
    }

    /**
     * Change the visibility/enabled as well as the title of the action depending on the context.
     *
     * @param event Event received when the associated menu item is chosen.
     */
    @Override
    public void update(@NotNull AnActionEvent event) {
        Context context = Contextualizing.infer(event);

        final Presentation presentation = event.getPresentation();

        if (context == null) {
            presentation.setEnabledAndVisible(false);
            return;
        }

        // Change the name depending whether it is a module or a global function.
        //
        // Mind that this is not the same logic as in actionPerformed(.), but is indeed
        // very similar.
        String target;
        if (context.pyFunction != null) {
            target = context.pyFunction.getName();
        } else if (context.module != null) {
            target = context.module;
        } else {
            target = Contextualizing.path(context);
        }

        presentation.setText(String.format("check %s", target), false);
        presentation.setDescription(
                String.format("Verify %s with crosshair-tool", target));

        presentation.setEnabledAndVisible(true);
    }
}
