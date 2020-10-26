package de.tubs.skeditor.spl.utils;

import de.ovgu.featureide.fm.core.configuration.Configuration;
import de.ovgu.featureide.fm.core.configuration.SelectableFeature;
import de.ovgu.featureide.fm.core.configuration.Selection;
import de.tubs.skeditor.spl.FeatureMapStandaloneSetup;
import de.tubs.skeditor.spl.featureMap.Alloc;
import de.tubs.skeditor.spl.featureMap.Model;

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
	 * This method calls {@link #extractGraphsViaConfiguration(URI, Configuration)}
	 * with a String instead of an URI.
	 */
	public static EList<String> extractGraphsViaConfiguration(String uri,  Configuration config) throws FileNotFoundException {
		return extractGraphsViaConfiguration(URI.createFileURI(uri),config);
	}
	
	/**
	 * This method reads the FeatureMaP file from the URI. Based on what features are selected
	 * in the Configuration, it extracts the Mappings and returns an EList of Strings,
	 * which hold the path to the skill graph files.
	 * @param uri the uri of the .fmp file to use
	 * @param config the configuration that is used to tell which features are active
	 * @return An EList<String> with the paths to all .sked files needed
	 * @throws FileNotFoundException
	 */
	public static EList<String> extractGraphsViaConfiguration(URI uri,  Configuration config) throws FileNotFoundException {
		Model model = getFeatureMap(uri);
		String uriStr = uri.toFileString().replace('\\', '/');
		String prev = "";
		int slaLoc = uriStr.lastIndexOf('/');
		if (slaLoc != -1) {
			prev = uriStr.substring(0, slaLoc+1);
		}
			
		EList<Alloc> filtAllocs = filterSelected(model.getMapping().getAllocs(), config);

		String graphPath = pathFormat(prev.concat(model.getGpath().getGraphspath()));
		
		EList<String> allGraphs = extractGraphs(filtAllocs);
		allGraphs.add(0, model.getBasic().getBasic());
		if (model.getPartorder() != null) {
			allGraphs = sortInOrder(allGraphs, model.getPartorder().getOrder());
		}
		allGraphs = addFolder(graphPath, allGraphs);
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

	/**
	 * Removes all Allocs from the EList,
	 * where their 'Feature' is not selected in the configuration
	 * @param allocs all allocations in the FeatureMaP file 
	 * @param config the configuration file,
	 * @return allocations that are also contained in the configuration
	 */
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
	
	/**
	 * Turns the Configuration into an EList<String> format,
	 * by iterating through the selected features and saving the concrete ones.
	 */
	protected static EList<String> getConcreteSelection(Configuration configuration) {
		EList<String> concreteFeatures = new BasicEList<String>();
		for (String name : configuration.getSelectedFeatureNames()) {
			SelectableFeature tester = configuration.getSelectableFeature(name);
			if ((tester.getSelection() == Selection.SELECTED) &&
					tester.getFeature().getStructure().isConcrete()) {
				concreteFeatures.add(tester.getName());
			}
		}
		return concreteFeatures;
	}
	
	/**
	 * Makes sure the string contains a legitimate path.
	 */
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
	
	/**
	 * Iterates through allocations and saves each graph path,
	 * if they are not in the list yet.
	 */
	protected static EList<String> extractGraphs(EList<Alloc> allocs) {
		EList<String> extracted = new BasicEList<String>();
		for (Alloc iteratorA : allocs) {
			for (String iteratorS : iteratorA.getFeat_graph()) {//no duplications
				if (!extracted.contains(iteratorS)) {
					extracted.add(iteratorS);
				}
			}
		}
		return extracted;
	}
	
	protected static EList<String> sortInOrder(EList<String> stringList, EList<String> order) {
		EList<String> ordered = new BasicEList<String>();
		int savepoint = 0; //saves point, where the order is applied first
		for (String iterateList: stringList) { 
			if (order.contains(iterateList)) { //found an item from the order list
				savepoint = stringList.indexOf(iterateList);
				break;
			}
			if (!ordered.contains(iterateList)) { //copies non doubles
				ordered.add(iterateList);
			}
		}
		for (String iterateOrder : order) { //copies items in order
			if (stringList.contains(iterateOrder)) {
				ordered.add(iterateOrder);
			}
		} //continues to copy non doubles at savepoint
		for (String iterateList: stringList.subList(savepoint, stringList.size())) {
			if (!ordered.contains(iterateList)) {
				ordered.add(iterateList);
			}
		}
		return ordered;
	}
	//adds the folder names in front of each file name
	protected static EList<String> addFolder(String folderPath, EList<String> graphNames) throws FileNotFoundException {
		EList<String> fullPath = new BasicEList<String>();
		for (String iteratorS : graphNames) {
			fullPath.add(folderPath + skedPathFormat(folderPath, iteratorS));
		}
		return fullPath;
	}
	//puts a '/' in front of the file name and then checks if the file exists 
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
}
