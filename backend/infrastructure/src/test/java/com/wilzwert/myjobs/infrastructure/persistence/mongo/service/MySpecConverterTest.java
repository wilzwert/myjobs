package com.wilzwert.myjobs.infrastructure.persistence.mongo.service;


import org.junit.jupiter.api.Test;

/**
 * @author Wilhelm Zwertvaegher
 * Date:13/05/2025
 * Time:13:47
 */

public class MySpecConverterTest {
    @Test
    void testConvert() {/*
        MySpecConverter<Job, MongoJob> converter = MySpecConverter.of(Job.class, MongoJob.class);
        Specification<MongoJob> spec = converter.domainSpecificationToSpecification(
            DomainSpecification.<Job>And(List.of(
                    DomainSpecification.Eq("title", "Truc"),
                    DomainSpecification.Lt("status_updated_at", Instant.now(), Instant.class),
                    DomainSpecification.In("status", JobStatus.activeStatuses()),
                    DomainSpecification.Or(
                            List.of(
                                DomainSpecification.Eq("company", "company1"),
                                DomainSpecification.Eq("company", "company2")
                            )
                    )
            ))
        );*/
    }
}
