/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package browser.termallod.core.html;

import browser.termallod.core.language.AlphabetFilesReader;
import browser.termallod.core.language.LangPairManager;
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
public class HtmlPageContentGen {

    private HashMap<String, List<LangPairManager>> langPages = new HashMap<String, List<LangPairManager>>();
    public Integer numberofElementEachPage = 100;
    private TreeSet<String> languages=new TreeSet();
    private Map<String, String> languageInitpage = new HashMap<String, String>();
    


    public HtmlPageContentGen( TreeMap<String, AlphabetFilesReader> langSortedTerms) throws Exception {
        if (!langSortedTerms.isEmpty()) {
            this.langPages = this.preparePageTerms(langSortedTerms);
            this.languages=new TreeSet(langPages.keySet());
        } else {
            throw new Exception("No list of terms found!!!");
        }
        this.display();
    }

    private HashMap<String, List<LangPairManager>> preparePageTerms(TreeMap<String, AlphabetFilesReader> langSortedTerms) throws Exception {
        HashMap<String, List<LangPairManager>> langTerms = new HashMap<String, List<LangPairManager>>();
        for (String language : langSortedTerms.keySet()) {
            AlphabetFilesReader retrieveAlphabetInfo= langSortedTerms.get(language);
            TreeMap<String, List<String>> alpahbetTerms =retrieveAlphabetInfo.getLangSortedTerms();
            List<LangPairManager> alphabetTermPageList = new ArrayList<LangPairManager>();
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
                LangPairManager alphabetTermPage = new LangPairManager(language,alphabetPair,props, partition,numericalValueOfPair);
                alphabetTermPageList.add(alphabetTermPage);
                numericalValueOfPair++;
            }
            langTerms.put(language, alphabetTermPageList);
        }
        return langTerms;
    }

    public List<LangPairManager> getLangPages(String language) {
        if (langPages.containsKey(language)) {
            return langPages.get(language);
        }
        return new ArrayList<LangPairManager>();
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
            List<LangPairManager> pages = langPages.get(language);
            for (LangPairManager page : pages) {
                //System.out.print(page);
            }

        }
    }

}
