package com.wilzwert.myjobs.infrastructure.utility;


import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;

/**
 * @author Wilhelm Zwertvaegher
 */

public class MemoryAppender extends ListAppender<ILoggingEvent> {
    public boolean contains(String string, Level level) {
        System.out.println(list);
        return list.stream()
                .anyMatch(event -> event.toString().contains(string)
                        && event.getLevel().equals(level));
    }
}