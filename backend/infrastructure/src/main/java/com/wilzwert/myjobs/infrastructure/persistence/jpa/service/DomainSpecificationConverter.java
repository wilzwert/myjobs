// This class should be completed for use with jpa, a relational db, and the Specification pattern
//
// package com.wilzwert.myjobs.infrastructure.persistence.jpa.service;
//
//import org.springframework.stereotype.Service;
//
//import java.util.Collections;
//import java.util.List;
//
///**
// * @author Wilhelm Zwertvaegher
// * Date:12/05/2025
// * Time:11:18
// * This converter should convert DomainSpecification types to jpa Specification types
// */
//
//@Service
//public class DomainSpecificationConverter {
//
//    /**
//     *
//     * @param domainSpecification a query criterion received from the domain
//     * @return a MongoDb Criteria
//     */
//
//    public <T, E> Specification<E> domainSpecificationToSpecification(DomainSpecification<T> domainSpecification, Class<T> domainClass, Class<E> entityClass) {
//        return MySpecConverter.of(domainClass, entityClass).domainSpecificationToSpecification(domainSpecification);
//    }
//}
