/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package browser.termallod.testcode;

/**
 *
 * @author elahi
 */
import browser.termallod.api.LanguageManager;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.util.FileManager;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.List;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

public class Conversion {

    //Need to check the funtion whether it works propoerly
    private String MODEL_TYPE;
    private String LANGUAGE_SEPERATE_SYMBOLE = "@";
    private LanguageManager languageInfo;
    private static String path = "/users/elahi/NetBeansProjects/prepareVersion/browser/src/java/browser/termallod/data/iate/";
    private static String inputPath1 = "/Users/elahi/NetBeansProjects/prepareVersion/browser/src/java/browser/termallod/data/iate/";
    private  static String inputFileName="output_ac.ttl";

    
    private static File inputFile = new File(path + "tbx2rdf_iate.ttl");
    private static File outputFile = new File(path + "tbx2rdf_iate.ntriple");
    private TreeMap<String, TreeMap<String, List<String>>> langSortedTerms = new TreeMap<String, TreeMap<String, List<String>>>();

    public Conversion(String PATH, String MODEL_TYPE) {
        this.MODEL_TYPE = MODEL_TYPE;
        try {
             this.langSortedTerms = this.readTermsAndLanguages(PATH);
            //Test2(path + "tbx2rdf_iate.ttl");
            //System.out.println(langSortedTerms.toString());
        } catch (Exception ex) {
            Logger.getLogger(Conversion.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Conversion() {

    }

    public static void main(String[] args) throws Exception {
        Conversion Conversion = new Conversion(inputPath1+inputFileName, "TURTLE");
        //Conversion Conversion=new Conversion();
        //Conversion.test();
        
       /* try {
            //this.langSortedTerms = this.readTermsAndLanguages(PATH);
            Test2(path + "tbx2rdf_iate.ttl");
            //System.out.println(langSortedTerms.toString());
        } catch (Exception ex) {
            Logger.getLogger(Conversion.class.getName()).log(Level.SEVERE, null, ex);
        }*/
    }

    private TreeMap<String, TreeMap<String, List<String>>> readTermsAndLanguages(String fileNameOrUri) {
        String pair = null;
        TreeMap<String, TreeMap<String, List<String>>> langTerms = new TreeMap<String, TreeMap<String, List<String>>>();
        Model model = ModelFactory.createDefaultModel();
        InputStream is = FileManager.get().open(fileNameOrUri);
        
        if (is != null) {
            model.read(is, null, MODEL_TYPE);
            //model.write(System.out, "N-TRIPLE");
            List<RDFNode> rdfNodes = model.listObjects().toList();            
            for (RDFNode rdfNode : rdfNodes) {
                //testrdfNode(rdfNode);
                if (rdfNode.isLiteral()) {
                    System.out.println(rdfNode.toString());
                    /*if (rdfNode.toString().toLowerCase().contains(LANGUAGE_SEPERATE_SYMBOLE)) {
                        String[] infor = rdfNode.toString().split(LANGUAGE_SEPERATE_SYMBOLE);;
                        String language = infor[1].toLowerCase();
                        String term = infor[0].toLowerCase();
                        //System.out.println(language + " " + term);
                        if (langTerms.containsKey(language)) {
                            langTerms = ifElementExist(language, term, langTerms);

                        } else {
                            langTerms = ifElementNotExist(language, term, langTerms);
                        }
                    }*/
                }
            }

        } else {
            System.err.println("cannot read " + fileNameOrUri);;
        }
        return langTerms;
    }

    public void test() throws IOException {
        //Path pathFile = Paths.get(path+"tbx2rdf_iate.ttl");
        File file = new File(path + "tbx2rdf_iate.ttl");
        //long numOfLines = getLineCount(pathFile);
        //System.out.println(numOfLines);

        LineIterator fileContents = FileUtils.lineIterator(file);
        Integer index = 0;
        for (index = 0; index < 1000000; index++) {
            String line = fileContents.next();
            //System.out.println(line);
        }
    }
    
    public static void Test2(String filelocation) throws Exception
    {
        RandomAccessFile raf = new RandomAccessFile(filelocation, "r");
        long numSplits = 10; //from user input, extract it from args
        long sourceSize = raf.length();
        long bytesPerSplit = sourceSize/numSplits ;
        long remainingBytes = sourceSize % numSplits;

        int maxReadBufferSize = 8 * 1024; //8KB
        for(int destIx=1; destIx <= numSplits; destIx++) {
            BufferedOutputStream bw = new BufferedOutputStream(new FileOutputStream("split."+destIx));
            if(bytesPerSplit > maxReadBufferSize) {
                long numReads = bytesPerSplit/maxReadBufferSize;
                long numRemainingRead = bytesPerSplit % maxReadBufferSize;
                for(int i=0; i<numReads; i++) {
                    readWrite(raf, bw, maxReadBufferSize);
                }
                if(numRemainingRead > 0) {
                    readWrite(raf, bw, numRemainingRead);
                }
            }else {
                readWrite(raf, bw, bytesPerSplit);
            }
            bw.close();
        }
        if(remainingBytes > 0) {
            BufferedOutputStream bw = new BufferedOutputStream(new FileOutputStream(path+"split."+(numSplits+1)));
            readWrite(raf, bw, remainingBytes);
            bw.close();
        }
            raf.close();
    }

    static void readWrite(RandomAccessFile raf, BufferedOutputStream bw, long numBytes) throws IOException {
        byte[] buf = new byte[(int) numBytes];
        int val = raf.read(buf);
        if(val != -1) {
            bw.write(buf);
        }
    }

   
}
