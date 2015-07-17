import org.math.R.RserverConf;
import org.math.R.Rsession;
import org.rosuda.REngine.REXP;

import com.google.common.collect.Lists;

public class FormatResponseTest {

	public static void main(String[] args) {
		String c1= "x<-matrix(c(1,2,3,4),nrow=2,ncol=3)";
		String c2="print(x)";
		String c3="2+2";
		
		
		try{
			 
		RserverConf c= RserverConf.parse("R://localhost:6311");
		Rsession s= Rsession.newInstanceTry(System.out, c);
 		
		REXP e1= s.eval(c1);
		REXP e2= s.eval(c2);
		REXP e3= s.eval(c3);
		String s0= e1.asString();
		String s1=e2.asString();
		String[] s2=e2.asStrings();
		String[] s3=e3.asStrings();
		System.out.println(s0);
		System.out.println(s1);
		System.out.println(Lists.newArrayList(s2).toString());
		System.out.println(Lists.newArrayList(s3).toString());
		
		
		s.end();
		}catch(Exception e){
			
			System.out.println("se elevo excepcion");
			e.printStackTrace();
		}
	}

}
