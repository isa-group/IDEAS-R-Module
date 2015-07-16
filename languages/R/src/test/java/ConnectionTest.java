import org.rosuda.REngine.REXP;

import org.junit.Test;
import org.math.R.RserverConf;
import org.math.R.Rsession;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class ConnectionTest {
	Rsession s;
	String local6311="R://localhost:6311";
	String script= "src/test/resources/Script_Prueba_1.R";
	@Test
	public void testConnection0(){
		
		try {
			RserverConf c= RserverConf.parse(local6311);
			 s= Rsession.newInstanceTry(System.out, c);
			assertTrue(s.connected);
			s.end();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	@Test
	public void testConnection1(){
		String command= "2+2";
		String r;
		Double spec=4.;
		Double response;
		
		try {
			RserverConf c= RserverConf.parse(local6311);
			 s= Rsession.newInstanceTry(System.out, c);
			REXP e= s.eval(command);
			 r= s.asString(command);
			response=e.asDouble();
			s.end();
		} catch (Exception o) {
			r= "Se lanzó excepcion.";
			response=0.;
			o.printStackTrace();
		}
		assertEquals(response, spec);
		assertEquals("[1] 4",r);
	
		
	}
	@Test
	public void testConnection2(){
		RserverConf c= RserverConf.parse(local6311);
		 s= Rsession.newInstanceTry(System.out, c);
		 
		 File archivo = new File (script);
		 FileReader fr;
		 BufferedReader br;
		 List<String> output= new ArrayList<String>();
		try {
			fr = new FileReader (archivo);
			br  = new BufferedReader(fr);
			String linea;
			 while((linea=br.readLine())!=null){
				 linea=br.readLine();
				 if(linea!=null)
				output.add(s.asString(linea));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		assertNotNull(output);
		assertEquals(output.get(output.size()-1), "[1] 5");
		 
		 s.end();
	}
	
}
