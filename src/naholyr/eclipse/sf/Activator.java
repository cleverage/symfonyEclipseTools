package naholyr.eclipse.sf;

import naholyr.eclipse.sf.tploverride.ResourceChangeListener;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "naholyr.eclipse.sf"; //$NON-NLS-1$

	public static final String PREFERENCES_DEFAULT_OPEN_TEMPLATES_OVERRIDE = "open-templates-override"; // $NON-NLS-1$ //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;

	// Resource listener
	private static IResourceChangeListener resourceChangeListener;

	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		resourceChangeListener = new ResourceChangeListener();
		ResourcesPlugin.getWorkspace().addResourceChangeListener(
				resourceChangeListener, IResourceChangeEvent.POST_CHANGE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
	 * )
	 */
	public void stop(BundleContext context) throws Exception {
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(
				resourceChangeListener);
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given plug-in
	 * relative path
	 * 
	 * @param path
	 *            the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	public static void openFile(IPath path) {
		IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		IWorkbenchPage page = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage();
		try {
			if (path.isAbsolute()) {
				IPath workspacePath = ResourcesPlugin.getWorkspace().getRoot()
						.getLocation();
				if (workspacePath.isPrefixOf(path)) {
					// Absolute path, but in the workspace anyway : convert to
					// relative
					path = path.removeFirstSegments(workspacePath
							.segmentCount());
					IFile file = workspaceRoot.getFile(path);
					IDE.openEditor(page, file);
				} else {
					// Absolute path, outside workspace
					IFileStore fileStore;
					fileStore = EFS.getStore(path.toFile().toURI());
					IDE.openEditorOnFileStore(page, fileStore);
				}
			} else {
				// Relative to workspace
				IFile file = workspaceRoot.getFile(path);
				IDE.openEditor(page, file);
			}
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void openFiles(IPath[] paths) {
		for (IPath path : paths) {
			openFile(path);
		}
	}

}
