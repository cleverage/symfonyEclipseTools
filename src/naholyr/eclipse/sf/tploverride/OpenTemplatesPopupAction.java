package naholyr.eclipse.sf.tploverride;

import naholyr.eclipse.sf.Activator;
import naholyr.eclipse.sf.SymfonyTools;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

public class OpenTemplatesPopupAction implements IObjectActionDelegate {

	private IWorkbenchPart part;
	private IFile selectedFile;

	/**
	 * Constructor for Action1.
	 */
	public OpenTemplatesPopupAction() {
		super();
	}

	/**
	 * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		part = targetPart;
	}

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) {
		if (selectedFile != null) {
			Shell parent = part.getSite().getShell();
			IPath[] paths = SymfonyTools.getTemplateOverrides(selectedFile);
			if (paths != null && paths.length > 0) {
				IPath[] allPaths = new IPath[paths.length + 1];
				allPaths[0] = selectedFile.getLocation();
				for (int i = 0; i < paths.length; i++) {
					allPaths[i + 1] = paths[i];
				}
				Activator.openFiles(paths);
			} else {
				MessageDialog
						.openError(
								parent,
								"Loupé",
								"Vous ne pouvez appeler cette action que sur un template qui surcharge ou est surcharge par un autre fichier.");
			}
		}
	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		if (selection != null && !selection.isEmpty()
				&& selection instanceof StructuredSelection) {
			Object selected = ((StructuredSelection) selection)
					.getFirstElement();
			if (selected != null && selected instanceof IFile) {
				selectedFile = (IFile) selected;
			} else {
				selectedFile = null;
			}
		} else {
			selectedFile = null;
		}
	}

}
