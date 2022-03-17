package com.sbrown52.examples.pdfmongo.parser;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DocumentParserTest {

    @Test
    public void testSampleTxtDocument() throws Exception {
        DocumentParser dp = new DocumentParser("./src/test/resources/test1.txt");
        var x = dp.parseFile();
        assertEquals(x, "this is a test document in a text file");
    }
}
