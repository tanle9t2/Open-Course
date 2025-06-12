package com.tp.opencourse.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;

@Configuration
public class ElasticConfig extends ElasticsearchConfiguration {
    @Autowired
    private Dotenv dotenv;


    @Override
    public ClientConfiguration clientConfiguration() {
        String ELASTIC_HOST = dotenv.get("ELASTIC_HOST");
        String ELASTIC_PORT = dotenv.get("ELASTIC_PORT");
        return ClientConfiguration.builder()
                .connectedTo(ELASTIC_HOST + ":" + ELASTIC_PORT).build();
    }

    @Override
    public RestClient elasticsearchRestClient(ClientConfiguration clientConfiguration) {
        String ELASTIC_HOST = dotenv.get("ELASTIC_HOST");
        int ELASTIC_PORT = Integer.parseInt(dotenv.get("ELASTIC_PORT"));
        return RestClient.builder(new HttpHost(ELASTIC_HOST, ELASTIC_PORT, "http")).build();
    }
}
