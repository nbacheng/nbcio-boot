package com.nbcio.modules.im.apithird.service;

import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.nbcio.modules.im.apithird.entity.SysDepart;
import com.nbcio.modules.im.apithird.entity.SysDepartTreeModel;
import com.nbcio.modules.im.apithird.entity.SysRole;
import com.nbcio.modules.im.apithird.entity.SysUser;

/**
 * 业务层需实现的接口定义
 *  支撑聊天模块与业务的关联
 * @author nbacheng
 * @date  2022-08-18
 */
public interface IImThirdService {
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
    
    /** 根据部门id获取部门信息
     * 
     */
    SysDepart selectDeptById(long deptId);
    
    
    /**
     * 获取某个部门的所有父级部门的ID
     *
     * @param departId 根据departId查
     */
    JSONObject queryAllParentIdByDepartId(String departId);
    
    /**
     * 获取某个部门的用户列表
     *
     * @param departId 根据departId查
     */
    List<SysUser> selectUserList(String departId);
    
    /**
     * 根据用户mobile获取用户
     *
     * @param mobile mobile
     * @return List<User>
     */
    List<SysUser> search(String mobile);
    
    /**
    * 根据用户username获取用户
    *
    * @param username username
    * @return SysUserUser
    */
    SysUser getUserByName(String username);
    /**
     * 更新用户 
     *
     * @param user 用户
     * @return 更新数
     */
    
    int updateUser(SysUser user);
    
    /**
	 * 查询数据 查出我的部门,并以树结构数据格式响应给前端
	 *
	 * @return 
	 */
    List<SysDepartTreeModel> queryMyDeptTreeList();
   
    /**
	 * 查出部门用户
	 *
	 * @return List<SysUser>
	 */
	public List<SysUser> queryDeptUserList(String deptId);
    
}
