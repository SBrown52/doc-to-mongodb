package com.sbrown52.examples.doc2mongo.parser;

import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static com.sbrown52.examples.doc2mongo.parser.DocumentParser.PERSONS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DocumentParserTest {

    @Test
    public void testSampleTxtDocument() throws Exception {
        DocumentParser dp = new DocumentParser("./src/test/resources/test1.txt");
        var content = dp.parseFile();
        assertEquals(content, "this is a test document in a text file");
    }

    @Test
    public void testEntityExtract() {
        var dp = new DocumentParser("");
        var entities = dp.extractEntities("Sam lives in a small house");
        assertEquals(entities.get(PERSONS).size(), 1);
        assertTrue(entities.get(PERSONS).contains("Sam"));
    }
}
