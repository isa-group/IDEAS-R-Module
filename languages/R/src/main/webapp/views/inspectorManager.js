var inspector={
		onClick:function(tabName){
			if(tabName=='environments'){
				$.ajax('/ideas-R-language/inspector/environment',{
					  'type':'post',
					  'data':{},
					  'success':function(result){
					    var jsp= result.context;
					    $('#editorInspectorLoader').load(jsp)
					  				},
					  'onError':function(result){console.log([ERROR]+result.context);},
					  'async':true,});
			}else if(tabName=='plots'){
				$.ajax('/ideas-R-language/inspector/plots',{
					  'type':'post',
					  'data':{},
					  'success':function(result){
					          var jsp= result.context;
					          $('#editorInspectorLoader').load(jsp)
					                            },
					  'onError':function(result){console.log([ERROR]+result.context);},
					  'async':true,});
			}else{
				window.alert("tab unrecognised.");
			}
		},
		refresh:function(tabName){
			//de momento no va a haber nada especial en el refresh
			inspector.onClick(tabName);
		},
		canvasPlot: function(){
			
		},
		expand: function(TrId){
			if($(TrId).children('.extendable').is(':visible')){
				$(TrId).children('extendable').css('visibility','hidden');
			}else{
				$(TrId).children('extendable').css('visibility','visible');
			}
			
		}
		
		
}

