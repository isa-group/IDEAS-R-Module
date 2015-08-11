<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page language="java" import="java.util.*" %>
<%@page language="java" import="es.us.isa.ideas.controller.R.RInspector" %>

<script type="text/javascript">

			function onClick(tabName){
				if(tabName=='environments'){
					$('#plot').toggleClass('active')
					$('#environment').toggleClass('active')
					$.ajax('/ideas-R-language/inspector/environment',{
						  'type':'post',
						  'data':{},
						  'success':function(result){
						    var jsp= result.context;
						    $('#editorInspectorLoader').load(jsp);
						  				},
						  'onError':function(result){console.log([ERROR]+result.context);},
						  'async':true,});
					
				}else if(tabName=='plots'){
					$('#plot').toggleClass('active')
					$('#environment').toggleClass('active')
					$.ajax('/ideas-R-language/inspector/plots',{
						  'type':'post',
						  'data':{},
						  'success':function(result){
						          var jsp= result.context;
						          $('#editorInspectorLoader').load(jsp);
						                            },
						  'onError':function(result){console.log([ERROR]+result.context);},
						  'async':true,});
				}else{
					window.alert("tab unrecognised.");
				}
			}
			function refresh(tabName){
				//de momento no va a haber nada especial en el refresh
				onClick(tabName);
			}
			function canvasPlot(){
				
			}
			function expand(TrId){
				var id=TrId.substring(1); //quitarle la v.
				var v=$('.value').children('#value'+id);
				if(v.is(':hidden')){
				    v.slideDown();
				  }else{
				   v.hide(); 
				  }
				
			}
	
 </script>