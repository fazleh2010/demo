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
public class DataBaseTemp {

    private String conceptFileName = "en.txt";
    private String subjectFileName = "subject.txt";
    private String cannonical = "canonicalForm.txt";
    private String SENSE = "sense";
    private String RELIABILITY_CODE = "reliabilityCode";
    private String ADMINISTRATIVE_STATUS = "administrativeStatus";
    private String subjectDescriptions = "conf/subjectFields.txt";
    private String administrativeStatus = "administrativeStatus.txt";
    private String reliabilityCode = "reliabilityCode.txt";
    private String location ;

    public DataBaseTemp(String location) {
        this.location=location;
    }
            
    public String getConceptFileName() {
        return this.conceptFileName;
    }

    public String getSubjectFileName() {
        return subjectFileName;
    }

    public String getCannonical() {
        return cannonical;
    }

    


    public String getSubjectDescriptions() {
        return this.location+subjectDescriptions;
    }

    public String getAdministrativeStatus() {
        return administrativeStatus;
    }

    public String getReliabilityCode() {
        return reliabilityCode;
    }

    public String getSENSE() {
        return SENSE;
    }

    public String getRELIABILITY_CODE() {
        return RELIABILITY_CODE;
    }

    public String getADMINISTRATIVE_STATUS() {
        return ADMINISTRATIVE_STATUS;
    }

}
