package com.guttv.datasource.elasticsearch.vo;

/**
 * Created by crg on 2018/9/13 15:41
 *
 * Es查询，文档id以及文档内容Json封装Vo
 */
public class EsQueryVo {

    // 文档id
    private String docId;

    // 文档类容实体
    private String contentJsonString;

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public String getContentJsonString() {
        return contentJsonString;
    }

    public void setContentJsonString(String contentJsonString) {
        this.contentJsonString = contentJsonString;
    }
}

