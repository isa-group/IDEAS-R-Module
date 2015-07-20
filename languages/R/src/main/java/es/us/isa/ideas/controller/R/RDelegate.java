package es.us.isa.ideas.controller.R;


import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.UUID;

import org.math.R.RserverConf;
import org.math.R.Rsession;
import org.rosuda.REngine.REXPGenericVector;

import es.us.isa.ideas.common.AppResponse;
import es.us.isa.ideas.common.AppResponse.Status;


public class RDelegate {
	public final static String EXECUTE_SCRIPT = "executeScript";
	public final static String LINT = "lint";
	String host="R://localhost:6311";

	public RDelegate() {
		super();
		
	}	
	
	public AppResponse executeScript(String content, String fileUri){
		AppResponse response= constructBaseResponse(fileUri);
		
		try {
			//Open connection 
			RserverConf c= RserverConf.parse(host);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			PrintStream ps = new PrintStream(baos);
			Rsession s= Rsession.newInstanceTry(ps, c);
			s.eval(content);
			
			String f = baos.toString("UTF-8");
		
			String f2=f.replace("[eval] "+content, "");
			String f3= f2.replaceFirst("(org).{1,}", "");
			//String m= "(!!) Rserve R://localhost:6311 is not accessible.\n ! null\r\nTrying to spawn R://localhost:6311\r\nEnvironment variables:\n  R_HOME=C:\\Program Files\\R\\R-3.2.1\\\r\nchecking Rserve is available... \r\n  ok\r\nstarting R daemon... R://localhost:6311\r\n  ok\r\nLocal Rserve started. (Version 103)\r\n";
			f3=f3.replace("(!!) Rserve R://localhost:6311 is not accessible.\n ! null\r\nTrying to spawn R://localhost:6311\r\nEnvironment variables:\n  R_HOME=C:\\Program Files\\R\\R-3.2.1\\\r\nchecking Rserve is available... \r\n  ok\r\nstarting R daemon... R://localhost:6311\r\n  ok\r\nLocal Rserve started. (Version 103)\r\n", "");
			String f4=f3.replaceAll(".{1,}(org.rosuda).{1,}","" );
			f4=f4.replaceAll("(org.).{1,}", "");
	//		String font4= font3.replaceFirst("(!!) Rserve R://localhost:6311 is not accesible.\n ! null\nTrying to spawn R://localhost:6311\nEnvironment variables:\n R_HOME=C:\\Program Files\\R\\R-3.2.1\\\nchecking Rserve is available...\n ok\nstarting R Daemon... R://localhost:6311\n ok\nLocal Rserve started. (Version 103)", "");
			String htmlMessage= "<pre>"+f4+"</pre>";
			
		
		response.setHtmlMessage(htmlMessage);			
			ps.close();
			s.end();
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
			response.setMessage(e.getMessage());
			response.setStatus(Status.ERROR);
		}
		
		return response;
	}
        
        public AppResponse lintScript(String content, String fileUri){
		AppResponse response= constructBaseResponse(fileUri);
		
		try {
                    
                        File f=savecontentToTempFile(content);
			//Open connection 
			RserverConf c= RserverConf.parse(host);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			PrintStream ps = new PrintStream(baos);
			Rsession s= Rsession.newInstanceTry(ps, c);
                        if(s.isPackageInstalled("lintr","0.2.0")){
                            if(!s.isPackageLoaded("lintr"))
                               s.loadPackage("lintr");
                            String command="lintr::lint(\""+f.getAbsolutePath().replace("\\","\\\\\\\\")+"\")";
                            REXPGenericVector result=(REXPGenericVector)s.eval(command);                                                
                            response.setAnnotations(ErrorBuilder.buildErrorStructure(result.asList()));
                            if(result.length()==0){
                                response.setStatus(Status.OK);
                                response.setMessage("Everything is Ok!");
                            }else{
                                response.setStatus(Status.OK_PROBLEMS);
                                response.setMessage(String.valueOf(result.length())+" issues were found!");
                            }
                        }else{
                            response.setStatus(Status.OK_PROBLEMS);
                            response.setMessage("The required library 'lintr' is not installed in the backend R.");
                        }
                            
			ps.close();
			s.end();
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
			response.setMessage(e.getMessage());
			response.setStatus(Status.ERROR);
		}
		
		return response;
	}
	
	/*private String asHTML(List<REXP> exp) {
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
	}*/

	
	private AppResponse constructBaseResponse(String fileUri) {
		AppResponse appResponse = new AppResponse();
		appResponse.setFileUri(fileUri);
		appResponse.setStatus(Status.OK);
		return appResponse;
	}
	private File savecontentToTempFile(String content) throws IOException {        
        UUID uuid=UUID.randomUUID();
    //create a temp file
    File temp = File.createTempFile(uuid.toString(), ".tmp");

        try ( //write it
                BufferedWriter bw = new BufferedWriter(new FileWriter(temp))) {
            bw.write(content);
        }
        return temp;            
}
	/*private Boolean evalCommand(String command, Rsession s2,REXP result) {
		Boolean res=false;
		if(checkNotprintingCommand(command)){
			result=null;
			res=s2.voidEval(command);
			
		}else{
			result= s2.eval(command);
			
			res=true;
		}
		
		return res;
	}*/

	/*private Boolean checkNotprintingCommand(String command) {
		Boolean res= false;
		List<String> fc= Lists.newArrayList();
		fc.add("while");fc.add("for");fc.add("if");fc.add("repeat");fc.add("break");
		fc.add("next");fc.add("{");fc.add("}");fc.add("<-");fc.add("#");
		for(String t:fc){
			if(command.contains(t))
			  res=true;	
				break;
		}
		
		return res;
	}*/
}
