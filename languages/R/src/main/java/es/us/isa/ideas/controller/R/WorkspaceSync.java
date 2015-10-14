package es.us.isa.ideas.controller.R;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.io.FileUtils;
import org.math.R.Rsession;


public class WorkspaceSync {

	public Rsession s;
	
	public WorkspaceSync(Rsession s) {
		this.s=s;
		
	}

	public String setTempDirectory() {
		String res= null;
		Path temp=null;
		Path p= null;
		try{
			temp= Files.createTempDirectory("RTemp");
			p=temp.resolve("IDEAS-R-OutputFolder");
			Files.createDirectories(p);//meterle al directorio temporal la carpeta de resultado
			res=temp.toString();
			
		}catch(Exception e){
			e.printStackTrace();
		}
		if(!setRWorkingDirectory(res)){//poner el wd en temp
			res=null;
		}
		return res;
	}

	

	public  Boolean setRWorkingDirectory(String tempD) {
		Boolean res=false;
		try {
			tempD=tempD.replace('\\', '/');
			s.eval("setwd(\""+tempD+"\")");
			res=true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return res;
	}
	
	public static String getProjectPath(String fileUri) {
		String path="";
		String copy= new String(fileUri).replace('\\', '/');
		String[] parts= copy.split("/");
		if(parts.length>=2){
		 path=parts[0]+"/"+ parts[1];
		}else{
			path=null;
		}
		
		return path;
	}

	/*public static void moveFiles(String tempD,String fileUri){
		File tem= new File(tempD);
		File project= new File(getProjectPath(fileUri));
		
			try {
				FileUtils.copyDirectory(tem, project); //this method does not overwrite
			} catch (IOException e) {
				
				e.printStackTrace();
			}
		
	}*/
	
	/*private static Boolean tempEqualsProject(String tempD,String fileUri){
		File tem= new File(tempD);
		String pr= getProjectPath(fileUri);
		pr=pr.replace('/', '\\'); //TODO: is this really necessary?
		File old= new File(pr);
		String[] tempContent=tem.list();
		String[] oldContent= old.list();
		
		return oldContent.equals(tempContent)?true:false;
	}*/

	/*public static void endExecution(String tempD, String fileUri) {
		if(!tempEqualsProject(tempD, fileUri)){//TODO: probably this check is not needed
			moveFiles(tempD, fileUri); 
		}
		try {
			FileUtils.deleteDirectory(new File(tempD));
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
	}*/
	public static boolean deleteTemp(String temp){
		return	FileUtils.deleteQuietly(new File(temp));
		
	}
}
