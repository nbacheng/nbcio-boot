package com.nbcio.modules.im.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nbcio.modules.im.apithird.entity.SysUser;
import com.nbcio.modules.im.domain.ImMessage;
import com.nbcio.modules.im.entity.Message;
import com.nbcio.modules.im.service.IImMessageService;
import com.nbcio.modules.im.service.IImUserService;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.*;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author nbacheng
 * @date 2022-8-07
 */
@RestController
@RequestMapping("/im/message")
public class ImMessageController {

    public static final int PAGE_SIZE = 20;

    public static final String FRIEND = "0";

    @Resource
    @Qualifier(value = "iImMessageService")
    private IImMessageService iImMessageService;

    @Resource
    @Qualifier(value = "imUserService")
    private IImUserService imUserService;


    /**
     * 获取聊天记录
     *
     * @param chatId 如果是单聊，是用户的ID，如果是多聊，是chat id
     * @return json
     */
    @ResponseBody
    @RequestMapping("list")
    public Map<String, Object> list(String chatId, String fromName,String chatType, Long pageNo) {
        if (StringUtils.isEmpty(chatId) || StringUtils.isEmpty(fromName)) {
            return new HashMap<>();
        }
        Page<ImMessage> page = new Page<>();
        page.setSize(PAGE_SIZE);
        if (pageNo == null) {
            pageNo = 0L;
        }
        page.setCurrent(pageNo);
        QueryWrapper<ImMessage> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("send_time");
        //System.out.println("chatType ="+chatType); //调试时可以打开
        if(FRIEND.equals(chatType)){
            wrapper.and(wrapper1 -> wrapper1.eq("to_name", chatId)
                    .eq("from_name", fromName));
            wrapper.or(wrapper2 -> wrapper2.eq("from_name", chatId)
                    .eq("to_name", fromName));
        }else {
            wrapper.eq("to_name",chatId);
        }
        IPage<ImMessage> messageIPage = iImMessageService.page(page, wrapper);

        List<ImMessage> imMessageList = messageIPage.getRecords();
        List<Message> messageList = new ArrayList<>();
        for (ImMessage imMessage : imMessageList) {
            Message message = new Message();
            message.setId(imMessage.getFromName());
            message.setMine(fromName.equals(imMessage.getFromName()));
            message.setType(imMessage.getType());
            SysUser imUser = imUserService.getByName(imMessage.getFromName());
            message.setAvatar(imUser.getAvatar());
            message.setUsername(imUser.getUsername());
            message.setFromname(imMessage.getFromName());
            message.setCid(String.valueOf(imMessage.getId()));
            message.setContent(imMessage.getContent());
            message.setTimestamp(imMessage.getSendTime());
            messageList.add(message);
        }
        Map<String, Object> map = new HashMap<>();
        map.put("messageList", messageList);
        map.put("pageNo", pageNo);
        map.put("count", messageIPage.getTotal());
        map.put("pageSize", messageIPage.getSize());
        return map;
    }

}
