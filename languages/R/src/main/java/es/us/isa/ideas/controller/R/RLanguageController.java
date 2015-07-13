package es.us.isa.ideas.controller.R;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import es.us.isa.ideas.common.AppResponse;
import es.us.isa.ideas.common.AppResponse.Status;
import es.us.isa.ideas.module.controller.BaseLanguageController;

@Controller
@RequestMapping("/language")
public class RLanguageController extends BaseLanguageController {
	
	


	public AppResponse checkLanguage(String format, String content, String fileUri) {
		/*en R no vamos a hacer checkeo */
		AppResponse response=new AppResponse();
		response.setMessage("No error were found in your script. Please, try to execute it to get the R output or errors found.");
		response.setStatus(Status.OK);
		return response;
	}

	
	public AppResponse convertFormat(String currentFormat, String desiredFormat, String fileUri, String content) {
		AppResponse response=new AppResponse();
		response.setMessage("Currently we only suppport the S language.");
		response.setStatus(Status.OK);
		response.setData(content);
		return response;
	}

	
	public AppResponse executeOperation(String id, String content, String fileUri) {
		// TODO Auto-generated method stub
		/*Esta es la que tiene que tiene que encargarse de la ejecucion de los script*/
		/*id de operacion; contenido; filerUri otro fichero por si hicieran falta datos adicionales ejemplo: el data set.*/
		return null;
	}

}
