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
			public static String PASSWORD_RESET = "/password/reset";

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
		public static String PASSWORD_RESET = "password_reset";
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
		public static String HOST_URL = "hostUrl";
		
		public static String DAYS_SINCE_LAST_PASSWORD_CHANGE = "daysSinceLastPasswordChange";
	}
	

	public class Error {

		public class Title {
			public static String KEY = "errorTitle";

			public static String TECHNICAL_ERROR = "Technical Error";
			public static String MALFORMED_URL = "Malformed URL";
			public static String FORBIDDEN = "Forbidden";
		}
		

		public class Message {
			public static String KEY = "errorMessage";

			public static String SMTH_WENT_WRONG = "Something went wrong. Please try again.";
			public static String TRY_WITH_VALID_URL = "Please try with valid URL.";
			public static String CREDENTIAL_IS_NOT_AUTHENTIC = "Provided credentials is not determined to be authentic.";
			public static String COULD_NOT_PROCESS = "Sorry, your request could not be processed.";
			public static String REQUEST_IS_INVALID = "Please try with valid request.";
			public static String CAN_NOT_SET_OLD_PASSWORD = "You can not set your old password.";

		}
	}
	
	public static String REQUEST_HOST = "requestHost";
	public static String HOST_URL = "hostUrl";
	public static String USERNAME = "username";
	
	public static String MODEL_AND_VIEW = "modelAndView";
	
	public static String CLIENT_ID = "clientId";
	public static String CLIENT_DISPLAY_NAME = "clientDisplayName";

	public static String SERVICE_ID = "serviceId";

	public static String CLIENTSERVICE_ID = "clientServiceId";

	public static String LOGO_URL = "logoUrl";
	public static String BACKGROUND_IMAGE_URL = "backgroundImageUrl";
	public static String BACKGROUND_COLOR_CODE = "backgroundColorCode";

	public static String POST_REQUEST_URL = "postRequestUrl";
	
	public static String IS_LOGIN_SUCCESSFUL = "isLoginSuccessFul";
	public static String IS_PASSWORD_RESET_SUCCESSFUL = "isLoginSuccessFul";
	
	public static String LOGIN_SUCCESS_PATH = "loginSuccessPath";

	public static String IS_PASSWORD_EXPIRED = "isPasswordExpired";
	
	public static String PAGE_DESCRIPTION = "pageDescription";

}
