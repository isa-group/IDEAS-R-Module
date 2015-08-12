package es.us.isa.ideas.controller.R;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

import org.math.R.Rsession;
import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPMismatchException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Lists;

import es.us.isa.ideas.common.AppResponse;
import es.us.isa.ideas.common.AppResponse.Status;

@Controller
@RequestMapping("/inspector")
public class RInspector {

	@RequestMapping(value = "/environment", method = RequestMethod.POST)
	@ResponseBody
	public static AppResponse environments(){
		AppResponse res= new AppResponse();
		
		try {
			/*RDelegate RD= new RDelegate(null,null,null);
			Rsession s= RD.getSession();*/
//			Rsession s= RDelegate.getSession();
//			String[] variables= getVariables();
//			String[] varValues= getVariablesValues(s,variables);
//			String format="<div class=\"\">";
//			for(int i =0;i<variables.length;i++){
//				//TODO: if the variable is a data frame should be treated in a different way, like a button.
//				/*if(isDataFrame(s,variables[i])){
//					format= DataFrameHTML(s,variables[i],i);
//					
//				}else{*/
//				 format+="<div class=\"EnvVariable"+i+"\">";
//				
//				String name="<span>"+variables[i]+"</span>";
//				String value="<input type=text readonly value=\""+varValues[i].replace("[1] ", "")+"/>";
//				format+=name+value;
//				format+="</div><br/>";
//				//UNTESTED/ COMPLETED(?)
//			}
			//}
			//res.setContext(format);
			res.setContext("/ideas-R-language/views/EnvironmentStatus.jsp");
			res.setStatus(Status.OK);
		} catch (Exception e) {
			res.setMessage(e.getMessage());
			res.setStatus(Status.ERROR);
			e.printStackTrace();
		}
		return res;
	}
	
	@RequestMapping(value = "/plots", method = RequestMethod.POST)
	@ResponseBody
	public static AppResponse plots(){
		AppResponse response= new AppResponse();
		response.setContext("/ideas-R-language/views/Plots.jsp");
		response.setStatus(Status.OK);
		
		return response;
	}
	
	public static boolean isDataFrame(String variable){
		return isDataFrame(RDelegate.getSession(),variable);
	}
	private static boolean isDataFrame(Rsession s, String variable) {
		
		boolean res=false;
		try {
			res = s.silentlyEval("is.data.frame("+variable+")").asString().contains("TRUE");
		} catch (REXPMismatchException e) {
			System.out.println("se elevó excepción en la evaluación del comando de comprobación.");
			e.printStackTrace();
		} catch(Exception e){
			e.printStackTrace();
		}
		return res; 
	}
	public static String[] getVariables(){
		List<String> result= Lists.newArrayList();
		String[] res=RDelegate.getEnvironmentVariables();
		for(String r:res){
			if(!r.equals("savegraphs")){
				result.add(r);
			}
		}
		return result.toArray(new String[0]);
	}
	/**
	 * Gives the values of the variables in HTML with the following format:
	 * <div class="value"> first line<br/> second line <br/>..... </div >
	 * */
	public static String[] getVariablesValues() throws REXPMismatchException{
		return getVariablesValues(RDelegate.getSession(), getVariables());
	}
	
	private static String[] getVariablesValues(Rsession s, String[] var) throws REXPMismatchException{
		List<String> res= Lists.newArrayList();
		if(var!=null){
			int index=0;
		for(String variable:var){
		if(!variable.equals("savegraphs")){
			String st=s.asHTML("print("+variable+")");
					st=st.replace("<html>", "");
					st.replace("</html>", "");
					st=st.substring(0, (st.length()/2)-1);
				String[] spl= st.split("(<br/>)");
				String r;
				if(isDataFrame(variable)){
					r= DataFrameHTML(spl,index);
				}else{
				
				 r="<div class=\"value\"><p>"+spl[0]+"</p><div class=\"extendable\" id=\"value"+index+"\">";
				for(int i=1;i<spl.length;i++){
					r+=spl[i]+"<br/>";
				}
				r+="</div></div>";
				}
			res.add(r);
			index++;
		
		}else{
			res.add("<div class=\"extendable\" id=\"value"+index+"\"> no value found.</div>");
		}
		}
	}
		return  res.toArray(new String[0]);
	}
	
	public static String DataFrameHTML(String[] lines, int index){
		String res="";
		 res+="<div class=\"value\">";
		 res+="<table class=\"table table-hover extendable\" id=\"value"+index+"\">";
		
		int i=0;
		
		for(String line: lines){
			if(i!=0){
				res+="<tr>";
				String[] spl=line.substring(1).trim().replaceAll("( {2,})",",").split("(,)");
				for(String piece: spl){
					res+="<td>"+piece+"</td>";
				}
				res+="</tr>";
			}else{

				res+="<tr>";
				String[] spl=line.replace('\t', '\u0020').trim().split("( )");
				for(String piece: spl){
					res+="<th>"+piece+"</th>";
				}
				res+="</tr>";
			}
			
			i++;
		}
		
		res+="</table></div>";
		
		return res;
	}
	public static String[] getPlots(){
		
		
		return RDelegate.getPlots();
	}

	public static String[] getPlots(String temp) {
		List<String> res = Lists.newArrayList();
		try {
			File t= new File(temp);
			File o= t.toPath().resolve("IDEAS-R-OutputFolder").toFile();
			
			for(String f:o.list()){
				if(f.contains(".jpg")){
					String[] spl= f.split("/");
					res.add(spl[spl.length-1]);
				}
			}
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		return res.toArray(new String[0]);
	}
	public static String getFileUri(){
		String res= RDelegate.getUri();
		String[] sp= res.split("/");
		
		return sp[0]+"/"+sp[1];
	}


}

