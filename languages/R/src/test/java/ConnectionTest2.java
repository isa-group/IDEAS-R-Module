
import org.math.R.RserverConf;
import org.math.R.Rsession;


public class ConnectionTest2 {
	

	public static void main(String[] args) {
		String command1= "x<-1:100";
		String command2="print(x)";
		String command= "2+2";
		String r;
		
		try{
			 
		RserverConf c= RserverConf.parse("R://localhost:6311");
		Rsession s= Rsession.newInstanceTry(System.out, c);
 		s.eval(command1);
 		s.eval(command2);
 		 r= s.asString(command);
 		 String r2= s.asString(command1);
 		 String r3= s.asHTML(command);
 		 String r4= s.asHTML(command2);
 		 System.out.println("=============================================");
 		 System.out.println(r);
 		 System.out.println("");
 		 System.out.println(r2);
 		System.out.println("");
 		 System.out.println(r3);
 		System.out.println("");
 		 System.out.println(r4);
 		System.out.println("= = = = = = =");
 		System.out.println(s.eval(command2).asString());
		s.end();
		}catch(Exception e){
			
			System.out.println("se elevo excepcion");
			e.printStackTrace();
		}
		
		
	}

}
