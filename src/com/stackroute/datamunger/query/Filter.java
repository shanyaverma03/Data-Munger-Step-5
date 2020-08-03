package com.stackroute.datamunger.query;

import com.stackroute.datamunger.query.parser.Restriction;

import java.text.ParseException;
import java.text.SimpleDateFormat;

//This class contains methods to evaluate expressions
public class Filter {


    /*
     * The evaluateExpression() method of this class is responsible for evaluating
     * the expressions mentioned in the query. It has to be noted that the process
     * of evaluating expressions will be different for different data types. there
     * are 6 operators that can exist within a query i.e. >=,<=,<,>,!=,= This method
     * should be able to evaluate all of them.
     * Note: while evaluating string expressions, please handle uppercase and lowercase
     *

     */

    public boolean evaluateExpression(Restriction restriction, String fieldValue, String dataType) {
        String dataValue = fieldValue;
        String expectedValue = restriction.getPropertyValue();
        boolean res = false;
        if (dataType.contains("Integer")) {
            int actual = Integer.parseInt(dataValue);
            int expected = Integer.parseInt(expectedValue);
            if (restriction.getCondition().equals("<=")) {
                res = (actual <= expected);
            } else if (restriction.getCondition().equals(">=")) {
                res = (actual >= expected);
            } else if (restriction.getCondition().equals("<")) {
                res = (actual < expected);
            } else if (restriction.getCondition().equals(">")) {
                res = (actual > expected);
            } else if (restriction.getCondition().equals("!=")) {
                res = (actual != expected);
            } else {
                res = (actual == expected);
            }
        }
        if (dataType.contains("Double")) {
            Double actual = Double.parseDouble(dataValue);
            Double expected = Double.parseDouble(expectedValue);
            int value = actual.compareTo(expected);
            if (restriction.getCondition().equals("<=")) {
                res = (value <= 0);
            } else if (restriction.getCondition().equals(">=")) {
                res = (value >= 0);
            } else if (restriction.getCondition().equals("<")) {
                res = (value < 0);
            } else if (restriction.getCondition().equals(">")) {
                res = (value > 0);
            } else if (restriction.getCondition().equals("!=")) {
                res = (value != 0);
            } else {
                res = (value == 0);
            }
        }
        if (dataType.contains("String")) {
            if (restriction.getCondition().equals("=")) {
                res = (dataValue.equalsIgnoreCase(expectedValue));
            } else {
                res = (!dataValue.equalsIgnoreCase(expectedValue));
            }
        }
        return res;
    }


}
