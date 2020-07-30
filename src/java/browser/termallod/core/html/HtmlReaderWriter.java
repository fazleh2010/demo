/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this document file, choose Tools | Templates
 * and open the document in the editor.
 */
package browser.termallod.core.html;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 *
 * @author elahi
 */
public class HtmlReaderWriter {

    private File inputFile;
    private Document inputDocument;
    private File outputFile;
    private Document outputDocument;

    public HtmlReaderWriter(File inputFile) {
        this.inputFile = inputFile;
        this.inputDocument = this.getDocument(inputFile);
    }

    private Document getDocument(File file) {
        String htmlString = null;
        try {
            htmlString = this.getHtmlHeader(file);
        } catch (IOException ex) {
            Logger.getLogger(HtmlReaderWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
        Document document = Jsoup.parse(htmlString);
        String title = document.title();
        String head = document.head().toString();
        String body = document.body().html();

        /*System.out.printf("head: ");
        System.out.printf(head);
        System.out.printf("Body: ");
        System.out.printf(body);*/
        return document;
    }

    private String getHtmlHeader(File file) throws FileNotFoundException, IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        StringBuilder stringBuilder = new StringBuilder();
        char[] buffer = new char[10];
        while (reader.read(buffer) != -1) {
            stringBuilder.append(new String(buffer));
            buffer = new char[10];
        }
        reader.close();

        String fileString = stringBuilder.toString();

        return fileString;
    }

    public void writeHtml(Document outputDocument, File outputFile) throws Exception {
        this.outputDocument = outputDocument;
        this.outputFile = outputFile;
        try {
           if (!outputFile.getName().contains(".html")) {
                System.out.println("Cannot process the File" + outputFile);
            } else {
               if(outputDocument!=null){
                   FileUtils.writeStringToFile(outputFile, outputDocument.outerHtml(), "UTF-8"); 
               }
               else
                   throw new Exception("No output HTML file is found to write!!");
            }
        } catch (IOException ex) {
            Logger.getLogger(HtmlReaderWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (NullPointerException ex) {
            Logger.getLogger(HtmlReaderWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public File getInputFile() {
        return inputFile;
    }

    public Document getInputDocument() {
        return inputDocument;
    }

    public File getOutputFile() {
        return outputFile;
    }

    public Document getOutputDocument() {
        return outputDocument;
    }

   

   

}
