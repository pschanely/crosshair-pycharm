package ch.ristin.crosshair_pycharm;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.ui.Messages;
import com.jetbrains.python.packaging.PyPackageManager;

/**
 * Check that the dependencies are correctly installed and inform the user via dialogs otherwise.
 */
public class Checking {
    /**
     * Check that the correct version of crosshair-tool is installed in the virtual environment.
     *
     * @param project related to the event
     * @return true if everything is OK
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    static public boolean crosshairToolInstalled(Project project) {
        final Sdk sdk = ProjectRootManager.getInstance(project).getProjectSdk();
        if (sdk == null) {
            Messages.showMessageDialog(
                    project,
                    "There is no SDK specified for the project.\n\n" +
                    "crosshair-pycharm needs an interpreter to run crosshair-tool.",
                    "No SDK Specified",
                    Messages.getErrorIcon());
            return false;
        }

        final var packages = PyPackageManager.getInstance(sdk).getPackages();
        if (packages == null) {
            Messages.showMessageDialog(
                    project,
                    "There are no packages specified for the SDK: " + sdk.getName() + ".\n" +
                            "Crosshair-pycharm depends on the package crosshair-tool.",
                    "No Packages Specified for the SDK",
                    Messages.getErrorIcon());
            return false;
        }

        String versionFound = "";
        for (var pkg : packages) {
            if (pkg.getName().equals("crosshair-tool")) {
                versionFound = pkg.getVersion();
                break;
            }
        }

        if (versionFound.equals("")) {
            Messages.showMessageDialog(
                    project,
                    "The package crosshair-tool has not been installed " +
                            "in the virtual environment of your current SDK: " + sdk.getName() + ".\n\n" +
                            "Crosshair-pycharm depends on crosshair-tool.\n" +
                            "Please install it in your virtual environment.",
                    "No Crosshair-Tool Found",
                    Messages.getErrorIcon());
            return false;
        }

        if (!versionFound.startsWith("0.") && !versionFound.startsWith("1.")) {
            Messages.showMessageDialog(
                    project,
                    "The version of this crosshair-pycharm expects the version 0.*.* or 1.*.* " +
                            "of the crosshair-tool.\n\n" +
                            "However, crosshair-tool " + versionFound + " is installed " +
                            "in the virtual environment of your current SDK: " + sdk.getName() + ".\n\n" +
                            "Please install the expected version of crosshair-tool in your virtual environment.",
                    "Unexpected Version of Crosshair-Tool Found",
                    Messages.getErrorIcon());
            return false;
        }

        return true;
    }
}
