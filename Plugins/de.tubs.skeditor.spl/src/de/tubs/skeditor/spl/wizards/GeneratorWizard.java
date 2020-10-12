package de.tubs.skeditor.spl.wizards;

import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

import de.tubs.skeditor.spl.SplPlugin;
import de.tubs.skeditor.spl.utils.ConfigLoader;
import de.tubs.skeditor.spl.utils.FmpReader;
import de.tubs.skeditor.compositionality.CreateFileOperationCompose;
import de.tubs.skeditor.views.SafetyGoalsView;

public class GeneratorWizard extends Wizard implements INewWizard {

	public static final String ID = SplPlugin.PLUGIN_ID;
	
	private GeneratorWizardPage loadMPage;
	private ISelection selection;

	public GeneratorWizard() {
		super();
		setNeedsProgressMonitor(true);
	}
	
	/**
	 * Adding the page to the wizard.
	 */
	@Override
	public void addPages() {
		loadMPage = new GeneratorWizardPage(selection);
		addPage(loadMPage);
	}
		
	@Override
	public boolean performFinish() {
		final String containerName = loadMPage.getContainerName();
		final String fileName = loadMPage.getFileName();
		final String modelName = loadMPage.getModelName();
		final String configName = loadMPage.getConfigName();
		final String mapName = loadMPage.getMapName();
		IRunnableWithProgress op = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) throws InvocationTargetException {
				try {
					doFinish(containerName, fileName, monitor, modelName, configName, mapName);
				} catch (CoreException e) {
					throw new InvocationTargetException(e);
				} finally {
					monitor.done();
				}
			}
		};
		try {
			getContainer().run(true, false, op);
		} catch (InterruptedException e) {
			return false;
		} catch (InvocationTargetException e) {
			Throwable realException = e.getTargetException();
			MessageDialog.openError(getShell(), "Error", realException.getMessage());
			return false;
		}
		return true;
	}
	
	private void doFinish(String containerName, String fileName, IProgressMonitor monitor, String model, String configuration, String map)
			throws CoreException {
		// create a sample file
		monitor.beginTask("Creating " + fileName, 2);
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IResource resource = root.findMember(new Path(containerName));
		IResource modelRes = root.findMember(new Path(model));
		IResource configRes = root.findMember(new Path(configuration));
		IResource mapRes = root.findMember(new Path(map));
		ConfigLoader confLoad = new ConfigLoader(configRes.getLocation().toString(), modelRes.getLocation().toString());
		EList<String> graphList = new BasicEList<String>();
		try {
			graphList.addAll(FmpReader.extractGraphsViaConfiguration(mapRes.getLocation().toString(), confLoad.config));
		}
		catch(FileNotFoundException fnf) {
			fnf.printStackTrace();
		}
		graphList = removeRoot(root.getLocation().toString(), graphList);
		System.out.println("\n<Combining> from " + graphList + "\n" + graphList.get(0) + "\nwith\n" + graphList.get(1) +"\n<end>");
		if (!resource.exists() || !(resource instanceof IContainer)) {
			throwCoreException("Container \"" + containerName + "\" does not exist.");
		}
		IContainer container = (IContainer) resource;
		final IFile file = container.getFile(new Path(fileName));

		ResourceSet rSet = new ResourceSetImpl();

		TransactionalEditingDomain editingDomain = TransactionUtil.getEditingDomain(rSet);
		if (editingDomain == null) {
			// Not yet existing, create one
			editingDomain = TransactionalEditingDomain.Factory.INSTANCE.createEditingDomain(rSet);
		}

		CreateFileOperationCompose operation = new CreateFileOperationCompose(editingDomain, containerName, fileName,
				graphList.get(0), graphList.get(1));
		editingDomain.getCommandStack().execute(operation);

		// Dispose the editing domain to eliminate memory leak
		editingDomain.dispose();

		monitor.worked(1);
		monitor.setTaskName("Opening file for editing...");

		getShell().getDisplay().asyncExec(new Runnable() {
			public void run() {
				IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				IViewReference[] ref = page.getViewReferences();

				for (IViewReference iViewReference : ref) {
					if (iViewReference.getId().equals("de.tubs.skeditor.views.SafetyGoalsView")) {// TODO rework, bit
																									// too hacky
						((SafetyGoalsView) iViewReference.getView(true)).getViewer().refresh();
					}
				}

				try {
					IDE.openEditor(page, file, true);
				} catch (PartInitException e) {
				}
			}
		});
		monitor.worked(1);

	}
	
	private EList<String> removeRoot(String root, EList<String> addreses) {
		EList<String> withoutRoot = new BasicEList<String>();
		for (String iter : addreses) {
			withoutRoot.add(iter.replaceAll(root, ""));
		}
		return withoutRoot;
	}

	private void throwCoreException(String message) throws CoreException {
		IStatus status = new Status(IStatus.ERROR, "ContractModelling", IStatus.OK, message, null);
		throw new CoreException(status);
	}

	/**
	 * We will accept the selection in the workbench to see if we can initialize
	 * from it.
	 * 
	 * @see IWorkbenchWizard#init(IWorkbench, IStructuredSelection)
	 */
	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.selection = selection;
	}


}