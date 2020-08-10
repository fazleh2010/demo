/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package browser.termallod.process;

import browser.termallod.constants.Parameter;
import static browser.termallod.constants.SparqlEndpoint.query_writtenRep;
import browser.termallod.core.sparql.CurlSparqlQuery;
import browser.termallod.core.termbase.TermDetail;
import browser.termallod.core.termbase.Termbase;
import browser.termallod.utils.StringMatcherUtil2;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Sets;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author elahi
 */
public class Matching {

    private String terminologyName = "otherTerminology";
    private Map<String, Set<TermDetail>> matchedTermsInto = new TreeMap<String, Set<TermDetail>>();

    public Matching(String INPUT_PATH, String otherTermSparqlEndpoint, String localLanguages, String otherTermTableName) throws IOException, Exception {
        CurlSparqlQuery curlSparqlQuery = new CurlSparqlQuery();
        Set<String> remoteLanguages = getLanguaes(localLanguages);
        for (String langCode : remoteLanguages) {

            RetrieveAlphabetInfo retrieveAlphabetInfo = new RetrieveAlphabetInfo(INPUT_PATH, langCode, false);
            Termbase otherTerminology = curlSparqlQuery.findListOfTerms(otherTermSparqlEndpoint, query_writtenRep, otherTermTableName);
            Map<String, String> localTermUrls = retrieveAlphabetInfo.getAllTerms();
            Set<String> matchedTerms = match(localTermUrls.keySet(), otherTerminology.getTerms().keySet());
            Set<TermDetail> termDetails = new HashSet<TermDetail>();
            for (String term : matchedTerms) {
                String url = localTermUrls.get(term);
                TermDetail remoteTermDetail = otherTerminology.getTerms().get(term);
                TermDetail termDetail = new TermDetail(term, url, terminologyName, remoteTermDetail.getTermUrl());
                termDetails.add(termDetail);
            }
            if (!matchedTerms.isEmpty()) {
                matchedTermsInto.put(langCode, termDetails);
            }
        }
    }

    public static Set<String> getLanguaes(String jsonStr) {
        ObjectMapper objectMapper = new ObjectMapper();
        Set<String> retrivedLangSets = new HashSet<String>();
        try {
            ArrayList<Object> langSet = objectMapper.readValue(jsonStr, ArrayList.class);
            for (Object object : langSet) {
                LinkedHashMap<String, Object> elements = (LinkedHashMap<String, Object>) object;
                for (String key : elements.keySet()) {
                    LinkedHashMap<String, Object> language = (LinkedHashMap<String, Object>) elements.get(key);
                    for (String lanCode : language.keySet()) {
                        String langCode = (String) language.get(lanCode);
                        langCode = StringMatcherUtil2.getLanguage(langCode);
                        if (langCode != null) {
                            retrivedLangSets.add(langCode);
                        }
                    }

                }
            }

        } catch (IOException ex) {
            System.out.println("json parse fails!!!");
            Logger.getLogger(Parameter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return retrivedLangSets;
    }

    private static Set<String> match(Set<String> myTerms, Set<String> otherTerms) {
        return Sets.intersection(myTerms, otherTerms);
    }

    public String getTerminologyName() {
        return terminologyName;
    }

    public Map<String, Set<TermDetail>> getMatchedTermsInto() {
        return matchedTermsInto;
    }

    @Override
    public String toString() {
        return "Matching{" + "terminologyName=" + terminologyName + ", matchedTermsInto=" + matchedTermsInto + '}';
    }

}
