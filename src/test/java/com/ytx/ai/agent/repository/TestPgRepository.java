package com.ytx.ai.agent.repository;

import cn.hutool.core.collection.ListUtil;
import com.ytx.ai.Application;
import com.ytx.ai.agent.llm.service.LlmService;
import com.ytx.ai.agent.repository.vo.Filter;
import com.ytx.ai.agent.repository.vo.SearchRequest;
import com.ytx.ai.agent.repository.vo.SearchResponse;
import com.ytx.ai.base.util.IdGenerator;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@Slf4j
public class TestPgRepository {

    @Autowired
    private Repository repository;


    @Autowired
    private LlmService llmService;


    @Test
    public void test_search(){
        SearchRequest searchRequest=new SearchRequest();
        searchRequest.setLabels(new String[]{"documents"});
        searchRequest.setClazz(DocumentEntity.class);
        searchRequest.setLimit(100);
        String searchText="tecno fire";
        List<Float> embeddings = llmService.createEmbeddings(searchText);
        Filter filter=Filter.builder().field("embedding").comparisonOpt(Filter.ComparisonOpt.SIMILAR).value(embeddings).logicalOpt(Filter.LogicalOpt.AND).build();
        searchRequest.setFilters(ListUtil.toList(filter));
        SearchResponse<?> results=  repository.search(searchRequest);
        System.out.println(results.getRecords().size());
    }


    @Test
    public void test_create(){
        String[] products={"itel speak222","itel speak2333","tecno fire444","tecno ox555",};
        Arrays.stream(products).forEach(item->{
            DocumentEntity document=new DocumentEntity();
            document.setBid(IdGenerator.next());
            document.setLabel(item);
            List<Float> embeddings = llmService.createEmbeddings(item);
            document.setEmbedding(embeddings);
            repository.createData(document,"documents");
        });

    }

    @Test
    public void test_update(){
        DocumentEntity document=new DocumentEntity();
        document.setBid("1020835766937841666");
        document.setLabel("testssss4-updated");
        document.setEmbedding(llmService.createEmbeddings("testssss4-updated"));
        repository.batchCreateData(ListUtil.toList(document),"documents",true);
    }

    @Test
    public void test_delete(){

        Filter filter=Filter.builder().field("bid").comparisonOpt(Filter.ComparisonOpt.EQUAL).value("1020835766937841666").logicalOpt(Filter.LogicalOpt.AND).build();
        repository.deleteData(ListUtil.toList("documents"),ListUtil.toList(filter));
    }


    @Test
    public void test_delete_index(){

        ProductInfo productInfo=new ProductInfo();
        repository.deleteIndex(productInfo.getLabel());
    }

    @Test
    public void test_create_index(){

        ProductInfo productInfo=new ProductInfo();
        repository.createIndex(productInfo.getLabel(),ProductInfo.class);
    }

}
