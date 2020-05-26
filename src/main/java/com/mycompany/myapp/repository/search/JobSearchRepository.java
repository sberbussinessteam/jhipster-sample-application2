package com.mycompany.myapp.repository.search;

import com.mycompany.myapp.domain.Job;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;


/**
 * Spring Data Elasticsearch repository for the {@link Job} entity.
 */
public interface JobSearchRepository extends ElasticsearchRepository<Job, Long> {
}
