package com.wilzwert.myjobs.infrastructure.configuration;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.bson.Document;

@Testcontainers
public abstract class MongoTestContainerConfiguration {

    // Container Mongo avec réplica set
    protected static final MongoDBContainer mongo = new MongoDBContainer("mongo:8.0")
            .withReuse(true)
            .withCommand("--replSet", "rs0");

    static {
        mongo.start();
        initReplicaSet();
    }

    // Initialisation du replica set (une seule fois)
    private static void initReplicaSet() {
        try (MongoClient client = MongoClients.create(mongo.getReplicaSetUrl())) {
            Document result = client.getDatabase("admin")
                    .runCommand(new Document("replSetInitiate", new Document()));
            System.out.println("Replica set initiated: " + result.toJson());
        } catch (Exception e) {
            System.err.println("Replica set already initiated or failed: " + e.getMessage());
        }
    }

    @DynamicPropertySource
    static void registerMongoProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongo::getReplicaSetUrl);
        registry.add("spring.data.mongodb.database", () -> "testdb");
    }
}
