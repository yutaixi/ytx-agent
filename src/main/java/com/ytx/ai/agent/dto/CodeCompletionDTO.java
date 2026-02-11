package com.ytx.ai.agent.dto;

import com.ytx.ai.agent.llm.vo.Message;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CodeCompletionDTO {
    private String code;
    private String language;
    private String question;
    private List<Message> history;
}