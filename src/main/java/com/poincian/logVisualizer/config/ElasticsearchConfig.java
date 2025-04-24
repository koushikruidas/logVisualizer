package com.poincian.logVisualizer.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

@Configuration
public class ElasticsearchConfig {

    @Value("${spring.elasticsearch.rest.uris}")
    private String elasticsearchUri;

    @Value("${spring.elasticsearch.rest.username}")
    private String username;

    @Value("${spring.elasticsearch.rest.password}")
    private String password;

    @Bean
    public ElasticsearchClient elasticsearchClient() {
        // Encode username and password in Base64
        String auth = Base64Coder.encodeString((username + ":" + password));

        RestClientBuilder builder = RestClient.builder(HttpHost.create(elasticsearchUri))
                .setDefaultHeaders(new org.apache.http.Header[]{
                        new org.apache.http.message.BasicHeader(HttpHeaders.AUTHORIZATION, "Basic " + auth)
                });

        RestClient restClient = builder.build();
        RestClientTransport transport = new RestClientTransport(restClient, new co.elastic.clients.json.jackson.JacksonJsonpMapper());

        return new ElasticsearchClient(transport);
    }
}