package com.guttv.datasource.elasticsearch.pool.factory;

import com.guttv.datasource.elasticsearch.client.ElasticSearchClient;
import com.guttv.datasource.elasticsearch.pool.config.ElasticSearchPoolConfig;
import org.apache.commons.pool2.KeyedPooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * es连接池工厂，用来管理池对象，支持多个集群同时连接
 */
@Component
public class ElasticSearchPoolFactory implements KeyedPooledObjectFactory<String, ElasticSearchClient> {
    private final Logger logger = LoggerFactory.getLogger(ElasticSearchPoolFactory.class);

    @Autowired
    private ElasticSearchPoolConfig elasticSearchPoolConfig;


    /**
     * 获取连接
     *
     * @param s 集群名称
     * @return
     * @throws Exception
     */
    @Override
    public PooledObject<ElasticSearchClient> makeObject(String s) throws Exception {
        List<HttpHost> httpHosts = new ArrayList<>();
        for (ElasticSearchPoolConfig.ElasticSearchClusterConfig elasticSearchClusterConfig : elasticSearchPoolConfig.getPool()) {
            if (s.equals(elasticSearchClusterConfig.getClusterName())) {
                for (ElasticSearchPoolConfig.ElasticSearchClusterConfig.ElasticSearchNodeConfig elasticSearchNodeConfig : elasticSearchClusterConfig.getNodes()) {
                    httpHosts.add(new HttpHost(elasticSearchNodeConfig.getIp(), elasticSearchNodeConfig.getPort(), elasticSearchNodeConfig.getSchema()));
                }
            }
        }
        HttpHost[] httpHosts1 = new HttpHost[httpHosts.size()];
        httpHosts1 = httpHosts.toArray(httpHosts1);
        RestHighLevelClient restHighLevelClient = new RestHighLevelClient(RestClient.builder(httpHosts1));
        ElasticSearchClient elasticSearchClient = new ElasticSearchClient(restHighLevelClient,s);
        return new DefaultPooledObject(elasticSearchClient);
    }



    /**
     * 销毁客户端
     *
     * @param s
     * @param pooledObject
     * @throws Exception
     */
    @Override
    public void destroyObject(String s, PooledObject<ElasticSearchClient> pooledObject) throws Exception {
        ElasticSearchClient elasticSearchClient = pooledObject.getObject();
        if(elasticSearchClient==null){
            pooledObject=null;
            return;
        }
        if(elasticSearchClient.getRestHighLevelClient()==null){
            pooledObject=null;
            return;
        }
        RestHighLevelClient restHighLevelClient = elasticSearchClient.getRestHighLevelClient();
        if (restHighLevelClient.ping(RequestOptions.DEFAULT)) {
            restHighLevelClient.close();
            pooledObject=null;
        }
    }

    /**
     * 验证客户端有效性
     *
     * @param s
     * @param pooledObject
     * @return
     */
    @Override
    public boolean validateObject(String s, PooledObject<ElasticSearchClient> pooledObject) {
        ElasticSearchClient elasticSearchClient = pooledObject.getObject();
        if(elasticSearchClient==null){
            return false;
        }
        if(elasticSearchClient.getRestHighLevelClient()==null){
            return false;
        }
        RestHighLevelClient restHighLevelClient = elasticSearchClient.getRestHighLevelClient();
        try {
            return restHighLevelClient.ping(RequestOptions.DEFAULT);
        } catch (Exception e) {
            logger.error("集群" + s + "验证es客户端有效性失败！", e);
        }
        return false;
    }

    /**
     * 唤醒客户端
     *
     * @param s
     * @param pooledObject
     * @throws Exception
     */
    @Override
    public void activateObject(String s, PooledObject<ElasticSearchClient> pooledObject) throws Exception {
//        RestHighLevelClient restHighLevelClient = pooledObject.getObject();
    }

    /**
     * 挂起客户端
     *
     * @param s
     * @param pooledObject
     * @throws Exception
     */
    @Override
    public void passivateObject(String s, PooledObject<ElasticSearchClient> pooledObject) throws Exception {
//        RestHighLevelClient restHighLevelClient = pooledObject.getObject();
    }
}
