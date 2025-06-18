package com.wilzwert.myjobs.infrastructure.persistence.mongo.transaction;


import com.wilzwert.myjobs.core.domain.shared.ports.driven.transaction.TransactionProvider;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Supplier;

/**
 * @author Wilhelm Zwertvaegher
 * Date:06/06/2025
 * Time:16:19
 */
@Component
public class TransactionProviderAdapter implements TransactionProvider {
    @Override
    @Transactional
    public <T> T executeInTransaction(Supplier<T> supplier) {
        return supplier.get();
    }
}
