package com.wilzwert.myjobs.infrastructure.configuration;

import com.wilzwert.myjobs.infrastructure.utility.TestDataLoader;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.bson.Document;

@Testcontainers
@SpringBootTest
@ActiveProfiles("integration")
@Tag("Integration")
@Slf4j
public abstract class AbstractBaseIntegrationTest {


    /**
     * testDataLoader is used to reset test data after each test
     * to ensure globally predictable tests
     */
    @Autowired
    private TestDataLoader testDataLoader;

    @AfterEach
    public void tearDown() {
        // reload all test data to ensure further tests consistency
        // we could e.g. manually delete the created entities but this would be unreliable
        // because for all we know, domain could trigger other data creation / update
        testDataLoader.resetAndReload();
    }

    // Container Mongo avec réplica set
    protected static final MongoDBContainer mongo = new MongoDBContainer("mongo:8.0")
            .withReuse(false)
            .waitingFor(Wait.forListeningPort())
            .withCommand("--replSet", "rs0");

    static {
        mongo.start();
        initReplicaSet();
    }

    // Initialisation du replica set (une seule fois)
    private static void initReplicaSet() {
        try (MongoClient client = MongoClients.create(mongo.getReplicaSetUrl())) {
            Document isMaster = client.getDatabase("admin").runCommand(new Document("isMaster", 1));
            if (!isMaster.getBoolean("ismaster", false)) {
                Document result = client.getDatabase("admin")
                        .runCommand(new Document("replSetInitiate", new Document()));
                log.info("Replica set initiated: {}", result.toJson());
            } else {
                log.info("Replica set already initiated.");
            }
        } catch (Exception e) {
            log.error("Error while initiating replica set : {}", e.getMessage());
        }
    }

    @DynamicPropertySource
    static void registerMongoProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongo::getReplicaSetUrl);
        registry.add("spring.data.mongodb.database", () -> "testdb");
    }
}