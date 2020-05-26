package com.mycompany.myapp.repository.search;

import com.mycompany.myapp.domain.JobHistory;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;


/**
 * Spring Data Elasticsearch repository for the {@link JobHistory} entity.
 */
public interface JobHistorySearchRepository extends ElasticsearchRepository<JobHistory, Long> {
}
