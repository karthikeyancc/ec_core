/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.core.tools;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays;

/**
 *
 * @author karthikeyan
 */
public class HtmlGenerator {
/**
Sample input file 
--------------------
Name,text,cand.name,true,,,,10,karthikeyan
DOB,date,cand.dob,true,,,,,,
Mobile,number,cand.mobile,,,,,, 
Email,email,cand.email,,,,,, 
Profile,textarea,cand.profile,,,,,, 
Hobbies,select,cand.hobby,,,,,,
Submit,button,cand.submit,,,,,,Submit

 */
    private static final int LABEL_INDEX = 0;
    private static final int TYPE_INDEX = LABEL_INDEX + 1;
    private static final int NAME_INDEX = TYPE_INDEX + 1;
    private static final int REQUIRED_INDEX = NAME_INDEX + 1;
    private static final int MINVAL_INDEX = REQUIRED_INDEX + 1;
    private static final int MAXVAL_INDEX = MINVAL_INDEX + 1;
    private static final int LENGTH_INDEX = MAXVAL_INDEX + 1;
    private static final int VALUE_INDEX = LENGTH_INDEX + 1;

    public static void main(String[] ar) {
        if (ar == null || ar.length == 0) {
            ar = new String[]{"/tmp/t.txt"};
        }
        try (BufferedReader fr = new BufferedReader(new FileReader(ar[0]))) {
            String line = null;
            StringBuilder sb = new StringBuilder();
            //label,text,name,required,min,max
            while ((line = fr.readLine()) != null) {
                sb.setLength(0);
                String words[] = line.split(",");
                if (words[TYPE_INDEX] == null || words[TYPE_INDEX].trim().length() < 1) {
                    words[TYPE_INDEX] = "text";
                }
                sb.append("<div class=\"form-group\">\n\t<label for=\"")
                        .append(words[NAME_INDEX]).append("\">")
                        .append(words[LABEL_INDEX]).append("</label>\n\t");
                String optionals = "";
                if ("text".equalsIgnoreCase(words[TYPE_INDEX])
                        || "number".equalsIgnoreCase(words[TYPE_INDEX])
                        || "email".equalsIgnoreCase(words[TYPE_INDEX])) {
                    sb.append("<input class=\"form-control\"  ");
                    appendOptionalAttributes(sb, "type", words, TYPE_INDEX);
                    addOptionals(sb, words);
                    sb.append("/>");
                } else if ("textarea".equalsIgnoreCase(words[TYPE_INDEX])) {
                    sb.append("<textarea class=\"form-control\" ");
                    addOptionals(sb, words);
                    sb.append("></textarea>");
                } else if ("select".equalsIgnoreCase(words[TYPE_INDEX])) {
                    sb.append("<select class=\"form-control\" ");
                    addOptionals(sb, words);
                    sb.append("></select>");
                } else if ("button".equalsIgnoreCase(words[TYPE_INDEX])) {
                    sb.append("<button class=\"form-control\" ");
                    addOptionals(sb, words);
                    sb.append("></button>");
                } else if ("date".equalsIgnoreCase(words[TYPE_INDEX])) {
                    String nn[] = words[NAME_INDEX].split("\\.");
                    sb.append("<div>\n"
                            + "        <p class=\"input-group\">\n"
                            + "          <input class=\"form-control\" type=\"text\" \n"
                            + "          uib-datepicker-popup=\"dd-MM-yyyy\" \n"
                            + "          ng-model=\"" + words[NAME_INDEX] + "\" is-open=\"" + nn[1] + "popup.opened\"\n"
                            + "          datepicker-options=\"" + nn[1] + "Options\"\n"
                            + "          ng-required=\"true\" close-text=\"Close\"  />\n"
                            + "          <span class=\"input-group-btn\">\n"
                            + "            <button type=\"button\" class=\"btn btn-default\" \n"
                            + "            ng-click=\"open" + nn[1] + "()\"><i class=\"glyphicon glyphicon-calendar\"></i></button>\n"
                            + "          </span> \n</p>\n </div>\n<script>\n"
                            + "             $scope." + nn[1] + "Options = {\n"
                            + "            showWeeks: false,\n"
                            + "            startingDay: 1\n"
                            + "        };\n"
                            + "        $scope.open" + nn[1] + " = function () {\n"
                            + "            $scope." + nn[1] + "popup.opened = true;\n"
                            + "        };\n"
                            + "        $scope." + nn[1] + "popup = {\n"
                            + "            opened: false\n"
                            + "        };\n"
                            + "        $scope.set" + nn[1] + " = function (year, month, day) {\n"
                            + "            $scope."+words[NAME_INDEX]+" = new Date(year, month, day);\n"
                            + "        };\n"
                            + "        </script>");
                }

                sb.append("\n</div>");
                System.out.println(sb.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void addOptionals(StringBuilder sb, String[] words) {
        appendOptionalAttributes(sb, "ng-model", words, NAME_INDEX);
        appendOptionalAttributes(sb, "ng-required", words, REQUIRED_INDEX);
        appendOptionalAttributes(sb, "min", words, MINVAL_INDEX);
        appendOptionalAttributes(sb, "max", words, MAXVAL_INDEX);
        appendOptionalAttributes(sb, "maxlength", words, LENGTH_INDEX);
        appendOptionalAttributes(sb, "ng-value", words, VALUE_INDEX);
    }

    private static void appendOptionalAttributes(StringBuilder sb, String attribute, String[] words, int index) {
        if (words.length>index&&words[index] != null && words[index].trim().length() > 1) {
            sb.append(' ').append(attribute).append("=\"").append(words[index]).append("\" ");
        }

    }
}
