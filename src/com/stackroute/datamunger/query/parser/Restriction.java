package com.stackroute.datamunger.query.parser;

/*
 * This class is used for storing name of field, condition and value for
 * each conditions
 * */
public class Restriction {

    private String name;
    private String value;
    private String condition;

    public Restriction(String name, String value, String condition) {
        this.name = name;
        this.value = value;
        this.condition = condition;
    }

    public String getPropertyName() {
        // TODO Auto-generated method stub
        return this.name;
    }

    public String getPropertyValue() {
        // TODO Auto-generated method stub
        return this.value;
    }

    public String getCondition() {
        // TODO Auto-generated method stub
        return this.condition;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }
}
