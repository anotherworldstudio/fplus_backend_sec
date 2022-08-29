package com.kbeauty.gbt.entity.enums;

public enum RecruitStatus implements CodeVal{
	REG("0000", "Register"),
	;

	private String code;
	private String val;

	RecruitStatus(String code, String val){
		this.code = code;
		this.val = val;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getVal() {
		return val;
	}

	public void setVal(String val) {
		this.val = val;
	}
}
