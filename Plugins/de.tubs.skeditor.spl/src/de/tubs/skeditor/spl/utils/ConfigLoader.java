package de.tubs.skeditor.spl.utils;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

import de.ovgu.featureide.fm.core.analysis.cnf.CNF;
import de.ovgu.featureide.fm.core.analysis.cnf.LiteralSet;
import de.ovgu.featureide.fm.core.analysis.cnf.formula.FeatureModelFormula;
import de.ovgu.featureide.fm.core.base.IFeature;
import de.ovgu.featureide.fm.core.base.IFeatureModel;
import de.ovgu.featureide.fm.core.base.impl.ConfigFormatManager;
import de.ovgu.featureide.fm.core.base.impl.ConfigurationFactoryManager;
import de.ovgu.featureide.fm.core.base.impl.DefaultConfigurationFactory;
import de.ovgu.featureide.fm.core.base.impl.DefaultFeatureModelFactory;
import de.ovgu.featureide.fm.core.base.impl.FMFactoryManager;
import de.ovgu.featureide.fm.core.base.impl.FMFormatManager;
import de.ovgu.featureide.fm.core.configuration.Configuration;
import de.ovgu.featureide.fm.core.configuration.Selection;
import de.ovgu.featureide.fm.core.configuration.XMLConfFormat;
import de.ovgu.featureide.fm.core.io.manager.ConfigurationManager;
import de.ovgu.featureide.fm.core.io.manager.FeatureModelManager;
import de.ovgu.featureide.fm.core.io.manager.FileHandler;
import de.ovgu.featureide.fm.core.io.xml.XmlFeatureModelFormat;

/**
 * The configloader class is able to load the .xml files of the configuration and the model.
 *
 * @author Anton Guenther 
 */
public class ConfigLoader {
	
	final String LINE_NUMBER_KEY_NAME = "lineNumber"; //$NON-NLS-1$
	
	public Configuration config;
	
	public IFeatureModel model;
	
//	public ConfigLoader() {
//		new ConfigLoader(Paths.get("model.xml"), Paths.get("configs/default.xml"));
//	}
	
	public ConfigLoader(Path configPath, Path modelpath) {
		config = getConfiguration(configPath, modelpath);
		model = config.getFeatureModel();
	}
	
	/**
	 * Loads the IFratueModel from the path
	 * @param featureModelPath The path for the featureModel file
	 * @return The IFeatureModel in the file
	 */
	public IFeatureModel getFeatureModel(Path featureModelPath) {
		XmlFeatureModelFormat format = new XmlFeatureModelFormat();
		FMFormatManager.getInstance().addExtension(format);
		
		checkFeatureModel(featureModelPath);
		
		FMFactoryManager.getInstance().addExtension(
				DefaultFeatureModelFactory.getInstance());
		FMFactoryManager.getInstance().setWorkspaceLoader(null);
		FMFactoryManager.getInstance().addFactoryWorkspace(featureModelPath);

		FileHandler<IFeatureModel> fmHandler =
				FeatureModelManager.getFileHandler(featureModelPath);
		IFeatureModel model = fmHandler.getObject();

		if (model == null) {
			throw new NullPointerException("Feature Model is null");
		}
		
		return model;
	}
	
	/**
	 * Loads the Configuration from the Path based on the featureModel file
	 * @param configuationPath The path of the configuration file
	 * @param modelPath The path of the featureModel file
	 * @return The configuration for the given IFeatureModel
	 */
	public Configuration getConfiguration(Path configurationPath, Path modelPath) {
		XMLConfFormat format = new XMLConfFormat();
		ConfigFormatManager.getInstance().addExtension(format);
		
		checkConfiguration(configurationPath);
		
		ConfigurationFactoryManager.getInstance().addExtension(
				DefaultConfigurationFactory.getInstance());
		ConfigurationFactoryManager.getInstance().setWorkspaceLoader(null);
		ConfigurationFactoryManager.getInstance().addFactoryWorkspace(configurationPath);

		final FileHandler<Configuration> confHandler =
				ConfigurationManager.getFileHandler(configurationPath);
		final Configuration confi = confHandler.getObject();

		if (confi == null) {
			throw new NullPointerException("Configuration is null");
		}
		return new Configuration(confi,
				new FeatureModelFormula(getFeatureModel(modelPath)));
	}
	/**
	 * Loads the Configuration from the Path based on the IFeatureModel
	 * @param configuationPath The path of the configuration file
	 * @param model The IFeatureModel that the Configuration is based on
	 * @return The configuration for the given IFeatureModel
	 */
	public Configuration getConfiguration(Path configurationPath, IFeatureModel model) {
		XMLConfFormat format = new XMLConfFormat();
		ConfigFormatManager.getInstance().addExtension(format);
		
		checkConfiguration(configurationPath);
		
		ConfigurationFactoryManager.getInstance().addExtension(DefaultConfigurationFactory.getInstance());
		ConfigurationFactoryManager.getInstance().setWorkspaceLoader(null);
		ConfigurationFactoryManager.getInstance().addFactoryWorkspace(configurationPath);

		final FileHandler<Configuration> confHandler = ConfigurationManager.getFileHandler(configurationPath);
		final Configuration confi = confHandler.getObject();

		if (confi == null) {
			throw new NullPointerException("Configuration is null");
		}
		return new Configuration(confi, new FeatureModelFormula(model));
	} 
	
	protected void checkFeatureModel(Path featureModelPath) {
		if ((null == FMFormatManager.getInstance().getFormatByContent(featureModelPath)) ||
				(!FMFormatManager.getInstance().getFormatByContent(featureModelPath).getId().equals(new XmlFeatureModelFormat().getId()))) {
			throw new IllegalArgumentException("This is not a FeatureModel file");
		}
	}
	
	protected void checkConfiguration(Path configurationPath) {
		if ((null == ConfigFormatManager.getInstance().getFormatByContent(configurationPath)) ||
				(!ConfigFormatManager.getInstance().getFormatByContent(configurationPath).getId().equals(new XMLConfFormat().getId()))) {
			throw new IllegalArgumentException("This is not a Configuration file");
		}
	}
	
	public Configuration generateConfiguration(IFeatureModel fm, CNF cnf, LiteralSet solution) {
		Configuration configuration = new Configuration(new FeatureModelFormula(fm));
		for (final int selection : solution.getLiterals()) {
			final String name = cnf.getVariables().getName(selection);
			configuration.setManual(name, selection > 0 ? Selection.SELECTED : Selection.UNSELECTED);
		}
		return configuration;
	}
	
	public Set<String> ConfigurationToString(Configuration c) {
		Set<String> set = new HashSet<>();
		for (IFeature sf : c.getSelectedFeatures()) {
			set.add(sf.getName());
		}
		return set;
	}
}
