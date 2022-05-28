package com.nbcio.modules.flowable.apithird.entity;

/**
 * @author PanMeiCheng
 * @version 1.0
 * @date 2021/11/26
 */
public interface ActStatus {
    //启动 撤回 驳回 审批中 审批通过 审批异常
    //本流程不应有启动状态，启动即进入审批，第一个节点就是发起人节点，未方便业务区分，设定为“启动”状态
    String start = "启动";
    String recall = "撤回";
    String reject = "驳回";
    String delegate ="委派";
    String assign = "转办";
    String doing = "审批中";
    String pass = "审批通过";
    String err = "审批异常";
}
