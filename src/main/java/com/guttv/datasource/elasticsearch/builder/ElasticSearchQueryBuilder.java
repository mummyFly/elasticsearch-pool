package com.guttv.datasource.elasticsearch.builder;

import com.guttv.datasource.elasticsearch.pool.ElasticSearchPool;
import org.elasticsearch.search.sort.SortOrder;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;


public class ElasticSearchQueryBuilder{
    protected String index;
    protected String type="_doc";
    protected String clusterName = ElasticSearchPool.DEFAULT_CLUSTER;

    private ElasticSearchQueryBuilder(String index,String type,String clusterName){
        this.index = index;
        this.type = type;
        this.clusterName = clusterName;
    }

    public static ElasticSearchQueryBuilder build(String index,String type,String clusterName){
        return new ElasticSearchQueryBuilder(index,type,clusterName);
    }
    public static ElasticSearchQueryBuilder build(String index,String type){
        return new ElasticSearchQueryBuilder(index,type,ElasticSearchPool.DEFAULT_CLUSTER);
    }

    public String getIndex() {
        return index;
    }

    public String getType() {
        return type;
    }



    public String getClusterName() {
        return clusterName;
    }


    private Object queryObj;

    public Object getQueryObj() {
        return queryObj;
    }

    public ElasticSearchQueryBuilder setQueryObj(Object queryObj) {
        this.queryObj = queryObj;
        return this;
    }
    private String sortField;
    private SortOrder sortOrder;
    private int pageNum;
    private int pageLimit;
    private List<String> returnIncludes = new LinkedList<>();
    private List<String> returnExcludes = new LinkedList<>();


    public String getSortField() {
        return sortField;
    }

    public ElasticSearchQueryBuilder setSortField(String sortField) {
        this.sortField = sortField;
        return this;
    }

    public SortOrder getSortOrder() {
        return sortOrder;
    }

    public ElasticSearchQueryBuilder setSortOrder(SortOrder sortOrder) {
        this.sortOrder = sortOrder;
        return this;
    }

    public int getPageNum() {
        return pageNum;
    }

    public ElasticSearchQueryBuilder setPageNum(int pageNum) {
        this.pageNum = pageNum;
        return this;
    }

    public int getPageLimit() {
        return pageLimit;
    }

    public ElasticSearchQueryBuilder setPageLimit(int pageLimit) {
        this.pageLimit = pageLimit;
        return this;
    }

    public List<String> getReturnIncludes() {
        return returnIncludes;
    }

    public ElasticSearchQueryBuilder setReturnIncludes(String... fieldName) {
        this.returnIncludes.addAll(Arrays.asList(fieldName));
        return this;
    }

    public List<String> getReturnExcludes() {
        return returnExcludes;
    }

    public ElasticSearchQueryBuilder setReturnExcludes(String... fieldName) {
        this.returnExcludes.addAll(Arrays.asList(fieldName));
        return this;
    }
}
