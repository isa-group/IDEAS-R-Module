package es.us.isa.ideas.controller.R;

import org.rosuda.REngine.Rserve.RConnection;

import es.us.isa.ideas.common.AppResponse;
import es.us.isa.ideas.common.AppResponse.Status;

public class RDelegate {
	public final static String EXECUTE_SCRIPT = "executescript";

	public RDelegate() {
		super();
		
	}	
	
	public AppResponse executeScript(String content, String fileUri){
		AppResponse response=  constructBaseResponse(fileUri);
		
		try {
			//TODO
			
			
		} catch (Exception e) {
			response.setMessage(e.getMessage());
			response.setStatus(Status.ERROR);
		}
		
		return null;
	}
	
	private AppResponse constructBaseResponse(String fileUri) {
		AppResponse appResponse = new AppResponse();
		appResponse.setFileUri(fileUri);
		appResponse.setStatus(Status.OK);
		return appResponse;
	}

}
