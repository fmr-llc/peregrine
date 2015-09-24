package com.alliancefoundry.model;

public class DataItem {
	
	private String dataType;
	private String value;
	
	public DataItem() { }
	
	/**
	 * @param dataType
	 * @param value
	 */
	public DataItem(String dataType, String value) { 
		this.dataType = dataType;
		this.value = value;
	}

	/**
	 * @return the dataType
	 */
	public String getDataType() {
		return dataType;
	}

	/**
	 * @param dataType the dataType to set
	 */
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}
	
}
