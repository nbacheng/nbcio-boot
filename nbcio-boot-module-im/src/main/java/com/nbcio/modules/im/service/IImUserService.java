package com.nbcio.modules.im.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nbcio.modules.im.apithird.entity.SysUser;
import com.nbcio.modules.im.domain.ImChatGroup;

import java.util.List;

/**
 * 用户service
 *
 * @author nbacheng-im
 * @since 2018-10-07
 */
public interface IImUserService extends IService<SysUser> {

	
	/**
     * 获取单个user
     *
     * @param name name
     * @return 字符串
     */
    SysUser getByName(String name);

    /**
     * 根据用户 获取用户所有的群
     *
     * @param userName 用户
     * @return 群List
     */
    List<ImChatGroup> getChatGroups(String userName);

    /**
     * 获取群组的用户
     *
     * @param chatId 群组id
     * @return 用户List
     */
    List<SysUser> getChatUserList(String chatId);

}
