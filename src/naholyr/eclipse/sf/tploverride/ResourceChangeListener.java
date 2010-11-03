package naholyr.eclipse.sf.tploverride;

import naholyr.eclipse.sf.Activator;
import naholyr.eclipse.sf.SymfonyTools;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.preference.IPreferenceStore;

public class ResourceChangeListener implements IResourceChangeListener {

	static final class ChangeVisitor implements IResourceDeltaVisitor {
		public boolean visit(IResourceDelta delta) {
			// Is a content change
			if (delta.getKind() == IResourceDelta.CHANGED
					&& (delta.getFlags() & IResourceDelta.CONTENT) != 0) {
				IResource res = delta.getResource();
				// Is a symfony template
				if (SymfonyTools.isTemplate(res)) {
					// Is overriden or overrides another template
					IPath[] templates = SymfonyTools.getTemplateOverrides(res);
					if (templates != null && templates.length > 0) {
						IPreferenceStore store = Activator.getDefault()
								.getPreferenceStore();
						String key = Activator.PREFERENCES_DEFAULT_OPEN_TEMPLATES_OVERRIDE;
						MessageDialog dialog = MessageDialogWithToggle
								.openYesNoQuestion(
										null,
										"Ouvrir les templates correspondants",
										String.format(
												"Le fichier '%s' surcharge ou est surcharge par un autre template dans le meme projet symfony. Vous devriez verifier qu'aucun changement ne doit etre applique aussi a ces fichiers. Voulez-vous les ouvrir ?",
												res.getName()), null, false,
										store, key);
						if (dialog.getReturnCode() == IDialogConstants.YES_ID) {
							Activator.openFiles(templates);
						}
					}
					return false; // stop here
				}
			}
			return true; // visit the children
		}
	}

	@Override
	public void resourceChanged(IResourceChangeEvent event) {
		// Only post change event
		if (event.getType() != IResourceChangeEvent.POST_CHANGE) {
			return;
		}
		// Visit participants
		try {
			event.getDelta().accept(new ChangeVisitor());
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}
}
