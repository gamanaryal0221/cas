package vcp.np.cas.utils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import vcp.np.cas.utils.Constants.Error;

public class Helper {
	
	@SuppressWarnings("deprecation")
	public static URL parseUrl(String _url) {
        System.out.println("Parsing url: " + _url);
        
        URL url = null;

		if (_url != null && !_url.isEmpty()) {
			try {
				
                url = new URL(_url);
                
	        } catch (MalformedURLException e) {
	            System.out.println("Malformed url:" + _url + " :" + e.getMessage());
	        	e.printStackTrace();
	        } catch (Exception e) {
	            e.printStackTrace();
	            System.out.println("Could not parse url:" + _url + " :" + e.getMessage());
	        }
		}else {
            System.out.println("Provided url(i.e. " + _url + ") is empty or null.");
		}
		return url;
	}

	public static boolean isGenuineRequest(HttpServletRequest request) {
		boolean isGenuineRequest = false;
		try {
			isGenuineRequest = (boolean) request.getAttribute(Constants.Request.Is_GENUINE);
		}catch(Exception e) {
			e.getStackTrace();
			System.out.println("Error encountered while checking isGenuineRequest "+ e.getMessage());
		}
		System.out.println("==> "+ request.getRequestURI() + ":" + request.getMethod() + " -> isGenuineRequest::: "+ isGenuineRequest);
		return isGenuineRequest? isGenuineRequest:false;
	}
	

	public static ModelAndView getAllModelAndView(HttpServletRequest request) {
		ModelAndView modelAndView = null;
		try {
			modelAndView = (ModelAndView) request.getAttribute(Constants.MODEL_AND_VIEW);
		}catch(Exception e) {
			e.getStackTrace();
			System.out.println("Error encountered while getting modelAndView "+ e.getMessage());
		}
		return modelAndView;
	}
	
	public static Map<String, String> error(String title, String message){
		Map<String, String> errorMap = new HashMap<String, String>();
		errorMap.put(Error.Title.KEY, (title != null && !title.isEmpty())? title:Error.Title.TECHNICAL_ERROR);
		errorMap.put(Error.Message.KEY, (message != null && !message.isEmpty())? message:Error.Message.SMTH_WENT_WRONG);
		return errorMap;
	}

	
}
