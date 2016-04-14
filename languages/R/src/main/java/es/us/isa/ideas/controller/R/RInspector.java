package es.us.isa.ideas.controller.R;

import java.io.File;
import java.util.List;

import org.math.R.Rsession;
import org.rosuda.REngine.REXPMismatchException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import es.us.isa.ideas.module.common.AppResponse;
import es.us.isa.ideas.module.common.AppResponse.Status;

import java.util.Set;

@Controller
@RequestMapping("/inspector")
public class RInspector {

    @RequestMapping(value = "/environment", method = RequestMethod.POST)
    @ResponseBody
    public AppResponse environments() {
        AppResponse res = new AppResponse();

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
    public AppResponse plots() {
        AppResponse response = new AppResponse();
        response.setContext("/ideas-R-language/views/Plots.jsp");
        response.setStatus(Status.OK);

        return response;
    }  
    

    

}
