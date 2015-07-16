package es.us.isa.ideas.controller.R;


import java.util.List;

import org.math.R.RserverConf;
import org.math.R.Rsession;
import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPMismatchException;

import com.google.common.collect.Lists;

import es.us.isa.ideas.common.AppResponse;
import es.us.isa.ideas.common.AppResponse.Status;

public class RDelegate {
	public final static String EXECUTE_SCRIPT = "executeScript";
	String host="R://localhost:6311";

	public RDelegate() {
		super();
		
	}	
	
	public AppResponse executeScript(String content, String fileUri){
		AppResponse response= constructBaseResponse(fileUri);
		String[] commands;
		try {
			//Open connection 
			RserverConf c= RserverConf.parse(host);
			Rsession s= Rsession.newInstanceTry(System.out, c);
			
			commands= content.split("\n"); 
			List<String> com= Lists.newArrayList(commands);
			
			List<REXP> exp= Lists.newArrayList();
			for(String command:com){
				REXP e=s.eval(command);
				if(!command.contains("<-"))
				exp.add(e);
			}
			String htmlMessage= asHTML(exp);
		response.setHtmlMessage(htmlMessage);
			
			s.end();
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
			response.setMessage(e.getMessage());
			response.setStatus(Status.ERROR);
		}
		
		return response;
	}
	
	private String asHTML(List<REXP> exp) {
		String res="<pre>";		
		for(REXP tem:exp){
			try {
//				res+=format(tem.asStrings());
				List<String>l=Lists.newArrayList(tem.asStrings());
				
				res+=l.toString()+"<br/>";
			} catch (REXPMismatchException e) {
				
				e.printStackTrace();
			}
			
		}
		res+="</pre>";
		return res;
	}

	private String format(String[] source) {
		
		return null;//source.replace("\n", "<br/>");
	}

	private AppResponse constructBaseResponse(String fileUri) {
		AppResponse appResponse = new AppResponse();
		appResponse.setFileUri(fileUri);
		appResponse.setStatus(Status.OK);
		return appResponse;
	}

}
