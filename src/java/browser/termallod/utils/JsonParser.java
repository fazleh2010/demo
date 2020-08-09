/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package browser.termallod.utils;

import browser.termallod.core.termbase.TermDetail;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author elahi
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class JsonParser {

    @JsonProperty("term")
    public String term;
    @JsonProperty("iri")
    public String url;

    @JsonProperty("lang")
    public String lang;

    public String getTerm() {
        return term;
    }

    public String getUrl() {
        return url;
    }

    public String getLang() {
        return lang;
    }

    @Override
    public String toString() {
        return "JsonParser{" + "\n term=" + term
                + ", \n url=" + url
                + ", \n lang=" + lang;

    }

}
