package com.alliancefoundry.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class DataItem {
	
	private PrimitiveDatatype dataType;
	private String value;
	
	public DataItem() { }
	
	public DataItem(PrimitiveDatatype dataType, String value) {
		this.dataType = dataType;
		this.value = value;
	}
	
	public PrimitiveDatatype getDataType() {
		return dataType;
	}
	public void setDataType(PrimitiveDatatype dataType) {
		this.dataType = dataType;
	}
	
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
}
