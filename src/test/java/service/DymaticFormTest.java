package service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.activiti.engine.*;
import org.activiti.engine.form.FormProperty;
import org.activiti.engine.form.StartFormData;
import org.activiti.engine.form.TaskFormData;
import org.activiti.engine.history.HistoricDetail;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DymaticFormTest extends BaseService{

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
    public void startProcess(){

        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionKey("DymaticForm").latestVersion().singleResult();
        StartFormData startFormData = formService.getStartFormData(processDefinition.getId());
        assertNull(startFormData.getFormKey());

        Map<String, String> formProperties = new HashMap<String, String>();
        formProperties.put("name", "HenryYan");

        ProcessInstance processInstance = formService.submitStartFormData(processDefinition.getId(), formProperties);
        assertNotNull(processInstance);

        // 运行时变量
        Map<String, Object> variables = runtimeService.getVariables(processInstance.getId());
        //assertEquals(variables.size(), 1);
        Set<Map.Entry<String, Object>> entrySet = variables.entrySet();
        for (Map.Entry<String, Object> entry : entrySet) {
            System.out.println(entry.getKey() + "=" + entry.getValue());
        }

        // 历史记录
        List<HistoricDetail> list = historyService.createHistoricDetailQuery().formProperties().list();
        //assertEquals(1, list.size());

        // 获取第一个节点
        List<Task> taskList = taskService.createTaskQuery().processDefinitionKey("DymaticForm").orderByTaskCreateTime().desc().list();
        Task task = taskList.get(0);
        assertEquals("First Step", task.getName());

        TaskFormData taskFormData = formService.getTaskFormData(task.getId());
        assertNotNull(taskFormData);
        assertNull(taskFormData.getFormKey());
        List<FormProperty> taskFormProperties = taskFormData.getFormProperties();
        assertNotNull(taskFormProperties);
        for (FormProperty formProperty : taskFormProperties) {
            System.out.println(formProperty.getName() + "=" + formProperty.getValue());
        }
        formProperties = new HashMap<String, String>();
        formProperties.put("setInFirstStep", "01/12/2012");
        formProperties.put("year", "1");
        formService.submitTaskFormData(task.getId(), formProperties);

        // 获取第二个节点
        task = taskService.createTaskQuery().taskName("Second Step").processDefinitionKey("DymaticForm").orderByTaskCreateTime().desc().list().get(0);
        assertNotNull(task);
        taskFormData = formService.getTaskFormData(task.getId());
        assertNotNull(taskFormData);
        List<FormProperty> formProperties2 = taskFormData.getFormProperties();
        assertNotNull(formProperties2);
        //assertEquals(1, formProperties2.size());
        assertNotNull(formProperties2.get(0).getValue());
        assertEquals(formProperties2.get(0).getValue(), "01/12/2012");
    }
}
