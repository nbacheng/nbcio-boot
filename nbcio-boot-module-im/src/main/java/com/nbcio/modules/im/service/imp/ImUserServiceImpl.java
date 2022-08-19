package com.nbcio.modules.im.service.imp;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nbcio.modules.im.apithird.entity.SysUser;
import com.nbcio.modules.im.apithird.service.IImThirdService;
import com.nbcio.modules.im.domain.ImChatGroup;

import com.nbcio.modules.im.mapper.ImUserMapper;
import com.nbcio.modules.im.service.IImChatGroupUserService;
import com.nbcio.modules.im.service.IImUserFriendService;
import com.nbcio.modules.im.service.IImUserService;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Service;


import javax.annotation.Resource;

import java.util.List;

/**
 * 服务实现类
 *
 * @author nbacheng
 * @since 2018-10-07
 */
@Service
@Qualifier(value = "imUserService")
public class ImUserServiceImpl extends ServiceImpl<ImUserMapper, SysUser> implements IImUserService {

    @Value("${v.im.admin.id}")
    private String adminId;

    @Value("${v.im.default.chat.id}")
    private String defaultChatId;


    @Resource
    @Qualifier(value = "imUserFriendService")
    private IImUserFriendService imUserFriendService;

    @Resource
    @Qualifier(value = "imChatGroupUserService")
    private IImChatGroupUserService imChatGroupUserService;
    
    @Resource
    private IImThirdService iImThirdService;
    
    @Override
    public SysUser getByName(String username) {
    	return iImThirdService.getUserByName(username);
    }

    @Override
    public List<ImChatGroup> getChatGroups(String userName) {
        return baseMapper.getUserGroups(userName);
    }

    @Override
    public List<SysUser> getChatUserList(String chatId) {
        return baseMapper.getChatUserList(chatId);
    }
}
