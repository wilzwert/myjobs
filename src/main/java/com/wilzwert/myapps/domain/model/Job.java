package com.wilzwert.myapps.domain.model;


import java.util.List;
import java.util.UUID;

/**
 * @author Wilhelm Zwertvaegher
 * Date:12/03/2025
 * Time:15:32
 */

public class Job {
    private UUID id;

    private String url;

    private JobStatus status;

    private String title;

    private String description;

    private String profile;

    private User user;

    private List<Activity> activities;




}
