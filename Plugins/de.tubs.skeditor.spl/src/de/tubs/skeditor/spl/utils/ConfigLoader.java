package de.tubs.skeditor.spl.utils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.ovgu.featureide.fm.core.analysis.cnf.CNF;
import de.ovgu.featureide.fm.core.analysis.cnf.LiteralSet;
import de.ovgu.featureide.fm.core.analysis.cnf.formula.FeatureModelFormula;
import de.ovgu.featureide.fm.core.base.IConstraint;
import de.ovgu.featureide.fm.core.base.IFeature;
import de.ovgu.featureide.fm.core.base.IFeatureModel;
import de.ovgu.featureide.fm.core.base.IFeatureModelStructure;
import de.ovgu.featureide.fm.core.base.IFeatureStructure;
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
	
	public ConfigLoader() {
	}
	
	Path modelpath = Paths.get("model.xml");
	Path configPath = Paths.get("configs/default.xml");
	
	IFeatureModel fmodel = getFeatureModel(modelpath);
	
	IFeatureModelStructure fmstruct = fmodel.getStructure(); //Struktur mit "tiefe".
	List<IConstraint> fconstraint = fmodel.getConstraints();
	
	final boolean hashidden = fmstruct.hasHidden();
	
	IFeatureStructure fstruct = fmstruct.getRoot();
	
	List<String> iter =  fmodel.getFeatureOrderList(); //Feature Liste ohne abstrakte Features
	
	public static IFeatureModel getFeatureModel(Path featureModelPath) {
		XmlFeatureModelFormat format = new XmlFeatureModelFormat();
		FMFormatManager.getInstance().addExtension(format);

		FMFactoryManager.getInstance().addExtension(DefaultFeatureModelFactory.getInstance());
		FMFactoryManager.getInstance().setWorkspaceLoader(null);
		FMFactoryManager.getInstance().addFactoryWorkspace(featureModelPath);

		FileHandler<IFeatureModel> fmHandler = FeatureModelManager.getFileHandler(featureModelPath);
		IFeatureModel model = fmHandler.getObject();

		if (model == null) {
			throw new NullPointerException("Feature Model is null");
		}
		
		return model;
	}
	
	/**
	 * Loads the Configuration, based on the configuration file and the featureModel file
	 * @param configuationPath The path of the configuration file
	 * @param modelPath The path of the featureModel file
	 * @return The configuration for the given IFeatureModel
	 */
	public static Configuration getConfiguration(Path configurationPath, Path modelPath) {//, IFeatureModelManager featureModelManager) {
		XMLConfFormat format = new XMLConfFormat();
		ConfigFormatManager.getInstance().addExtension(format);
		
		ConfigurationFactoryManager.getInstance().addExtension(DefaultConfigurationFactory.getInstance());
		ConfigurationFactoryManager.getInstance().setWorkspaceLoader(null);
		ConfigurationFactoryManager.getInstance().addFactoryWorkspace(configurationPath);

		final FileHandler<Configuration> confHandler = ConfigurationManager.getFileHandler(configurationPath);
		final Configuration confi = confHandler.getObject();

		if (confi == null) {
			throw new NullPointerException("Configuration is null");
		}
		return new Configuration(confi, new FeatureModelFormula(getFeatureModel(modelPath)));
	} 
	
	/**
	 * Loads the Configuration, based on the configuration file and the IFeatureModel
	 * @param configuationPath The path of the configuration file
	 * @param model The IFeatureModel that the Configuration is based on
	 * @return The configuration for the given IFeatureModel
	 */
	public static Configuration getConfiguration(Path configurationPath, IFeatureModel model) {//, IFeatureModelManager featureModelManager) {
		XMLConfFormat format = new XMLConfFormat();
		ConfigFormatManager.getInstance().addExtension(format);
		
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
	
	public static int getMaxDepth(IFeatureStructure ifs) {
		final char[] struc = ifs.toString().toCharArray();
		int maxDepth = 0;
		int depth = 0;
		
		for (char ch : struc) {
			if (ch == '[') {
				depth++;
			}
			else if (ch == ']') {
				depth--;
			}
			if (depth > maxDepth) {
				maxDepth = depth;
			}
		}
		return maxDepth;
	}
	
	public static Configuration generateConfiguration(IFeatureModel fm, CNF cnf, LiteralSet solution) {
		Configuration configuration = new Configuration(new FeatureModelFormula(fm));
		for (final int selection : solution.getLiterals()) {
			final String name = cnf.getVariables().getName(selection);
			configuration.setManual(name, selection > 0 ? Selection.SELECTED : Selection.UNSELECTED);
		}
		return configuration;
	}
	
	public static Set<String> ConfigurationToString(Configuration c) {
		Set<String> set = new HashSet<>();
		for (IFeature sf : c.getSelectedFeatures()) {
			set.add(sf.getName());
		}
		return set;
	}
}
