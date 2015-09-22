
<%@page import="es.us.isa.ideas.controller.R.RDelegate"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page language="java" import="java.util.*" %>
<%@page language="java" import="es.us.isa.ideas.controller.R.RInspector" %>
<%@ include file="inspectorManager.jsp" %>

<%
                    RDelegate rdelegate=(RDelegate)session.getAttribute("RDelegate");
                    %>

<div class="container">
<style>

canvas{
float:left;
overflow:scroll;

}
.plots{
overflow:scroll;
}
#inspectorFooter{
clear:both;
}


</style>

<!--   <script src="ideas-R-language/js/inspectorManager.js"></script> -->

  <ul class="nav nav-tabs">
    <li id="environment" ><a href="javascript:onClick('environments')">Environment</a></li>
    <li id="plot" class="active"><a href="javascript:onClick('plots')">Plots</a></li>
  </ul>
  
	<div class="plots">
			
		<% 
		String fileuri;
		try{
		 fileuri= rdelegate.getFileUri(); 
		}catch(NullPointerException e){
			fileuri= "";
		}
		%>
	
	<select id="selecPlot" onchange="canvasPlot('<%=fileuri%>')">
	<%
	String[] graficas;
	try{
	graficas= rdelegate.getPlots();
	}catch(NullPointerException e){
		graficas = new String[]{};
	}
	String format="";/* <option value=\"default\">Last Graph</option>";*/
	
	for(String graf:graficas){
		format+="<option value=\""+graf+"\">"+graf+"</option>";
	}
	%>
	<%=format %>
	
	<script type="text/javascript">
	
	
  	chargeImages(<%=fileuri%>);
  </script>
	 </select>
	 <button onclick="canvasPlot('<%=fileuri%>')"> Graphic!</button>
	<canvas id="canvasPlot" width="auto" height="auto">
			Browser doesn't support Canvas :( 	
		</canvas>
		
	</div>
	
	 <div id="inspectorFooter">
	 <!-- Refresh Button -->
	 <h5>Your plots are also in you IDEAS-R-OutputFolder, press f5 to see them!</h5>
	 <h6>If is the first time the plot is showed, you may need to click the "Graphic!" button.</h6>
	 <div class="buttons" id="buttons">
    	<button type="button" onclick="refresh('plots')" class="btn btn-default btn-sm">
          <span class="glyphicon glyphicon-refresh"></span> Refresh
        </button>
        </div>
    </div>
    
</div>
