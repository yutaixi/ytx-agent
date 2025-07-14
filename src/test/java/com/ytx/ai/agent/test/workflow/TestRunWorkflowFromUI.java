package com.ytx.ai.agent.test.workflow;

import com.ytx.ai.Application;
import com.ytx.ai.agent.entity.SkillEntity;
import com.ytx.ai.agent.service.SkillService;
import com.ytx.ai.workflow.Workflow;
import com.ytx.ai.workflow.WorkflowOutput;
import com.ytx.ai.workflow.execute.FlowContext;
import com.ytx.ai.workflow.execute.FlowExecutor;
import com.ytx.ai.workflow.execute.WorkflowWrapper;
import com.ytx.ai.workflow.plugin.FlowStart;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@Slf4j
public class TestRunWorkflowFromUI {

    @Autowired
    private FlowExecutor flowExecutor;

    @Autowired
    private SkillService skillService;


    @Test
    public void test_run_workflow(){


        SkillEntity skill= skillService.findSkill(37);

        Workflow workflow=Workflow.of(skill);
        WorkflowWrapper workflowWrapper=new WorkflowWrapper(workflow);
        FlowStart.StartNodeMeta startMata= (FlowStart.StartNodeMeta) workflowWrapper.getStartNode().getMeta();

        startMata.getInputs().forEach(item->{
            item.setContent("hello");
        });
        FlowContext context = FlowContext.of();
        WorkflowOutput workflowOutput = flowExecutor.execute(workflow, context);
        System.out.println(workflowOutput.getAnswer());
    }
}
