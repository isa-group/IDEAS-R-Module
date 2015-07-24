import java.nio.file.Path;

import org.math.R.RserverConf;
import org.math.R.Rsession;

import es.us.isa.ideas.controller.R.WorkspaceSync;

public class WorkspaceSyncTest {
	
	public static void main(String[] args) {
		RserverConf c= RserverConf.parse("R://localhost:6311");
		Rsession s= Rsession.newInstanceTry(System.out, c);
		
		WorkspaceSync ws= new WorkspaceSync(s);
		//Path td=ws.setTempDirectory();
		//Path actualWD=WorkspaceSync.getProjectPath();
		//ws.setRWorkingDirectory(td);
		
		//Path changedPath= WorkspaceSync.getProjectPath();
		
		//System.out.println("temp Direc. path: "+td.toString());
	//	System.out.println("WD. path: "+actualWD.toString());
		//System.out.println("New Path: "+changedPath.toString());
		
		
	}
	
	
}
