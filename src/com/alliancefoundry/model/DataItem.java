package com.alliancefoundry.model;

public class DataItem {
	
	private String dataType;
	private String value;
	
	public DataItem() { }
	
	public DataItem(String dataType, String value) { 
		this.dataType = dataType;
		this.value = value;
	}
	
	public String getDataType() {
		return dataType;
	}
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
}
