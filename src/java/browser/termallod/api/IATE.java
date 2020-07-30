/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package browser.termallod.api;

/**
 *
 * @author elahi
 */
public interface IATE {
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
   
    
   
}
