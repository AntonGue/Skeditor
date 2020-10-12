package de.tubs.skeditor.spl.utils;

import de.ovgu.featureide.fm.core.configuration.Configuration;
import de.ovgu.featureide.fm.core.configuration.SelectableFeature;
import de.ovgu.featureide.fm.core.configuration.Selection;
import de.tubs.skeditor.spl.FeatureMapStandaloneSetup;
import de.tubs.skeditor.spl.featureMap.*;

import java.io.File;
import java.io.FileNotFoundException;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

import com.google.inject.Injector;

public class FmpReader {
	
	final Injector injector = new FeatureMapStandaloneSetup().createInjectorAndDoEMFRegistration();
	
	/**
	 * This method reads the FeatureMaP file from the URI. Based on what features are selected
	 * in the Configuration, it extracts the Mappings and returns an EList of Strings,
	 * which hold the path to the skill graph files.
	 * @param uri, the uri of the .fmp file to use
	 * @param config, the configuration that is used to tell which features are active
	 * @return An EList<String> with the paths to all .sked files needed
	 * @throws FileNotFoundException
	 */
	
	public static EList<String> extractGraphsViaConfiguration(String uri,  Configuration config) throws FileNotFoundException {
		return extractGraphsViaConfiguration(URI.createFileURI(uri),config);
	}
	
	public static EList<String> extractGraphsViaConfiguration(URI uri,  Configuration config) throws FileNotFoundException {
		Model model = getFeatureMap(uri);
		String uriStr = uri.toFileString().replace('\\', '/');
		String prev = "";
		int slaLoc = uriStr.lastIndexOf('/');
		if (slaLoc != -1) {
			prev = uriStr.substring(0, slaLoc+1);
		}
		String modelPath = prev.concat(model.getGpath().getGraphspath());
			
		EList<Alloc> filtAllocs = filterSelected(model.getMapping().getAllocs(), config);

		String graphPath = pathFormat(modelPath);
		
		EList<String> allGraphs = addFolder(graphPath, extractGraphs(filtAllocs));
		allGraphs.add(0, addFolder(graphPath, model.getBasic().getBasic()));
		return allGraphs;
	}
	
	/**
	 * Returns the first EObject from the .fmp file, already cast into it's first content type.
	 * @param uri, the way to the .fmp file
	 * @return A Model of the featureMap Objects
	 */
	protected static Model getFeatureMap(URI uri) {
		return (Model) new ResourceSetImpl().getResource(uri, true).getContents().get(0);
	}	

	protected static EList<Alloc> filterSelected(EList<Alloc> allocs, Configuration config) {
		EList<Alloc> filtered = new BasicEList<Alloc>();
		EList<String> filter = getConcreteSelection(config);
		if (filtered.size() < filter.size()) {//smaller list first is faster
			for (Alloc iteratorA : allocs) {
				for (String iteratorS : filter) {
					if (iteratorA.getFeature().equals(iteratorS)) {
						filtered.add(iteratorA);
						break;//can speed up the search, because features are unique
					}
				}
			}
		}
		else {
			for (String iteratorS : filter) {
				for (Alloc iteratorA : allocs) {
					if (iteratorA.getFeature().equals(iteratorS)) {
						filtered.add(iteratorA);
						break;
					}
				}
			}
		}
		return filtered;
	}
	
	protected static EList<String> getConcreteSelection(Configuration configuration) {
		EList<String> concreteF = new BasicEList<String>();
		for (String name : configuration.getSelectedFeatureNames()) {
			SelectableFeature tester = configuration.getSelectableFeature(name);
			if ((tester.getSelection() == Selection.SELECTED) &&
					tester.getFeature().getStructure().isConcrete()) {
				concreteF.add(tester.getName());
			}
		}
		return concreteF;
	}
	
	protected static String pathFormat(String pathName) {
		String result = "";
		File path = new File(pathName);
		if (path.isDirectory()) {
			result = pathName;
		}
		else {
			System.out.println("Directory "+ pathName + " not found.");
		}
		return result;
	}
	
	protected static String addFolder(String folderPath, String graphName) throws FileNotFoundException {
		String fullPath = folderPath.concat(skedPathFormat(folderPath, graphName));
		return fullPath;
	}
	
	protected static EList<String> addFolder(String folderPath, EList<String> graphNames) throws FileNotFoundException {
		EList<String> fullPath = new BasicEList<String>();
		for (String iteratorS : graphNames) {
			fullPath.add(folderPath + skedPathFormat(folderPath, iteratorS));
		}
		return fullPath;
	}
	
	protected static String skedPathFormat(String gPath, String skedFile) throws FileNotFoundException {
		String result = "";
		if (skedFile.charAt(0) != '/') {
			skedFile = '/' + skedFile;
		}
		File path = new File(gPath+skedFile);
		if (path.isFile()) {
			result = skedFile;
		}
		else {
			throw new FileNotFoundException("File " + gPath + skedFile + " not found.");
		}
		return result;
	}
	
	protected static EList<String> extractGraphs(EList<Alloc> allocs) {
		EList<String> extracted = new BasicEList<String>();
		for (Alloc iteratorA : allocs) {
			extracted.addAll(iteratorA.getFeat_graph());
		}
		return extracted;
	}
}
