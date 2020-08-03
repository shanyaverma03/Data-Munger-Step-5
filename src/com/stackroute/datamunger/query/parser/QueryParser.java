package com.stackroute.datamunger.query.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QueryParser {

    private QueryParameter queryParameter = new QueryParameter();

    /*
     * This method will parse the queryString and will return the object of
     * QueryParameter class
     */
    public QueryParameter parseQuery(String queryString) {

        queryParameter.setFileName(getFileName(queryString));
        queryParameter.setBaseQuery(getBaseQuery(queryString));
        queryParameter.setFields(getFields(queryString));
        queryParameter.setRestrictions(getRestrictions(queryString));
        queryParameter.setLogicalOperators(getLogicalOperators(queryString));
        queryParameter.setAggregateFunctions(getAggregateFunctions(queryString));
        queryParameter.setOrderByFields(getOrderByFields(queryString));
        queryParameter.setGroupByFields(getGroupByFields(queryString));


        return queryParameter;
    }

    /*
     * extract the name of the file from the query. File name can be found after the
     * "from" clause.
     */


    public String getFileName(String queryString) {

        String str = queryString.split("from")[1].trim();
        String fileName = str.split(" ")[0].trim();
        return fileName;
    }

    public String getBaseQuery(String queryString) {

        StringBuilder base = new StringBuilder();
        if (queryString == null) {
            base = null;
        } else if (queryString.contains("where") == false) {
            String reg = "(.+)(\\sgroup.+)";
            Pattern pattern = Pattern.compile(reg);
            Matcher matcher = pattern.matcher(queryString);
            String ans = "";
            if (matcher.find()) {
                ans = matcher.group(1);
            }
            return ans;
        } else {
            int index = 0;
            String[] splitString = queryString.split(" ");
            for (int i = 0; i < splitString.length; i++) {

                if (splitString[i].equals("where")) {
                    index = i;
                    break;

                } else {

                    base.append(splitString[i]).append(" ");

                }
            }

        }

        if (base != null) {
            base.deleteCharAt(base.length() - 1);

            String baseString = base.toString();

            return baseString;
        } else {
            return "";
        }
    }

    /*
     * extract the order by fields from the query string. Please note that we will
     * need to extract the field(s) after "order by" clause in the query, if at all
     * the order by clause exists. For eg: select city,winner,team1,team2 from
     * data/ipl.csv order by city from the query mentioned above, we need to extract
     * "city". Please note that we can have more than one order by fields.
     */

    public List<String> getOrderByFields(String queryString) {

        if (queryString.isEmpty() || queryString == null) {
            return null;
        }

        String query = queryString.toLowerCase();

        String reg = "(order\\sby\\s)(\\w+)";
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(query);
        String res = "";
        int check = 0;
        if (matcher.find()) {
            res += matcher.group(2);
            check = 1;
        }
        List<String> fields = new ArrayList<>();
        if (check == 0) {
            return fields;
        } else {

            fields.add(res);
            return fields;
        }

    }



    /*
     * extract the group by fields from the query string. Please note that we will
     * need to extract the field(s) after "group by" clause in the query, if at all
     * the group by clause exists. For eg: select city,max(win_by_runs) from
     * data/ipl.csv group by city from the query mentioned above, we need to extract
     * "city". Please note that we can have more than one group by fields.
     */


    public List<String> getGroupByFields(String queryString) {


        if (queryString == null || queryString.isEmpty()) {
            return null;
        }
        String query = queryString.toLowerCase();

        String reg = "(group\\sby\\s)(\\w+)";
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(query);

        String res = "";
        int check = 0;
        if (matcher.find()) {
            res += matcher.group(2);
            check = 1;
        }
        List<String> fields = new ArrayList<>();
        if (check == 0) {
            return fields;
        } else {

            fields.add(res);
            return fields;
        }

    }



    /*
     * extract the selected fields from the query string. Please note that we will
     * need to extract the field(s) after "select" clause followed by a space from
     * the query string. For eg: select city,win_by_runs from data/ipl.csv from the
     * query mentioned above, we need to extract "city" and "win_by_runs". Please
     * note that we might have a field containing name "from_date" or "from_hrs".
     * Hence, consider this while parsing.
     */

    public List<String> getFields(String queryString) {


        String sSelect = queryString.toLowerCase().split("select")[1].trim();
        String sFrom = sSelect.split("from")[0].trim();
        String[] selectFields = null;
        ArrayList<String> list = new ArrayList<>();
        if (sFrom.contains(",")) {
            selectFields = sFrom.split(",");


            for (int i = 0; i < selectFields.length; i++) {
                list.add(selectFields[i].trim());
            }
            return list;
        } else {
            list.add(sFrom);
            return list;
        }
    }




    /*
     * extract the conditions from the query string(if exists). for each condition,
     * we need to capture the following:
     * 1. Name of field
     * 2. condition
     * 3. value
     *
     * For eg: select city,winner,team1,team2,player_of_match from data/ipl.csv
     * where season >= 2008 or toss_decision != bat
     *
     * here, for the first condition, "season>=2008" we need to capture:
     * 1. Name of field: season
     * 2. condition: >=
     * 3. value: 2008
     *
     * the query might contain multiple conditions separated by OR/AND operators.
     * Please consider this while parsing the conditions.
     *
     */

    public List<Restriction> getRestrictions(String queryString) {
        String trimmed = queryString.trim();
        String[] split = trimmed.trim().split("where");

        if (split.length == 1) {
            return null;
        }

        String[] conditions = split[1].trim().split("order by|group by");
        String[] split2 = conditions[0].trim().split(" and | or ");
        List<Restriction> restrictionList = new LinkedList<>();
        for (String string : split2) {
            String condition = "";
            if (string.contains(">=")) {
                condition = ">=";
            } else if (string.contains("<=")) {
                condition = "<=";
            } else if (string.contains("!=")) {
                condition = "!=";
            } else if (string.contains(">")) {
                condition = ">";
            } else if (string.contains("<")) {
                condition = "<";
            } else if (string.contains("=")) {
                condition = "=";
            }
            String name = string.split(condition)[0].trim();
            String value = string.split(condition)[1].trim().replaceAll("'", "");
            Restriction restriction = new Restriction(name, value, condition);
            restrictionList.add(restriction);
        }
        return restrictionList;
    }


    public String getConditionsPartQuery(String queryString) {


        if (queryString.isEmpty() || queryString == null) {
            return null;
        }
        if (!queryString.contains("where")) {
            return null;
        }

        String ans = "";
        if (!queryString.contains("group by") && !queryString.contains("order by")) {

            String reg = "(where\\s)(.+)";

            Pattern pattern = Pattern.compile(reg);
            Matcher matcher = pattern.matcher(queryString);
            if (matcher.find()) {
                ans = matcher.group(2);
            }

        } else if ((queryString.contains("group by") && !queryString.contains("order by")) || (queryString.contains("order by") && !queryString.contains("group by"))) {
            String reg = "(where\\s)(.+)(\\sorder\\sby|\\sgroup\\sby)";
            Pattern pattern = Pattern.compile(reg);
            Matcher matcher = pattern.matcher(queryString);
            if (matcher.find()) {
                ans = matcher.group(2);
            }
        } else if (queryString.contains("group by") && queryString.contains("order by")) {
            String reg = "(where\\s)(.+)(\\sgroup\\sby)";
            Pattern pattern = Pattern.compile(reg);
            Matcher matcher = pattern.matcher(queryString);
            if (matcher.find()) {
                ans = matcher.group(2);
            }
        }


        return ans;

    }


    public String[] getConditions(String queryString) {

        if (queryString == null || queryString.isEmpty()) {
            return null;
        }
        if (!queryString.contains("where")) {
            return null;
        }

        String req = getConditionsPartQuery(queryString);

        String[] str;
        if (req.contains("and") && !req.contains("or")) {
            str = req.split("and");

        } else if (req.contains("or") && !req.contains("and")) {
            str = req.split("\\sor\\s");
        } else if (req.contains("and") && req.contains("or")) {
            str = req.split(" and | or ");
        } else {
            str = req.split(",");
        }

        int i = 0;

        return str;

    }

    /*
     * extract the logical operators(AND/OR) from the query, if at all it is
     * present. For eg: select city,winner,team1,team2,player_of_match from
     * data/ipl.csv where season >= 2008 or toss_decision != bat and city =
     * bangalore
     *
     * the query mentioned above in the example should return a List of Strings
     * containing [or,and]
     */


    public List<String> getLogicalOperators(String queryString) {


        if (queryString == null || !queryString.contains("where")) {
            return null;
        }
        if (!queryString.contains("and") && !queryString.contains("or")) {
            return new ArrayList<String>();
        } else {

            String[] logical = queryString.split(" ");
            ArrayList<String> finalAns = new ArrayList<>();
            for (String str : logical) {
                if (str.equals("and") || str.equals("or")) {
                    finalAns.add(str);
                }
            }
            return finalAns;

        }
    }


    /*
     * extract the aggregate functions from the query. The presence of the aggregate
     * functions can determined if we have either "min" or "max" or "sum" or "count"
     * or "avg" followed by opening braces"(" after "select" clause in the query
     * string. in case it is present, then we will have to extract the same. For
     * each aggregate functions, we need to know the following:
     * 1. type of aggregate function(min/max/count/sum/avg)
     * 2. field on which the aggregate function is being applied
     *
     * Please note that more than one aggregate function can be present in a query
     *
     *
     */

    public List<AggregateFunction> getAggregateFunctions(String queryString) {

        String[] functions = aggregateFunctions(queryString);
        if (functions == null) {
            return null;
        }
        List<AggregateFunction> aggregateFunctions = new ArrayList<>();
        String function = "";
        for (String f : functions) {
            if (f.contains("sum")) {
                function = "sum";
            } else if (f.contains("count")) {
                function = "count";
            } else if (f.contains("min")) {
                function = "min";
            } else if (f.contains("max")) {
                function = "max";
            } else if (f.contains("avg")) {
                function = "avg";
            }
            String[] split = f.split("\\(|\\)");
            aggregateFunctions.add(new AggregateFunction(split[1], function));
        }


        return aggregateFunctions;
    }

    public String[] aggregateFunctions(String queryString) {

        if (queryString.isEmpty() || queryString == null) {
            return null;
        }

        String query = queryString.toLowerCase();
        if (!query.contains("sum") && !query.contains("count") && !query.contains("min") && !query.contains("max") && !query.contains("avg")) {

            return null;
        } else {
            String reg2 = "(sum\\(\\w+\\)|count\\(\\w+\\)|max\\(\\w+\\)|min\\(\\w+\\)|avg\\(\\w+\\))";
            Pattern pattern = Pattern.compile(reg2);
            Matcher matcher = pattern.matcher(query);
            StringBuilder sb = new StringBuilder();
            while (matcher.find()) {
                String ans = matcher.group();
                sb.append(ans).append((" "));
            }
            String ans2 = sb.toString();
            String[] finalAns = ans2.split(" ");

            return finalAns;


        }


    }
}
	
	

