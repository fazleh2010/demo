/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package browser.termallod.process;

import browser.termallod.api.LanguageManager;
import browser.termallod.utils.FileRelatedUtils;
import browser.termallod.core.termbase.TermDetail;
import browser.termallod.core.termbase.Termbase;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 *
 * @author Mohammad Fazleh Elahi
 */
public class CreateAlphabetFiles {

    private Termbase myTerminology = null;
    private LanguageManager languageInfo = null;
    private TreeMap<String, TreeMap<String, List<TermDetail>>> langTerms = new TreeMap<String, TreeMap<String, List<TermDetail>>>();

    public CreateAlphabetFiles(LanguageManager languageInfo, Termbase myTerminology) throws Exception {
        this.languageInfo = languageInfo;
        this.myTerminology = myTerminology;
        this.generate();
    }

    private void generate() throws Exception {
        for (String term : myTerminology.getTerms().keySet()) {
            TermDetail termDetailNew = myTerminology.getTerms().get(term);
            String language = termDetailNew.getLanguage();
            if (langTerms.containsKey(termDetailNew.getLanguage())) {
                langTerms = ifElementExist(language, termDetailNew, langTerms);
            } else {
                langTerms = ifElementNotExist(language, termDetailNew, langTerms);
            }
        }
    }

    private TreeMap<String, TreeMap<String, List<TermDetail>>> ifElementExist(String language, TermDetail term, TreeMap<String, TreeMap<String, List<TermDetail>>> langTerms) {
        String pair = null;
        pair = getAlphabetPair(language, term.getTermOrg());

        TreeMap<String, List<TermDetail>> alpahbetTerms = langTerms.get(language);
        try {
            if (alpahbetTerms.containsKey(pair)) {
                List<TermDetail> terms = alpahbetTerms.get(pair);
                terms.add(term);
                alpahbetTerms.put(pair, terms);
                langTerms.put(language, alpahbetTerms);
            } else {
                List<TermDetail> terms = new ArrayList<TermDetail>();
                terms.add(term);
                alpahbetTerms.put(pair, terms);
                langTerms.put(language, alpahbetTerms);
            }
        } catch (NullPointerException e) {
            System.out.println("Null pointer:" + language + " " + term + " " + pair);

        }
        return langTerms;
    }

    private TreeMap<String, TreeMap<String, List<TermDetail>>> ifElementNotExist(String language, TermDetail term, TreeMap<String, TreeMap<String, List<TermDetail>>> langTerms) {
        String pair = null;
        try {
            pair = getAlphabetPair(language, term.getTermOrg());

            TreeMap<String, List<TermDetail>> alpahbetTerms = new TreeMap<String, List<TermDetail>>();
            List<TermDetail> terms = new ArrayList<TermDetail>();
            terms.add(term);
            alpahbetTerms.put(pair, terms);
            langTerms.put(language, alpahbetTerms);
        } catch (NullPointerException e) {
            System.out.println("Null pointer:" + language + " " + term + " " + pair);

        }
        return langTerms;
    }

    private String getAlphabetPair(String language, String term) {
        Map<String, String> alphabetPairs = new HashMap<String, String>();
        String pair = null;
        try {
            alphabetPairs = languageInfo.getLangAlphabetHash(language);
            term = term.trim();

            String letter = term.substring(0, 1).toLowerCase();

            if (alphabetPairs.containsKey(letter)) {
                pair = alphabetPairs.get(letter);

            }

        } catch (Exception ex) {
            System.out.println("language:" + language);
            System.out.println("term:" + term);
        }

        return pair;
    }

    public TreeMap<String, TreeMap<String, List<TermDetail>>> getLangTerms() {
        return langTerms;
    }

    public void display() {
        for (String language : this.langTerms.keySet()) {
            System.out.println(language);
            TreeMap<String, List<TermDetail>> terms = this.langTerms.get(language);
            for (String termString : terms.keySet()) {
                List<TermDetail> termDetailNews = terms.get(termString);
                for (TermDetail termDetailNew : termDetailNews) {
                    System.out.println(termDetailNew.getTermUrl() + " " + termDetailNew.getTermUrl());
                }
            }
        }
    }

}
