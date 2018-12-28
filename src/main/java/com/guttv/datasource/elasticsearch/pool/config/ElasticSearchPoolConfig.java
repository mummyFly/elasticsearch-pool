package com.guttv.datasource.elasticsearch.pool.config;

import org.apache.commons.pool2.impl.GenericKeyedObjectPoolConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "elasticsearch")
public class ElasticSearchPoolConfig extends GenericKeyedObjectPoolConfig {
    private ElasticSearchClusterConfig[] pool;
    private String defaultCluster;
    private int minIdlePerKey;
    private int maxIdlePerKey;
    private int maxTotalPerKey;
    private int maxTotal;
    private boolean testOnBorrow;
    private boolean testWhileIdle;
    private boolean testOnReturn;
    private boolean testOnCreate;
    private long maxWaitMillis;

    public String getDefaultCluster() {
        return defaultCluster;
    }

    public void setDefaultCluster(String defaultCluster) {
        this.defaultCluster = defaultCluster;
    }

    @Override
    public boolean getTestOnReturn() {
        return testOnReturn;
    }

    @Override
    public void setTestOnReturn(boolean testOnReturn) {
        this.testOnReturn = testOnReturn;
    }

    @Override
    public boolean getTestOnCreate() {
        return testOnCreate;
    }

    @Override
    public void setTestOnCreate(boolean testOnCreate) {
        this.testOnCreate = testOnCreate;
    }



    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        int i = 0;
        for (ElasticSearchClusterConfig elasticSearchClusterConfig : pool) {
            sb.append("\n集群").append(i).append(":").append(elasticSearchClusterConfig.getClusterName()).append("初始化：\n");
            int j = 0;
            for (ElasticSearchClusterConfig.ElasticSearchNodeConfig elasticSearchNodeConfig : elasticSearchClusterConfig.getNodes()) {
                sb.append("节点").append(j).append("host:").append(elasticSearchNodeConfig.getIp()).append(",port:").append(elasticSearchNodeConfig.getPort()).append("\n");
                j++;
            }
            i++;
        }
        sb.append("testOnBorrow:").append(testOnBorrow).append("\n")
                .append("testWhileIdle:").append(testWhileIdle).append("\n")
                .append("testOnReturn:").append(testOnReturn).append("\n")
                .append("testOnCreate:").append(testOnCreate).append("\n")
                .append("maxWaitMillis:").append(maxWaitMillis).append("\n")
                .append("minIdlePerKey:").append(minIdlePerKey).append("\n")
                .append("maxIdlePerKey:").append(maxIdlePerKey).append("\n")
                .append("maxTotalPerKey:").append(maxTotalPerKey).append("\n")
                .append("maxTotal:").append(maxTotal).append("\n");
        return sb.toString();
    }

    @Override
    public long getMaxWaitMillis() {
        return maxWaitMillis;
    }

    @Override
    public void setMaxWaitMillis(long maxWaitMillis) {
        this.maxWaitMillis = maxWaitMillis;
    }

    @Override
    public int getMinIdlePerKey() {
        return minIdlePerKey;
    }

    @Override
    public void setMinIdlePerKey(int minIdlePerKey) {
        this.minIdlePerKey = minIdlePerKey;
    }

    @Override
    public int getMaxIdlePerKey() {
        return maxIdlePerKey;
    }

    @Override
    public void setMaxIdlePerKey(int maxIdlePerKey) {
        this.maxIdlePerKey = maxIdlePerKey;
    }

    @Override
    public int getMaxTotalPerKey() {
        return maxTotalPerKey;
    }

    @Override
    public void setMaxTotalPerKey(int maxTotalPerKey) {
        this.maxTotalPerKey = maxTotalPerKey;
    }

    @Override
    public int getMaxTotal() {
        return maxTotal;
    }

    @Override
    public void setMaxTotal(int maxTotal) {
        this.maxTotal = maxTotal;
    }

    @Override
    public boolean getTestOnBorrow() {
        return testOnBorrow;
    }

    @Override
    public void setTestOnBorrow(boolean testOnBorrow) {
        this.testOnBorrow = testOnBorrow;
    }

    @Override
    public boolean getTestWhileIdle() {
        return testWhileIdle;
    }

    @Override
    public void setTestWhileIdle(boolean testWhileIdle) {
        this.testWhileIdle = testWhileIdle;
    }

    public ElasticSearchClusterConfig[] getPool() {
        return pool;
    }

    public void setPool(ElasticSearchClusterConfig[] pool) {
        this.pool = pool;
    }

    public static class ElasticSearchClusterConfig {
        private String clusterName;
        private ElasticSearchNodeConfig[] nodes;

        public String getClusterName() {
            return clusterName;
        }

        public void setClusterName(String clusterName) {
            this.clusterName = clusterName;
        }

        public ElasticSearchNodeConfig[] getNodes() {
            return nodes;
        }

        public void setNodes(ElasticSearchNodeConfig[] nodes) {
            this.nodes = nodes;
        }

        public static class ElasticSearchNodeConfig {
            private String name;
            private String ip;
            private Integer port;
            private String schema;

            public String getSchema() {
                return schema;
            }

            public void setSchema(String schema) {
                this.schema = schema;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getIp() {
                return ip;
            }

            public void setIp(String ip) {
                this.ip = ip;
            }

            public Integer getPort() {
                return port;
            }

            public void setPort(Integer port) {
                this.port = port;
            }
        }
    }


}
