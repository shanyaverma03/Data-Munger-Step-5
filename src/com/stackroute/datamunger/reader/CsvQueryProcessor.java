package com.stackroute.datamunger.reader;

import com.stackroute.datamunger.query.*;
import com.stackroute.datamunger.query.parser.QueryParameter;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CsvQueryProcessor implements QueryProcessingEngine {
    /*
     * This method will take QueryParameter object as a parameter which contains the
     * parsed query and will process and populate the ResultSet
     */
    public DataSet getResultSet(QueryParameter queryParameter) throws IOException {

        /*
         * initialize BufferedReader to read from the file which is mentioned in
         * QueryParameter. Consider Handling Exception related to file reading.
         */
        FileReader fileReader = new FileReader(queryParameter.getFileName());
        BufferedReader reader = new BufferedReader(fileReader);


        /*
         * read the first line which contains the header. Please note that the headers
         * can contain spaces in between them. For eg: city, winner
         */


        String[] headers = reader.readLine().split(",");
        reader.mark(1);

        /*
         * read the next line which contains the first row of data. We are reading this
         * line so that we can determine the data types of all the fields. Please note
         * that ipl.csv file contains null value in the last column. If you do not
         * consider this while splitting, this might cause exceptions later
         */

        String[] fields = reader.readLine().split(",", headers.length);


        /*
         * populate the header Map object from the header array. header map is having
         * data type <String,Integer> to contain the header and it's index.
         */

        Header headerMap = new Header();
        for (int i = 0; i < headers.length; i++) {
            headerMap.put(headers[i].trim(), i);
        }

        /*
         * We have read the first line of text already and kept it in an array. Now, we
         * can populate the RowDataTypeDefinition Map object. RowDataTypeDefinition map
         * is having data type <Integer,String> to contain the index of the field and
         * it's data type. To find the dataType by the field value, we will use
         * getDataType() method of DataTypeDefinitions class
         */

        RowDataTypeDefinitions rowMap = new RowDataTypeDefinitions();

        for (int i = 0; i < fields.length; i++) {
            rowMap.put(i, (String) DataTypeDefinitions.getDataType(fields[i]));
        }

        /*
         * once we have the header and dataTypeDefinitions maps populated, we can start
         * reading from the first line. We will read one line at a time, then check
         * whether the field values satisfy the conditions mentioned in the query,if
         * yes, then we will add it to the resultSet. Otherwise, we will continue to
         * read the next line. We will continue this till we have read till the last
         * line of the CSV file.
         */


        /* reset the buffered reader so that it can start reading from the first line */

        reader.reset();

        /*
         * skip the first line as it is already read earlier which contained the header
         */

        /* read one line at a time from the CSV file till we have any lines left */

        /*
         * once we have read one line, we will split it into a String Array. This array
         * will continue all the fields of the row. Please note that fields might
         * contain spaces in between. Also, few fields might be empty.
         */

        DataSet dataset = new DataSet();
        long rowIndex = 1;
        Filter filter = new Filter();
        String line;
        while ((line = reader.readLine()) != null) {
            String[] rowFields = line.split(",", headers.length);
            boolean res = false;
            ArrayList<Boolean> checks = new ArrayList<>();
            if (queryParameter.getRestrictions() == null) {
                res = true;
            } else {
                for (int i = 0; i < queryParameter.getRestrictions().size(); i++) {
                    int index = headerMap.get(queryParameter.getRestrictions().get(i).getPropertyName());
                    checks.add(filter.evaluateExpression(queryParameter.getRestrictions().get(i), rowFields[index].trim(), rowMap.get(index)));
                }
                res = solveOperators(checks, queryParameter.getLogicalOperators());
            }
            if (res) {
                Row rowMap2 = new Row();
                for (int i = 0; i < queryParameter.getFields().size(); i++) {
                    if (queryParameter.getFields().get(i).equals("*")) {
                        for (int j = 0; j < rowFields.length; j++) {
                            rowMap2.put(headers[j].trim(), rowFields[j]);
                        }
                    } else {
                        rowMap2.put(queryParameter.getFields().get(i), rowFields[headerMap.get(queryParameter.getFields().get(i))]);
                    }
                }
                dataset.put(rowIndex++, rowMap2);
            }
        }
        reader.close();

        /*
         * if there are where condition(s) in the query, test the row fields against
         * those conditions to check whether the selected row satifies the conditions
         */

        /*
         * from QueryParameter object, read one condition at a time and evaluate the
         * same. For evaluating the conditions, we will use evaluateExpressions() method
         * of Filter class. Please note that evaluation of expression will be done
         * differently based on the data type of the field. In case the query is having
         * multiple conditions, you need to evaluate the overall expression i.e. if we
         * have OR operator between two conditions, then the row will be selected if any
         * of the condition is satisfied. However, in case of AND operator, the row will
         * be selected only if both of them are satisfied.
         */

        /*
         * check for multiple conditions in where clause for eg: where salary>20000 and
         * city=Bangalore for eg: where salary>20000 or city=Bangalore and dept!=Sales
         */

        /*
         * if the overall condition expression evaluates to true, then we need to check
         * if all columns are to be selected(select *) or few columns are to be
         * selected(select col1,col2). In either of the cases, we will have to populate
         * the row map object. Row Map object is having type <String,String> to contain
         * field Index and field value for the selected fields. Once the row object is
         * populated, add it to DataSet Map Object. DataSet Map object is having type
         * <Long,Row> to hold the rowId (to be manually generated by incrementing a Long
         * variable) and it's corresponding Row Object.
         */

        /* return dataset object */
        return dataset;
    }

    private boolean solveOperators(ArrayList<Boolean> checks, List<String> logicalOperators) {

        Boolean answer;
        if (checks.size() == 1) {
            return checks.get(0);
        } else if (checks.size() == 2) {
            if (logicalOperators.get(0).matches("AND|and")) {
                answer = checks.get(0) & checks.get(1);
            } else {
                answer = checks.get(0) | checks.get(1);
            }
        } else if (checks.size() == 3) {

            int index = logicalOperators.indexOf("AND|and");
            boolean res;
            if (index < 0) {
                answer = checks.get(0) | checks.get(1) | checks.get(2);
            } else if (index == 0) {
                answer = checks.get(0) & checks.get(1) | checks.get(2);

            } else if (index == 1) {
                answer = checks.get(0) | checks.get(1) & checks.get(2);

            } else {
                answer = false;
            }
        } else {
            answer = false;
        }
        return answer;
    }

}
