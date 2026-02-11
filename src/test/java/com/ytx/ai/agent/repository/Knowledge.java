package com.ytx.ai.agent.repository;


import com.ytx.ai.agent.repository.vo.Node;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;


@Document(indexName = "knowledge")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class Knowledge implements Node {

    @Id
    private String bid;

    @Field(type = FieldType.Text,name="content")
    private String content;

    @Override
    public String getLabel() {
        return "knowledge";
    }
}
