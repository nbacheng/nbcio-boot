package com.nbcio.modules.im.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nbcio.modules.im.apithird.entity.SysUser;
import com.nbcio.modules.im.domain.ImUserFriend;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author jobob
 * @since 2018-12-31
 */
public interface ImUserFriendMapper extends BaseMapper<ImUserFriend> {

    /**
     * 根据用户 获取 用户好友(双向用户关系)
     * @param userName 用户
     * @return 好友分组的列表
     */
    List<SysUser> getUserFriends(String userName);

}
