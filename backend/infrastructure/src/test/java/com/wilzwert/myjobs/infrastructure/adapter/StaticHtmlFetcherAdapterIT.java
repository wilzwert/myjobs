package com.wilzwert.myjobs.infrastructure.adapter;


import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

/**
 * @author Wilhelm Zwertvaegher
 * Date:10/04/2025
 * Time:15:01
 */
@EnabledIfSystemProperty(named = "spring.profiles.active", matches = "integration")
public class StaticHtmlFetcherAdapterIT {

}
