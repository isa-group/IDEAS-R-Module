<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page language="java" import="java.util.*" %>
<%@page language="java" import="es.us.isa.ideas.controller.R.RInspector" %>
<%@ include file="inspectorManager.jsp" %>
<div class="container">
<!--   <script src="ideas-R-language/js/inspectorManager.js"></script> -->

  <ul class="nav nav-tabs">
    <li id="environment" ><a href="javascript:onClick('environments')">Environment</a></li>
    <li id="plot" class="active"><a href="javascript:onClick('plots')">Plots</a></li>
  </ul>

	<div class="plots">
	<h1>PLOTS</h1>
		<canvas id="canvasPlot" width="500" height="360">
			Browser doesn't support Canvas :( 	
		</canvas>
		<!-- Unfinished!! --> 
		<button onClick="canvasPlot()">Graphic</button>
	</div>
	
	 <div class="buttons" id="buttons">
    	<button type="button" onclick="refresh('plots')" class="btn btn-default btn-sm">
          <span class="glyphicon glyphicon-refresh"></span> Refresh
        </button>
    </div>
</div>
