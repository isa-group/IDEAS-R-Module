package es.us.isa.ideas.controller.R;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;

import org.apache.commons.io.FileUtils;
import org.math.R.Rsession;

import es.us.isa.ideas.utils.repolab.AuthenticationManagerDelegate;
import es.us.isa.ideas.utils.repolab.RepoLab;
import es.us.isa.ideas.utils.repolab.impl.fs.FSFacade;

public class WorkspaceSync {

	public static Rsession s;
	
	public WorkspaceSync(Rsession s) {
		WorkspaceSync.s=s;
		
	}

	public Path setTempDirectory(String fileUri) {
		Path tem= null;

		try{
			tem= Files.createTempDirectory("temp");
			//TODO:el enlace simbÃ³lico lo tiene que hacer IDEAS - app
		}catch(Exception e){
			System.out.println("No se  crear el directorio temporal.");
			e.printStackTrace();
		}
		
		try {
			String project= getProjectPath(fileUri);
			File temp= new File(tem.toString());
			File pro= new File(project);
			FileUtils.copyDirectoryToDirectory(pro, temp);
			
			
		} catch (Exception e) {
			System.out.println("No se pudo copiar el proyecto al directorio temporal.");
		}
		
		return tem;
	}

	

	public  Boolean setRWorkingDirectory(Path tempD) {
		Boolean res=false;
		try {
			s.eval("setwd("+tempD.toString()+")");
			res=true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return res;
	}
	
	public static String getProjectPath(String fileUri) {
		String path="";
		final String owner="jlljunior90";
		RepoLab.init(new AuthenticationManagerDelegate() {

			@Override
			public boolean operationAllowed(String authenticatedUser,
					String Owner, String workspace, String project,
					String fileOrDirectoryUri, AuthOpType operationType) {
				return true;
			}

			@Override
			public String getAuthenticatedUserId() {
				// return LoginService.getPrincipal().getUsername();
				
				return owner;
			}
		});
		
		
		
		String copy= new String(fileUri);
		String[] parts= copy.split("/");
		String pr= parts[1];
		/*try {
		FSFacade.getProjectTree(wsName, owner, project);
		path=FSFacade.getSelectedWorkspace(owner)+"/"+pr;
		//path hasta el workspace mÃ¡s pr. 
		
		} catch (IOException e) {
			System.out.println("no se pudo obtener la ruta del workspace");
			e.printStackTrace();
		}*/
		return path;
	}

	public Boolean WDisEmpty() {
		Boolean result=false;
		//TODO: check if the WD is empty at the end of the script execution.
		return result;
	}

	public static Boolean deleteTempDirectory(Path tempD) {
		Boolean res=false;
		try {
			//TODO: before doing this tempD must be empty.
			Files.delete(tempD);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}
}
