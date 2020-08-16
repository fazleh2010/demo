/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package browser.termallod.app;

import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author elahi
 */
public class LanguageTerm {
    private String langCode=null;
    private Map<String,String> termUrls=new TreeMap<String,String>();
    
    public LanguageTerm(String langCode,Map<String,String> termUrls){
       this.langCode=langCode;
       this.termUrls=termUrls;
    }

    public String getLangCode() {
        return langCode;
    }

    public Map<String, String> getTermUrls() {
        return termUrls;
    }
    
}
