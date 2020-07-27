/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package browser.termallod.core.html;

import browser.termallod.process.RetrieveAlphabetInfo;
import browser.termallod.core.AlphabetTermPage;
import browser.termallod.core.PageContentGenerator;
import browser.termallod.core.html.HtmlReaderWriter;
import browser.termallod.core.html.HtmlParameter;
import browser.termallod.core.termbase.TermLinker;
import browser.termallod.utils.FileRelatedUtils;
import browser.termallod.utils.Partition;
import browser.termallod.core.termbase.TermDetailNew;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import org.jsoup.nodes.Document;

/**
 *
 * @author Mohammad Fazleh Elahi
 */
public class HtmlCreator {

    private final Set<String> languages;
    private String OUTPUT_PATH;
    private String TEMPLATE_LOCATION;
    private String categoryName;

    public HtmlCreator(String INPUT_PATH, Set<String> languages, String TEMPLATE_LOCATION, String OUTPUT_PATH, String categoryName) throws Exception {
        this.languages = languages;
        this.TEMPLATE_LOCATION = TEMPLATE_LOCATION;
        this.OUTPUT_PATH = OUTPUT_PATH;
        this.categoryName = categoryName;
        this.getInfoFromSavedFiles(INPUT_PATH);
    }

    public void getInfoFromSavedFiles(String INPUT_PATH) throws Exception {
        TreeMap<String, RetrieveAlphabetInfo> langSortedTerms = new TreeMap<String, RetrieveAlphabetInfo>();
        /*String langCode = null;
        if (languages.contains("en")) {
            langCode = "en";
        } else {
            langCode = languages.iterator().next();
        }*/

        for (String langCode : languages) {
            RetrieveAlphabetInfo retrieveAlphabetInfo = new RetrieveAlphabetInfo(INPUT_PATH, langCode);
            langSortedTerms.put(langCode, retrieveAlphabetInfo);
            //System.out.println("terms:"+retrieveAlphabetInfo.getLangSortedTerms().get("G_H"));
        }

        createHtmlForEachLanguage(langSortedTerms);
    }

    private void createHtmlForEachLanguage(TreeMap<String, RetrieveAlphabetInfo> langSortedTerms) throws Exception {
        PageContentGenerator pageContentGenerator = new PageContentGenerator(langSortedTerms);
        for (String language : pageContentGenerator.getLanguages()) {
            File LIST_OF_Terms = getTemplate(language, ".html");
            List<AlphabetTermPage> alphabetTermPageList = pageContentGenerator.getLangPages(language);
            for (AlphabetTermPage alphabetTermPage : alphabetTermPageList) {
                createHtmlForEachAlphabetPair( LIST_OF_Terms, language, alphabetTermPage, pageContentGenerator);
                //Alpahbet pair pages
                //break;
            }
            //language pages..
            //break;
        }
    }

    private void createHtmlForEachAlphabetPair( File templateFile, String language, AlphabetTermPage alphabetTermPage, PageContentGenerator pageContentGenerator) throws Exception {
        Partition partition = alphabetTermPage.getPartition();

        for (Integer page = 0; page < partition.size(); page++) {
            Integer currentPageNumber = page + 1;
            List<String> terms = partition.get(page);
            HtmlReaderWriter htmlReaderWriter = new HtmlReaderWriter(templateFile);
            Document templateHtml = htmlReaderWriter.getInputDocument();
            HtmlParameter info = new HtmlParameter(language, categoryName, alphabetTermPage);
            HtmlListOfTerms htmlPage = new HtmlListOfTerms(info, htmlReaderWriter,templateHtml);
            File outputFileName = new File(OUTPUT_PATH + info.creatHtmlFileName(currentPageNumber, alphabetTermPage));
            String htmlFileName = outputFileName.getName();
            Document listOfTermHtmlPage = htmlPage.createAllElements(templateHtml,terms, pageContentGenerator, htmlFileName, currentPageNumber);
            htmlReaderWriter.writeHtml(listOfTermHtmlPage, outputFileName);
            //System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"+htmlFileName);
            //page indexes..number of pages of same alphabet..
            break;
        }

    }

    /*private List<TermDetailNew> getTermDetails(String category, String language, List<String> terms) {
        List<TermDetailNew> termDetails = new ArrayList<TermDetailNew>();
        String browserName = FileRelatedUtils.getBrowser(category);
        for (String term : terms) {
            System.out.println(term);
            TermDetailNew termDetail = new TermDetailNew(browserName, language, term);
            termDetails.add(termDetail);
        }
        return termDetails;
    }*/

    private File getTemplate(String langCode, String extension) throws Exception {
        return new File(TEMPLATE_LOCATION + categoryName + extension);
    }

}
