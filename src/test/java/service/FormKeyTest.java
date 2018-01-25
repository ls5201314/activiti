package service;

import org.activiti.engine.*;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 *  formProperties.put("startName", "HenryYan");
 *  formService.submitTaskFormData(task.getId(), formProperties);
 *
 *  formService.getRenderedTaskForm(task.getId());执行后去渲染标签${startName} 与id="start-name" 无关 只替换${}对应名称的数据
 *  <input id="start-name" value="${startName}" />
 *  结果：<input id="start-name" value="HenryYan" />
 */
public class FormKeyTest extends BaseService {

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private FormService formService;

    @Test
    public void testTaskFormsWithVacationRequestProcess() {

        // Get start form
        String procDefId = repositoryService.createProcessDefinitionQuery().processDefinitionKey("FormKey").orderByProcessDefinitionVersion().desc().list().get(0).getId();
        Object startForm = formService.getRenderedStartForm(procDefId); //外置form必须打包部署
        assertNotNull(startForm);

        assertEquals("<input id=\"start-name\" />", startForm);

        Map<String, String> formProperties = new HashMap<String, String>();
        formProperties.put("startName", "HenryYan");
        ProcessInstance processInstance = formService.submitStartFormData(procDefId, formProperties);
        assertNotNull(processInstance);

        Task task = taskService.createTaskQuery().taskAssignee("user1").orderByTaskCreateTime().desc().list().get(0);
        Object renderedTaskForm = formService.getRenderedTaskForm(task.getId());
        assertEquals("<input id=\"start-name\" value=\"HenryYan\" />\n<input id=\"first-name\" />", renderedTaskForm);

        formProperties = new HashMap<String, String>();
        formProperties.put("firstName", "kafeitu");
        formService.submitTaskFormData(task.getId(), formProperties);

        task = taskService.createTaskQuery().taskAssignee("user2").orderByTaskCreateTime().desc().list().get(0);
        assertNotNull(task);
        renderedTaskForm = formService.getRenderedTaskForm(task.getId());
        assertEquals("<input id=\"first-name\" value=\"kafeitu\" />", renderedTaskForm); //<input id="first-name" />
    }
}
