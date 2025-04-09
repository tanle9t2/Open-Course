package com.tp.opencourse.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;

@Configuration
public class ElasticConfig extends ElasticsearchConfiguration{

    @Override
    public ClientConfiguration clientConfiguration() {
        return ClientConfiguration.builder()
                .connectedTo("localhost:9200").build();
    }

    @Override
    public RestClient elasticsearchRestClient(ClientConfiguration clientConfiguration) {
        return  RestClient.builder(new HttpHost("localhost", 9200, "http")).build();
    }
}
