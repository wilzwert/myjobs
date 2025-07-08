package com.wilzwert.myjobs.core.domain.model.job.jsonld;


/**
 * @author Wilhelm Zwertvaegher
 */

public class HiringOrganization {
    private String name;
    private String legalName;
    private String url;
    private String logo;

    public HiringOrganization() {
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

    public String getName() {
        return name;
    }
    public String getLegalName() {
        return legalName;
    }
    public String getUrl() {
        return url;
    }
    public String getLogo() {
        return logo;
    }
}