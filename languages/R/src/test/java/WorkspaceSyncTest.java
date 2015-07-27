import java.nio.file.Path;

import org.math.R.RserverConf;
import org.math.R.Rsession;

import es.us.isa.ideas.controller.R.WorkspaceSync;

public class WorkspaceSyncTest {
	
	public static void main(String[] args) {
		RserverConf c= RserverConf.parse("R://localhost:6311");
		Rsession s= Rsession.newInstanceTry(System.out, c);
		String fileUri="...\\Scripts R\\Gauss_RScripts\\Script_Prueba_1.R";
		
		WorkspaceSync ws= new WorkspaceSync(s);
		
		try {
			String tem=testSetTemp(ws);
			System.out.println(s.asString("getwd()"));
			//testEndExecution(ws,fileUri,tem);
			//testDeleteTemp(tem);
			testMoveFiles(tem,fileUri);
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		s.end();
	}

	private static void testMoveFiles(String tempD,String fileUri) {
		WorkspaceSync.moveFiles(tempD, fileUri, true);
		
	}

	private static void testDeleteTemp(String tempD) {
		WorkspaceSync.deleteTempDirectory(tempD);

		
	}

	private static void testEndExecution(WorkspaceSync ws, String fileUri, String tem) {
		WorkspaceSync.endExecution(tem, fileUri);
		
	}

	private static String testSetTemp(WorkspaceSync ws) {
		
		String temp = ws.setTempDirectory();
		
		System.out.println(temp);
		return temp;
		
	}
	
	
}
