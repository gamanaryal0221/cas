package vcp.np.cas.services;

import org.springframework.beans.factory.annotation.Autowired;

public class PasswordService {


	@Autowired
	public CommonService commonService;

	@Autowired
	public AuthenticationService authenticationService;

	@Autowired
	public JwtTokenService jwtTokenService;
	
	

}
