package com.nbcio.modules.im.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nbcio.modules.im.domain.ImMessage;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author nbacheng
 * @since 2018-10-08
 */
public interface IImMessageService extends IService<ImMessage> {

    /**
     * 保存消息
     *
     * @param imMessage 消息
     */
    void saveMessage(ImMessage imMessage);

    /**
     * 获取未读消息根据接收人的用户
     *
     * @param toUser 接收人的用户名
     */
    List<ImMessage> getUnReadMessage(String toUser);

}
