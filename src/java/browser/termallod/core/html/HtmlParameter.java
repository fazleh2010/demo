/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package browser.termallod.core.html;


import browser.termallod.core.language.LangPairManager;
import java.io.File;
import java.util.List;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import browser.termallod.constants.HtmlConts;

/**
 *
 * @author elahi
 */
public class HtmlParameter implements HtmlConts{

    private String language;
    private String ontologyFileName;
    private String categoryType;
    private LangPairManager alphabetTermPage;

    public HtmlParameter(String language, String categoryName, LangPairManager alphabetTermPage) {
        this.language = language;
        this.ontologyFileName = categoryName;
        this.categoryType =ontologyFileName;
        this.alphabetTermPage = alphabetTermPage;

    }

    public String getLanguage() {
        return language;
    }

    public String getOntologyFileName() {
        return ontologyFileName;
    }

    public String getCategoryType() {
        return categoryType;
    }

    public String createFileNameUnicode(Integer pageNumber,LangPairManager alphabetTermPage) {
        String pair = getPairValue(alphabetTermPage);
        return browser + UNDERSCORE + this.language + UNDERSCORE + pair.toString() + UNDERSCORE + pageNumber + HTML_EXTENSION;
    }

    public Document getTermPageTemplate(String templateLocation, String extension) {
        File templateFile = new File(templateLocation + this.ontologyFileName + "_" + language + "_" + "term" + extension);
        HtmlReaderWriter htmlReaderWriter = new HtmlReaderWriter(templateFile);
        return htmlReaderWriter.getInputDocument();

    }

    public LangPairManager getAlphabetTermPage() {
        return alphabetTermPage;
    }

    public String getPairValue(LangPairManager alphabetTermPage) {
        String pair;
        if (this.language.equals("en")) {
            pair = alphabetTermPage.getAlpahbetPair();
        } else {
            pair = alphabetTermPage.getNumericalValueOfPair().toString();
        }
        return pair;
    }

    private String termFileExtension(String term) {
        return term.trim().replace(" ", "+") + "-" + this.language.toUpperCase() + HTML_EXTENSION;
    }

    public String generateTermFileName() {
        String outputfileString = this.ontologyFileName + "/" + "data" + "/" + this.categoryType + "/";
        return outputfileString;
    }

    public String createFileNameWithPairNumber(Integer pageNumber) {
        String pair = this.getPairValue(alphabetTermPage);
        return browser + UNDERSCORE + this.language + UNDERSCORE + pair.toString() + UNDERSCORE + pageNumber + HTML_EXTENSION;
    }

    /*public File makeHtmlFileName(String base_path, Integer currentPageNumber,AlphabetTermPage alphabetTermPage) {
        return new File(base_path + this.ontologyFileName + "/" + this.createFileNameUnicode(currentPageNumber,alphabetTermPage));
    }*/
    public String creatHtmlFileName(Integer currentPageNumber,LangPairManager alphabetTermPage) {
        return this.createFileNameUnicode(currentPageNumber,alphabetTermPage);
    }

}
