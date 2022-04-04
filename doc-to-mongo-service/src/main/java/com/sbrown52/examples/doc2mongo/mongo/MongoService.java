package com.sbrown52.examples.doc2mongo.mongo;

import com.mongodb.client.model.Projections;
import com.mongodb.client.result.InsertOneResult;
import com.sbrown52.examples.doc2mongo.parser.DocumentParser;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class MongoService {

    @Autowired
    MongoTemplate template;

    @Value("${spring.data.mongodb.collection}")
    String collection;

    public InsertOneResult saveDoc(String filename, boolean extractEntities) throws Exception {
        var parser = new DocumentParser(filename);
        var content = parser.parseFile();
        Map<String, Set<String>> entities = new HashMap<>();

        if (extractEntities) {
            entities = parser.extractEntities(content);
        }

        var doc = new Document();
        doc.put("filename", filename);
        doc.put("timestamp", LocalDateTime.now());
        doc.put("content", content);
        doc.put("entities", entities);

        var result = template.getCollection(collection).insertOne(doc);

        return result;
    }

    public List<Document> getUploadedDocs() {
        var projection = Projections.fields(Projections.exclude("content", "entities"));
        return template.getCollection(collection).find().projection(projection).limit(10).into(new ArrayList<>());
    }

}
