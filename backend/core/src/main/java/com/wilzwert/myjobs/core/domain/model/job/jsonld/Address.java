package com.wilzwert.myjobs.core.domain.model.job.jsonld;


/**
 * @author Wilhelm Zwertvaegher
 * Date:04/04/2025
 * Time:13:22
 */

public class Address {
    private String addressLocality;

    private String postalCode;

    public Address() {}

    public Address(String addressLocality, String postalCode) {
        this.addressLocality = addressLocality;
        this.postalCode = postalCode;

    }
    public Address(String addressLocality) {
        this(addressLocality, "");
    }

    public String addressLocality() {
        return addressLocality;
    }

    public String postalCode() {
        return postalCode;
    }

    public void setAddressLocality(String addressLocality) {
        this.addressLocality = addressLocality;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }
}