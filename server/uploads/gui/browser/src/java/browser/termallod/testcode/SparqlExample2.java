/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package browser.termallod.testcode;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author elahi
 */
public class SparqlExample2 {
    
     public static void main(String[] args) throws Exception {
         File file=null;
         sparqlExample(file);
     }

    public static void sparqlExample(File file) throws FileNotFoundException, IOException {
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

}
