package com.wilzwert.myjobs.core.domain.shared.ports.driven.transaction;


import java.util.function.Supplier;

/**
 * @author Wilhelm Zwertvaegher
 * Date:06/06/2025
 * Time:16:00
 */
@FunctionalInterface
public interface TransactionProvider {
    <T> T executeInTransaction(Supplier<T> supplier);
}