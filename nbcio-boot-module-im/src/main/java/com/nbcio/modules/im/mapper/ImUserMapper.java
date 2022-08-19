package com.nbcio.modules.im.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nbcio.modules.im.apithird.entity.SysUser;
import com.nbcio.modules.im.domain.ImChatGroup;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author nbacheng
 * @since 2018-10-07
 */
@Component
@Qualifier("imUserMapper")
public interface ImUserMapper extends BaseMapper<SysUser> {

    /**
     * 根据用户 获取群组
     * @param userName 
     * @return List<ImChatGroup>
     */
    List<ImChatGroup> getUserGroups(String userName);


    /**
     * 获取群组的用户
     * @param chatId 群组id
     * @return 用户List
     */
    List<SysUser> getChatUserList(String chatId);
}
