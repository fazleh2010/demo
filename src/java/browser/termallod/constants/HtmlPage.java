/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package browser.termallod.constants;

import browser.termallod.core.termbase.TermLinker;
import java.io.File;
import java.util.List;
import org.jsoup.nodes.Element;

/**
 *
 * @author elahi
 */
public interface HtmlPage {
    
    public static String browser="browser";
    public static String HTML_EXTENSION = ".html";
    public String UNDERSCORE = "_";
    public static String LOCALHOST_URL_LIST_OF_TERMS_PAGE = "";
    public static Integer INITIAL_PAGE = 1;
    public void createTerms(Element body, List<String> terms, String alphebetPair, Integer emptyTerm,String htmlFileName)throws Exception;

}
