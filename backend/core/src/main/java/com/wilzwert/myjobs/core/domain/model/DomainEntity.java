package com.wilzwert.myjobs.core.domain.model;

/**
 * @author Wilhelm Zwertvaegher
 * Date:18/03/2025
 * Time:13:17
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
}
