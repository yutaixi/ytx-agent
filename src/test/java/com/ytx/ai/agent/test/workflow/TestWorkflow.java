package com.ytx.ai.agent.test.workflow;

import cn.hutool.json.JSONUtil;
import com.ytx.ai.Application;
import com.ytx.ai.base.agent.ChatDTO;
import com.ytx.ai.workflow.Workflow;
import com.ytx.ai.workflow.WorkflowOutput;
import com.ytx.ai.workflow.execute.FlowContext;
import com.ytx.ai.workflow.execute.FlowExecutor;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@Slf4j
public class TestWorkflow {

    @Autowired
    private FlowExecutor flowExecutor;

    @Test
    public void test_run_sub_flow() {
        String workflowJson = """
        {
            "nodes": [
                {
                    "id": "0",
                    "componentType": "workflowNode",
                    "componentId": "start",
                    "label": "开始"
                },
                {
                    "id": "1",
                    "componentType": "workflowNode",
                    "componentId": "llm",
                    "label": "大模型写诗",
                    "inputs": {},
                    "metaData": {
                        "llm_system_prompt": {"content": "你是唐朝的一位很厉害的诗人，诗词风格像李白", "type": "string"},
                        "llm_user_prompt": {"content": "写一首五言绝句，内容都和鱼有关", "type": "string"},
                        "llm_temperature": {"content": "0.7", "type": "number"},
                        "llm_model_name": {"content": "gpt-4o", "type": "string"}
                    },
                    "outputs": {
                        "llm_output": {"type": "string"}
                    }
                },
                {
                    "id": "2",
                    "componentType": "workflowNode",
                    "componentId": "code",
                    "label": "执行代码",
                    "inputs": {},
                    "metaData": {
                        "code_script": {
                            "content": "// 同步函数定义\\r\\nfunction main({ params }) {\\r\\n console.log('this is code workflowNode running.');\\r\\n const ret = {\\r\\n body: 'this is body'\\r\\n};\\r\\n return ret;\\r\\n}",
                            "type": "string"
                        },
                        "code_language": {"content": "js", "type": "string"},
                        "code_test": {"type": "string", "source": {"type": "ref", "nId": "1", "vName": "llm_model_name"}}
                    }
                },
                {
                    "id": "3",
                    "componentType": "workflow",
                    "componentId": "15",
                    "label": "测试子流程",
                    "inputs": {}
                },
                {
                    "id": "4",
                    "componentType": "workflowNode",
                    "componentId": "end",
                    "label": "结束",
                    "outputs": {
                        "test_output": {"type": "string", "source": {"type": "ref", "nId": "1", "vName": "llm_output"}}
                    }
                }
            ],
            "edges": [
                {"id": "1", "source": "0", "target": "1"},
                {"id": "2", "source": "1", "target": "2"},
                {"id": "3", "source": "2", "target": "3"},
                {"id": "4", "source": "3", "target": "4"}
            ]
        }
        """;

        Workflow workflow = JSONUtil.toBean(workflowJson, Workflow.class);
        ChatDTO chatDTO=new ChatDTO();
        chatDTO.setQuestion("heeeeeeeellooooo");
        FlowContext context = FlowContext.of().chat(chatDTO);
        WorkflowOutput workflowOutput = flowExecutor.execute(workflow, context);
        System.out.println(JSONUtil.toJsonStr(workflowOutput));
    }

    @Test
    public void test_run_condition() {
        String workflowJson = """
        {
            "nodes": [
                {
                    "id": "0",
                    "componentType": "workflowNode",
                    "componentId": "start",
                    "label": "开始"
                },
                {
                    "id": "1",
                    "componentType": "workflowNode",
                    "componentId": "llm",
                    "label": "大模型写诗",
                    "inputs": {},
                    "metaData": {
                        "llm_system_prompt": {"content": "你是唐朝的一个很厉害的诗人，诗词风格像李白","type": "string"},
                        "llm_user_prompt": {"content": "写一首五言绝句，内容和鲤鱼有关","type": "string"},
                        "llm_temperature": {"content": 0.7,"type": "number"},
                        "llm_model_name": {"content": "gpt-40","type": "string"}
                    },
                    "outputs": {
                        "llm_output": {"type": "string"}
                    }
                },
                {
                    "id": "2",
                    "componentType": "workflowNode",
                    "componentId": "condition",
                    "label": "条件判断",
                    "inputs": {},
                    "metaData": {
                        "condition_group_info": {
                            "content": [
                                {"id": "1","opt": "and","order": "0","items": [
                                        {"left": {"type": "string","source": {"type": "ref","nId": "1","vName": "llm_output"}},
                                        "opt": "len.>=","right": {"type": "integer","content": "1"}}
                                    ]
                                },
                                {"id": "2","opt": "else","order": "99999"}
                            ],"type": "object"}
                    }
                },
                {
                    "id": "3",
                    "componentType": "workflowNode",
                    "componentId": "llm",
                    "label": "分支1",
                    "inputs": {},
                    "metaData": {
                        "llm_system_prompt": {"content": "你是唐朝的一个很厉害的诗人，诗词风格像李白","type": "string"},
                        "llm_user_prompt": {"content": "写一首五言绝句，内容和母亲有关，需要有标题","type": "string"},
                        "llm_temperature": {"content": 0.7,"type": "number"},
                        "llm_model_name": {"content": "gpt-40","type": "string"}
                    },
                    "outputs": {"llm_output": {"type": "string"}}
                },
                {   
                    "id": "4",
                    "componentType": "workflowNode",
                    "componentId": "llm",
                    "label": "分支2",
                    "inputs": {},
                    "metaData": {
                        "llm_system_prompt": {"content": "你是唐朝的一个很厉害的诗人，诗词风格像李白","type": "string"},
                        "llm_user_prompt": {"content": "写一首五言绝句，内容和做饭有关，需要有标题","type": "string"},
                        "llm_temperature": {"content": 0.7,"type": "number"},
                        "llm_model_name": {"content": "gpt-40","type": "string"}
                    },
                    "outputs": {"llm_output": {"type": "string"}}
                },
                {
                    "id": "5",
                    "componentType": "workflowNode",
                    "componentId": "end",
                    "label": "结束",
                    "outputs": {
                        "test_output": {"type": "string","source": {"type": "ref","nId": "1","vName": "llm_output"}}
                    }
                }
            ],
            "edges": [
                {"id": "1","source": "0","target": "1"},
                {"id": "2","source": "1","target": "2"},
                {"id": "3","source": "2","target": "3","depends": "1"},
                {"id": "4","source": "2","target": "4","depends": "2"},
                {"id": "5","source": "4","target": "5"}
            ]
        }
    """;

        Workflow workflow = JSONUtil.toBean(workflowJson, Workflow.class);
        ChatDTO chatDTO=new ChatDTO();
        chatDTO.setQuestion("heeeeeeeellooooo");
        FlowContext context = FlowContext.of().chat( chatDTO );
        WorkflowOutput workflowOutput = flowExecutor.execute(workflow, context);
        System.out.println(JSONUtil.toJsonStr(workflowOutput));
    }

}
