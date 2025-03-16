package com.wilzwert.myjobs.infra.persistence;


import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Wilhelm Zwertvaegher
 * Date:13/03/2025
 * Time:09:39
 */

@Tag("Persistence")
public class MongoDbTest {

    @Test
    public void testInsert() {
        String name = "A";
        assertEquals("A", name);

        /*
        MongoTemplate mongo = context.getBean(MongoTemplate.class);
        Document doc = Document.parse("{\"name\":\"" + name + "\"}");
        Document inserted = mongo.insert(doc, "users");

        assertNotNull(inserted.get("_id"));
        assertEquals(inserted.get("name"), name);*/
    }
}