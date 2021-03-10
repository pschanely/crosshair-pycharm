// Copyright (c) 2021 Marko Ristin <marko@ristin.ch>

package ch.ristin.crosshair_pycharm;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.actionSystem.Presentation;
import org.jetbrains.annotations.NotNull;

public class ActionGroup extends DefaultActionGroup {
    @Override
    public void update(@NotNull AnActionEvent e) {
        final Context context = Contextualizing.infer(e);

        final Presentation presentation = e.getPresentation();

        if (context == null) {
            presentation.setEnabledAndVisible(false);
        }

        presentation.setEnabledAndVisible(true);
    }
}
