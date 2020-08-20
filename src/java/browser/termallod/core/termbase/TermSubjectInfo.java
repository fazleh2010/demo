/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package browser.termallod.core.termbase;

import browser.termallod.utils.StringMatcher;

/**
 *
 * @author elahi
 */
public class TermSubjectInfo {

    private String ReferenceID = "";
    private String subjectId = "";
    private String subjectDescription = "";

    public TermSubjectInfo() {
        this.ReferenceID = "";
        this.subjectId = "";
        this.subjectDescription = "";
    }

    public TermSubjectInfo(Object termID, Object subjectID, Object subjectDescription) {
        this.ReferenceID = termID.toString();
        this.subjectId = subjectID.toString();
        this.subjectDescription = subjectDescription.toString();
    }

    public String getReferenceID() {
        return ReferenceID;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public String getSubjectDescription() {
        return subjectDescription;
    }

    public void setReferenceID(String ReferenceID) {
        this.ReferenceID = StringMatcher.getLastString(ReferenceID, '/',Boolean.FALSE);
    }

    public void setSubjectId(String subjectId) {
        if(subjectId.contains("http"))
          this.subjectId =  StringMatcher.getLastString(subjectId, '/',Boolean.FALSE);
        else
            this.subjectId=subjectId;
    }

    public void setSubjectDescription(String subjectDescription) {
        this.subjectDescription = subjectDescription;
    }

    @Override
    public String toString() {
        return "SubjectInfo{" + "ReferenceID=" + ReferenceID + ", subjectId=" + subjectId + ", subjectDescription=" + subjectDescription + '}';
    }

}
