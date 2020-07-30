/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package browser.termallod.testcode;

import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.VCARD;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.apache.jena.riot.lang.PipedRDFIterator;
import org.apache.jena.riot.lang.PipedRDFStream;
import org.apache.jena.riot.lang.PipedTriplesStream;

/**
 *
 * @author elahi
 */
public class Tutle {

    private static String PATH = "/Users/elahi/NetBeansProjects/prepareVersion/tbx2rdf_staging/src/java/tbx2rdf/utils/virtuoso/data/";

    public static void main(String[] args) throws IOException {
        Tutle tutle = new Tutle();
        tutle.readingAndTraversingTurtlefile();

    }

    private void readingAndTraversingTurtlefile() throws FileNotFoundException, MalformedURLException {
        PipedRDFIterator<Triple> iter = new PipedRDFIterator<>();
        final PipedRDFStream<Triple> inputStream = new PipedTriplesStream(iter);

        // PipedRDFStream and PipedRDFIterator need to be on different threads
        ExecutorService executor = Executors.newSingleThreadExecutor();

        // Create a runnable for our parser thread
        Runnable parser = new Runnable() {

            @Override
            public void run() {
                // Call the parsing process.
                RDFDataMgr.parse(inputStream, PATH + "current.ttl");
            }
        };

        // Start the parser on another thread
        executor.submit(parser);

        // We will consume the input on the main thread here
        // We can now iterate over data as it is parsed, parsing only runs as
        // far ahead of our consumption as the buffer size allows
        while (iter.hasNext()) {
            Triple next = iter.next();
            // Do something with each triple

            System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            //System.out.println(next.toString());
            System.out.println("Subject:  " + next.getSubject());
            System.out.println("Object:  " + next.getObject());
            System.out.println("Predicate:  " + next.getPredicate());
            System.out.println("\n");
        }
    }

    

}
