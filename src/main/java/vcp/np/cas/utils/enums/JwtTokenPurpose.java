package vcp.np.cas.utils.enums;

public enum JwtTokenPurpose {
	LOGIN_SUCCESSFUL("vcp.token.jwt.login.successful"),
	PASSWORD_RESET("vcp.token.jwt.password.reset"),
	FORCED_PASSWORD_RESET("vcp.token.jwt.forced.password.reset");
	
    private final String code;
	
    JwtTokenPurpose(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}

}
