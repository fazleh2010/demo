/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package browser.termallod.core.html;

import static browser.termallod.core.Parameter.ListOfTerms;
import browser.termallod.core.language.AlphabetFilesReader;
import browser.termallod.core.language.LangPairManager;
import browser.termallod.utils.Partition;
import browser.termallod.core.termbase.TermDetail;
import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import org.jsoup.nodes.Document;

/**
 *
 * @author Mohammad Fazleh Elahi
 */
public class HtmlCreator {

    private final String OUTPUT_PATH;
    private final String TEMPLATE_LOCATION;

    public HtmlCreator(String TEMPLATE_LOCATION, String OUTPUT_PATH) {
        this.TEMPLATE_LOCATION = TEMPLATE_LOCATION;
        this.OUTPUT_PATH = OUTPUT_PATH;

    }

    public void createListOfTermHtmlPage(String INPUT_PATH, Set<String> languages, String categoryName, Boolean pairFlag, String pair, Integer pageNUmber) throws Exception {
        TreeMap<String, AlphabetFilesReader> langSortedTerms = new TreeMap<String, AlphabetFilesReader>();
        /*String langCode = null;
        if (languages.contains("en")) {
            langCode = "en";
        } else {
            langCode = languages.iterator().next();
        }*/

        for (String langCode : languages) {
            AlphabetFilesReader retrieveAlphabetInfo = new AlphabetFilesReader(INPUT_PATH, langCode, pairFlag);
            langSortedTerms.put(langCode, retrieveAlphabetInfo);
        }

        createHtmlForEachLanguage(langSortedTerms, categoryName, languages, pair, pageNUmber);
    }

    public void createHtmlTermPage(TermDetail termDetail, String categoryName) throws Exception {
        File templateFile = getTemplate(categoryName, ".html");
        HtmlReaderWriter htmlReaderWriter = new HtmlReaderWriter(templateFile);
        Document templateHtml = htmlReaderWriter.getInputDocument();
        HtmlTermDetail htmlTermDetail = new HtmlTermDetail(termDetail, templateHtml);
        File outputFileName = new File(OUTPUT_PATH + categoryName + ".html");
        Document termPage = htmlTermDetail.getOutputHtml();
        htmlReaderWriter.writeHtml(termPage, outputFileName);
    }

    private void createHtmlForEachLanguage(TreeMap<String, AlphabetFilesReader> langSortedTerms, String categoryName, Set<String> languages, String pair, Integer pageNUmber) throws Exception {
        HtmlPageContentGen pageContentGenerator = new HtmlPageContentGen(langSortedTerms);
        for (String language : pageContentGenerator.getLanguages()) {
            File LIST_OF_Terms = getTemplate(ListOfTerms, ".html");
            List<LangPairManager> alphabetTermPageList = pageContentGenerator.getLangPages(language);
            for (LangPairManager alphabetTermPage : alphabetTermPageList) {
                if (pair.contains("all")) {
                    createHtmlForEachAlphabetPair(LIST_OF_Terms, language, alphabetTermPage, pageContentGenerator, categoryName, languages, pageNUmber);
                } else if (alphabetTermPage.getAlpahbetPair().contains(pair)) {
                    createHtmlForEachAlphabetPair(LIST_OF_Terms, language, alphabetTermPage, pageContentGenerator, categoryName, languages, pageNUmber);
                    break;
                }
                //Alpahbet pair pages
                //break;
            }
            //language pages..
            //break;
        }
    }

    private void createHtmlForEachAlphabetPair(File templateFile, String language, LangPairManager alphabetTermPage, HtmlPageContentGen pageContentGenerator, String categoryName, Set<String> languages, Integer pageNUmber) throws Exception {
        Partition partition = alphabetTermPage.getPartition();

        for (Integer page = 0; page < partition.size(); page++) {
            Integer currentPageNumber = page + 1;
            if (currentPageNumber == pageNUmber) {
                List<String> terms = partition.get(page);
                HtmlReaderWriter htmlReaderWriter = new HtmlReaderWriter(templateFile);
                Document templateHtml = htmlReaderWriter.getInputDocument();
                HtmlParameter info = new HtmlParameter(language, categoryName, alphabetTermPage);
                HtmlListOfTerms htmlPage = new HtmlListOfTerms(info, htmlReaderWriter, templateHtml);
                File outputFileName = new File(OUTPUT_PATH + info.creatHtmlFileName(currentPageNumber, alphabetTermPage));
                String htmlFileName = outputFileName.getName();
                Document listOfTermHtmlPage = htmlPage.createAllElements(templateHtml, terms, pageContentGenerator, htmlFileName, currentPageNumber, language, languages);
                htmlReaderWriter.writeHtml(listOfTermHtmlPage, outputFileName);
                //page indexes..number of pages of same alphabet..

                break;
            }
        }

    }

    private File getTemplate(String categoryName, String extension) throws Exception {
        return new File(TEMPLATE_LOCATION + categoryName + extension);
    }

}
