package es.us.isa.ideas.controller.R;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.math.R.Rsession;

import com.google.common.collect.Lists;

import es.us.isa.ideas.utils.repolab.AuthenticationManagerDelegate;
import es.us.isa.ideas.utils.repolab.RepoLab;
import es.us.isa.ideas.utils.repolab.impl.fs.FSFacade;

public class WorkspaceSync {

	public static Rsession s;
	
	public WorkspaceSync(Rsession s) {
		WorkspaceSync.s=s;
		
	}

	public String setTempDirectory() {
		String tem= null;
		try{
			tem= Files.createTempDirectory("temp").toString();
		}catch(Exception e){
			System.out.println("No se pudo crear el directorio temporal.");
			e.printStackTrace();
		}
		if(!setRWorkingDirectory(tem)){
			tem=null;
		}
		return tem;
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

	public static void moveFiles(String tempD,String fileUri,Boolean overwrite){
		File tem= new File(tempD);
		File project= new File(getProjectPath(fileUri));
		if(overwrite){
			try {
				FileUtils.copyDirectory(tem, project); //TODO: I thinks this is not actually overwriting, need to test...
			} catch (IOException e) {
				
				e.printStackTrace();
			}
		}else{
		
		//TODO: en principio se va a quedar asi.
		
		}
	}
	
	private static Boolean tempEqualsProject(String tempD,String fileUri){
		File tem= new File(tempD);
		String pr= getProjectPath(fileUri);
		pr=pr.replace('/', '\\');
		File old= new File(pr);
		String[] tempContent=tem.list();
		String[] oldContent= old.list();
		//TODO: to test this method.
		return oldContent.equals(tempContent)?true:false;
	}

	 public static Boolean deleteTempDirectory(String tempD) {
		Boolean res=true;
		File tem= new File(tempD);
		try {
			deleteDirectory(tem);
			
		} catch (Exception e) {
			e.printStackTrace();
			res=false;
		}
		return res;
	}

	 private static void deleteDirectory(File directory){
		 
		 if(directory.isFile()||directory.listFiles().length==0){
			directory.delete();
		 }else{
			for(File f :directory.listFiles()){
			 deleteDirectory(f);
			}
			directory.delete();
		 }
		 
	 }
	public static void endExecution(String tempD, String fileUri) {
		if(!tempEqualsProject(tempD, fileUri)){
			moveFiles(tempD, fileUri,true); 
		}
		deleteTempDirectory(tempD);
		
	}
}
