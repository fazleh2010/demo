/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package browser.termallod.core;

import browser.termallod.api.LanguageManager;
import browser.termallod.utils.Mathmatices;
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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author elahi
 */
public class LanguageAlphabetPro implements LanguageManager {

    private HashMap<String, HashMap<String, String>> langAlphabetPair = new HashMap<String, HashMap<String, String>>();
    private HashMap<String, List<String>> langAlphabetPairSorted = new HashMap<String, List<String>>();
    private String UNDERSCORE = "_";

    public LanguageAlphabetPro(File file) throws IOException {
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

            /*String pair = alphebets[index] + this.UNDERSCORE + alphebets[index + 1];
            pair=pair.toUpperCase();
            alphabetsPair.put(alphebets[index], pair);
            alphabetsPair.put(alphebets[index + 1], pair);
            alphebetsPairList.add(pair);
            index=index+2;*/
        }
        langAlphabetPair.put(languageName, alphabetsPair);
        langAlphabetPairSorted.put(languageName, alphebetsPairList);
    }

    @Override
    public HashMap<String, String> getLangAlphabetHash(String language) throws Exception {
        if (langAlphabetPair.containsKey(language)) {
            return langAlphabetPair.get(language);
        } else {
            throw new Exception("No alphabet found for the language " + language);
        }
    }

    @Override
    public List<String> getLangAlphabetPairSorted(String language) throws Exception {
        if (langAlphabetPair.containsKey(language)) {
            return langAlphabetPairSorted.get(language);
        } else {
            throw new Exception("No alphabet found for the language " + language);
        }
    }

    /*public HashMap<String, String> getLangAlphabetHash(String language) throws Exception {
        if (langAlphabetPair.containsKey(language)) {
            return langAlphabetPair.get(language);
        } else {
            throw new Exception("No alphabet found for the language " + language);
        }

    }*/

 /*private String[] getAlphebets(String[] alphebets) {
        String[] pickedNumbers = new String[alphebets.length];
        for (Integer index=0;index<alphebets.length;){
             pickedNumbers [index]=alphebets
        }
        String[] pickedNumbers = list.toArray(new String[list.size()]);
        return pickedNumbers;
    }*/

 /*private String[] getAlphebets(String[] alphebets) {
        Integer index = 0;
        String[] pickedNumbers = new String[alphebets.length / 2];
        if (alphebets.length > 1) {
            for (int i = 1; i < alphebets.length; i = i + 2) {
                pickedNumbers[i / 2] = alphebets[i - 1] + UNDERSCORE + alphebets[i];
                index++;
            }
        }
        if (Mathmatices.isOdd(alphebets)) {
            pickedNumbers[pickedNumbers.length - 1] = alphebets[alphebets.length - 1];
        }
        return pickedNumbers;
    }
    private HashMap<String, String> makeMapping(String[] alphebets, String[] pickedAlphebets) {
        HashMap<String, String> langAlphabetHash = new HashMap<String, String>();
        for (String letter : alphebets) {
            for (String pair : pickedAlphebets) {
                if (pair.contains(letter)) {
                    langAlphabetHash.put(letter.toLowerCase(), pair);
                    break;
                }

            }
        }
        return langAlphabetHash;
    }*/
    @Override
    public boolean isLanguageExist(String language) throws Exception {
        return langAlphabetPairSorted.containsKey(language);
    }

    @Override
    public String toString() {
        return "LanguageAlphabetPro{" + "langAlphabetPair="  + langAlphabetPairSorted + ", UNDERSCORE=" + UNDERSCORE + '}';
    }

    @Override
    public HashMap<String, List<String>> getLangAlphabetPairSorted() {
        return langAlphabetPairSorted;
    }

   
}
