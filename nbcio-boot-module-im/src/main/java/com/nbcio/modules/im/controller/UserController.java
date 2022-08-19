package com.nbcio.modules.im.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nbcio.modules.im.apithird.entity.SysUser;
import com.nbcio.modules.im.apithird.service.IImThirdService;
import com.nbcio.modules.im.service.IImUserFriendService;
import com.nbcio.modules.im.service.IImUserService;
import com.nbcio.modules.im.utils.ChatUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户接口类
 * @author nbacheng
 * @date 2022-8-07
 */
@RestController
@RequestMapping("/im/user")
public class UserController {

    private final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Resource
    private IImThirdService iImThirdService;
    
    @Resource
    @Qualifier(value = "imUserService")
    private IImUserService imUserService;

    @Resource
    @Qualifier(value = "imUserFriendService")
    private IImUserFriendService imUserFriendService;

    /**
     * 用户信息初始化
     *
     * @param request request
     * @return json
     */
    @PostMapping("init")
    public Map<String, Object> list(HttpServletRequest request) {
        logger.debug("init");
        Map<String, Object> objectMap = new HashMap<>();
        //获取好友信息
        SysUser user = iImThirdService.getLoginUser();
        objectMap.put("friends", imUserFriendService.getUserFriends(user.getUsername()));

        //获取本人信息
        String host = ChatUtils.getHost(request);
        user.setAvatar(user.getAvatar());
        user.setPassword(null);
        objectMap.put("me", user);

        //用户的群组信息
        objectMap.put("groups", imUserService.getChatGroups(user.getUsername()));
        return objectMap;
    }


    /**
     * 获取群组的用户
     *
     * @param chatId 群组id
     * @return 用户List
     */
    @PostMapping("chatUserList")
    public List<SysUser> chatUserList(String chatId) {
        return imUserService.getChatUserList(chatId);
    }

    /**
     * 单个用户
     *
     * @param id userId
     * @return ImUser
     */
    @PostMapping("get")
    public SysUser get(String userName) {
        return iImThirdService.getUserByName(userName);
    }
}
