package com.wilzwert.myjobs.core.domain.model.job.command;


/**
 * @author Wilhelm Zwertvaegher
 * Date:05/06/2025
 * Time:13:38
 */
public sealed interface UpdateJobCommand permits UpdateJobFullCommand, UpdateJobRatingCommand, UpdateJobStatusCommand, UpdateJobFieldCommand{
}
