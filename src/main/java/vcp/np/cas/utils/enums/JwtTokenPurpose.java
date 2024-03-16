package vcp.np.cas.utils.enums;

public enum JwtTokenPurpose {
	LOGIN_SUCCESSFUL("login.successful"),
	PASSWORD_RESET("password.reset"),
	CHANGE_PASSWORD("change.password");
	
    private final String code;
	
    JwtTokenPurpose(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}

}
