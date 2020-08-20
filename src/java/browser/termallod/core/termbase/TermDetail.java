/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package browser.termallod.core.termbase;

import browser.termallod.utils.JsonParser;
import browser.termallod.utils.StringMatcher;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author elahi
 */
public class TermDetail {

    public static String LANGUAGE_SEPERATE_SYMBOLE = "@";
    public static String HASH_SYMBOLE = "#";
    private String termOrg = "";
    private String termDecrpt = "";
    private String termUrl = "";
    private TermSubjectInfo subject = new TermSubjectInfo();
    private String reliabilityCode = "";
    private String administrativeStatus = "";
    private String POST = "";
    private String Number = "";
    private String Gender = "";
    private String Definition = "";
    private String Hypernym = "";
    private String Hyponym = "";
    private String Variant = "";
    private String Synonym = "";
    private String language = "";
    private Map<String,String> termLinks = new HashMap<String,String>();

    public TermDetail(String subject, String predicate, String object, Boolean flag) {
        if (flag) {
            this.termUrl = makeTermUrl(subject);
            this.termOrg =StringMatcher.encripted(object.trim());
            this.termDecrpt = StringMatcher.decripted(termOrg.trim());
            this.language = this.setLanguage(this.termUrl);

        } else {
            this.termUrl = this.makeTermUrl(subject);
            this.setTermAndLanguage(object);
        }
    }
    
    public TermDetail(JsonParser jsonParser) {
            this.termUrl = jsonParser.getUrl();
            this.termDecrpt = jsonParser.getTerm().trim();
            this.termOrg =StringMatcher.encripted(this.termDecrpt).trim();
            this.language = this.setLanguage(this.termUrl);
    }


    public TermDetail() {

    }

    public TermDetail(String line) {
        String[] info = line.split("=");
        this.termOrg = info[0].toLowerCase().trim();
        this.termDecrpt = StringMatcher.decripted(termOrg).trim();
        this.termUrl = info[1];
        this.language= info[2].toLowerCase().trim();
    }
    
    public TermDetail(String line,Map<String,String> termLinks) {
        String[] info = line.split("=");
        this.termOrg = info[0].toLowerCase().trim();
        this.termDecrpt = StringMatcher.decripted(termOrg).trim();
        this.termUrl = info[1];
        this.language= info[2].toLowerCase().trim();
        this.termLinks=termLinks;
    }

    public TermDetail(String term, String url) {
        this.termOrg=term;
        this.termOrg = termOrg.replaceAll("\\s","_");
        this.termDecrpt=termOrg.replaceAll("_","\\s");
        this.termUrl = url;
        this.language = this.setLanguage(this.termUrl);
    }
    
    public TermDetail(String term, String termUrl, String terminologyName,String otherTermUrl) {
        this(term, termUrl);
        this.termLinks.put(terminologyName,otherTermUrl);
    }


    public String getTermOrg() {
        return termOrg;
    }

    public String getTermDecrpt() {
        return StringMatcher.decripted(termOrg);
    }

    public String getTermUrl() {
        return termUrl;
    }

    public String getSubjectDescription() {
        return this.subject.getSubjectDescription();
    }

    public String getReliabilityCode() {
        return reliabilityCode;
    }

    public String getAdministrativeStatus() {
        return administrativeStatus;
    }

    public String getReferenceID() {
        return this.subject.getReferenceID();
    }

    public String getSubjectId() {
        return this.subject.getSubjectId();
    }

    public TermSubjectInfo getSubject() {
        return subject;
    }

    public String getPOST() {
        return POST;
    }

    public String getNumber() {
        return Number;
    }

    public String getGender() {
        return Gender;
    }

    public String getDefinition() {
        return Definition;
    }

    public String getHypernym() {
        return Hypernym;
    }

    public String getHyponym() {
        return Hyponym;
    }

    public String getVariant() {
        return Variant;
    }

    public String getSynonym() {
        return Synonym;
    }
    
    private String findTermUrl(String subject) {
        boolean isSubjectFound = subject.toString().indexOf(HASH_SYMBOLE) != -1 ? true : false;
        if (isSubjectFound) {
            String[] info = subject.toString().split(HASH_SYMBOLE);
            return info[0];
        }
        return null;
    }

    public String getLanguage() {
        return language;
    }

    private String makeTermUrl(String subject) {
        //System.out.println(subject);
        boolean isSubjectFound = subject.toString().indexOf(HASH_SYMBOLE) != -1 ? true : false;
        if (isSubjectFound) {
            String[] info = subject.toString().split(HASH_SYMBOLE);
            return info[0];
        }
        return subject;
    }

    private void setTermAndLanguage(String object) {
        boolean isObjectFound = object.toString().indexOf(LANGUAGE_SEPERATE_SYMBOLE) != -1 ? true : false;
        if (isObjectFound) {
            String[] info = object.toString().split(LANGUAGE_SEPERATE_SYMBOLE);
            this.termOrg = info[0].toLowerCase().trim();
            this.termDecrpt = StringMatcher.decripted(termOrg);
            this.language = info[1].toLowerCase().trim();
        }
    }

    @Override
    public String toString() {
        return "TermInfo{" + "language=" + language + ",termOrg=" + termOrg + ", termDecrpt=" + termDecrpt + ", termUrl=" + termUrl
                +", subject=" + subject
                + ", reliabilityCode=" + reliabilityCode + ", administrativeStatus=" + administrativeStatus + ", POST=" + POST
                + ", Number=" + Number + ", Gender=" + Gender + ", Definition=" + Definition
                + ", Hypernym=" + Hypernym + ", Hyponym=" + Hyponym + ", Variant=" + Variant
                + ", links=" + this.termLinks.toString()
                + ", Synonym=" + Synonym + '}';
    }

    public String setLanguage(String subject) {
        return StringMatcher.getLanguage(subject);
    }

    public Map<String, String> getTermLinks() {
        return termLinks;
    }

    public void setTermLinks(Map<String, String> termLinks) {
        this.termLinks = termLinks;
    }
    
    public void setLang(String subject) {
        this.language= StringMatcher.getLastString(subject,'/', Boolean.TRUE);
    }

    public void setTerm(String object) {
        this.termOrg = StringMatcher.encripted(object.trim());
        this.termDecrpt = StringMatcher.decripted(termOrg.trim());
    }

    public void setUrl(String textContent) {
        this.termUrl=textContent;
    }

    public void setReliabilityCode(String textContent) {
        this.reliabilityCode=textContent;
    }

    public void setAdministrativeStatus(String textContent) {
        this.administrativeStatus= StringMatcher.getLastString(textContent, '#',Boolean.FALSE);
    }

    public void setSubject(TermSubjectInfo subjectInfo) {
        this.subject=subjectInfo;
    }

}
