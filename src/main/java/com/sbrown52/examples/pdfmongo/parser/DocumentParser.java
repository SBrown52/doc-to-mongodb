package com.sbrown52.examples.pdfmongo.parser;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Takes a file path, runs document text extraction via Apache Tika
 */
public class DocumentParser {

    public DocumentParser(String filename) {
        this.filename = filename;
    }

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

}
