package com.wilzwert.myjobs.core.domain.model.job.jsonld;


/**
 * @author Wilhelm Zwertvaegher
 */
public class BaseSalary {
    private String currency;
    private BaseSalaryValue value;
    private String numberValue;

    public BaseSalary() {
        this.currency = null;
        this.value = null;
        this.numberValue = null;
    }

    public BaseSalary(String stringValue) {
        this.currency = null;
        this.value = null;
        this.numberValue = stringValue;
    }

    public BaseSalary(String currency, BaseSalaryValue value) {
        this.currency = currency;
        this.value = value;
        this.numberValue = null;
    }

    public BaseSalary(Long value) {
        this.currency = null;
        this.value = null;
        this.numberValue = String.valueOf(value);
    }

    public String currency() {
        return currency;
    }

    public BaseSalaryValue value() {
        return value;
    }

    public String numberValue() {
        return numberValue;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public void setValue(BaseSalaryValue value) {
        this.value = value;
    }

    public void setNumberValue(String numberValue) {
        this.numberValue = numberValue;
    }
}
