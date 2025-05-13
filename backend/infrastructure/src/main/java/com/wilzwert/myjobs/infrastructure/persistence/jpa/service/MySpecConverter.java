package com.wilzwert.myjobs.infrastructure.persistence.jpa.service;


// import jakarta.persistence.criteria.Path;
// import jakarta.persistence.criteria.Predicate;
// import org.springframework.data.jpa.domain.Specification;

/**
 * @author Wilhelm Zwertvaegher
 * Date:13/05/2025
 * Time:13:26
 */

//public class MySpecConverter<T, E> {
//
//    public static <T, E> MySpecConverter<T, E> of(Class<T> domainClass, Class<E> entityClass) {
//        return new MySpecConverter<>(domainClass, entityClass);
//    }
//
//    private final Class<T> domainClass;
//    private final Class<E> entityClass;
//
//    private MySpecConverter(Class<T> domainClass, Class<E> entityClass) {
//        this.domainClass = domainClass;
//        this.entityClass = entityClass;
//    }
//    /**
//     *
//     * @param domainSpecification a query criterion received from the domain
//     * @return a MongoDb Criteria
//     */
//    public Specification<E> domainSpecificationToSpecification(DomainSpecification<T> domainSpecification) {
//        if(domainSpecification instanceof DomainSpecification.Eq<T, ?> c) {
//            return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(c.getField()), c.getValue());
//        }
//
//        if(domainSpecification instanceof DomainSpecification.In<T, ?> c) {
//            return (root, query, criteriaBuilder) -> criteriaBuilder.in(root.get(c.getField()).in(c.getValues()));
//        }
//
//        // FIXME : suppressing warnings is not very clean
//        if(domainSpecification instanceof DomainSpecification.Lt<T, ?> c) {
//            return (root, query, criteriaBuilder) -> {
//                Path<?> path = root.get(c.getField());
//                Object value = c.getValue();
//                @SuppressWarnings("unchecked")
//                Path<? extends Comparable<Object>> comparablePath = (Path<? extends Comparable<Object>>) path;
//                @SuppressWarnings("unchecked")
//                Comparable<Object> comparableValue = (Comparable<Object>) value;
//                return criteriaBuilder.lessThan(comparablePath, comparableValue);
//            };
//        }
//
//        if(domainSpecification instanceof DomainSpecification.And<T> c) {
//            return c.getSpecifications().stream()
//                    .map(this::domainSpecificationToSpecification)
//                    .reduce(Specification::and)
//                    .orElse(Specification.where(null));
//        }
//
//        if(domainSpecification instanceof DomainSpecification.Or<T> c) {
//            return (root, query, criteriaBuilder) -> criteriaBuilder.or(
//                    c.getSpecifications().stream()
//                            .map(sub -> domainSpecificationToSpecification(sub).toPredicate(root, query, criteriaBuilder))
//                            .toArray(Predicate[]::new));
//        }
//
//        throw new UnsupportedDomainCriterionException(domainSpecification.getClass().getName());
//    }
//}