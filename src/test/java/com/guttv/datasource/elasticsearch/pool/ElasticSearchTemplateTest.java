package com.guttv.datasource.elasticsearch.pool;

import com.guttv.datasource.elasticsearch.template.ElasticSearchTemplate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ElasticSearchTemplateTest {
    private Logger logger = LoggerFactory.getLogger(ElasticSearchTemplate.class);

    private String userCode = "jsydac4afe8fe30a";
    private String serviceComboCode = "1100121022988022335581750";
    private String serviceGroupCode = "1100122022055962258270018";
    private String productCode = "123";

    @Autowired
    private ElasticSearchTemplate elasticSearchTemplate;
    @Test
    public void testQueryCountInMap(){
        Map queryMap = new HashMap();
//        queryMap.put("userCode",userCode);
//        queryMap.put("isDeleted",0);
        queryMap.put("status",1);
        int count = elasticSearchTemplate.queryCountInMap(queryMap,"ums_userplayhistory","doc");
        logger.info("count:"+count);
    }
}
