package com.wilzwert.myjobs.core.domain.model;

import com.wilzwert.myjobs.core.domain.shared.exception.IncompleteAggregateException;

/**
 * @author Wilhelm Zwertvaegher
 */

public abstract class DomainEntity<I> {

    @Override
    public boolean equals(Object o) {
        if(this == o) {return true;}
        if(o == null || getClass() != o.getClass()) {return false;}
        DomainEntity<?> that = (DomainEntity<?>) o;
        return this.getId().equals(that.getId());
    }

    public abstract I getId();

    protected void requireLoadedProperty(Object property) {
        if(null == property) {
            throw new IncompleteAggregateException();
        }
    }
}
