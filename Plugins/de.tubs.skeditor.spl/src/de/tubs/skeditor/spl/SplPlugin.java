package de.tubs.skeditor.spl;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle.
 *
 * @author Alexander Knueppel, Anton Guenther 
 */
public class SplPlugin extends de.tubs.skeditor.Activator {
	
	// The plug-in ID
	public static final String PLUGIN_ID = "de.tubs.skeditor.spl"; //$NON-NLS-1$

	// The shared instance
	private static SplPlugin plugin;

	/**
	 * The constructor
	 */
	public SplPlugin() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}
	
	public static SplPlugin getDefault() {
		return plugin;
	}
	
	public String getID() {
		return SplPlugin.PLUGIN_ID;
	}
	
	public static IProject getCurrentProject() {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		IWorkbenchPage activePage = window.getActivePage();

		IStructuredSelection selection = (IStructuredSelection) window.getSelectionService().getSelection();
		if (!selection.isEmpty()) {
			Object firstElement = selection.getFirstElement();
			if (firstElement instanceof IAdaptable && firstElement instanceof IProject) {
				return (IProject) ((IAdaptable) firstElement).getAdapter(IProject.class);
			}
		}

		IEditorPart activeEditor = activePage.getActiveEditor();

		if (activeEditor != null) {
			IEditorInput input = activeEditor.getEditorInput();

			IProject project = input.getAdapter(IProject.class);
			if (project == null) {
				IResource resource = input.getAdapter(IResource.class);
				if (resource != null) {
					project = resource.getProject();
				}
			}
			return project;
		}
		return null;
	}
}
