package tbx2rdf.vocab;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;

/**
 * Common Jena Resources for Provo - the W3C provenance ontology
 * @author Victor - Ontology Engineering Group - UPM
 */
public class PROVO {

    private static Model defaultModel = ModelFactory.createDefaultModel();
    
    public static Resource Activity = defaultModel.createResource("http://www.w3.org/ns/prov#Activity");
    public static Resource Agent = defaultModel.createResource("http://www.w3.org/ns/prov#Agent");
    public static Property wasGeneratedBy = defaultModel.createProperty("http://www.w3.org/ns/prov#wasGeneratedBy");
    public static Property endedAtTime = defaultModel.createProperty("http://www.w3.org/ns/prov#endedAtTime");
    public static Property wasAssociatedWith = defaultModel.createProperty("http://www.w3.org/ns/prov#wasAssociatedWith");
}
