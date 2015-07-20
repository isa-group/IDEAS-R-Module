/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.us.isa.ideas.controller.R;

import es.us.isa.ideas.common.AppAnnotations;
import java.util.ArrayList;
import java.util.List;
import org.rosuda.REngine.REXPGenericVector;
import org.rosuda.REngine.REXPInteger;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.REXPString;
import org.rosuda.REngine.RList;

/**
 *
 * @author José Antonio Parejo
 */
public class ErrorBuilder {

	// refactorizar
	public static AppAnnotations[] buildErrorStructure(
			RList lErrors) throws REXPMismatchException {

		System.out.println("Building errors: " + lErrors.toString());

		List<AppAnnotations> result = new ArrayList<AppAnnotations>();
                RList error=null;
                REXPString myRString=null;
                REXPInteger myRInteger=null;
		for (Object element:lErrors) {
                        error=((REXPGenericVector)element).asList();
			AppAnnotations annotations = new AppAnnotations();
                        myRInteger=(REXPInteger)error.get(1);
			annotations.setRow(String.valueOf(myRInteger.asInteger()-1));
                        myRInteger=(REXPInteger)error.get(2);
			annotations.setColumn( myRInteger.asString());
                        myRString=(REXPString)error.get(4);
			annotations.setText(myRString.asString());
                        myRString=(REXPString)error.get(3);
			if (myRString.asString().equals("style"))
				annotations.setType("info");
			else if (myRString.asString().equals("warning"))
				annotations.setType("warning");        
			else
				annotations.setType("error");
                        
			result.add(annotations);

		}

		return result.toArray(new AppAnnotations[result.size()]);
	}

}
