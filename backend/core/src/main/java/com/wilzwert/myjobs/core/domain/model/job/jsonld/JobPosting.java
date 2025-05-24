package com.wilzwert.myjobs.core.domain.model.job.jsonld;


/**
 * @author Wilhelm Zwertvaegher
 */

public record JobPosting (
    String title,
    String datePosted,
    String description,
    String experienceRequirements,
    String qualifications,
    String industry,
    String employmentType,
    String url,
    String educationRequirements,
    String validThrough,
    JobLocation jobLocation,
    HiringOrganization hiringOrganization,
    BaseSalary baseSalary,
    String salaryCurrency
) {

    public String computeSalary() {
        if(baseSalary == null) {
            return "";
        }

        if(baseSalary.numberValue() != null) {
            return baseSalary().numberValue()+(salaryCurrency() != null ? "  "+salaryCurrency() : "");
        }

        if(baseSalary.value() == null) {
            return "";
        }

        // minValue implies that we have a MonetaryAmount with a range
        if(baseSalary.value().minValue() != null) {
            return baseSalary.value().minValue()+" - "+baseSalary.value().maxValue()+ " " +baseSalary.currency()+" / "+baseSalary.value().unitText();
        }

        // a price range
        if(baseSalary.value().minPrice() != null) {
            return baseSalary.value().minPrice()+" - "+baseSalary.value().maxPrice()+ " " +baseSalary.value().priceCurrency();
        }

        return "";
    }
}