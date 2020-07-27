/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package browser.termallod.process;

import browser.termallod.utils.FileRelatedUtils;
import browser.termallod.utils.NameExtraction;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;

/**
 *
 * @author elahi
 */
public class RetrieveAlphabetInfo {

    private Map<String, File> pairFile = new TreeMap<String, File>();
    private TreeMap<String, List<String>> langSortedTerms = new TreeMap<String, List<String>>();

    public RetrieveAlphabetInfo(String INPUT_PATH, String langCode) throws IOException, IOException, IOException, IOException, IOException {
        List<File> files = FileRelatedUtils.getFiles(INPUT_PATH,langCode, ".txt");
       
        for (File file : files) {
            String []info= file.getName().split("-");
            String pair=info[1].replace(".txt","");
            this.pairFile.put(pair, file);
            this.getValuesFromTextFile(file, pair);
        }

    }

    private void getValuesFromTextFile(File propFile, String pair) throws FileNotFoundException, IOException {
        Properties props = FileRelatedUtils.getPropertyHash(propFile);
        Set<String> termSet = props.stringPropertyNames();
        List<String> termList = new ArrayList<String>(termSet);
        Collections.sort(termList);
        //System.out.println("pair:"+pair);
        //System.out.println("termList:"+termList);
        langSortedTerms.put(pair, termList);
    }

    public TreeMap<String, List<String>> getLangSortedTerms() {
        return langSortedTerms;
    }

    public File getPairFile(String pair) {
        return pairFile.get(pair);
    }

   
}
