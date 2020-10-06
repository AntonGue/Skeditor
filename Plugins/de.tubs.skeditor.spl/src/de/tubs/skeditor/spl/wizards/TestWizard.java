package de.tubs.skeditor.spl.wizards;

//import java.io.ByteArrayInputStream;
//import java.io.InputStream;
//
//import org.eclipse.core.runtime.CoreException;
//import org.eclipse.core.runtime.IProgressMonitor;
//import org.eclipse.core.runtime.IStatus;
//import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

public class TestWizard extends Wizard implements INewWizard {
	
	private LoadModelPage loadMPage;
	private ISelection selection;

	public TestWizard() {
		super();
		setNeedsProgressMonitor(true);
	}

	@Override
	public void addPages() {
		loadMPage = new LoadModelPage(selection);
		addPage(loadMPage);
	}
	
	@Override
	public boolean performFinish() {	//Überarbeiten
//		final String containerName = loadMPage.getContainerName();
//		final String modelName = loadMPage.getModelName();
//		final String fileName = loadMPage.getFileName();
//		IRunnableWithProgress op = new IRunnableWithProgress() {
//			public void run(IProgressMonitor monitor) throws InvocationTargetException {
//				try {
//					doFinish(containerName, fileName, monitor, modelName, fileName);
//				} catch (CoreException e) {
//					throw new InvocationTargetException(e);
//				} finally {
//					monitor.done();
//				}
//			}
//		};
//		try {
//			getContainer().run(true, false, op);
//		} catch (InterruptedException e) {
//			return false;
//		} catch (InvocationTargetException e) {
//			Throwable realException = e.getTargetException();
//			MessageDialog.openError(getShell(), "Error", realException.getMessage());
//			return false;
//		}
		return true;
	}
	
//	private void doFinish(String containerName, String fileName, IProgressMonitor monitor, String GraphA, String GraphB)
//			throws CoreException {
		// create a sample file
//		monitor.beginTask("Creating " + fileName, 2);
//		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
//		IResource resource = root.findMember(new Path(containerName));
//		if (!resource.exists() || !(resource instanceof IContainer)) {
//			throwCoreException("Container \"" + containerName + "\" does not exist.");
//		}
//		IContainer container = (IContainer) resource;
//		final IFile file = container.getFile(new Path(fileName));
//
//		ResourceSet rSet = new ResourceSetImpl();
//
//		URI uri = URI.createFileURI(file.getFullPath().toString());
//
//		TransactionalEditingDomain editingDomain = TransactionUtil.getEditingDomain(rSet);
//		if (editingDomain == null) {
//			// Not yet existing, create one
//			editingDomain = TransactionalEditingDomain.Factory.INSTANCE.createEditingDomain(rSet);
//		}
//
//		CreateFileOperationCompose operation = new CreateFileOperationCompose(editingDomain, containerName, fileName,
//				GraphA, GraphB);
//		editingDomain.getCommandStack().execute(operation);
//
//		// Dispose the editing domain to eliminate memory leak
//		editingDomain.dispose();
//
//		monitor.worked(1);
//		monitor.setTaskName("Opening file for editing...");
//
//		getShell().getDisplay().asyncExec(new Runnable() {
//			public void run() {
//				IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
//				IViewReference[] ref = page.getViewReferences();
//
//				for (IViewReference iViewReference : ref) {
//					if (iViewReference.getId().equals("de.tubs.skeditor.views.SafetyGoalsView")) {// TODO rework, bit
//																									// too hacky
//						((SafetyGoalsView) iViewReference.getView(true)).getViewer().refresh();
//					}
//				}
//
//				try {
//					IDE.openEditor(page, file, true);
//				} catch (PartInitException e) {
//				}
//			}
//		});
//		monitor.worked(1);
//	}
	
//	private InputStream openContentStream() {
//		String contents = "This is the initial file contents for *.sked files that should be word-sorted in the Preview page of the multi-page editor";
//		return new ByteArrayInputStream(contents.getBytes());
//	}
//
//	private void throwCoreException(String message) throws CoreException {
//		IStatus status = new Status(IStatus.ERROR, "ContractModelling", IStatus.OK, message, null);
//		throw new CoreException(status);
//	}
	
	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.selection = selection;
	}
}