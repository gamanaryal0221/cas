package vcp.np.cas.utils.enums;

public enum JwtTokenPurpose {
	LOGIN_SUCCESSFUL("vcp.token.jwt.login.successful"),
	RESET_PASSWORD("vcp.token.jwt.forgot_password.reset");
	
    private final String code;
	
    JwtTokenPurpose(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}

}
