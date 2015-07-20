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
	public final static String END_SESSION= "endsession";
	String host="R://localhost:6311";
	public Rsession s;
	public PrintStream ps;
	public ByteArrayOutputStream baos;
	public Boolean isConnected=false;
	
	
	
	
	public RDelegate(Rsession s, PrintStream ps, ByteArrayOutputStream baos) {
		super();
	if(s!=null){
		this.s = s;
		this.ps = ps;
		this.baos = baos;
	}else{
		try{
			RserverConf c= RserverConf.parse(host);
			this.baos = new ByteArrayOutputStream();
			 this.ps = new PrintStream(this.baos);
			this.s= Rsession.newInstanceTry(this.ps, c);
			
				}catch(Exception e){
					System.out.println(e.getMessage());
					//TODO: hay que poner gestón de excepciones.
				}
		}
	}

	public AppResponse endSession(){
		AppResponse res= new AppResponse();
		try{	
			String[] vars=s.ls();
			if(vars!=null)
				this.s.rm(vars);
		this.baos.reset();
		this.s.close();
		this.s.end();
		res.setMessage("Session correctly ended.");
		res.setStatus(Status.OK);
			
		}catch(Exception e){
			res.setMessage("Session couldn't be ended.");
			res.setStatus(Status.ERROR);
			e.printStackTrace();
			
		}
		
		return res;
	}
	
	public AppResponse executeScript(String content, String fileUri){
		AppResponse response= constructBaseResponse(fileUri);
		
		try {
			 
			/*RserverConf c= RserverConf.parse(host);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			PrintStream ps = new PrintStream(baos);
			Rsession s= Rsession.newInstanceTry(ps, c);*/
			baos.reset();
			s.eval(content);
			
			String f = baos.toString("UTF-8");
		
			String f2=f.replace("[eval] "+content, "");
			f2=f2.replaceAll("(.eval).{1,}", "");
			String f3= f2.replaceFirst("(org).{1,}", "");
			//String m= "(!!) Rserve R://localhost:6311 is not accessible.\n ! null\r\nTrying to spawn R://localhost:6311\r\nEnvironment variables:\n  R_HOME=C:\\Program Files\\R\\R-3.2.1\\\r\nchecking Rserve is available... \r\n  ok\r\nstarting R daemon... R://localhost:6311\r\n  ok\r\nLocal Rserve started. (Version 103)\r\n";
			f3=f3.replace("(!!) Rserve R://localhost:6311 is not accessible.\n ! null\r\nTrying to spawn R://localhost:6311\r\nEnvironment variables:\n  R_HOME=C:\\Program Files\\R\\R-3.2.1\\\r\nchecking Rserve is available... \r\n  ok\r\nstarting R daemon... R://localhost:6311\r\n  ok\r\nLocal Rserve started. (Version 103)\r\n", "");
			String f4=f3.replaceAll(".{1,}(org.rosuda).{1,}","" );
			f4=f4.replaceAll("(org.).{1,}", "");
	
			String htmlMessage= "<pre>"+f4+"</pre>";
			
		
		response.setHtmlMessage(htmlMessage);			
			
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
			/*RserverConf c= RserverConf.parse(host);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			PrintStream ps = new PrintStream(baos);
			Rsession s= Rsession.newInstanceTry(ps, c);*/
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
                            
			/*ps.close();
			s.end();*/
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
			response.setMessage(e.getMessage());
			response.setStatus(Status.ERROR);
		}
		
		return response;
	}
	
  
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
	
}
