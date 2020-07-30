/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package browser.termallod.core.termbase;

import browser.termallod.utils.FileUrlUtils;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author elahi
 */
public class Termbase {

    private final String termbaseName;
    private Map<String, TermDetail> terms = new HashMap<String, TermDetail>();

    public Termbase(String termbaseName, Map<String, TermDetail> terms) throws IOException {
        this.termbaseName = termbaseName;
        this.terms =terms;
    }

    public Map<String, TermDetail> getTerms() {
        return terms;
    }

    public String getTermbaseName() {
        return termbaseName;
    }

    
    public void display() {
        for(String term:terms.keySet()){
            TermDetail terminfo=terms.get(term);
            System.out.println(terminfo);
            
        }
    }

}
