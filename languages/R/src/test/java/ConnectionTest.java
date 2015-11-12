import org.rosuda.REngine.REXP;

import com.google.common.collect.Lists;

import org.junit.Test;
import org.math.R.RserverConf;
import org.math.R.Rsession;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class ConnectionTest {
	Rsession s;
	String local6311="R://localhost:6311";
	String script= "src/test/resources/Script_Prueba_1.R";
	String scriptContent= "x <- c(1,2,4,8,16)\nfor (loop in x)\n{\n  cat(\"value of loop: \",loop,\"\n\");\n}";
	String script2= "src/test/resources/Script_Prueba_2.R";
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
	
	//@Test	
	public void testConnection3(){
		Properties p= new Properties();
		p.put("r-source", "enable");
		RserverConf c= RserverConf.parse(local6311);
		c.properties=p;
		Rsession s= Rsession.newInstanceTry(System.out, c);
		 
		 File archivo = new File (script2);
		 FileReader fr;
		 BufferedReader br;
		 List<String> output= new ArrayList<String>();
		 String spec="value of loop: 1\nvalue of loop: 2\nvalue of loop: 4\nvalue of loop: 8\nvalue of loop: 16";
		try {
			fr = new FileReader (archivo);
			br  = new BufferedReader(fr);
			String linea;
			 while((linea=br.readLine())!=null){
				 //linea=br.readLine();
				 if(linea!=null&&linea!=""){
					 String asS= s.eval(linea).asString();
					 if(asS!=null&&asS!="")
						 output.add(asS);
			}
			 }			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		assertNotNull(output);
		assertEquals( spec,output.get(output.size()-1));
		 
		 s.end();
	}
	
	//@Test
	public void testConnection4(){
		String command1="x <- c(1,2,4,8,16)";
		String command= "for (loop in x){ cat(\"value of loop: \",loop,\"\n\");}";
		String spec="value of loop: 1\nvalue of loop: 2\nvalue of loop: 4\nvalue of loop: 8\nvalue of loop: 16";
		String result="";
		try {
			RserverConf c= RserverConf.parse(local6311);
			 s= Rsession.newInstanceTry(System.out, c);
			loop(command1,s);
			result= loop(command,s);
			
			s.end();
		} catch (Exception o) {
			
			o.printStackTrace();
		}
		
		assertEquals(spec, result);
	}

	//@Test
	public void testConnection5(){
		Properties p= new Properties();
		p.put("r-source", "r-source enable");
		/*RserverConf c= RserverConf.parse(local6311);
		c.properties=p;*/
		RserverConf c= new RserverConf("localhost", 6311, null, null, p);
		Rsession s= Rsession.newInstanceTry(System.out, c);
		String command= "for (loop in x){ cat(\"value of loop: \",loop,\"\n\");}";
		REXP e= s.eval(command);
		
		
		s.end();
	}
	
	//@Test
	public void testConnection6(){
		
		try {
			RserverConf c= RserverConf.parse(local6311);
			 s= Rsession.newInstanceTry(System.out, c);
			 	String res= s.asString("eval(parse("+scriptContent+"))");
			s.end();
		} catch (Exception o) {
			
			o.printStackTrace();
		}
		
	}
	
	private String loop(String command, Rsession s2) {
		String res="";
		
		if(checkNotPrintingCommand(command)){
			s2.voidEval(command);
		}else{
			res= s2.asString(command);
		}
		
		return res;
	}

	private Boolean checkNotPrintingCommand(String command) {
		Boolean res= false;
		List<String> fc= Lists.newArrayList();
		fc.add("while");fc.add("for");fc.add("if");fc.add("repeat");fc.add("break");
		fc.add("next");fc.add("{");fc.add("}");fc.add("<-");
		for(String t:fc){
			if(command.contains(t))
			  res=true;	
				break;
		}
		
		return res;
	}
}
