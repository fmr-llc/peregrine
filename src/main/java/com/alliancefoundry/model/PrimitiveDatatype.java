package com.alliancefoundry.model;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Created by Paul Bernard on 11/11/15.
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum PrimitiveDatatype {

    Byte(1, "Byte"), String(2, "String"), Integer(3, "Integer"),
    Double(4, "Double"), Float(5, "Float"), Boolean(6, "Boolean"),
    Long(7, "Long"), Short(8, "Short");

    private Integer id;
    private String name;

    private PrimitiveDatatype(final Integer id, final String name) {
        this.id = id;
        this.name = name;
    }


    public String getName() {
        return name;
    }

}
