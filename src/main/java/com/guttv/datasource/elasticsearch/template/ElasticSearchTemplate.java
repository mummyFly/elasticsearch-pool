package com.guttv.datasource.elasticsearch.template;


import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.guttv.datasource.elasticsearch.builder.ElasticSearchQueryBuilder;
import com.guttv.datasource.elasticsearch.client.ElasticSearchClient;
import com.guttv.datasource.elasticsearch.pool.ElasticSearchPool;
import com.guttv.datasource.elasticsearch.vo.EsQueryVo;
import com.guttv.datasource.elasticsearch.vo.JSONArr;
import com.guttv.datasource.elasticsearch.vo.JSONObj;
import com.guttv.datasource.elasticsearch.vo.MapUtils;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.*;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

/**
 * Created by crg on 2018/9/13 16:01
 */
@Component
public class ElasticSearchTemplate {
    private Logger logger = LoggerFactory.getLogger(getClass());

    public int queryCountInMap(Map criteriaMaps, String index, String type){
        return queryCountInMap(criteriaMaps,index,type,ElasticSearchPool.DEFAULT_CLUSTER);
    }
    public int queryCountInMap(Map criteriaMaps, String index, String type,String clusterName){
        List<EsQueryVo> queryVos = new ArrayList<>();
        try (ElasticSearchClient elasticSearchClient = ElasticSearchPool.getClient()){
            RestClient restClient = elasticSearchClient.getRestHighLevelClient().getLowLevelClient();
            String endPoint = "/"+index+"/"+type+"/_count";
            Request request = new Request("GET",endPoint);
            JSONArr jsonArr = new JSONArr();
            criteriaMaps.forEach((k,v)->
                jsonArr.add("term",new JSONObj()
                                .add(k.toString(),v.toString()))
            );
            JSONObj json = new JSONObj()
                    .add("query", new JSONObj()
                            .add("bool",new JSONObj()
                                    .add("must",jsonArr)));
            logger.info("json:"+json);
            request.setJsonEntity(json.toString());
            Response response  = restClient.performRequest(request);
            String responseBody = EntityUtils.toString(response.getEntity());
            Gson gson = new Gson();
            JsonObject result =  gson.fromJson(responseBody, JsonObject.class);
            logger.info("resp:"+responseBody);
            return result.get("count").getAsInt();
        } catch (Exception e) {
            logger.error("查询es时出错", e);
        }
        return 0;
    }



    public boolean insertObject(Object obj, String index, String type, String docId) {
        return this.insertObject(obj,index,type,docId,ElasticSearchPool.DEFAULT_CLUSTER);
    }
    public boolean insertObject(Object obj, String index, String type, String docId,String clusterName) {

        // 2、获取插入请求实例，将存储的数据封装到请求中
        IndexRequest indexRequest = new IndexRequest(index, type);
        // 3、判断传入id是否为空
        if (docId != null && !docId.isEmpty()) {
            indexRequest.id(docId);
        }
        if (obj instanceof Map) {
            indexRequest.source((Map) obj);
        } else {
            Map map = new HashMap();
            try {
                map = MapUtils.object2Map(obj);
            } catch (Exception e) {
                logger.error("对象转换异常！",e);
            }
            indexRequest.source(map);
        }
        return this.insert(indexRequest,clusterName);
    }

    /**
     * 插入数据
     *
     * @param beanJsonString 插入数据的Json格式数据
     * @param index          索引
     * @param type           文档类型
     * @param docId          文档id，插入时使用ES自动生成，如果覆盖数据则需要先查询然后传入
     */
    public boolean insertJsonString(String beanJsonString, String index, String type, String docId) {
        return this.insertJsonString(beanJsonString,index,type,docId,ElasticSearchPool.DEFAULT_CLUSTER);
    }
    public boolean insertJsonString(String beanJsonString, String index, String type, String docId,String clusterName) {

        IndexRequest indexRequest = new IndexRequest(index, type);
        if (docId != null && !docId.isEmpty()) {
            indexRequest.id(docId);
        }

        indexRequest.source(beanJsonString, XContentType.JSON);
        return this.insert(indexRequest,clusterName);
    }

    private boolean insert(IndexRequest indexRequest,String clusterName) {
        try (ElasticSearchClient elasticSearchClient = ElasticSearchPool.getClient()){
            // 4、发送插入请求
            IndexResponse indexResponse = elasticSearchClient.getRestHighLevelClient().index(indexRequest, RequestOptions.DEFAULT);
            logger.info("es插入返回结果：" + indexResponse.toString());
            int successful = indexResponse.getShardInfo().getSuccessful();
            if (successful > 0) {
                return true;
            }
        } catch (IOException e) {
            logger.error("es插入数据时出现异常：", e);
            return false;
        }
        return false;
    }

    /**
     * 根据条件删除
     *
     * @param queryJsonString 删除的Json指令
     * @param index           索引
     * @param type            文档类型
     */
    public boolean deleteByMap(String queryJsonString, String index, String type) {
        return deleteByMap(queryJsonString,index,type,ElasticSearchPool.DEFAULT_CLUSTER);
    }
    public boolean deleteByMap(String queryJsonString, String index, String type,String clusterName) {
        try (ElasticSearchClient elasticSearchClient = ElasticSearchPool.getClient()){
                // 1、获取客户端实例
            RestClient lowLevelClient = elasticSearchClient.getRestHighLevelClient().getLowLevelClient();

            String endPoint = "/" + index + "/" + type + "/_delete_by_query";

            // 2、创建请求
            Request request = new Request("POST", endPoint);

            request.setEntity(new NStringEntity(queryJsonString, ContentType.APPLICATION_JSON));
             lowLevelClient.performRequest(request);
        } catch (IOException e) {
            logger.error("删除es数据时出错！", e);
            return false;
        }
        return true;
    }


    /**
     * 返回单个结果
     *
     * @param map
     * @param index
     * @param docType
     * @return
     */
    private EsQueryVo querySingleInMap(ElasticSearchQueryBuilder queryBuilder) {
        queryBuilder.setPageNum(1);
        queryBuilder.setPageLimit(1);
        EsQueryVo esQueryVo = new EsQueryVo();
        List<EsQueryVo> list = query(queryBuilder);
        if (list.isEmpty()) {
            return esQueryVo;
        }
        return list.get(0);
    }


    public Object querySingle(ElasticSearchQueryBuilder queryBuilder){
        EsQueryVo esQueryVo = querySingleInMap(queryBuilder);
        Gson gson = new Gson();
        return gson.fromJson(esQueryVo.getContentJsonString(),queryBuilder.getQueryObj().getClass());
    }

    @Deprecated
    public EsQueryVo querySingleInMap(Map map,String index,String type){
        EsQueryVo esQueryVo = new EsQueryVo();
        ElasticSearchQueryBuilder queryBuilder = ElasticSearchQueryBuilder.build(index,type).setQueryObj(map);
        List<EsQueryVo> list = this.query(queryBuilder);
        if(!list.isEmpty()){
            return list.get(0);
        }
        return esQueryVo;
    }
    /**
     * 分页查询
     *
     * @param index     索引
     * @param type      存储文档类型
     * @param sortField 排序字段
     * @param sortOrder 排序方式
     * @param pageLimit 每页查询条数,数值必须大于0否则查所有
     * @param pageNum   当前页，必须大于0否则查所有
     * @return 查询结果集合, 默认排序规则时间降序
     */
    public List<EsQueryVo> query(ElasticSearchQueryBuilder queryBuilder) {
        List<EsQueryVo> queryVos = new ArrayList<>();
        try (ElasticSearchClient elasticSearchClient = ElasticSearchPool.getClient()){
            Map queryMap = new HashMap();
            Object object = queryBuilder.getQueryObj();
            if(object instanceof Map){
                queryMap = (Map)object;
            }else{
                queryMap = MapUtils.object2Map(object);
            }

            SearchRequest searchRequest = new SearchRequest(queryBuilder.getIndex());
            searchRequest.types(queryBuilder.getType());
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();


            String[] includeFields = new String[0];
            String[] excludeFields = new String[0];
            if(queryBuilder.getReturnIncludes()!=null&&!queryBuilder.getReturnIncludes().isEmpty()){
                includeFields = queryBuilder.getReturnIncludes().toArray(includeFields);
            }
            if(queryBuilder.getReturnExcludes()!=null&&!queryBuilder.getReturnExcludes().isEmpty()){
                excludeFields = queryBuilder.getReturnExcludes().toArray(excludeFields);
            }
            searchSourceBuilder.fetchSource(includeFields,excludeFields);

            //查询条件
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            Set<Map.Entry<String, Object>> entries = queryMap.entrySet();
            for (Map.Entry<String, Object> entry : entries) {
                TermQueryBuilder criteria = QueryBuilders.termQuery(entry.getKey(), entry.getValue());
                boolQueryBuilder.must(criteria);
            }
            searchSourceBuilder.query(boolQueryBuilder);
            //分页
            int pageLimit = queryBuilder.getPageLimit();
            int pageNum = queryBuilder.getPageNum();
            if (pageLimit > 0 && pageNum > 0) {
                int pageStart = pageLimit * (pageNum - 1);
                searchSourceBuilder.from(pageStart);
                searchSourceBuilder.size(pageLimit);
            }
            //排序
            if (queryBuilder.getSortField() != null && !queryBuilder.getSortField().isEmpty()) {
                FieldSortBuilder sortBuilder = SortBuilders.fieldSort(queryBuilder.getSortField());
                sortBuilder.order(queryBuilder.getSortOrder());
                searchSourceBuilder.sort(sortBuilder);
            }
            logger.info("es查询语句：" + searchSourceBuilder.toString());
            //封装
            searchRequest.source(searchSourceBuilder);
            logger.info("es查询请求：" + searchRequest.toString());
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



}

