/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package browser.termallod.core.html;

import static browser.termallod.api.HtmlStringConts.divClassEnd;
import static browser.termallod.api.HtmlStringConts.divClassStr;
import static browser.termallod.api.IATE.SUBJECT_FIELD;
import static browser.termallod.constants.Languages.languageMapper;
import browser.termallod.core.termbase.TermDetail;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 *
 * @author elahi
 */
public final class HtmlTermDetail {

    private Document templateHtml;
    private TermDetail termDetail;

    public HtmlTermDetail(TermDetail termDetail, Document templateHtml) throws Exception {
        this.templateHtml = templateHtml;
        this.termDetail = termDetail;
        System.out.println("term detail:"+termDetail);
        this.createTermPage();

    }

    private void createTermPage() throws Exception {
        String term = termDetail.getTermDecrpt();
        String url = termDetail.getTermUrl();
        String langDetail = languageMapper.get(termDetail.getLanguage());
        Element body = templateHtml.body();
        Element divTerm = body.getElementsByClass("webtop-g").get(0);

        //<a class="academic" href="https://www.oxfordlearnersdictionaries.com/wordlist/english/academic/">
        String classStr = "<a class=" + "\"" + "academic" + "\"" + " href=" + "\"" + "https://www.oxfordlearnersdictionaries.com/wordlist/english/academic/" + "\"" + ">";
        //</a><span class="z"> </span>
        String spanStr = "</a><span class=" + "\"" + "z" + "\"" + "> </span>";
        //<h2 class="h">abandon</h2>
        String wordStr = "<h2 class=" + "\"" + "h" + "\"" + ">" + term + "</h2>";
        //<span class="z"> </span>
        String extraStr = "<span class=" + "\"" + "z" + "\"" + ">" + "</span>";

        String str = classStr + spanStr + wordStr + extraStr; //language;//+titleStr+langStr;
        divTerm.append(str);

        Element divLang = body.getElementsByClass("top-g").get(0);
        String langDiv = "<span class=" + "\"" + "collapse" + "\"" + " title=" + "\"" + langDetail + "\"" + ">";
        langDiv += "<span class=" + "\"" + "heading" + "\"" + ">" + langDetail + "</span></span>";
        divLang.append(langDiv);

        Map<String, String> matchedTerms = termDetail.getTermLinks();
        List<String> divStrS = createTermInfo(matchedTerms, term, url);

        if (!divStrS.isEmpty()) {
            Integer index = 0;
            List<Element> divTerms = body.getElementsByClass("panel panel-default");
            for (Element divLinkTerm : divTerms) {
                String divStr = divStrS.get(index);
                divLinkTerm.append(divStr);
                index++;
                if (index == divStrS.size()) {
                    break;
                }

            }
        }

    }

    private List<String> createTermInfo(Map<String, String> matchedTerms, String term, String url) throws Exception {
        List<String> divStrS = new ArrayList<String>();
        String subjectFieldTr = "", ReferenceTr = "", languageTr = "", reliabilityCodeTr = "", administrativeTr = "", subjectID = "";;
        String posTr = "", numberTr = "", genderTr = "", definitionTr = "", hypernymTr = "", hyponymTr = "", variantTr = "", synonymTr = "", writtenFormTr = "",termUrlTr="";

        String langValueStr = languageMapper.get(this.termDetail.getLanguage());
        //languageTr = getTr(getProperty("Language"), getValueNew(langValueStr));
        //writtenFormTr = getTr(getProperty("written form:"), getValueNew(term));
        //termUrlTr=getTr(getProperty("Url:"), getValueNew(url));
        
        if (this.termDetail.getReliabilityCode() != null) {
            reliabilityCodeTr = getTr(getProperty("Reliability Code:"), getValueNew(this.termDetail.getReliabilityCode()));
        }
        if (this.termDetail.getAdministrativeStatus() != null) {
            administrativeTr = getTr(getProperty("Administrative Status:"), getValueNew(this.termDetail.getAdministrativeStatus()));
        }
        if (this.termDetail.getSubjectId() != null) {
            String subjectFieldPro = " " + SUBJECT_FIELD + ":";
            if (this.termDetail.getSubjectId().length() != 0) {
                subjectID = "(" + this.termDetail.getSubjectId() + ")";
            } else {
                subjectID = "";
            }
            subjectFieldTr = getTr(getProperty(subjectFieldPro), getValueNew(subjectID + this.termDetail.getSubjectDescription()));
        }
        if (this.termDetail.getReferenceID() != null) {
            ReferenceTr = getTr(getProperty("Reference:"), getValueNew(this.termDetail.getReferenceID()));
        }
        if (this.termDetail.getPOST() != null) {
            posTr = getTr(getProperty("POS:"), getValueNew(this.termDetail.getPOST()));
        }
        if (this.termDetail.getNumber() != null) {
            numberTr = getTr(getProperty("Number:"), getValueNew(this.termDetail.getNumber()));
        }
        if (this.termDetail.getGender() != null) {
            genderTr = getTr(getProperty("Gender:"), getValueNew(this.termDetail.getGender()));
        }
        if (this.termDetail.getDefinition() != null) {
            definitionTr = getTr(getProperty("Definition:"), getValueNew(this.termDetail.getDefinition()));
        }
        if (this.termDetail.getHypernym() != null) {
            hypernymTr = getTr(getProperty("Hypernym:"), getValueNew(this.termDetail.getHypernym()));
        }
        if (this.termDetail.getHyponym() != null) {
            hyponymTr = getTr(getProperty("Hyponym:"), getValueNew(this.termDetail.getHyponym()));
        }
        if (this.termDetail.getVariant() != null) {
            variantTr = getTr(getProperty("Variant:"), getValueNew(this.termDetail.getVariant()));
        }
        if (this.termDetail.getSynonym() != null) {
            synonymTr = getTr(getProperty("Synonym:"), getValueNew(this.termDetail.getSynonym()));
        }

        String table = this.getTable(this.getTbody(writtenFormTr + languageTr + termUrlTr+ definitionTr + reliabilityCodeTr + administrativeTr + subjectFieldTr + ReferenceTr
                + posTr + numberTr + genderTr + hypernymTr + hyponymTr + variantTr + synonymTr));
        String divStr = table;
        divStrS.add(divStr);
        divStrS = this.generateTermLink(matchedTerms, divStrS);

        return divStrS;
    }

    /*private List<String> generateTermLink(Map<String, String> matchedTerms, List<String> divStrS) {
        String panelHeadingStart = "<h3>Links to other terminologies</h3>";
        String panelHeadingEnd = "</div>";
        divStrS.add(panelHeadingStart);

        for (String otherTerminology : matchedTerms.keySet()) {
            String spanTerminologyName = otherTerminology;
            String spanTerminologyUrl = "http";
            String term = matchedTerms.get(otherTerminology);
            String url = matchedTerms.get(otherTerminology);
            String thirdTr = getTr(getProperty(spanTerminologyUrl, otherTerminology), getValue(url, url, url));
            String table = this.getTable(this.getTbody(thirdTr));
            String divStr = table;
            divStrS.add(divStr);

        }
        divStrS.add(panelHeadingEnd);
        return divStrS;
    }*/
    
    private List<String> generateTermLink(Map<String, String> matchedTerms, List<String> divStrS) {
        String panelHeadingStart = "<h3>Links to other terminologies</h3>";
        String panelHeadingEnd = "</div>";
        String tableStart="<table>";
        String tableEnd="</table>";
        String columnNames="<tr>"
                          +"<th>Terminology</th>"
                          +"<th>Link</th>"
                          +"<th>Match</th>"
                          +"</tr>";

        String rows="";
        String rowStart="<tr>",rowEnd="</tr>",colStart="<td>",colEnd="</td>";  
        for (String otherTerminology : matchedTerms.keySet()) {
            String term = matchedTerms.get(otherTerminology);
            String url = matchedTerms.get(otherTerminology);
            String col1=colStart+otherTerminology+colEnd;
            String col2=colStart+url+colEnd;
            String col3=colStart+"exact"+colEnd;
            String row=rowStart+col1+col2+col3+rowEnd;
            rows+=row;  
        }
        rows=panelHeadingStart+tableStart+columnNames+rows+tableEnd+panelHeadingEnd;
        divStrS.add(rows);
        return divStrS;
    }


    /*private TermInfo getTermInformation(String termUrl) {
        UrlMatching urlMatching = new UrlMatching(merging, termUrl);
        return urlMatching.getTermInfo();
    }*/
    private String getAcceptDenyButton() {
        String yesNoButtonDiv
                = "<div class=" + this.getWithinQuote("w3-container") + ">"
                + "<div class=" + this.getWithinQuote("w3-container w3-center") + ">"
                + "<div class=" + this.getWithinQuote("w3-section") + ">"
                + "<button class=" + this.getWithinQuote("w3-button w3-green") + ">Accept</button>"
                + "<button class=" + this.getWithinQuote("w3-button w3-red") + ">Decline</button>"
                + "</div>"
                + " </div>"
                + " </div>";
        return yesNoButtonDiv;
    }

    private String getValueNew(String str) {
        String tdEnd = "</td>";
        String tdRdfValueStart = "<td" + ">";
        String langValue = tdRdfValueStart + str + "</a>" + tdEnd;
        return langValue;
    }

    private String getProperty(String str) {
        String tdPropStart = "<td " + ">";
        String tdEnd = "</td>";
        String langProp = tdPropStart + str + "</a>" + tdEnd;
        return langProp;
    }

    private String getValue(String url1, String url2, String str) {
        String tdEnd = "</td>";
        String tdRdfValueStart = "<td class=" + this.getWithinQuote("rdf_value rdf_first_value") + ">";
        String langValue = tdRdfValueStart + "<a href=" + this.getWithinQuote(url1) + " property=" + this.getWithinQuote(url2) + " class=" + this.getWithinQuote("rdf_link rdf_prop") + ">" + str + "</a>" + tdEnd;
        return langValue;
    }

    private String getProperty(String url, String str) {
        String tdPropStart = "<td class=" + this.getWithinQuote("rdf_prop") + ">";
        String tdEnd = "</td>";
        String langProp = tdPropStart + " <a href=" + this.getWithinQuote(url) + " class=" + this.getWithinQuote("rdf_link") + ">" + str + "</a>" + tdEnd;
        return langProp;
    }

    private String getWithinQuote(String term) {
        return "\"" + term + "\"";
    }

    private String getTr(String langProp, String langValue) {
        String trStart = " <tr>";
        String trEnd = "</tr>";
        return trStart + langProp + langValue + trEnd;
    }

    private String getSpanProp(String url1, String url2, String str) {
        return "<span property=" + this.getWithinQuote(url1) + " datatype=" + this.getWithinQuote(url2) + ">" + str + "</span>";
    }

    private String getSpanValue(String url, String str) {
        return "<span class=" + this.getWithinQuote("pull-right rdf_datatype") + "><a href=" + this.getWithinQuote(url) + " class=" + this.getWithinQuote("rdf_link") + ">" + str + "</a></span>";
    }

    private String getValue(String string) {
        String tdEnd = "</td>";
        String tdRdfValueStart = "<td class=" + this.getWithinQuote("rdf_value rdf_first_value") + ">";
        return tdRdfValueStart + string + tdEnd;

    }

    private String getTbody(String trs) {
        String tbodyStart = "<tbody>";
        String tbodyEnd = "</tbody>";
        return tbodyStart + trs + tbodyEnd;

    }

    private String getTable(String tbody) {
        String tableStart = "<table class=" + this.getWithinQuote("panel-body rdf_embedded_table") + ">";
        String tableEnd = "</table>";
        return tableStart + tbody + tableEnd;
    }

    public Document getOutputHtml() {
        return templateHtml;
    }

}
