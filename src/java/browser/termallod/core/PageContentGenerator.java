/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package browser.termallod.core;

import browser.termallod.process.RetrieveAlphabetInfo;
import browser.termallod.utils.FileRelatedUtils;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import browser.termallod.utils.Partition;
import browser.termallod.core.termbase.TermDetail;
import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;
import java.util.TreeSet;

/**
 *
 * @author elahi
 */
public class PageContentGenerator {

    private HashMap<String, List<AlphabetTermPage>> langPages = new HashMap<String, List<AlphabetTermPage>>();
    public Integer numberofElementEachPage = 100;
    private TreeSet<String> languages=new TreeSet();
    private Map<String, String> languageInitpage = new HashMap<String, String>();
    


    public PageContentGenerator( TreeMap<String, RetrieveAlphabetInfo> langSortedTerms) throws Exception {
        if (!langSortedTerms.isEmpty()) {
            this.langPages = this.preparePageTerms(langSortedTerms);
            this.languages=new TreeSet(langPages.keySet());
        } else {
            throw new Exception("No list of terms found!!!");
        }
        this.display();
    }

    private HashMap<String, List<AlphabetTermPage>> preparePageTerms(TreeMap<String, RetrieveAlphabetInfo> langSortedTerms) throws Exception {
        HashMap<String, List<AlphabetTermPage>> langTerms = new HashMap<String, List<AlphabetTermPage>>();
        for (String language : langSortedTerms.keySet()) {
            RetrieveAlphabetInfo retrieveAlphabetInfo= langSortedTerms.get(language);
            TreeMap<String, List<String>> alpahbetTerms =retrieveAlphabetInfo.getLangSortedTerms();
            List<AlphabetTermPage> alphabetTermPageList = new ArrayList<AlphabetTermPage>();
            if(!alpahbetTerms.isEmpty()) {
               this.languageInitpage.put(language, alpahbetTerms.keySet().iterator().next());
            }
            Integer numericalValueOfPair=1;
            for (String alphabetPair : alpahbetTerms.keySet()) {
                List<String> termSet = alpahbetTerms.get(alphabetPair);
                List<String>termList=new ArrayList<String>(termSet);
                Collections.sort(termList);
                Partition<String> partition = Partition.ofSize(termList, this.numberofElementEachPage);               
                Properties props=FileRelatedUtils.getPropertyHash(retrieveAlphabetInfo.getPairFile(alphabetPair));
                AlphabetTermPage alphabetTermPage = new AlphabetTermPage(language,alphabetPair,props, partition,numericalValueOfPair);
                alphabetTermPageList.add(alphabetTermPage);
                numericalValueOfPair++;
            }
            langTerms.put(language, alphabetTermPageList);
        }
        return langTerms;
    }

    public List<AlphabetTermPage> getLangPages(String language) {
        if (langPages.containsKey(language)) {
            return langPages.get(language);
        }
        return new ArrayList<AlphabetTermPage>();
    }

    public TreeSet<String> getLanguages() {
        return this.languages;
    }

    public String getLanguageInitpage(String language) throws Exception{
        if (languageInitpage.containsKey(language)) {
            return languageInitpage.get(language);
        }
        else {
            throw new Exception("No Alphabet pair!");
        }
    }
   
    private void display() {
        for (String language : this.langPages.keySet()) {
            List<AlphabetTermPage> pages = langPages.get(language);
            for (AlphabetTermPage page : pages) {
                //System.out.print(page);
            }

        }
    }

}
