/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package browser.termallod.utils;

import browser.termallod.core.AlphabetTermPage;
import browser.termallod.core.termbase.TermDetailNew;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;

/**
 *
 * @author elahi
 */
public class FileRelatedUtils {

    public static File[] getFiles(String fileDir, String ntriple) throws Exception {
        File dir = new File(fileDir);
        FileFilter fileFilter = new WildcardFileFilter("*" + ntriple);
        File[] files = dir.listFiles(fileFilter);
        return files;
    }

    public static List<File> getFiles(String fileDir, String category, String extension) {
        String[] files = new File(fileDir).list();
        List<File> selectedFiles = new ArrayList<File>();
        for (String fileName : files) {
            if (fileName.contains(category) && fileName.contains(extension)) {
                selectedFiles.add(new File(fileDir + fileName));
            }
        }

        return selectedFiles;

    }

    public static List<File> getFiles(String fileDir, String category, String language, String extension) {

        String[] files = new File(fileDir).list();
        List<File> selectedFiles = new ArrayList<File>();
        for (String fileName : files) {
            if (fileName.contains(category) && fileName.contains(language) && fileName.contains(extension)) {
                selectedFiles.add(new File(fileDir + fileName));
            }
        }

        return selectedFiles;

    }

    public static File getFile(String fileDir, String category, String language, String extension) {
        String[] files = new File(fileDir).list();
        File selectedFile = null;
        for (String fileName : files) {
            if (fileName.contains(category) && fileName.contains(language) && fileName.contains(extension)) {
                selectedFile = new File(fileDir + fileName);
            }
        }
        return selectedFile;
    }

    /*public static List<File> writeFile(TreeMap<String, TreeMap<String, List<String>>> langSortedTerms, String path) throws IOException {
        List<File> files = new ArrayList<File>();
        for (String language : langSortedTerms.keySet()) {
            String str = "";
            String fileName = path + "_" + language +".txt";
            files.add(new File(fileName));
            TreeMap<String, List<String>> idSense = langSortedTerms.get(language);
            for (String pair : idSense.keySet()) {
                List<String> terms = idSense.get(pair);
                String line = pair + " = " + terms.toString().replace("[", "");
                line = line.replace("]", "");
                str += line + "\n";
            }
            stringToFile(str, fileName);
        }
        return files;
    }*/
 /*public static List<File> writeFile(TreeMap<String, TreeMap<String, List<TermInfo>>> langSortedTerms, String path) throws IOException {
        List<File> files = new ArrayList<File>();
        for (String language : langSortedTerms.keySet()) {
            String str = "";
            TreeMap<String, List<TermInfo>> idSense = langSortedTerms.get(language);
            for (String pair : idSense.keySet()) {
                String fileName = path + "_" + language + "_" + pair + ".txt";
                List<TermInfo> terms = idSense.get(pair);
                str = "";
                for (TermInfo term : terms) {
                    String line = term.getTermString() + " = " + term.getTermUrl();
                    str += line + "\n";
                }
                stringToFile_ApendIf_Exists(str, fileName);
            }

        }
        return files;
    }*/
 /*public static List<File> writeFile(TreeMap<String, TreeMap<String, List<TermInfo>>> langSortedTerms, String path) throws IOException {
        List<File> files = new ArrayList<File>();
        for (String language : langSortedTerms.keySet()) {
            String str = "";
            TreeMap<String, List<TermInfo>> idSense = langSortedTerms.get(language);
            for (String pair : idSense.keySet()) {
                String fileName = path + "_" + language + "_" + pair + ".txt";
                List<TermInfo> terms = idSense.get(pair);
                str = "";
                for (TermInfo term : terms) {
                    String line = term.getTermString() + " = " + term.getTermUrl();
                    str += line + "\n";
                }
                stringToFile_ApendIf_Exists(str, fileName);
            }

        }
        return files;
    }*/
    /*public static List<File> writeFile(TreeMap<String, TreeMap<String, List<TermInfo>>> langSortedTerms, String path) throws IOException {
        List<File> files = new ArrayList<File>();
        for (String language : langSortedTerms.keySet()) {
            String str = "";
            TreeMap<String, List<TermInfo>> alphabetPairTerms = langSortedTerms.get(language);
            Integer index=0;
            for (String pair : alphabetPairTerms.keySet()) {
                String fileName = path + "_" + language + "_" + pair + ".txt";
                List<TermInfo> terms = alphabetPairTerms.get(pair);
                str = "";
                for (TermInfo term : terms) {
                    String line = term.getTermString() + " = " + term.getTermUrl();
                    str += line + "\n";
                }
                stringToFile_ApendIf_Exists(str, fileName);
            }

        }
        return files;
    }*/
    
    /*public static List<File> writeFile(TreeMap<String, TreeMap<String, List<TermInfo>>> langSortedTerms, String path) throws IOException {
        List<File> files = new ArrayList<File>();
        for (String language : langSortedTerms.keySet()) {
            String str = "";
            TreeMap<String, List<TermInfo>> alphabetPairTerms = langSortedTerms.get(language);
             Integer pairIndex=0;
            for (String pair : alphabetPairTerms.keySet()) {
                String fileName = path + "_" + language + "_" + pair + ".txt";
                List<TermInfo> terms = alphabetPairTerms.get(pair);
                str = "";
                pairIndex++;
                Integer termIndex=0;
                for (TermInfo term : terms) {
                    String pairNote=null;
                    
                    if(language.contains("en"))
                        pairNote=pair;
                    else
                        pairNote=pairIndex.toString();
                    
                        
                    String line = term.getTermString() + " = " + term.getTermUrl()
                                  + " = " +"browser"+ "_" + language+ "_" + pairNote+ "_" +"term" + "_" + (termIndex++) + ".html";
                    str += line + "\n";
                }
                stringToFile_ApendIf_Exists(str, fileName);
            }

        }
        return files;
    }*/
    
    public static List<File> writeFile(TreeMap<String, TreeMap<String, List<TermDetailNew>>> langSortedTerms, String path) throws IOException {
        List<File> files = new ArrayList<File>();
        for (String language : langSortedTerms.keySet()) {
            String str = "";
            TreeMap<String, List<TermDetailNew>> alphabetPairTerms = langSortedTerms.get(language);
             Integer pairIndex=0;
            for (String pair : alphabetPairTerms.keySet()) {
                String fileName = path + language + "-" + pair + ".txt";
                List<TermDetailNew> terms = alphabetPairTerms.get(pair);
                str = "";
                pairIndex++;
                Integer termIndex=0;
                for (TermDetailNew term : terms) {
                    String pairNote=null;
                    
                    if(language.contains("en"))
                        pairNote=pair;
                    else
                        pairNote=pairIndex.toString();
                    
                        
                    String line = term.getTermOrg() + " = " + term.getTermUrl();
                    str += line + "\n";
                }
                stringToFile_ApendIf_Exists(str, fileName);
            }

        }
        return files;
    }
    

    public static void writeLangFile(Map<String, TreeMap<String, String>> langHash, String path) throws IOException {
        for (String language : langHash.keySet()) {
            String str = "";
            System.out.println(language);
            TreeMap<String, String> idSense = langHash.get(language);
            String fileName = path + language + ".txt";
            for (String id : idSense.keySet()) {

                String sense = idSense.get(id);
                String line = id + " = " + sense;
                str += line + "\n";
            }
            stringToFile_ApendIf_Exists(str, fileName);
        }

    }
    public static void writeLangFile(Map<String, TreeMap<String, String>> langHash, String path,String type) throws IOException {
        for (String language : langHash.keySet()) {
            String str = "";
            TreeMap<String, String> idSense = langHash.get(language);
            String fileName = path + language + "_"+type+ ".txt";
            for (String id : idSense.keySet()) {

                String sense = idSense.get(id);
                String line = id + " = " + sense;
                str += line + "\n";
            }
            stringToFile_ApendIf_Exists(str, fileName);
        }

    }

    public static void writeLangFile2(Map<String, String> langHash, String path,String type) throws IOException {
        for (String language : langHash.keySet()) {
            String str = "";
            str = langHash.get(language);
            String fileName = path + language + "_"+type+".txt";
            stringToFile_ApendIf_Exists(str, fileName);
        }

    }

    public static void writeFile(Map<String, String> terms, String fileName) throws IOException {
        String str = "";
        for (String term : terms.keySet()) {
            String line = term + " = " + terms.get(term);
            str += line + "\n";
        }
        stringToFile_If_Exists(str, fileName);
    }

    public static void writeFile(String content, String fileName) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
        writer.write(content);
        writer.close();
    }

    public static void writeFileNew(Map<String, String> terms, String fileName) throws IOException {
        if (terms.isEmpty()) {
            return;
        }
        String str = "";
        for (String term : terms.keySet()) {
            String line = term + "=" + terms.get(term);
            str += line + "\n";
            System.out.println(line);
        }
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
        writer.write(str);
        writer.close();
    }

    /*public static void writeFile(List<TermDetail> terms, String fileName) throws IOException {
        String str = "";
        for (TermDetail term : terms) {
            String line = term.getTerm() + " = " + term.getUrl()+ " = " +term.getAlternativeUrl();
            str += line + "\n";
        }
        stringToFile_If_Exists(str, fileName);
    }*/
    public static void stringToFile_If_Exists(String str, String fileName)
            throws IOException {
        File file = new File(fileName);
        if (file.exists()) {
            file.delete();
        }
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
        writer.write(str);
        writer.close();

    }

    public static void stringToFile_ApendIf_Exists(String str, String fileName)
            throws IOException {
        if (new File(fileName).exists()) {
            appendStringInFile(str, fileName);
            return;
        } else {
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
            writer.write(str);
            writer.close();
        }

    }
    
    public static void stringToFile(String str, String fileName)
            throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
        writer.write(str);
        writer.close();

    }

    public static void stringToFile_DeleteIf_Exists(String str, String fileName)
            throws IOException {
        /*File file = new File(fileName);
        if (file.exists()) {
            file.deleteOnExit();
        }*/
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
        writer.write(str);
        writer.close();

    }

    public static Properties getPropertyHash(File propFile) throws FileNotFoundException, IOException {
      
        FileReader fr = new FileReader(propFile);
        BufferedReader br = new BufferedReader(fr);
        Properties props = new Properties();
        props.load(new InputStreamReader(new FileInputStream(propFile), "UTF-8"));
        return props;
    }

    public static Map<String, String> getHash(String fileName) throws FileNotFoundException, IOException {
        Map<String, String> hash = new HashMap<String, String>();
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(fileName));
            String line = reader.readLine();
            while (line != null) {

                // read next line
                line = reader.readLine();
                if(line!=null){
                String[] info = line.split("=");
                hash.put(info[0], info[1]);
                }
                 
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return hash;
    }

    public static void appendStringInFile(String textToAppend, String fileName) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true));
        writer.write(textToAppend);
        writer.close();
    }

    public static void cleanDirectory(Set<String> categorySet, String PATH, String TEXT_DIR) throws IOException {
        //deleting all generated term filkes
        for (String browser : categorySet) {
            String sourceTextDir = getSourcePath(PATH, browser) + TEXT_DIR;
            FileRelatedUtils.deleteDirectory(sourceTextDir);
            FileRelatedUtils.createDirectory(sourceTextDir);
        }
    }

    public static void cleanDirectory(Map<String, String> categoryOntologyMapper, String PATH) throws IOException {

        for (String key : categoryOntologyMapper.keySet()) {
            key = categoryOntologyMapper.get(key);
            String mainDir = PATH + key;
            FileRelatedUtils.deleteDirectory(mainDir);
            //FileRelatedUtils.createDirectory(mainDir);
        }

    }
    
    public static void cleanDirectory(Map<String, String> categoryOntologyMapper, String PATH,String DIR) throws IOException {

        for (String key : categoryOntologyMapper.keySet()) {
            key = categoryOntologyMapper.get(key);
            String mainDir = PATH + key;
            String jsDir = mainDir+File.separator+DIR+File.separator;
             FileRelatedUtils.deleteDirectory(mainDir);
             FileRelatedUtils.createDirectory(mainDir);
             FileRelatedUtils.deleteDirectory(jsDir);
             FileRelatedUtils.createDirectory(jsDir);
         
        }

    }

    public static Map<String, List<File>> getLanguageFiles(List<File> inputfiles, String model_extension) {
        Map<String, List<File>> languageFiles = new HashMap<String, List<File>>();
        for (File file : inputfiles) {
            String langCode = NameExtraction.getLanCode(file, model_extension);
            if (languageFiles.containsKey(langCode)) {
                List<File> files = languageFiles.get(langCode);
                files.add(file);
                languageFiles.put(langCode, files);
            } else {
                List<File> files = new ArrayList<File>();
                files.add(file);
                languageFiles.put(langCode, files);
            }

        }
        return languageFiles;
    }

    public static Map<String, List<File>> getLanguageFiles(List<File> inputfiles, String model_extension, Boolean alternativeFlag) {
        Map<String, List<File>> languageFiles = new HashMap<String, List<File>>();
        for (File file : inputfiles) {
            String langCode = NameExtraction.getLanCode(file, model_extension);
            if (languageFiles.containsKey(langCode)) {
                List<File> files = languageFiles.get(langCode);
                files.add(file);
                languageFiles.put(langCode, files);
            } else {
                List<File> files = new ArrayList<File>();
                files.add(file);
                languageFiles.put(langCode, files);
            }

        }
        return languageFiles;
    }

    public static void deleteDirectory(String dir) throws IOException {
        FileUtils.deleteDirectory(new File(dir));
    }

    public static void createDirectory(String dir) throws IOException {
        Files.createDirectories(Paths.get(dir));
    }

    public static String getSourcePath(String PATH, String browser) {
        String source = PATH + browser + File.separator;
        return source;
    }

  
    public static void cleanDirectory(Set<String> categorySet, String PATH, String TEXT_DIR, String givenBrowser) throws IOException {
        //deleting all generated term filkes
        for (String browser : categorySet) {
            if (browser.contains(givenBrowser)) {
                String sourceTextDir = getSourcePath(PATH, browser) + TEXT_DIR;
                FileRelatedUtils.deleteDirectory(sourceTextDir);
                FileRelatedUtils.createDirectory(sourceTextDir);
            }

        }
    }


    public static void deleteFile(String fileName) {
        new File(fileName).delete();
    }

   
     public static Properties getProperties(String subjectFileName) throws IOException {
        File propFile = new File(subjectFileName);
        Properties props = new Properties();
        props.load(new InputStreamReader(new FileInputStream(propFile), "UTF-8"));
        return props;
    }


}
