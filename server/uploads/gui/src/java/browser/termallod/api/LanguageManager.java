/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package browser.termallod.api;

import java.util.HashMap;
import java.util.List;

/**
 *
 * @author elahi
 */
public interface LanguageManager {

    public HashMap<String, String> getLangAlphabetHash(String language)throws Exception;
    public List<String> getLangAlphabetPairSorted(String language) throws Exception;
    public HashMap<String, List<String>> getLangAlphabetPairSorted();
    public boolean isLanguageExist(String language)throws Exception;

}
