package com.ytx.ai.agent.repository;

import com.ytx.ai.agent.repository.annotation.Document;
import com.ytx.ai.agent.repository.annotation.Field;
import com.ytx.ai.agent.repository.vo.Node;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Document(indexName = "product_info")
public class ProductInfo implements Node {

    private Integer id;

    private String bid;

    private String content;

    @Field(type = Field.Type.VECTOR)
    private List<Float> features;

}
