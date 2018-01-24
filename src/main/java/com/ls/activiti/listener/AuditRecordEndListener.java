package com.ls.activiti.listener;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service("auditRecordEndListener")
public class AuditRecordEndListener implements ExecutionListener {

    private final Logger logger = Logger.getLogger(AuditRecordEndListener.class);

    @Override
    public void notify(DelegateExecution execution) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.info("processInstanceId:" + execution.getProcessInstanceId());
        }
        Long tableId = (Long) execution.getVariable("tableId");
        String uuid = (String) execution.getVariable("uuid");
        Boolean approve = (Boolean) execution.getVariable("approve");
        if (logger.isDebugEnabled()) {
            logger.info("tableId:" + tableId + "\nuuid:" + uuid + "\napprove:" + approve);
        }
        if (Objects.isNull(approve) || !approve) {
        } else {
        }
    }
}
