
<%@page import="es.us.isa.ideas.controller.R.RDelegate"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page language="java" import="java.util.*" %>
<%@page language="java" import="es.us.isa.ideas.controller.R.RInspector" %>
<%@ include file="inspectorManager.jsp" %> 
 
 <script type="text/javascript">
	
		$("#editorInspectorLoader").css("overflow","auto");
		 	
</script>
<!-- bootstrap tabs and pills --> 
<div class="container">
 <!--  <script src="ideas-R-language/js/inspectorManager.js"></script>--> 
 
  <ul class="nav nav-tabs">
    <li id="environment" class="active"><a href="javascript:onClick('environments')">Environment</a></li>
    <li id="plot"><a href="javascript:onClick('plots')">Plots</a></li>
  </ul>
		<%
                    RDelegate rdelegate=(RDelegate)session.getAttribute("RDelegate");
		String[] variables;
		String[] varValues;
					try{
                    variables=rdelegate.getEnvironmentVariables();
                    varValues=rdelegate.getVariablesValues(variables);
					}catch(NullPointerException e){
						variables=new String[]{};
						varValues=new String[]{};
					}
		
		%>
<style>
/*div.container{
overflow:scroll;
}
.variables{
overflow:scroll;
}*/
#editorInspectorLoader{
	overflow: auto;
}
div.variables {
		border-bottom-style:solid;
		border-radius:0.1em;
		border-color: #C0C0C0;
		border-width: 0.2em;
		overflow:scroll;		
}

.variables .table{
width:40%; 
height:100%;
/*overflow:scroll;*/
}
.value table{
/*overflow:scroll;*/
width:100%;
}
tr{
border-style:solid;
border-top-style: none;
border-right-style: none;
border-left-style: none;
border-color:#C0C0C0;
border-width: 0.15em;
}

td{
margin:1em,0em,1em,1em;
text-align: center;
border-right-style: solid;
border-margin:0.2em;
border-color:#C0C0C0;
}
.extendable{
	display:none;
	height: 20%;
}
/*.value:hover, .value:focus, value:target{
	display: block;
}*/
		
	</style>
	
  <div class="variables">
	  
    <% 
   
    String format= "";
    format+="<table class=\"table table-hover\">";
    format+="<tr><th>Variables</th><th>Values</th></tr>";
    if(variables.length==0)
    	format+="<tr><td>No variables found</td><td>...</td></tr>";
    for(int i =0;i<variables.length;i++){
    	
    	format+="<tr id=\"v"+i+"\" onclick=\"expand(this.id)\">";
    	String name= "<td>"+variables[i]+"</td>";
    	String value="<td>"+varValues[i]+"</td>";
    	
    	
    	format+=name+value;
    	format+="</tr>";
    	
    }
    format+="</table>";
    %>
   
    <%=format %>
    
    <div class="buttons" id="buttons">
    	<button type="button" onclick="refresh('environments')" class="btn btn-default btn-sm">
          <span class="glyphicon glyphicon-refresh"></span> Refresh
        </button>
    </div>
  </div>
</div>
