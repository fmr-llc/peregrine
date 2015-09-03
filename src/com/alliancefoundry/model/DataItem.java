package com.alliancefoundry.model;

public class DataItem {
	
	//private PrimativeDataType dataType;
	private String dataType;
	private String value;
	
	public DataItem() { }
	
	/*public DataItem(PrimativeDataType dataType, String value) { 
		this.dataType = dataType;
		this.value = value;
	}
	
	public PrimativeDataType getDataType() {
		return dataType;
	}
	public void setDataType(PrimativeDataType dataType) {
		this.dataType = dataType;
	}*/
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
