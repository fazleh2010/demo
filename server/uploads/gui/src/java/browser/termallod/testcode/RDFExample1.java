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
public class RDFExample1 {

    private static String PATH = "src/java/tbx2rdf/utils/virtuoso/";
    private static File file = new File(PATH + "/data/" + "bloggers.rdf");

    public static void main(String[] args) throws IOException {
        //example1();
        RDFExample1 rdfExample1 = new RDFExample1();
        //rdfExample1.example2();
        
        //rdfExample1.readingAndTraversingTurtlefile();

    }

    public static void example2() throws FileNotFoundException, IOException {
        // Open the bloggers RDF graph from the filesystem
        InputStream in = new FileInputStream(file);

// Create an empty in‑memory model and populate it from the graph
        Model model = ModelFactory.createDefaultModel();
        //Model model = ModelFactory.createMemModelMaker().createModel();
        model.read(in, null); // null base URI, since model URIs are absolute
        in.close();

// Create a new query
        String queryString
                = "PREFIX foaf: <http://blog.planetrdf.com/> "
                + "SELECT ?url "
                + "WHERE {"
                + "      ?contributor foaf:name \"Jon Foobar\" . "
                + "      ?contributor foaf:weblog ?url . "
                + "      }";

        Query query = QueryFactory.create(queryString);

// Execute the query and obtain results
        QueryExecution qe = QueryExecutionFactory.create(query, model);
        ResultSet results = qe.execSelect();

// Output query results    
        ResultSetFormatter.out(System.out, results, query);

// Important ‑ free up resources used running the query
        qe.close();

    }

    private static void example1() throws NoSuchElementException {
        // some definitions
        String personURI = "http://somewhere/JohnSmith";
        String givenName = "John";
        String familyName = "Smith";
        String fullName = givenName + " " + familyName;

// create an empty Model
        Model model = ModelFactory.createDefaultModel();

// create the resource
//   and add the properties cascading style
        Resource johnSmith
                = model.createResource(personURI)
                        .addProperty(VCARD.FN, fullName)
                        .addProperty(VCARD.N,
                                model.createResource()
                                        .addProperty(VCARD.Given, givenName)
                                        .addProperty(VCARD.Family, familyName));

        System.out.println(johnSmith);

// list the statements in the Model
        StmtIterator iter = model.listStatements();

// print out the predicate, subject and object of each statement
        while (iter.hasNext()) {
            Statement stmt = iter.nextStatement();  // get next statement
            Resource subject = stmt.getSubject();     // get the subject
            Property predicate = stmt.getPredicate();   // get the predicate
            RDFNode object = stmt.getObject();      // get the object

            System.out.print(subject.toString());
            System.out.print(" " + predicate.toString() + " ");
            if (object instanceof Resource) {
                System.out.print(object.toString());
            } else {
                // object is a literal
                System.out.print(" \"" + object.toString() + "\"");
            }

            System.out.println(" .");

            System.out.println("------------------------------------------------");

            // now write the model in XML form to a file
            //model.write(System.out);
            // now write the model in XML form to a file
            //model.write(System.out, "RDF/XML-ABBREV");
            // now write the model in N-TRIPLES form to a file
            model.write(System.out, "N-TRIPLES");

        }
    }
     String pathString = "/Users/elahi/NetBeansProjects/prepareVersion/tbx2rdf_staging/src/java/tbx2rdf/utils/virtuoso/data/";

    private void readingAndTraversingTurtlefile() throws FileNotFoundException, MalformedURLException {
        //Model model = ModelFactory.createDefaultModel();
        //model.read(new FileInputStream(pathString + "current.ttl"), null, "TTL");
         
      
        //final String filename = "yagoTransitiveType2.ttl";

        // Create a PipedRDFStream to accept input and a PipedRDFIterator to
        // consume it
        // You can optionally supply a buffer size here for the
        // PipedRDFIterator, see the documentation for details about recommended
        // buffer sizes
        PipedRDFIterator<Triple> iter = new PipedRDFIterator<>();
        final PipedRDFStream<Triple> inputStream = new PipedTriplesStream(iter);

        // PipedRDFStream and PipedRDFIterator need to be on different threads
        ExecutorService executor = Executors.newSingleThreadExecutor();

        // Create a runnable for our parser thread
        Runnable parser = new Runnable() {

            @Override
            public void run() {
                // Call the parsing process.
                RDFDataMgr.parse(inputStream, pathString + "current-2.ttl");
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
            System.out.println("Subject:  "+next.getSubject());
            System.out.println("Object:  "+next.getObject());
            System.out.println("Predicate:  "+next.getPredicate());
            System.out.println("\n");
        }
    }

   

}
