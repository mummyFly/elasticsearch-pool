package com.guttv.datasource.elasticsearch.client;

import com.guttv.datasource.elasticsearch.pool.ElasticSearchPool;
import org.elasticsearch.client.RestHighLevelClient;

import java.io.Closeable;
import java.io.IOException;

public class ElasticSearchClient implements Closeable{
    private RestHighLevelClient restHighLevelClient;
    private String cluster;

    public RestHighLevelClient getRestHighLevelClient() {
        return restHighLevelClient;
    }

    public void setRestHighLevelClient(RestHighLevelClient restHighLevelClient) {
        this.restHighLevelClient = restHighLevelClient;
    }

    public String getCluster() {
        return cluster;
    }

    public void setCluster(String cluster) {
        this.cluster = cluster;
    }

    public ElasticSearchClient(RestHighLevelClient client,String cluster){
        this.restHighLevelClient = client;
        this.cluster = cluster;
    }
    @Override
    public void close() throws IOException {
        ElasticSearchPool.returnClient(this);
    }
}
