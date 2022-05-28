package com.nbcio.modules.flowable.apithird.service;

import java.util.List;

import com.nbcio.modules.flowable.apithird.entity.SysCategory;
import com.nbcio.modules.flowable.apithird.entity.SysRole;
import com.nbcio.modules.flowable.apithird.entity.SysUser;

/**
 * 业务层需实现的接口定义<br/>
 *  支撑工作流模块与业务的关联
 * @author pmc
 */
public interface IFlowThirdService {
    /**
     * 获取当前登录用户
     *
     * @return 当前登录用户信息
     */
    public SysUser getLoginUser();
    /**
     * 所有用户
     * @return
     */
    List<SysUser> getAllUser();
    /**
     * 通过角色id获取用户
     * @return
     */
    List<SysUser> getUsersByRoleId(String roleId);
    /**
     * 根据用户username查询用户信息
     * @param username
     * @return
     */
    SysUser getUserByUsername(String username);

    /**
     * 获取所有角色
     * @return
     */
    public List<SysRole> getAllRole();
    /**
     * 获取所有流程分类
     * @return
     */
    List<SysCategory> getAllCategory();
    /**
     * 通过用户账号查询部门 name
     * @param username
     * @return 部门 name
     */
    List<String> getDepartNamesByUsername(String username);
    
    /**
	 * 发消息
	 * @param fromUser
	 * @param toUser
	 * @param title
	 * @param msgContent
	 * @param setMsgCategory
	 */
    void sendSysAnnouncement(String fromUser, String toUser, String title, String msgContent, String setMsgCategory);
    /**
     * 获取流程发送消息基地址
     * @return
     */
    String getBaseUrl();
    
}
