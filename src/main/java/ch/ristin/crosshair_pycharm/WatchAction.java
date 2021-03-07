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
public class WatchAction extends AnAction {
    /**
     * Specify and run the "crosshair watch" command as a run configuration.
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

        final String target = Contextualizing.path(context);

        final String runName = String.format("crosshair watch %s", target);

        var scriptParameters = new ArrayList<String>();
        scriptParameters.add("watch");
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

        final String target = Contextualizing.path(context);

        presentation.setText(String.format("watch %s", target), false);
        presentation.setDescription(
                String.format("Watch and continuously verify %s with crosshair-tool", target));

        presentation.setEnabledAndVisible(true);
    }
}
