import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import org.apache.commons.io.FileUtils;
import org.math.R.RserverConf;
import org.math.R.Rsession;

import es.us.isa.ideas.controller.R.WorkspaceSync;

public class WorkspaceSyncTest {
	
	public static void main(String[] args) {
		RserverConf c= RserverConf.parse("R://localhost:6311");
		Rsession s= Rsession.newInstanceTry(System.out, c);
		String fileUri="Scripts R\\Gauss_RScripts\\Script_Prueba_1.R";
		
		WorkspaceSync ws= new WorkspaceSync(s);
		
		try {
			String tem=testSetTemp(ws);
			System.out.println(s.asString("getwd()"));
			
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		s.end();
	}


	private static String testSetTemp(WorkspaceSync ws) {
		
		String temp = ws.setTempDirectory();
		
		System.out.println(temp);
		return temp;
		
	}
	
	
}
