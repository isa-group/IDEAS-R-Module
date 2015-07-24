import java.io.IOException;

import es.us.isa.ideas.utils.repolab.AuthenticationManagerDelegate;
import es.us.isa.ideas.utils.repolab.RepoLab;
import es.us.isa.ideas.utils.repolab.impl.fs.FSFacade;

public class PathTest {

	public static void main(String[] args) {
		final String owner="Juanlu_lopez_fr";
		String re="Nothing at all";
		
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
		try {
			re = FSFacade.getSelectedWorkspace(owner);
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		System.out.println(re);
	}
}
