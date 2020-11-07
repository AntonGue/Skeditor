/*
 * generated by Xtext 2.22.0
 */
package de.tubs.skeditor.spl.validation;

import java.util.Iterator;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.xtext.validation.Check;
import de.tubs.skeditor.spl.featureMap.Alloc;
import de.tubs.skeditor.spl.featureMap.Basic;
import de.tubs.skeditor.spl.featureMap.FeatureMapPackage;
import de.tubs.skeditor.spl.featureMap.Mapping;
import de.tubs.skeditor.spl.featureMap.Model;
import de.tubs.skeditor.spl.featureMap.Tuple;
import de.tubs.skeditor.spl.featureMap.GPath;

/**
 * This class contains custom validation rules. 
 *
 * See https://www.eclipse.org/Xtext/documentation/303_runtime_concepts.html#validation
 */
public class FeatureMapValidator extends AbstractFeatureMapValidator {
	
	public static final String INVALID_NAME = "invalidName";
	public static final String INVALID_Type = "invalidType";
	public static final String FEATURE_NAME = "featureName";
	public static final String CIRCLE = "Circle";
	public static final String ORDER_NAME = "orderName";
	
	@Check
	public void checkOrderContent(Tuple tuple) {
		EList<Alloc> allocs = ((Model) tuple.eContainer()).getMapping().getAllocs();
		EList<String> fgraphs = new BasicEList<String>();
		for (Alloc iterata : allocs) {
			fgraphs.addAll(iterata.getFeat_graph());
		}
		EList<String> order = tuple.getOrder();
		for (String iterate : order) {	
			if (!fgraphs.contains(iterate) &&
					!((Model) tuple.eContainer()).getBasic().getBasic().equals(iterate)) {
				warning("'" + iterate + "' not found under basic nor mapping",
						FeatureMapPackage.Literals.TUPLE__ORDER,
						ORDER_NAME);
			}
		}
	}
	
	@Check
	public void checkCircle(Tuple tuple) {
		EList<String> order = tuple.getOrder();
		for (String iterate : order) {
			if (order.indexOf(iterate) != order.lastIndexOf(iterate)) {
				error("Circle detected, please remove one '" + iterate + "'",
						FeatureMapPackage.Literals.TUPLE__ORDER,
						CIRCLE);
			}
		}
	}

	@Check
	public void checkPathStartsWithSlash(GPath path) {
		if (path.getGraphspath().charAt(0) =='/') {
			error("The path can not start with a '/'",
					FeatureMapPackage.Literals.GPATH__GRAPHSPATH,
					INVALID_NAME);
		}
	}
	
	@Check
	public void checkBasicSked(Basic basic) {
		int dotLoc = basic.getBasic().lastIndexOf('.');
		if (dotLoc != -1) {
			String ext = basic.getBasic().substring(dotLoc + 1);
			if (!ext.equalsIgnoreCase("sked")) {
				error("This has to be a .sked file",
						FeatureMapPackage.Literals.BASIC__BASIC,
						INVALID_Type);
			}
		}
	}
	
	@Check
	public void checkAllocsAreUnique(Alloc alloc) {
		Mapping mapping = (Mapping) alloc.eContainer();
		EList<Alloc> allocRun = mapping.getAllocs();
		for (int i = 0; i < allocRun.size(); i++) {
			if (!allocRun.get(i).equals(alloc)) {
				String itItem1 = allocRun.get(i).getFeature();
	        	if (itItem1.equals(alloc.getFeature())) {
	        		error("Feature names have to be unique",
	        				FeatureMapPackage.Literals.ALLOC__FEATURE,
	        				FEATURE_NAME);
	        	}
	        }
        }
	}
	
	@Check
	public void checkFeatGraphSked(Alloc alloc) {
		Iterator<String> iterString = alloc.getFeat_graph().iterator();
		while (iterString.hasNext()) {
			String checkString = iterString.next();
			int dotLoc = checkString.lastIndexOf('.');
			if (dotLoc != -1) {
				String ext = checkString.substring(dotLoc + 1);
				if (!ext.equalsIgnoreCase("sked")) {
					error("This have to be a .sked file",
							FeatureMapPackage.Literals.ALLOC__FEAT_GRAPH,
							INVALID_Type);
				}
			}
		}
	}
}
