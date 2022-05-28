package com.nbcio.modules.flowable.service;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.common.api.vo.Result;
import com.nbcio.modules.flowable.domain.dto.FlowProcDefDto;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 */
public interface IFlowDefinitionService {

    boolean exist(String processDefinitionKey);


    /**
     * 流程定义列表
     *
     * @param pageNum  当前页码
     * @param pageSize 每页条数
     * @param flowProcDefDto
     * @return 流程定义分页列表数据
     */
    Page<FlowProcDefDto> list(Integer pageNum, Integer pageSize, FlowProcDefDto flowProcDefDto);

    /**
     * 导入流程文件
     *
     * @param name
     * @param category
     * @param in
     */
    void importFile(String name, String category, InputStream in);

    /**
     * 读取xml
     * @param deployId
     * @return
     */
    Result readXml(String deployId) throws IOException;
    Result readXmlByDataId(String dataId) throws IOException;
	Result readXmlByName(String processDefinitionName) throws IOException; // add by nbacheng
    /**
     * 根据流程定义Key启动流程实例
     *启动最新一个版本
     * @param procDefKey
     * @param variables
     * @return
     */
    Result startProcessInstanceByKey(String procDefKey, Map<String, Object> variables);

    /**
     * 根据流程定义ID启动流程实例,这个涉及业务dataid
     *
     * @param procDefId
     * @param variables
     * @return
     */

    Result startProcessInstanceById(String procDefId, Map<String, Object> variables);


    /**
     * 根据流程定义ID启动流程实例，这个与业务dataid无关，直接通过发布定义流程进行发起实例
     *
     * add by nbacheng
     * @param procDefId
     * @param variables
     * @return
     */
	Result startProcessInstanceByProcDefId(String procDefId, Map<String, Object> variables);
    
    /**
     * 根据流程关联的数据ID启动流程实例
     * @param dataId
     * @param variables
     * @return
     */
    Result startProcessInstanceByDataId(String dataId, String serviceName, Map<String, Object> variables);

    /**
     * 激活或挂起流程定义
     *
     * @param state    状态
     * @param deployId 流程部署ID
     */
    void updateState(Integer state, String deployId);


    /**
     * 删除流程定义
     *
     * @param deployId 流程部署ID act_ge_bytearray 表中 deployment_id值
     */
    void delete(String deployId);

    /**
     *获取流程定义的所有节点信息
     *
     * @param processDefinitionName 流程ID act_re_procdef 表中 name值
     */
    JSONArray ListAllNode(String processDefinitionName);

    /**
     * 读取图片文件
     * @param deployId
     * @return
     */
    InputStream readImage(String deployId);


    InputStream readImageByDataId(String dataId);


}
