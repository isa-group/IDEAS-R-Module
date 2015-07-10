package es.us.isa.ideas.controller.sample;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import es.us.isa.ideas.common.AppResponse;
import es.us.isa.ideas.module.controller.BaseLanguageController;

@Controller
@RequestMapping("/language")
public class SampleLanguageController extends BaseLanguageController {

	@Override
	public AppResponse executeOperation(String id, String content, String fileUri, Map<String, String> data) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AppResponse checkLanguage(String format, String content, String fileUri) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AppResponse convertFormat(String currentFormat, String desiredFormat, String fileUri, String content) {
		// TODO Auto-generated method stub
		return null;
	}

}
