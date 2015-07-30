package es.us.isa.ideas.controller.R;

import static com.sun.corba.se.spi.presentation.rmi.StubAdapter.request;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import es.us.isa.ideas.common.AppResponse;
import es.us.isa.ideas.common.AppResponse.Status;
import es.us.isa.ideas.module.controller.BaseLanguageController;
import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/language")
public class RLanguageController extends BaseLanguageController {
	

	@RequestMapping(value = "/format/{format}/checkLanguage", method = RequestMethod.POST)
	@ResponseBody
	public AppResponse checkLanguage(String format, String content, String fileUri) {
		
		AppResponse response=new AppResponse();
		response.setMessage("No error were found in your script. Please, try to execute it to get the R output or errors found.");
		response.setStatus(Status.OK);
		return response;
	}

	@RequestMapping(value = "/convert", method = RequestMethod.POST)
	@ResponseBody
	public AppResponse convertFormat(String currentFormat, String desiredFormat, String fileUri, String content) {
		AppResponse response=new AppResponse();
		response.setMessage("Currently we only suppport the S language.");
		response.setStatus(Status.OK);
		response.setData(content);
		return response;
	}

	@RequestMapping(value = "/operation/{id}/execute", method = RequestMethod.POST)
	@ResponseBody	
	public AppResponse executeOperation(String id, String content, String fileUri,HttpServletRequest request) {
		RDelegate RD=(RDelegate)request.getSession().getAttribute("RDelegate");
                if(RD==null) {
                	request.setAttribute("IDEAS-R-OutputFolder", "output");
                    RD = new RDelegate(null, null, null);
                    request.getSession().setAttribute("RDelegate", RD);
                }
		
		 
		/*Esta es la que tiene que tiene que encargarse de la ejecucion de los script*/
		/*id de operacion; contenido; fileUri otro fichero por si hicieran falta datos adicionales ejemplo: el data set.*/
		AppResponse response;
		
		if(id.equals(RDelegate.EXECUTE_SCRIPT)){
			response=RD.executeScript(content, fileUri);
		}else if(id.equals(RDelegate.EXECUTE_SCRIPT2)){
			response=RD.executeScript2(content, fileUri);
		}else if(id.equals(RDelegate.DELETE_TEMP)){
			response=RD.deleteTemp();
			 
		}else if(id.equals(RDelegate.LINT)){
			response=RD.lintScript(content, fileUri);
		}else if(id.equals(RDelegate.END_SESSION)){
			response=RD.endSession();
            request.getSession().setAttribute("RDelegate", null);
		}else{
			response = new AppResponse();
			response.setMessage("No operation with id " + id);
			response.setStatus(Status.ERROR);
		}
		return response;
	}

}
