package com.nbcio.modules.flowable.factory;

import lombok.Getter;
import org.flowable.engine.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * flowable 引擎注入封装
 */
@Component
@Getter
public class FlowServiceFactory {

    @Resource
    protected RepositoryService repositoryService;

    @Resource
    protected RuntimeService runtimeService;

    @Resource
    protected IdentityService identityService;

    @Resource
    protected TaskService taskService;

    @Resource
    protected FormService formService;

    @Resource
    protected HistoryService historyService;

    @Resource
    protected ManagementService managementService;

    @Qualifier("processEngine")
    @Resource
    protected ProcessEngine processEngine;

}
