package de.tubs.skeditor.spl.wizards;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;
import org.eclipse.ui.dialogs.ResourceSelectionDialog;

public class GeneratorWizardPage extends WizardPage {

	private Text containerText;
	private Text modelText;
	private Text configuText;
	private Text mappingText;
	private Text fileText;
	private ISelection selection;
	
	public GeneratorWizardPage(ISelection selection) {
		super("wizardPage");
		setTitle("Skeditor Feature Composition");
		setDescription("This wizard composes Skill Graphs based on selected Features.");
		this.selection = selection;
	}
	
	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 3;
		layout.verticalSpacing = 9;
		
		Label label = new Label(container, SWT.NULL);
		label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label.setText("Container to save:");

		containerText = new Text(container, SWT.BORDER | SWT.SINGLE);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		containerText.setLayoutData(gd);
		containerText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		});

		Button button = new Button(container, SWT.PUSH);
		button.setText("Browse...");
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				handleBrowseContainer();
			}
		});
		
		label = new Label(container, SWT.NULL);
		label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label.setText("XML Model File:");

		modelText = new Text(container, SWT.BORDER | SWT.SINGLE);
		GridData gdX = new GridData(GridData.FILL_HORIZONTAL);
		modelText.setLayoutData(gdX);
		modelText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		});

		Button buttonX = new Button(container, SWT.PUSH);
		buttonX.setText("Browse...");
		buttonX.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent b) {
				handleBrowseResource(modelText);
			}
		});
		
		label = new Label(container, SWT.NULL);
		label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label.setText("XML Configuration File:");

		configuText = new Text(container, SWT.BORDER | SWT.SINGLE);
		gdX = new GridData(GridData.FILL_HORIZONTAL);
		configuText.setLayoutData(gdX);
		configuText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		});

		Button buttonY = new Button(container, SWT.PUSH);
		buttonY.setText("Browse...");
		buttonY.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent b) {
				handleBrowseResource(configuText);
			}
		});
		
		label = new Label(container, SWT.NULL);
		label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label.setText("FMP Mapping File:");

		mappingText = new Text(container, SWT.BORDER | SWT.SINGLE);
		gdX = new GridData(GridData.FILL_HORIZONTAL);
		mappingText.setLayoutData(gdX);
		mappingText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		});

		Button buttonZ = new Button(container, SWT.PUSH);
		buttonZ.setText("Browse...");
		buttonZ.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent b) {
				handleBrowseResource(mappingText);
			}
		});
		
		label = new Label(container, SWT.NULL);
		label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label.setText("Graph Name:");

		fileText = new Text(container, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		fileText.setLayoutData(gd);
		fileText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		});
		
		initialize();
		dialogChanged();
		setControl(container);
	}
	
	private void initialize() {
		if (selection != null && selection.isEmpty() == false && selection instanceof IStructuredSelection) {
			IStructuredSelection ssel = (IStructuredSelection) selection;
			if (ssel.size() > 1)
				return;
			Object obj = ssel.getFirstElement();
			if (obj instanceof IResource) {
				IContainer container;
				if (obj instanceof IContainer)
					container = (IContainer) obj;
				else
					container = ((IResource) obj).getParent();
				containerText.setText(container.getFullPath().toString());
			}
		}
		containerText.setText("/SkillGraph Generator/result"); //prefilled for faster testing
		modelText.setText("/SkillGraph Generator/model.xml");
		configuText.setText("/SkillGraph Generator/configs/default.xml");
		mappingText.setText("/SkillGraph Generator/mapping.fmp");
		fileText.setText("myGraph.sked");
	}

	/**
	 * Uses the standard container selection dialog to choose the new value for the
	 * container field.
	 */
	
	private void handleBrowseContainer() {
		ContainerSelectionDialog dialog = new ContainerSelectionDialog(getShell(),
				ResourcesPlugin.getWorkspace().getRoot(), false, "Select new file container");
		if (dialog.open() == ContainerSelectionDialog.OK) {
			Object[] result = dialog.getResult();
			if (result.length == 1) {
				containerText.setText(((Path) result[0]).toString());
			}
		}
	}

	private void handleBrowseResource(Text text) {
		ResourceSelectionDialog dialog = new ResourceSelectionDialog(getShell(),
				ResourcesPlugin.getWorkspace().getRoot(), "Select resource file");
		if (dialog.open() == ResourceSelectionDialog.OK) {
			Object[] result = dialog.getResult();
			if (result.length != 0) {
				String newText = result[0].toString();
				String clone = "";
				for (int i = 1; i < newText.length(); i++) {
					clone += newText.charAt(i);
				}
				text.setText(clone);
			}
		}
	}

	/**
	 * Ensures that both text fields are set.
	 */

	private void dialogChanged() {
		IResource container = ResourcesPlugin.getWorkspace().getRoot().findMember(new Path(getContainerName()));
		IResource model = ResourcesPlugin.getWorkspace().getRoot().findMember(new Path(getModelName()));
		IResource confi = ResourcesPlugin.getWorkspace().getRoot().findMember(new Path(getConfigName()));
		IResource map = ResourcesPlugin.getWorkspace().getRoot().findMember(new Path(getMapName()));


		if (getContainerName().length() == 0) {
			updateStatus("File container must be specified");
			return;
		}
		if (getModelName().length() == 0) {
			updateStatus("Model file must be specified");
			return;
		}
		if (getConfigName().length() == 0) {
			updateStatus("Configuration file must be specified");
			return;
		}
		if (getMapName().length() == 0) {
			updateStatus("FeatureMaP file must be specified");
			return;
		}
		if (container == null || (container.getType() & (IResource.PROJECT | IResource.FOLDER)) == 0) {
			updateStatus("File container must exist");
			return;
		}
		if (model == null || (model.getType() & (IResource.FILE)) == 0) {
			updateStatus("Model file must exist");
			return;
		}
		if (confi == null || (confi.getType() & (IResource.FILE)) == 0) {
			updateStatus("Configuration file must exist");
			return;
		}
		if (map == null || (map.getType() & (IResource.FILE)) == 0) {
			updateStatus("Mapping file must exist");
			return;
		}
		if (!container.isAccessible()) {
			updateStatus("Project must be writable");
			return;
		}
		int dotLoc = getModelName().lastIndexOf('.');
		if (dotLoc != -1) {
			String ext = getModelName().substring(dotLoc + 1);
			if (ext.equalsIgnoreCase("xml") == false) {
				updateStatus("File extension must be \"xml\"");
				return;
			}
		}
		dotLoc = getConfigName().lastIndexOf('.');
		if (dotLoc != -1) {
			String ext = getConfigName().substring(dotLoc + 1);
			if (ext.equalsIgnoreCase("xml") == false) {
				updateStatus("File extension must be \"xml\"");
				return;
			}
		}
		dotLoc = getMapName().lastIndexOf('.');
		if (dotLoc != -1) {
			String ext = getMapName().substring(dotLoc + 1);
			if (ext.equalsIgnoreCase("fmp") == false) {
				updateStatus("File extension must be \"fmp\"");
				return;
			}
		}
		dotLoc = getFileName().lastIndexOf('.');
		if (dotLoc != -1) {
			String ext = getFileName().substring(dotLoc + 1);
			if (ext.equalsIgnoreCase("sked") == false) {
				updateStatus("File extension must be \"sked\"");
				return;
			}
		}
		if (getFileName().contains("/") || getFileName().contains("\\")) {
			updateStatus("No folders in file name allowed. Use Container instead.");
			return;
		}
		updateStatus(null);
	}

	private void updateStatus(String message) {
		setErrorMessage(message);
		setPageComplete(message == null);
	}
	
	public String getContainerName() {
		return containerText.getText();
	}

	public String getFileName() {
		return fileText.getText();
	}

	public String getModelName() {
		return modelText.getText();
	}
	
	public String getConfigName() {
		return configuText.getText();
	}
	
	public String getMapName() {
		return mappingText.getText();
	}
	
}