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
import de.ovgu.featureide.fm.core.base.impl.DefaultFeatureModelFactory;
import de.ovgu.featureide.fm.core.base.impl.FMFactoryManager;
import de.ovgu.featureide.fm.core.base.impl.FMFormatManager;
import de.ovgu.featureide.fm.core.configuration.Configuration;
import de.ovgu.featureide.fm.core.configuration.Selection;
import de.ovgu.featureide.fm.core.io.manager.FeatureModelManager;
import de.ovgu.featureide.fm.core.io.manager.FileHandler;
import de.ovgu.featureide.fm.core.io.xml.XmlFeatureModelFormat;

public class ModelLoader {
final String LINE_NUMBER_KEY_NAME = "lineNumber"; //$NON-NLS-1$
	
	public ModelLoader() {
	}
	
	
		Path modelpath = Paths.get("model.xml");		 
		IFeatureModel fmodel = getFeatureModel(modelpath);
		
		IFeatureModelStructure fmstruct = fmodel.getStructure(); //Struktur mit "tiefe".
		List<IConstraint> fconstraint = fmodel.getConstraints();
		
		final boolean hashidden = fmstruct.hasHidden();

		IFeatureStructure fstruct = fmstruct.getRoot();

		List<String> iter =  fmodel.getFeatureOrderList(); //Feature Liste ohne abstrakte Features
		
				
		//Collection<IFeature> alle Features mit: fmstruct.getFeaturesPreorder();
//		for (Iterator<String> m = iter.iterator(); m.hasNext();) {
//			String next = m.next();
//			//System.out.println(next);
//			if (hashidden) {
//				//handle hidden Features() e.g. ignore them;
//			}
//		}
	
	public static IFeatureModel getFeatureModel(Path fmPath) {
		XmlFeatureModelFormat format = new XmlFeatureModelFormat();
		FMFormatManager.getInstance().addExtension(format);

		FMFactoryManager.getInstance().addExtension(DefaultFeatureModelFactory.getInstance());
		FMFactoryManager.getInstance().setWorkspaceLoader(null);
		FMFactoryManager.getInstance().addFactoryWorkspace(fmPath);

		FileHandler<IFeatureModel> fmHandler = FeatureModelManager.getFileHandler(fmPath);
		IFeatureModel model = fmHandler.getObject();

		if (model == null) {
			throw new NullPointerException("Feature Model is null");
		}
		
		return model;
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
