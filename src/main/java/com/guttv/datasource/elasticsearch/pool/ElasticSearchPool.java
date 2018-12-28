package com.guttv.datasource.elasticsearch.pool;

import com.guttv.datasource.elasticsearch.client.ElasticSearchClient;
import com.guttv.datasource.elasticsearch.pool.config.ElasticSearchPoolConfig;
import com.guttv.datasource.elasticsearch.pool.factory.ElasticSearchPoolFactory;
import org.apache.commons.pool2.impl.GenericKeyedObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


@Scope("singleton")
@Component
public class ElasticSearchPool {
    private static final Logger logger = LoggerFactory.getLogger(ElasticSearchPool.class);


//    private final ElasticSearchPoolFactory elasticSearchPoolFactory;
//    private final ElasticSearchPoolConfig elasticSearchPoolConfig;

    private volatile static GenericKeyedObjectPool<String, ElasticSearchClient> elasticSearchPool;

    public static String DEFAULT_CLUSTER = "";


    @Autowired
    private ElasticSearchPool(ElasticSearchPoolFactory elasticSearchPoolFactory, ElasticSearchPoolConfig elasticSearchPoolConfig) {
//        this.elasticSearchPoolConfig = elasticSearchPoolConfig;
        logger.info("读取elasticsearch连接池池配置成功：" + elasticSearchPoolConfig.toString() + "，当前默认集群：" + elasticSearchPoolConfig.getDefaultCluster());
//        this.elasticSearchPoolFactory = elasticSearchPoolFactory;
        synchronized (this) {//防止多例模式下出现一个连接池被初始化两次的问题
            if (elasticSearchPool == null) {
                synchronized (this) {
                    this.elasticSearchPool = new GenericKeyedObjectPool<String, ElasticSearchClient>(elasticSearchPoolFactory, elasticSearchPoolConfig);
                }
            }
            DEFAULT_CLUSTER = elasticSearchPoolConfig.getDefaultCluster();
            for (ElasticSearchPoolConfig.ElasticSearchClusterConfig elasticSearchClusterConfig : elasticSearchPoolConfig.getPool()) {
                try {
                    String clusterName = elasticSearchClusterConfig.getClusterName();
                    elasticSearchPool.preparePool(clusterName);
                    logger.info("预热elasticsearch连接池" + clusterName + "成功,当前连接数:" + elasticSearchPool.getNumActivePerKey().get(clusterName));
                } catch (Exception e) {
                    logger.error("预热elasticsearch连接池失败，索引名：" + elasticSearchClusterConfig.getClusterName(), e);
                }
            }
        }
    }


    public static ElasticSearchClient getClient() {
        return getClient(DEFAULT_CLUSTER);
    }

    public static ElasticSearchClient getClient(String clusterName) {
        try {
            ElasticSearchClient elasticSearchClient = elasticSearchPool.borrowObject(clusterName);
            logger.info("es获取客户端:" + elasticSearchClient);
            return elasticSearchClient;
        } catch (Exception e) {
            logger.error("获取es客户端失败！", e);
        }
        return null;
    }

    public static void returnClient(ElasticSearchClient client) {
        try {
            String clientInfo = client.toString();
            elasticSearchPool.returnObject(client.getCluster(), client);
            logger.info("es关闭客户端:" + clientInfo);
        } catch (Exception e) {
            logger.error("归还es客户端失败！", e);
        }
    }
}
