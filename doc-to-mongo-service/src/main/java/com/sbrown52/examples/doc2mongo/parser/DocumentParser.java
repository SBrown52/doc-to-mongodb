package com.sbrown52.examples.doc2mongo.parser;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ner.opennlp.OpenNLPNERecogniser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Takes a file path, runs document text extraction via Apache Tika
 */
public class DocumentParser {

    public DocumentParser(String filename) {
        this.filename = filename;
    }

    public static final String LOCATIONS = "locations";
    public static final String PERSONS = "persons";
    public static final String ORGANISATIONS = "organisations";

    private String filename;

    public String parseFile() throws IOException, SAXException, TikaException {
        AutoDetectParser parser = new AutoDetectParser();
        BodyContentHandler handler = new BodyContentHandler();
        Metadata metadata = new Metadata();
        try (InputStream stream = Files.newInputStream(Paths.get(filename))) {
            parser.parse(stream, handler, metadata);
            return handler.toString().trim();
        }
    }

    public Map<String, Set<String>> extractEntities(String text) {
        Map<String, String> models = new HashMap<>();
        models.put(LOCATIONS, "en-ner-location.bin");
        models.put(ORGANISATIONS, "en-ner-organization.bin");
        models.put(PERSONS, "en-ner-person.bin");
        var nerRecogniser = new OpenNLPNERecogniser(models);
        return nerRecogniser.recognise(text);
    }

}
