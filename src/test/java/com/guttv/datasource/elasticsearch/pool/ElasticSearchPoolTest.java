package com.guttv.datasource.elasticsearch.pool;

import com.guttv.datasource.elasticsearch.builder.ElasticSearchQueryBuilder;
import com.guttv.datasource.elasticsearch.client.ElasticSearchClient;
import com.guttv.datasource.elasticsearch.pool.config.ElasticSearchPoolConfig;
import com.guttv.datasource.elasticsearch.template.ElasticSearchTemplate;
import com.guttv.datasource.elasticsearch.vo.EsQueryVo;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.*;


@RunWith(SpringRunner.class)
@SpringBootTest
public class ElasticSearchPoolTest {
    private Logger logger = LoggerFactory.getLogger(ElasticSearchPoolTest.class);

    @Autowired
    private ElasticSearchPoolConfig elasticSearchPoolConfig;
    @Autowired
    private ElasticSearchTemplate elasticSearchTemplate;

    private String hostname = "10.3.1.2";
    private int port = 9200;
    private String clusterName = "zym";

    private String userCode = "1300110023765669043666803";

    @Test
    public void testInitPoolConfig() {
        elasticSearchPoolConfig.getPool();
        String str = elasticSearchPoolConfig.toString();
        logger.info("测试配置" + elasticSearchPoolConfig.getMaxIdlePerKey() + "");
        logger.info("debug" + str);
    }

    @Test
    public void testGetPool() {
        long start = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++) {
            RestHighLevelClient client = null;
//            client = ElasticSearchPool.getClient(clusterName);
//            client = new RestHighLevelClient(RestClient.builder(new HttpHost(hostname,port,"http")));
            logger.info("获取第" + (i + 1) + "个连接" + client);
//            ElasticSearchPool.returnClient(clusterName,client);
        }
        long end = System.currentTimeMillis();
        logger.info("总共耗时:" + (end - start));
    }

    @Test
    public void testSearch() {
        Map map = new HashMap();
        map.put("userCode", userCode);
        ElasticSearchQueryBuilder queryMapBuilder = ElasticSearchQueryBuilder.build("ums_userfavorite","doc");
        queryMapBuilder.setQueryObj(map)
                .setReturnExcludes("userCode","serviceC")
                .setReturnIncludes("userCode");
        Map resultMap = (Map) elasticSearchTemplate.querySingle(queryMapBuilder);
        logger.info("end");
    }

    @Test
    public void testGetPoolConcurrent() {
        long start = System.currentTimeMillis();
        Thread[] threads = new Thread[100];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new MyThread(i);
            threads[i].start();
        }
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                logger.error("线程加入失败：", e);
            }
        }

        long end = System.currentTimeMillis();
        logger.info("总共耗时:" + (end - start));
    }

    public List<EsQueryVo> queryPageInMap(Map criteriaMaps, String sortField, SortOrder sortOrder, String index, String type, int pageNum, int pageLimit) {
        List<EsQueryVo> queryVos = new ArrayList<>();
        try (ElasticSearchClient elasticSearchClient = ElasticSearchPool.getClient("zym")) {
//        RestHighLevelClient highLevelClient = new RestHighLevelClient(RestClient.builder(new HttpHost(hostname,port,"http")));

            SearchRequest searchRequest = new SearchRequest(index);
            searchRequest.types(type);
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            //查询条件
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            Set<Map.Entry<String, String>> entries = criteriaMaps.entrySet();
            for (Map.Entry<String, String> entry : entries) {
                TermQueryBuilder criteria = QueryBuilders.termQuery(entry.getKey(), entry.getValue());
                boolQueryBuilder.must(criteria);
            }
            searchSourceBuilder.query(boolQueryBuilder);
            //分页
            if (pageLimit > 0 && pageNum > 0) {
                int pageStart = pageLimit * (pageNum - 1);
                searchSourceBuilder.from(pageStart);
                searchSourceBuilder.size(pageLimit);
            }
            //排序
            if (sortField != null && !sortField.isEmpty()) {
                FieldSortBuilder sortBuilder = SortBuilders.fieldSort(sortField);
                sortBuilder.order(sortOrder);
                searchSourceBuilder.sort(sortBuilder);
            }
//            logger.info("es查询语句：" + searchSourceBuilder.toString());
            //封装
            searchRequest.source(searchSourceBuilder);
//            logger.info("es查询请求：" + searchRequest.toString());
            SearchResponse searchResponse = elasticSearchClient.getRestHighLevelClient().search(searchRequest, RequestOptions.DEFAULT);
            logger.info("es查询结果：" + searchResponse.toString());
            SearchHits hits = searchResponse.getHits();
            if (hits == null || hits.totalHits <= 0) {
                return queryVos;
            }
            for (SearchHit hit : hits) {
                EsQueryVo esQueryVo = new EsQueryVo();
                esQueryVo.setDocId(hit.getId());
                esQueryVo.setContentJsonString(hit.getSourceAsString());
                queryVos.add(esQueryVo);
            }
        } catch (IOException e) {
            logger.error("查询es时出错", e);
        }
        return queryVos;
    }


    class MyThread extends Thread {
        private int threadFlag;

        public MyThread(int threadFlag) {
            this.threadFlag = threadFlag;
        }

        public void run() {
            for (int i = 0; i < 100; i++) {
                int random = new Random().nextInt(1000);
                RestHighLevelClient client = null;
//                client = ElasticSearchPool.getClient("zym");
                Map map = new HashMap();
                map.put("userCode", userCode);
//            client = new RestHighLevelClient(RestClient.builder(new HttpHost(hostname,port,"http")));
                queryPageInMap(map, null, null, "ums_userfavorite", "doc", 1, 10000);
//                ElasticSearchPool.returnClient("zym",client);
//                try {
//                    Thread.sleep(random);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
                logger.info(threadFlag + "号线程获取第" + (i + 1) + "个连接" + client + "，睡眠时间：" + random);
            }
        }
    }


}
