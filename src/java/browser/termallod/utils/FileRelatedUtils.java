/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package browser.termallod.utils;

import browser.termallod.core.BrowserMain;
import browser.termallod.core.language.LangPairManager;
import browser.termallod.core.termbase.TermDetail;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;

/**
 *
 * @author elahi
 */
public class FileRelatedUtils {
    
    public  static String BASE_PATH = "src/java/resources/data/";
    public  static String OUTPUT_PATH = BASE_PATH + "/output/";
    public  static String INPUT_PATH = BASE_PATH + "/input/";
    public  static String CONFIG_PATH = BASE_PATH + "/conf/";
    public  static String TEMPLATE_PATH = BASE_PATH + "/template/";

    public static void main(String[] args) {
        
        zipFile("/Users/elahi/demo/src/java/resources/data/template/JsFormat.js");
    }

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

    public static List<File> writeFile(TreeMap<String, TreeMap<String, List<TermDetail>>> langSortedTerms, String path) throws IOException {
        List<File> files = new ArrayList<File>();
        for (String language : langSortedTerms.keySet()) {
            String str = "";
            TreeMap<String, List<TermDetail>> alphabetPairTerms = langSortedTerms.get(language);
            Integer pairIndex = 0;
            for (String pair : alphabetPairTerms.keySet()) {
                String fileName = path + language + "-" + pair + ".txt";
                List<TermDetail> terms = alphabetPairTerms.get(pair);
                str = "";
                pairIndex++;
                Integer termIndex = 0;
                for (TermDetail term : terms) {
                    String pairNote = null;

                    if (language.contains("en")) {
                        pairNote = pair;
                    } else {
                        pairNote = pairIndex.toString();
                    }

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
            //System.out.println(language);
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

    public static void writeLangFile(Map<String, TreeMap<String, String>> langHash, String path, String type) throws IOException {
        for (String language : langHash.keySet()) {
            String str = "";
            TreeMap<String, String> idSense = langHash.get(language);
            String fileName = path + language + "_" + type + ".txt";
            for (String id : idSense.keySet()) {

                String sense = idSense.get(id);
                String line = id + " = " + sense;
                str += line + "\n";
            }
            stringToFile_ApendIf_Exists(str, fileName);
        }

    }

    public static void writeLangFile2(Map<String, String> langHash, String path, String type) throws IOException {
        for (String language : langHash.keySet()) {
            String str = "";
            str = langHash.get(language);
            String fileName = path + language + "_" + type + ".txt";
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
            //System.out.println(line);
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
                if (line != null) {
                    String[] info = line.split("=");
                    String key = StringMatcher.encripted(info[0].toLowerCase().trim());
                    String value = info[1].trim();
                    hash.put(key, value);
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

    public static void cleanDirectory(Map<String, String> categoryOntologyMapper, String PATH, String DIR) throws IOException {

        for (String key : categoryOntologyMapper.keySet()) {
            key = categoryOntologyMapper.get(key);
            String mainDir = PATH + key;
            String jsDir = mainDir + File.separator + DIR + File.separator;
            FileRelatedUtils.deleteDirectory(mainDir);
            FileRelatedUtils.createDirectory(mainDir);
            FileRelatedUtils.deleteDirectory(jsDir);
            FileRelatedUtils.createDirectory(jsDir);

        }

    }

    /*public static Map<String, List<File>> getLanguageFiles(List<File> inputfiles, String model_extension) {
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
    }*/

 /*public static Map<String, List<File>> getLanguageFiles(List<File> inputfiles, String model_extension, Boolean alternativeFlag) {
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
    }*/
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

    public static void cleanDirectory(String INPUT_PATH, String OUTPUT_PATH) {
        try {
            FileRelatedUtils.deleteDirectory(INPUT_PATH);
            FileRelatedUtils.createDirectory(INPUT_PATH);
            FileRelatedUtils.deleteDirectory(OUTPUT_PATH);
            FileRelatedUtils.createDirectory(OUTPUT_PATH);
        } catch (IOException ex) {
            Logger.getLogger(BrowserMain.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void writeINFile(String insertFile, String term, String localTermUrl, String remoteTermUrl) throws IOException {
        Map<String, String> element = new HashMap<String, String>();
        element.put("localTerm", term);
        element.put("localUrl", localTermUrl);
        element.put("remoteTerm", term);
        element.put("remoteUrl", remoteTermUrl);
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            String json = objectMapper.writeValueAsString(element);
            System.out.println("!!!!!!!!!!!!!!!inside Java!!!!!!!!!!!!!!!!!!!!" + json);
            objectMapper.writeValue(new File(insertFile), json);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

    }

    public static void appendToFile(String file, String sparqlEnd) {
        FileWriter fileWriter;
        try {
            fileWriter = new FileWriter(file, true);
            BufferedWriter bufferFileWriter = new BufferedWriter(fileWriter);
            bufferFileWriter.append(sparqlEnd);
            bufferFileWriter.newLine();
            bufferFileWriter.close();
        } catch (IOException ex) {
            Logger.getLogger(BrowserMain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static Boolean zipFile(String filePath) {
        try {
            File file = new File(filePath);
            String zipFileName = file.getName().concat(".zip");

            FileOutputStream fos = new FileOutputStream(zipFileName);
            ZipOutputStream zos = new ZipOutputStream(fos);

            zos.putNextEntry(new ZipEntry(file.getName()));

            byte[] bytes = Files.readAllBytes(Paths.get(filePath));
            zos.write(bytes, 0, bytes.length);
            zos.closeEntry();
            zos.close();
            return true;

        } catch (FileNotFoundException ex) {
            System.err.format("The file %s does not exist", filePath);
            return false;
        } catch (IOException ex) {
            System.err.println("I/O error: " + ex);
            return false;
        }
    }

}
