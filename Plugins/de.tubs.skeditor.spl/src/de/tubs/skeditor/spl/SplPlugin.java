package de.tubs.skeditor.spl;

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
		new Thread() {

			@Override
			public void run() {
				try {
					ClassLoader.getSystemClassLoader().loadClass("de.tubs.skeditor.spl.wizards.TestWizard");
				} catch (final ClassNotFoundException e) {
					SplPlugin.getDefault().logError(e);
				}
			};
		}.start();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static SplPlugin getDefault() {
		return plugin;
	}

	@Override
	public String getID() {
		return SplPlugin.PLUGIN_ID;
	}

}
