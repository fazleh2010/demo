/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package browser.termallod.utils;

import static browser.termallod.core.termbase.TermDetail.HASH_SYMBOLE;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author elahi
 */
public class StringMatcherUtil2 {

    private static Integer orginalIndex = 0;
    private static Integer alternativeIndex = 1;

    public static void main(String args[]) {
        String language = getLanguage("http://webtentacle1.techfak.uni-bielefeld.de/tbx2rdf_atc/data/atc/fatigue-EN");
        System.out.println(language);
        String terminologyName=getTerminologyName("http://webtentacle1.techfak.uni-bielefeld.de/tbx2rdf_intaglio/data/intaglio/hole-EN");
        System.out.println(terminologyName);
        String searchTerm="termPage?term="+StringMatcherUtil.encripted("anderson,_b.l.")+"&lang="+"en";
        System.out.println(searchTerm);
    }

    public static String encripted(String term) {
        return term.replaceAll("\\s+", "_");
    }

    public static String decripted(String term) {
        return term.replaceAll("\\_", " ");
    }

    public static String getUrl(String checkField) {
        checkField = checkField.substring(0, checkField.lastIndexOf('#'));
        return checkField;
    }

    public static String getAlternativeUrl(String value, Boolean alternativeUrlFlag) {
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

    public static String getLanguage(String subject) {
        String language = null;
        int index = subject.lastIndexOf('/');
        if(index<1)
            return null;
        String lastString = subject.substring(index + 1);

        boolean isSubjectFound = lastString.indexOf("-") != -1 ? true : false;
        if (isSubjectFound) {
            String[] info = lastString.split("-");
            language = info[info.length - 1].toLowerCase();
        }
        else if(lastString.indexOf("#") != -1)
            return null;
        else
           language = lastString;
            
        return language.toLowerCase();
    }

    public static String getTerminologyName(String url) {
        int index = url.lastIndexOf('/');
        String lastString = url.substring(index + 1);
        try {
            url = new URI(url).getPath().replace(lastString, "");
            String info[] = url.split("/");
            return info[info.length - 1];
        } catch (URISyntaxException ex) {
            Logger.getLogger(StringMatcherUtil2.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

}
