/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package browser.termallod.constants;

import java.util.List;
import org.jsoup.nodes.Element;

/**
 *
 * @author elahi
 */
public interface HtmlConts {

    public String divClassStr = "<div class=";
    public String divClassEnd = "</div>";
    
    //Language
    public String langPropUrl = "http://www.w3.org/ns/lemon/ontolex#language";
    public String langPropStr = "Language";
    public String langValueUrl1 = "http://www.lexvo.org/page/iso639-3/eng";
    public String langValueUrl2 = "http://www.w3.org/ns/lemon/ontolex#language";
    
    
    
    public String langTermUrl = "https://www.w3.org/2003/glossary/";
    public String langTermStr = "Terminology";
    public String spanPropUrl1 = "";
    public String spanPropUrl2 = "";
    //public String spanPropUrl1 = "http://tbx2rdf.lider-project.eu/tbx#reliabilityCode";
    //public String spanPropUrl2 = "http://www.w3.org/2001/XMLSchema#integer";
    
    //public String spanPropStr = "3";
    //public String spanValueUrl = "http://www.w3.org/2001/XMLSchema#integer";
    public String spanPropStr = "";
    public String spanValueUrl = "https://iate.europa.eu/";
    //public String spanTerminologyName = "iate";
    
    
    
    public String matchPropUrl = "https://www.w3.org/TR/swbp-skos-core-spec/";
    public String matchPropStr = "skos concept";
    
    public String matchValueUrl1 = "https://terms.tdwg.org/wiki/skos:exactMatch";
    public String matchValueUrl2 = "https://terms.tdwg.org/wiki/skos:exactMatch";
    public String matchValueStr = "extact match";
    
      public static final String ADMINISTRATIVE_STATUS="administrativeStatus";
    public static final String RELIABILITY_CODE = "reliabilityCode";
    public static final String SUBJECT_FIELD = "SubjectField";
    
    public static final String IATE_ID = "IATE-";
    public static final String SENSE = "Sense";
    public static final String LANGUAGE_SEPERATE_SYMBOLE = "@";
    public static final String HTTP = "http";

    public static final String HTTP_IATE = "http://webtentacle1.techfak.uni-bielefeld.de/tbx2rdf_iate/data/iate/";
    public static final String HTTP_SUBJECT_FIELD = "http://tbx2rdf.lider-project.eu/data/iate/subjectField/";
    public static final String HTTP_ADMINISTRATIVE_STATUS = " http://tbx2rdf.lider-project.eu/tbx#";
    public static final String HTTP_RELIABILTY_CODE = "^^http://www.w3.org/2001/XMLSchema#integer";
    
    public static String browser="browser";
    public static String HTML_EXTENSION = ".html";
    public String UNDERSCORE = "_";
    public static String LOCALHOST_URL_LIST_OF_TERMS_PAGE = "";
    public static Integer INITIAL_PAGE = 1;
    public static String SPACE =" ";
   
}
