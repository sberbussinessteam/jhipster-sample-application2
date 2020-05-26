package com.mycompany.myapp.config;

import io.github.jhipster.config.JHipsterProperties;
import io.github.jhipster.config.cache.PrefixedKeyGenerator;
import java.util.concurrent.TimeUnit;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.expiry.CreatedExpiryPolicy;
import javax.cache.expiry.Duration;
import org.hibernate.cache.jcache.ConfigSettings;
import org.redisson.Redisson;
import org.redisson.config.Config;
import org.redisson.jcache.configuration.RedissonConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cache.JCacheManagerCustomizer;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.info.GitProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.serviceregistry.Registration;
import org.springframework.context.annotation.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfiguration {
    private GitProperties gitProperties;
    private BuildProperties buildProperties;

    @Bean
    public javax.cache.configuration.Configuration<Object, Object> jcacheConfiguration(JHipsterProperties jHipsterProperties) {
        MutableConfiguration<Object, Object> jcacheConfig = new MutableConfiguration<>();
        Config config = new Config();
        if (jHipsterProperties.getCache().getRedis().isCluster()) {
            config.useClusterServers().addNodeAddress(jHipsterProperties.getCache().getRedis().getServer());
        } else {
            config.useSingleServer().setAddress(jHipsterProperties.getCache().getRedis().getServer()[0]);
        }
        jcacheConfig.setStatisticsEnabled(true);
        jcacheConfig.setExpiryPolicyFactory(
            CreatedExpiryPolicy.factoryOf(new Duration(TimeUnit.SECONDS, jHipsterProperties.getCache().getRedis().getExpiration()))
        );
        return RedissonConfiguration.fromInstance(Redisson.create(config), jcacheConfig);
    }

    @Bean
    public HibernatePropertiesCustomizer hibernatePropertiesCustomizer(javax.cache.CacheManager cm) {
        return hibernateProperties -> hibernateProperties.put(ConfigSettings.CACHE_MANAGER, cm);
    }

    @Bean
    public JCacheManagerCustomizer cacheManagerCustomizer(javax.cache.configuration.Configuration<Object, Object> jcacheConfiguration) {
        return cm -> {
            createCache(cm, com.mycompany.myapp.repository.UserRepository.USERS_BY_LOGIN_CACHE, jcacheConfiguration);
            createCache(cm, com.mycompany.myapp.repository.UserRepository.USERS_BY_EMAIL_CACHE, jcacheConfiguration);
            createCache(cm, com.mycompany.myapp.domain.User.class.getName(), jcacheConfiguration);
            createCache(cm, com.mycompany.myapp.domain.Authority.class.getName(), jcacheConfiguration);
            createCache(cm, com.mycompany.myapp.domain.User.class.getName() + ".authorities", jcacheConfiguration);
            createCache(cm, com.mycompany.myapp.domain.Region.class.getName(), jcacheConfiguration);
            createCache(cm, com.mycompany.myapp.domain.Country.class.getName(), jcacheConfiguration);
            createCache(cm, com.mycompany.myapp.domain.Location.class.getName(), jcacheConfiguration);
            createCache(cm, com.mycompany.myapp.domain.Department.class.getName(), jcacheConfiguration);
            createCache(cm, com.mycompany.myapp.domain.Department.class.getName() + ".employees", jcacheConfiguration);
            createCache(cm, com.mycompany.myapp.domain.Task.class.getName(), jcacheConfiguration);
            createCache(cm, com.mycompany.myapp.domain.Task.class.getName() + ".jobs", jcacheConfiguration);
            createCache(cm, com.mycompany.myapp.domain.Employee.class.getName(), jcacheConfiguration);
            createCache(cm, com.mycompany.myapp.domain.Employee.class.getName() + ".jobs", jcacheConfiguration);
            createCache(cm, com.mycompany.myapp.domain.Job.class.getName(), jcacheConfiguration);
            createCache(cm, com.mycompany.myapp.domain.Job.class.getName() + ".tasks", jcacheConfiguration);
            createCache(cm, com.mycompany.myapp.domain.JobHistory.class.getName(), jcacheConfiguration);
            // jhipster-needle-redis-add-entry
        };
    }

    private void createCache(
        javax.cache.CacheManager cm,
        String cacheName,
        javax.cache.configuration.Configuration<Object, Object> jcacheConfiguration
    ) {
        javax.cache.Cache<Object, Object> cache = cm.getCache(cacheName);
        if (cache == null) {
            cm.createCache(cacheName, jcacheConfiguration);
        }
    }

    @Autowired(required = false)
    public void setGitProperties(GitProperties gitProperties) {
        this.gitProperties = gitProperties;
    }

    @Autowired(required = false)
    public void setBuildProperties(BuildProperties buildProperties) {
        this.buildProperties = buildProperties;
    }

    @Bean
    public KeyGenerator keyGenerator() {
        return new PrefixedKeyGenerator(this.gitProperties, this.buildProperties);
    }
}
