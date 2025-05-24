package com.wilzwert.myjobs.infrastructure.batch.service;


/**
 * @author Wilhelm Zwertvaegher
 */
/*
@Service
public class BatchMetadataPurgeService {

    private final JdbcTemplate jdbcTemplate;

    public BatchMetadataPurgeService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public int purgeBatchMetadataOlderThan(Duration duration) {
        String interval = toPostgresInterval(duration);

        int totalDeleted = 0;

        totalDeleted += jdbcTemplate.update(
                "DELETE FROM BATCH_STEP_EXECUTION_CONTEXT WHERE STEP_EXECUTION_ID IN (" +
                        "SELECT STEP_EXECUTION_ID FROM BATCH_STEP_EXECUTION WHERE JOB_EXECUTION_ID IN (" +
                        "SELECT JOB_EXECUTION_ID FROM BATCH_JOB_EXECUTION WHERE END_TIME < now() - INTERVAL '" + interval + "'))");

        totalDeleted += jdbcTemplate.update(
                "DELETE FROM BATCH_STEP_EXECUTION WHERE JOB_EXECUTION_ID IN (" +
                        "SELECT JOB_EXECUTION_ID FROM BATCH_JOB_EXECUTION WHERE END_TIME < now() - INTERVAL '" + interval + "')");

        totalDeleted += jdbcTemplate.update(
                "DELETE FROM BATCH_JOB_EXECUTION_PARAMS WHERE JOB_EXECUTION_ID IN (" +
                        "SELECT JOB_EXECUTION_ID FROM BATCH_JOB_EXECUTION WHERE END_TIME < now() - INTERVAL '" + interval + "')");

        totalDeleted += jdbcTemplate.update(
                "DELETE FROM BATCH_JOB_EXECUTION WHERE END_TIME < now() - INTERVAL '" + interval + "'");

        totalDeleted += jdbcTemplate.update(
                "DELETE FROM BATCH_JOB_INSTANCE WHERE JOB_INSTANCE_ID NOT IN (" +
                        "SELECT JOB_INSTANCE_ID FROM BATCH_JOB_EXECUTION)");

        return totalDeleted;
    }

    private String toPostgresInterval(Duration duration) {
        long days = duration.toDays();
        return days + " days";
    }
}
*/