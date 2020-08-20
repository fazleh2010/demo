/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package browser.termallod.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import browser.termallod.constants.LanguageDetail;
/**
 *
 * @author elahi
 */
public class LanguageManager implements LanguageDetail {

    private HashMap<String, HashMap<String, String>> langAlphabetPair = new HashMap<String, HashMap<String, String>>();
    private HashMap<String, List<String>> langAlphabetPairSorted = new HashMap<String, List<String>>();
    private String UNDERSCORE = "_";

    public LanguageManager(File file) throws IOException {
        generateLanguage(file);

    }

    private void generateLanguage(File propFile) throws FileNotFoundException, IOException {
        Properties props = new Properties();
        props.load(new InputStreamReader(new FileInputStream(propFile), "UTF-8"));
        Set<String> languages = props.stringPropertyNames();
        for (String languageName : languages) {
            String input = props.getProperty(languageName).toUpperCase();
            String[] alphebets = input.toLowerCase().split(" ");
            preparePair(languageName.trim(), alphebets);
        }

    }

    private void preparePair(String languageName, String[] alphebets) {
        HashMap<String, String> alphabetsPair = new HashMap<String, String>();
        List<String> alphebetsPairList = new ArrayList<String>();
        String pair = null;

        for (Integer index = 0; index < alphebets.length;) {
            Integer nextIndex = index + 1;

            if (nextIndex == alphebets.length) {
                pair = alphebets[index];
                pair = pair.toUpperCase();
                alphabetsPair.put(alphebets[index], pair);
                alphebetsPairList.add(pair);
                break;
            } else {
                pair = alphebets[index] + this.UNDERSCORE + alphebets[nextIndex];
                pair = pair.toUpperCase();
                alphabetsPair.put(alphebets[index], pair);
                alphabetsPair.put(alphebets[nextIndex], pair);
                alphebetsPairList.add(pair);
                index = index + 2;
            }

        }
        langAlphabetPair.put(languageName, alphabetsPair);
        langAlphabetPairSorted.put(languageName, alphebetsPairList);
    }

    public HashMap<String, String> getLangAlphabetHash(String language) throws Exception {
        if (langAlphabetPair.containsKey(language)) {
            return langAlphabetPair.get(language);
        } else {
            throw new Exception("No alphabet found for the language " + language);
        }
    }

    public List<String> getLangAlphabetPairSorted(String language) throws Exception {
        if (langAlphabetPair.containsKey(language)) {
            return langAlphabetPairSorted.get(language);
        } else {
            throw new Exception("No alphabet found for the language " + language);
        }
    }

   
    public boolean isLanguageExist(String language) throws Exception {
        return langAlphabetPairSorted.containsKey(language);
    }

    @Override
    public String toString() {
        return "LanguageAlphabetPro{" + "langAlphabetPair="  + langAlphabetPairSorted + ", UNDERSCORE=" + UNDERSCORE + '}';
    }

    public HashMap<String, List<String>> getLangAlphabetPairSorted() {
        return langAlphabetPairSorted;
    }

   
}
