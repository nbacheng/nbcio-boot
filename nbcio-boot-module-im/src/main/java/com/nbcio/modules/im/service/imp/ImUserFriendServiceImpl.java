package com.nbcio.modules.im.service.imp;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nbcio.modules.im.apithird.entity.SysUser;
import com.nbcio.modules.im.domain.ImUserFriend;
import com.nbcio.modules.im.mapper.ImUserFriendMapper;
import com.nbcio.modules.im.service.IImUserFriendService;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 用户关系表
 * </p>
 *
 * @author jobob
 * @since 2018-12-31
 */
@Service
@Qualifier(value = "imUserFriendService")
public class ImUserFriendServiceImpl extends ServiceImpl<ImUserFriendMapper, ImUserFriend> implements IImUserFriendService {

    /**
     * 根据用户的ID 获取 用户好友(双向用户关系)
     *
     * @param userId 用户ID
     * @return 好友分组的列表
     */
    @Override
    public List<SysUser> getUserFriends(String userName) {
        return baseMapper.getUserFriends(userName);
    }
}
