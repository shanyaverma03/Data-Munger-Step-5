package com.stackroute.datamunger.query.parser;

/* This class is used for storing name of field, aggregate function for
 * each aggregate function
 * */
public class AggregateFunction {

    private String field;
    private String function;

    public AggregateFunction(String field, String function) {

        super();
        this.field = field;
        this.function = function;
    }

    public String getFunction() {
        // TODO Auto-generated method stub


        return this.function;
    }

    public String getField() {
        // TODO Auto-generated method stub
        return this.field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public void setFunction(String function) {
        this.function = function;
    }
}
