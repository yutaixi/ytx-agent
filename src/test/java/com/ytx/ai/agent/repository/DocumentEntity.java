package com.ytx.ai.agent.repository;

import com.ytx.ai.agent.repository.vo.Node;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DocumentEntity implements Node {

    private String bid;

    private String label;

    private List<Float> embedding;
}
