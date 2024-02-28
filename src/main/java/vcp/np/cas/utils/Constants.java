package vcp.np.cas.utils;

import java.util.ArrayList;
import java.util.List;

public class Constants {
	

	public class Request {

		public static String Is_GENUINE = "isGenuineRequest";

		public class Uri {

			public static String LOGIN = "/login";
			public static String ERROR = "/error";
			public static String PASSWORD = "/password";
			public static String PASSWORD_CHANGE = "/password/change";

			private static List<String> ALL = new ArrayList<String>(List.of(LOGIN, PASSWORD));
			
			public static boolean contains(String uri) {
				return ALL.contains(uri);
			}
		}
		

		public class Method {

			public static String GET = "GET";
			public static String POST = "POST";
		}
	}
	
	public class Templates {
		public static String ERROR = "error";
		public static String LOGIN = "login";
		public static String FORGOT_PASSWORD = "forgot_password";
		public static String RESET_PASSWORD = "reset_password";
	}
	

	// Used in Jwt token -- Sensitive
	public class JwtToken {
		public static String KEY = "token";
		
		public static String PURPOSE = "purpose";
		
		public static String FIRST_NAME = "firstName";
		public static String MIDDLE_NAME = "middleName";
		public static String LAST_NAME = "lastName";
		
		public static String CLIENT_DISPLAY_NAME = "clientDisplayName";

		public static String REQUEST_HOST = "requestHost";
	}
	
	public static String REQUEST_HOST = "requestHost";
	public static String HOST_URL = "hostUrl";
	public static String USERNAME = "username";
	
	public static String MODEL_AND_VIEW = "modelAndView";
	
	public static String ERROR_TITLE = "errorTitle";
	public static String ERROR_MESSAGE = "errorMessage";

	public static String CLIENT_ID = "clientId";
	public static String CLIENT_DISPLAY_NAME = "clientDisplayName";

	public static String SERVICE_ID = "serviceId";

	public static String CLIENTSERVICE_ID = "clientServiceId";

	public static String LOGO_URL = "logoUrl";
	public static String BACKGROUND_IMAGE_URL = "backgroundImageUrl";
	public static String BACKGROUND_COLOR_CODE = "backgroundColorCode";

	public static String POST_REQUEST_URL = "postRequestUrl";
	
	public static String IS_LOGIN_SUCCESSFUL = "isLoginSuccessFul";
	
	public static String LOGIN_SUCCESS_PATH = "loginSuccessPath";

	public static String IS_PASSWORD_EXPIRED = "isPasswordExpired";

}
