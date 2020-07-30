/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package browser.termallod.utils;

import browser.termallod.api.IATE;
import java.net.URI;

/**
 *
 * @author elahi
 */
public class StringMatcherUtil implements IATE {
    private  static Integer orginalIndex=0;
    private  static Integer alternativeIndex=1;

    public static String encripted(String term) {
        return term.replaceAll("\\s+", "_");
    }

    public static String decripted(String term) {
        return term.replaceAll("\\_", " ");
    }

    public static String getLanguage(String checkField) throws Exception {
        checkField = checkField.replace(HTTP_IATE, "");
        String idStr = checkField.substring(checkField.lastIndexOf('-') + 1);
        return idStr;
    }

    public static String modifyId(String id) {
        id = id.replace(HTTP_IATE, "");
        return id;
    }

    public static String modifyUrl(String url) {
        url = url.replace(HTTP_IATE, "");
        return url;
    }

    public static String modifySubject(String checkField) {
        return checkField = checkField.replace(HTTP_SUBJECT_FIELD, "");
    }

    public static String modifyReliabiltyCode(String checkField) {
        return checkField = checkField.replace(HTTP_RELIABILTY_CODE, "");
    }

    public static String modifyAdministrativeStatus(String checkField) {
        checkField = checkField.substring(checkField.lastIndexOf('#') + 1);
        return checkField = checkField.replace(HTTP_ADMINISTRATIVE_STATUS, "");
    }
    
     public static String getUrl(String checkField) {
        checkField = checkField.substring(0,checkField.lastIndexOf('#'));
        return checkField;
    }
     
     public static  String getAlternativeUrl(String value,Boolean alternativeUrlFlag) {
        if (value.contains("=")) {
                String[] urls = value.split("=");
                String orgUrl = urls[orginalIndex];
                String alterUrl = urls[alternativeIndex];
                if (alternativeUrlFlag) {
                    value = alterUrl;
                } else {
                    value = orgUrl;
                }
            }
        return value;
    }
     

}
