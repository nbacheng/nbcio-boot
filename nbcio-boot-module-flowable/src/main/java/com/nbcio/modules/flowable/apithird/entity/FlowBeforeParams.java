package com.nbcio.modules.flowable.apithird.entity;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 *流程运行之前从业务层获取的相关参数
 *@author PanMeiCheng
 *@date 2021/12/3
 *@version 1.0
 */
@Data
public class FlowBeforeParams {
    /**指定下个节点的候选人，用户名username*/
    List<String> candidateUsernames;
    /**流程变量*/
    Map<String,Object> values;
}
