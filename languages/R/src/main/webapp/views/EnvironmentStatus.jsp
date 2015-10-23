<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page language="java" import="java.util.*" %>
<%@page language="java" import="es.us.isa.ideas.controller.R.RDelegate"%>
<%@page language="java" import="es.us.isa.ideas.controller.R.RInspector" %>
<%@page language="java" import="es.us.isa.ideas.controller.R.WorkspaceSync" %>
 
<script type="text/javascript">    
    //@ sourceURL=InsperctorManagerScript.js
    function onClick(tabName) {
        if (tabName == 'environments') {
            $('#plot').toggleClass('active')
            $('#environment').toggleClass('active')
            $.ajax('/ideas-R-language/inspector/environment', {
                'type': 'post',
                'data': {},
                'success': function (result) {
                    var jsp = result.context;
                    $('#editorInspectorLoader').load(jsp);
                },
                'onError': function (result) {
                    console.log([ERROR] + result.context);
                },
                'async': true, });

        } else if (tabName == 'plots') {
            $('#plot').toggleClass('active')
            $('#environment').toggleClass('active')
            $.ajax('/ideas-R-language/inspector/plots', {
                'type': 'post',
                'data': {},
                'success': function (result) {
                    var jsp = result.context;
                    $('#editorInspectorLoader').removeAttr("style");
                    $('#editorInspectorLoader').load(jsp);
                },
                'onError': function (result) {
                    console.log([ERROR] + result.context);
                },
                'async': true, });
        } else {
            window.alert("tab unrecognised.");
        }
    }
    function refresh(tabName) {
        //de momento no va a haber nada especial en el refresh
        onClick(tabName);
    }
    function canvasPlot(fileUri) {
        var canvas = document.getElementById("canvasPlot");
        var img = $('#selecPlot').val();
        var url = '/file/get/' + fileUri + '/<%=WorkspaceSync.OUTPUT_FOLDER%>/' + img;        
        //window.alert(fileUri);
        /*$.get(url, function(image){
         var con=canvas.getContext("2d");
         con.drawImage(image,100,100);
         });*/
        var image = new Image();
        image.onload = function(){
            canvas.height = image.height;
            canvas.width = image.width;
            var con = canvas.getContext("2d");
            con.drawImage(image, 10, 10);
        };
        image.src = url;
        
        /*$.ajax(url,{
         'type' : 'get',
         'success' : function(result){
         image=result;
         canvas.height=image.height;
         canvas.width=image.width;
         var con=canvas.getContext("2d");
         con.drawImage(image,10,10);
         
         },
         'onError' : function(result){},
         'async' : true
         });*/

    }
    function expand(TrId) {
        var id = TrId.substring(1); //quitarle la v.
        var v = $('.value').children('#value' + id);
        if (v.is(':hidden')) {
            v.slideDown();
        } else {
            v.hide();
        }

    }
    function chargeImages(fileUri) {        
        document.getElementById('selecPlot').onchange();
    }	
    
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
 