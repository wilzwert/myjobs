package com.wilzwert.myjobs.infrastructure.adapter;


import com.wilzwert.myjobs.infrastructure.configuration.AbstractBaseIntegrationTest;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

/**
 * @author Wilhelm Zwertvaegher
 */
@EnabledIfSystemProperty(named = "spring.profiles.active", matches = "integration")
public class StaticHtmlFetcherAdapterIT extends AbstractBaseIntegrationTest {

}
