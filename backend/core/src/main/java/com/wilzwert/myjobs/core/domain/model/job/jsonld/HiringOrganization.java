package com.wilzwert.myjobs.core.domain.model.job.jsonld;


/**
 * @author Wilhelm Zwertvaegher
 * Date:04/04/2025
 * Time:13:21
 */

public class HiringOrganization {
    private String name;
    private String legalName;
    private String url;
    private String logo;

    public HiringOrganization() {}

    public HiringOrganization(String name, String legalName, String url) {
        this.name = name;
        this.legalName = legalName;
        this.url = url;
    }

    public HiringOrganization(String name) {
        this.name = name;
        this.legalName = "";
        this.url = "";
    }

    public HiringOrganization(String name, String url) {
        this.name = name;
        this.legalName = "";
        this.url = url;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLegalName(String legalName) {
        this.legalName = legalName;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String name() {
        return name;
    }
    public String legalName() {
        return legalName;
    }
    public String url() {
        return url;
    }
    public String logo() {
        return logo;
    }
}