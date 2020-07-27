/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package browser.termallod.core.termbase;

import browser.termallod.utils.StringMatcherUtil;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author elahi
 */
public class TermLinker {

    private final String term;
    private final String termModified;
    private final String category;
    private final String langCode;
    private Map<String, String> termLinks = new HashMap<String, String>();
    private Boolean alternativeFlag;
    private String alternativeUrl = null;
    private String url;
  

    public TermLinker(String category, String langCode, String term) {
        this.category = category;
        this.langCode = langCode;
        this.term = term;
        this.termModified = StringMatcherUtil.decripted(term);
    }

    public TermLinker(String category, String langCode, String term, String url) {
        this(category, langCode, term);
        this.url = url;
    }

    public TermLinker(String category, String langCode, String term, String url, String alternativeUrl) {
        this(category, langCode, term, url);
        this.alternativeUrl = alternativeUrl;
    }

    public TermLinker(String category, String langCode, String term, String url, String givenCategory, String givenUrl) {
        this(category, langCode, term, url, url);
        this.termLinks.put(category, url);
        this.termLinks.put(givenCategory, givenUrl);
    }

    /*public TermLinker(String categoryName,String langCode, String term,String url, String alternativeUrl) {
        this(categoryName,langCode,term);
        this.url = url;
        this.alternativeUrl = alternativeUrl;
    }*/
    public TermLinker(TermLinker termDetail, String url, String alternativeUrl) {
        this(termDetail.getCategory(), termDetail.getLangCode(), termDetail.getTerm(), url, alternativeUrl);
    }

    public String getTerm() {
        return term;
    }

    public void setAlternativeUrl(String alternativeUrl) {
        this.alternativeUrl = alternativeUrl;
    }

    public String getLangCode() {
        return langCode;
    }

    public String getCategory() {
        return category;
    }

    public String getUrl() {
        return url;
    }

    public String getAlternativeUrl() {
        return alternativeUrl;
    }

    public String getTermModified() {
        return termModified;
    }

    public String getTermLinks(String givenCategory) {
        for (String categoryName : termLinks.keySet()) {
            if (!categoryName.equals(givenCategory)) {
                return termLinks.get(categoryName);
            }
        }
        return termLinks.get(this.category);
    }

    @Override
    public String toString() {
        return "TermDetail{" + "term=" + term + ", termModified=" + termModified + ", category=" + category + ", langCode=" + langCode + ", termLinks=" + termLinks + ", alternativeFlag=" + alternativeFlag + ", alternativeUrl=" + alternativeUrl + ", url=" + url + '}';
    }

    public String getOtherCategory(String categoryType) {
        for (String categoryName : termLinks.keySet()) {
            if (!categoryName.equals(categoryType)) {
                return categoryName;
            }
        }
        return this.category;
    }
    

    public String getUrl(String otherTerminology) {
        return this.termLinks.get(otherTerminology);
    }

    public String getAlternativeUrl(String otherTerminology) {
        return StringMatcherUtil.getAlternativeUrl(this.termLinks.get(otherTerminology),true);
    }

    public String termFilter(String text) {
        if (text.contains("\"")) {
            return text.replaceAll("\"", "");
        }
        if (text.contains("\'")) {
            return text.replaceAll("\'", "");
        }
        if (text.contains("\\[")) {
            return text.replaceAll("[", "");
        }
        if (text.contains("\\]")) {
            return text.replaceAll("]", "");
        }
        if (text.contains("\\,")) {
            return text.replaceAll(",", "");
        }
        if (text.contains("_")) {
            return text.replaceAll("_", " ");
        }
        if (text.contains(".")) {
            return text.replaceAll(".", " ");
        }

        return text;
    }

    public Map<String, String> getTermLinks() {
        return termLinks;
    }

}
