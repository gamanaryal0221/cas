package vcp.np.cas.utils.enums;

public enum Gender {
	MALE(1),
	FEMALE(2),
	OTHERS(3);
	
    private final int code;
	
	Gender(int code) {
		this.code = code;
	}

	public int getCode() {
		return code;
	}
	
}
