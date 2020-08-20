/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package browser.termallod.core.html;

import java.util.ArrayList;
import java.util.List;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import browser.termallod.core.LangPairManager;
import browser.termallod.core.html.HtmlReaderWriter;
import browser.termallod.core.html.HtmlParameter;
import browser.termallod.utils.StringMatcher;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import browser.termallod.constants.HtmlConts;
import browser.termallod.constants.LanguageDetail;

/**
 *
 * @author elahi //this is super dirty and horrible codes ever!!
 */
public class HtmlListOfTerms implements  LanguageDetail, HtmlConts {

    public static Map<String, String> termAlterUrl = new TreeMap<String, String>();
    private Integer maximumNumberOfPages = 4;
    private HtmlParameter info;
    private Document templateHtml;
    private Document termPageTemplate;
    private Set<String> allLanguages=new HashSet<String>();

    public HtmlListOfTerms(HtmlParameter info, HtmlReaderWriter htmlReaderWriter, Document templateHtml) throws Exception {
        this.info = info;
        //this.termPageTemplate = termPageTemplate;
        //this.termPage = new HtmlTermPage(info, htmlReaderWriter, termPageTemplate);
    }

    public Document createAllElements(Document templateHtml, List<String> terms, HtmlPageContentGen pageContentGenerator, String htmlFileName, Integer currentPageNumber, String lang,Set<String> lanCodes) throws Exception {
        LangPairManager alphabetTermPage = info.getAlphabetTermPage();
        Element body = templateHtml.body();
        String alphebetPair = alphabetTermPage.getAlpahbetPair();
        Integer numberofPages = alphabetTermPage.getNumberOfPages();
        //currently not
        Integer emptyTerm = alphabetTermPage.getEmptyTerm();
        //this part of code is used to automatically generated language selection box
        //currently it is hard coded in HTML template
        this.createLangSelectBox(body,lanCodes );
       

        createAlphabet(body, alphebetPair, pageContentGenerator);
        createTerms(body, terms, alphebetPair, emptyTerm, htmlFileName, lang);

        /*Element divCurrentPageUpper = body.getElementsByClass("activepageUpper").get(0);
        this.assignCurrentPageNumber(divCurrentPageUpper);
         Element divCurrentPageLower = body.getElementsByClass("activepageLower");
        this.assignCurrentPageNumber(divCurrentPageLower);*/
        //createUperPageNumber(body, alphebetPair, numberofPages);
        //upper page number
        createPageNumber(body, "paging_links inner", alphebetPair, numberofPages, currentPageNumber);
        //lower page number
        createPageNumber(body, "paging_links inner_down", alphebetPair, numberofPages, currentPageNumber);
        return templateHtml;
    }
    
    
    private void createLangSelectBox(Element body,Set<String> lanCodes) throws Exception {
        //System.out.println("!!!!!!!!!!!!!!!!!!!!!language!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        Element divLanguage = body.getElementsByClass("langauge_selection_box").get(0);
        String url=null,str="";
        for (String languageCode :lanCodes) {
            if (languageMapper.containsKey(languageCode)) {
                String languageDetail = languageMapper.get(languageCode);
                if(languageCode.contains("en"))
                  url = "browser_"+languageCode+"_A_B_1.html"; 
                else
                  url = "browser_"+languageCode+"_1_1.html";
               
                String line = "<td><a href=\"listOfTerms?page="+url+"\""+ " " + "style=\"color:#00008B\";"+ " " +">"+languageDetail+"</a></td>";
                str += line;
            }
        }
        String table ="<table class=\"panel-body rdf_embedded_table\" id=\"langDetail\">"
                      +"<tbody>"+"<tr>"+str+"</tr>"+" </tbody>"+"</table>";
        //System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!\n"+table);
        divLanguage.append(table);
    }

    /*private void createLangSelectBox(Element body, PageContentGenerator pageContentGenerator, AlphabetTermPage alphabetTermPage) throws Exception {
        Element divLanguage = body.getElementsByClass("langauge selection box").get(0);
        String options = "<ul class=" + "\"" + "language-list" + "\"" + ">";
        for (String languageCode : pageContentGenerator.getLanguages()) {
            if (languageMapper.containsKey(languageCode)) {
                String languageDetail = languageMapper.get(languageCode);
                String pair = pageContentGenerator.getLanguageInitpage(languageCode);
                String url = this.createUrlLink(INITIAL_PAGE, alphabetTermPage);
                String option = "<li>&#8227; <a href=" + "\"" + url + "\"" + ">" + languageDetail + "</a></li>";
                options += option;
            }
        }
        options = options + "</ul>";
        String form = "<form>" + options + "</form>";
        divLanguage.append(form);
    }*/

    private String createAlphabet(Element body, String alphebetPair, HtmlPageContentGen pageContentGenerator) throws Exception {
        Element divAlphabet = body.getElementsByClass("currentpage").get(0);
        String span="<span>" + alphebetPair + "</span>";
        divAlphabet.append(span);
        List<LangPairManager> alphabetPairs = pageContentGenerator.getLangPages(info.getLanguage());
        for (LangPairManager alphabetTermPage : alphabetPairs) {
            if (!alphabetTermPage.getAlpahbetPair().contains(alphebetPair)) {
                String li = getAlphebetLi(INITIAL_PAGE, alphabetTermPage);
                divAlphabet.append(li);
            }
        }
        return alphebetPair;
    }

    private void createPageNumber(Element body, String elementName, String alphebetPair, Integer numberofPages, Integer currentPageNumber) {
        Element divPage = body.getElementsByClass(elementName).get(0);
        List<String> liS = getPageLi(alphebetPair, numberofPages, info.getAlphabetTermPage(), currentPageNumber);
        if (liS.isEmpty()) {
            return;
        }
        for (String li : liS) {
            divPage.append(li);
        }
    }

    public void createTerms(Element body, List<String> terms, String alphebetPair, Integer emptyTerm, String htmlFileName, String lang) throws Exception {
        //Element divTerm = body.getElementsByClass("result-list1 wordlist-oxford3000 list-plain").get(0);
        Element divTerm = body.getElementsByClass("termUrlList").get(0);
        Integer index = 0;
        for (String term : terms) {
            //System.out.println(term);
            /*TermDetail newTermDetail = termPage.createTerms(termDetail, index++, htmlFileName);
            String liString = termPage.getTermLi(newTermDetail);
            if (termPage.isReliabilityFlag()) {
                divTerm.append(liString);
            }*/
            String url = info.getAlphabetTermPage().getProps().getProperty(term);
            String liString = getTermLi(term, url, lang);
            divTerm.append(liString);
        }
    }

    public String getTermLi(String term, String url, String lang) {

        //System.out.println("Term Original:"+term);
        String searchTerm=null;
        String termPresent = StringMatcher.decripted(term).toLowerCase().trim();
        //term = term.toLowerCase().trim();
        // /api?paramA=valueA&paramB=valueB
        //System.out.println("Term decripted:"+term);
        //String searchTerm = "termPage?term="+StringMatcherUtil.encripted(term) + "&" + "url="+url+ "&" +"lang="+lang;
        //searchTerm="term="+StringMatcherUtil.encripted(term) + "&" + "paramB="+url+"lang="+lang;
        //String searchTerm="termPage?term="+StringMatcherUtil.encripted(term)+"&url="+url+"&lang="+lang;
        //&latitude=12.12
        //searchTerm="termPage?term="+StringMatcherUtil.encripted(term)+"&lang="+lang;
        //System.out.println("original term:"+term);
        //System.out.println("termPresent:"+termPresent);
                
        searchTerm= "termPage?term="+"{\"term\":\""+StringMatcher.encripted(term)+"\","
                                    + "\"iri\":\""+url+"\","
                                    + "\"lang\":\""+lang+"\"}";
        /*searchTerm="termPage?term="
                   +"{\"term\":\""+StringMatcherUtil.encripted(term)
                   +"{\"url\":\""+url
                   +"\",\"lang\":\""+lang+"\"}";*/
        String title = "url=" + '"' + url + " definition" + '"';
        String a = "<a href=" + searchTerm + " " + title + " " + "class=\"software\""+ " " + ">" + termPresent + "</a>";
        String li = "\n<li>" + a + "</li>\n";
        return li;
    }

    /*public String getTermLi(String term,String url) {
        //System.out.println("Term Original:"+term);
        term=StringMatcherUtil.decripted(term);
        term=term.toLowerCase().trim();
        //System.out.println("Term decripted:"+term);
        url="termPage?page="+url;
        String title = "title=" + '"' + term + " definition" + '"';
        String a = "<a href=" + url + " " + title + ">" + term + "</a>";
        String li = "\n<li>" + a + "</li>\n";
        return li;
    }*/
    private String getAlphebetLi(Integer pageNumber, LangPairManager alphabetTermPage) {
        String url = this.createUrlLink(pageNumber, alphabetTermPage);
        //String url = LOCALHOST_URL_LIST_OF_TERMS_PAGE + alphabetFileName;
        url = "listOfTerms?page=" + url;

        if (info.getLanguage().contains("hu") && alphabetTermPage.getNumericalValueOfPair() == 1) {
            url = "browser_hu_A_1_1.html";
        }

        String a = "<a href=" + url + " " + "class=\"alphabet\""+ " " + ">" + alphabetTermPage.getAlpahbetPair() + "</a>";
        String li = "\n" + "<li>" + a + "</li>" + "\n";
        return li;
    }

    private List<String> getPageLi(String pair, Integer pages, LangPairManager alphabetTermPage, Integer currentPageNumber) {
        //Elements divAlphabet = body.getElementsByClass("side-selector__left");
        //Element content = body.getElementById("entries-selector");
        List<String> liS = new ArrayList<String>();
        String pageUrl = null;
        String li = "";
        String incrementPage="pageNumber?page=";
        String id= " " + "class=\"pageNumber\""+ " " ;
      
        if (pages == 1) {
            return new ArrayList<String>();
        }
        if (currentPageNumber > INITIAL_PAGE) {
            pageUrl = incrementPage+createUrlLink(currentPageNumber - 1, alphabetTermPage);
            if (info.getLanguage().contains("hu") && currentPageNumber == 2) {
                pageUrl = incrementPage+"browser_hu_A_1_1.html";
            }
            String a = "<a href=" + pageUrl +  id+">" + "Previous" + "</a>";
            li = "\n<li>" + a + "</li>\n";
            liS.add(li);
        }
        Integer index = 0;
        for (Integer page = currentPageNumber; page < pages; page++) {
            Integer pageNumber = (page + 1);
            pageUrl = incrementPage+createUrlLink(pageNumber, alphabetTermPage);
            if (info.getLanguage().contains("hu") && pageNumber == 1) {
                pageUrl =incrementPage+ "browser_hu_A_1_1.html";
            }

            String a = "<a href=" + pageUrl  + id+ ">" + pageNumber + "</a>";
            li = "\n<li>" + a + "</li>\n";
            liS.add(li);
            if (index > this.maximumNumberOfPages && (pageNumber + 1) < pages) {
                pageUrl = incrementPage+createUrlLink(pageNumber + 1, alphabetTermPage);
                a = "<a href=" + pageUrl  +  id+ ">" + "Next" + "</a>";
                li = "\n<li>" + a + "</li>\n";
                liS.add(li);
                break;
            }

            index++;

        }

        return liS;
    }

    private String createUrlLink(Integer pageNumber, LangPairManager alphabetTermPage) {
        return LOCALHOST_URL_LIST_OF_TERMS_PAGE + info.createFileNameUnicode(pageNumber, alphabetTermPage);
    }

    private String generateTermUrl(String term, LangPairManager alphabetTermPage) {
        return alphabetTermPage.getProps().getProperty(term);
    }

    public Map<String, String> getTermList() {
        return termAlterUrl;
    }

    private void assignCurrentPageNumber(Element divCurrentPage, Integer currentPageNumber) {
        divCurrentPage.append("<span>" + currentPageNumber + "</span> </li>");

    }

    private String assignHighLightPageNumber(String pageUrl) {
        String a = "<a href=" + pageUrl + ">" + "&gt;" + "</a>";
        return "\n<li>" + a + "</li>\n";

    }


}
