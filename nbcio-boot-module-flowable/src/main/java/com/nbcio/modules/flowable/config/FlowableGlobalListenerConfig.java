package com.nbcio.modules.flowable.config;


import lombok.RequiredArgsConstructor;

import org.flowable.common.engine.api.delegate.event.FlowableEngineEventType;
import org.flowable.common.engine.api.delegate.event.FlowableEventDispatcher;
import org.flowable.spring.SpringProcessEngineConfiguration;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;

import com.nbcio.modules.flowable.listener.ProcessCompleteListener;
import com.nbcio.modules.flowable.listener.TaskCreateListener;

/**
 * Flowable添加全局监听器
 *
 * @author nbacheng
 */
@Configuration
@RequiredArgsConstructor
public class FlowableGlobalListenerConfig implements ApplicationListener<ContextRefreshedEvent> {

    private final SpringProcessEngineConfiguration configuration;

    private final TaskCreateListener taskCreateListener;
    private final ProcessCompleteListener processCompleteListener;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        FlowableEventDispatcher dispatcher = configuration.getEventDispatcher();
        // 任务创建全局监听-待办消息发送
        dispatcher.addEventListener(taskCreateListener, FlowableEngineEventType.TASK_CREATED);
        //任务创建全局监听-完成消息发送
        dispatcher.addEventListener(processCompleteListener, FlowableEngineEventType.PROCESS_COMPLETED);
    }

}