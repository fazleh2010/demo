/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package browser.termallod.constants;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author elahi
 */
public interface Languages {
     public static Map<String, String> languageMapper = new HashMap<String, String>() {
        {
            put("bg", "Bulgarian");
            put("cs", "Czech");
            put("da", "Danish");
            put("de", "German");
            put("el", "Greek");
            put("en", "English");
            put("es", "German");
            put("et", "Estonian");
            put("fi", "Finnish");
            put("fr", "French");
            put("ga", "Irish");
            put("hr", "Croatian");
            put("hu", "Hungarian");
            put("it", "Italian");
            put("lt", "Lithuanian");
            put("lv", "Latvian");
            put("mt", "Maltese");
            put("nl", "Dutch");
            put("ro", "Romanian");
            put("sk", "Slovak");
            put("sl", "Slovenian");
            put("sv", "Swedish");
        }
    };
    
}
