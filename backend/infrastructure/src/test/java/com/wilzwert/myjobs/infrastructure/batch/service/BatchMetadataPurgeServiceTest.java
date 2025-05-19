package com.wilzwert.myjobs.infrastructure.batch.service;


/**
 * @author Wilhelm Zwertvaegher
 * Date:16/05/2025
 * Time:13:51
 */
/*
@ExtendWith(MockitoExtension.class)
class BatchMetadataPurgeServiceTest {

    @Mock
    JdbcTemplate jdbcTemplate;

    @InjectMocks
    BatchMetadataPurgeService purgeService;

    @Test
    void purgeBatchMetadataOlderThan_shouldCallAllDeleteQueries() {
        when(jdbcTemplate.update(anyString())).thenReturn(1);

        int deleted = purgeService.purgeBatchMetadataOlderThan(Duration.ofDays(30));

        // 5 requêtes delete, chacune retourne 1
        assertEquals(5, deleted);

        verify(jdbcTemplate, times(5)).update(anyString());
    }
}*/