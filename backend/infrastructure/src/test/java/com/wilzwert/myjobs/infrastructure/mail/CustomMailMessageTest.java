package com.wilzwert.myjobs.infrastructure.mail;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Wilhelm Zwertvaegher
 */

public class CustomMailMessageTest {

    @Test
    void shouldPushVariable() {
        CustomMailMessage message = new CustomMailMessage("template", "recipiant@myjobs", "MyJobs", "Test subject", "EN");

        assertEquals(0, message.getVariables().size());

        message.setVariable("myvar", "myvalue");
        assertEquals(1, message.getVariables().size());
        assertEquals("myvalue", message.getVariables().get("myvar"));
    }
}
