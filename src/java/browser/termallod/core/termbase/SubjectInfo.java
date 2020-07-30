/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package browser.termallod.core.termbase;

/**
 *
 * @author elahi
 */
public class SubjectInfo {

    private String ReferenceID = "";
    private String subjectId = "";
    private String subjectDescription = "";

    public SubjectInfo() {
        this.ReferenceID = "";
        this.subjectId = "";
        this.subjectDescription = "";
    }

    public SubjectInfo(Object termID, Object subjectID, Object subjectDescription) {
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

    @Override
    public String toString() {
        return "SubjectInfo{" + "ReferenceID=" + ReferenceID + ", subjectId=" + subjectId + ", subjectDescription=" + subjectDescription + '}';
    }

}
