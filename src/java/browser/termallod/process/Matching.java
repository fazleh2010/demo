/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package browser.termallod.process;

import browser.termallod.api.LanguageManager;
import browser.termallod.constants.Parameter;
import static browser.termallod.constants.SparqlEndpoint.query_writtenRep;
import static browser.termallod.constants.SparqlEndpoint.query_writtenRep_lang;
import browser.termallod.core.sparql.CurlSparqlQuery;
import browser.termallod.core.termbase.TermDetail;
import browser.termallod.core.termbase.Termbase;
import browser.termallod.utils.FileRelatedUtils;
import browser.termallod.utils.StringMatcherUtil2;
import static browser.termallod.utils.StringMatcherUtil2.getTerminologyName;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Sets;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
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

    private Map<String, Set<TermDetail>> matchedTermsInto = new TreeMap<String, Set<TermDetail>>();

    public Matching(Parameter parameter) throws IOException, Exception {

        String myTermSparqlEndpoint = parameter.getMyTermSparqlEndpoint();
        String otherTermSparqlEndpoint = parameter.getOtherTermSparqlEndpoint();
        String localLanguages = parameter.getLocalLangJson();
        LanguageManager langInfo = parameter.getLanguageInfo();

        CurlSparqlQuery curlSparqlQuery = new CurlSparqlQuery();
        Set<String> remoteLanguages = getLanguaes(localLanguages);

        //for (String langCode : remoteLanguages) {
        //System.out.println("langCode:" + langCode);
        Termbase yourTerminology = curlSparqlQuery.findListOfTerms(myTermSparqlEndpoint, query_writtenRep, "myTerminology", true);
        Termbase otherTerminology = curlSparqlQuery.findListOfTerms(otherTermSparqlEndpoint, query_writtenRep, "otherTerminology", true);
        Set<String> matchedTerms = match(yourTerminology, otherTerminology);
        //System.out.println(matchedTerms);
        //Set<TermDetail> termDetails = new HashSet<TermDetail>();
        for (String term : matchedTerms) {
            TermDetail localTermDetail = yourTerminology.getTerms().get(term);
            TermDetail remoteTermDetail = otherTerminology.getTerms().get(term);
            String otherTerminologyName = getTerminologyName(remoteTermDetail.getTermUrl());
            TermDetail linkedTermDetail = new TermDetail(term, localTermDetail.getTermUrl(), otherTerminologyName, remoteTermDetail.getTermUrl());
            //System.out.println("linkedTermDetail:" + linkedTermDetail);
            String insertSparql = "SPARQL INSERT DATA {\n"
                    + "GRAPH <http://tbx2rdf.lider-project.eu/> {\n"
                    + "<" + localTermDetail.getTermUrl() + "> <http://www.w3.org/ns/lemon/ontolex#sameAs> <" + remoteTermDetail.getTermUrl() + ">\n"
                    + "} };";
            FileRelatedUtils.writeSparqlToFile(parameter.getInsertFile(), insertSparql);
        }
        //}
    }

    private static Set<String> getTerms(Termbase yourTerminology) {
        List<String> localTerms = new ArrayList<String>(yourTerminology.getTerms().keySet());
        Collections.sort(localTerms);
        return new HashSet<String>(localTerms);
    }

    private static Set<String> match(Termbase yourTerminology, Termbase otherTerminology) {
        return match(getTerms(yourTerminology), getTerms(otherTerminology));
    }

    private static Set<String> match(Set<String> myTerms, Set<String> otherTerms) {
        return Sets.intersection(myTerms, otherTerms);
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

    public Map<String, Set<TermDetail>> getMatchedTermsInto() {
        return matchedTermsInto;
    }

    @Override
    public String toString() {
        return "Matching{" + "matchedTermsInto=" + matchedTermsInto + '}';
    }

}
