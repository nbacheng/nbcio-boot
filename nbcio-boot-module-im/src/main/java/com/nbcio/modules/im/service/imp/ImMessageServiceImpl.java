package com.nbcio.modules.im.service.imp;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nbcio.modules.im.domain.ImMessage;
import com.nbcio.modules.im.mapper.ImMessageMapper;
import com.nbcio.modules.im.service.IImMessageService;
import com.nbcio.modules.im.utils.ChatUtils;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 服务实现类
 *
 * @author nbacheng
 * @since 2018-10-08
 */
@Service
@Qualifier(value = "iImMessageService")
public class ImMessageServiceImpl extends ServiceImpl<ImMessageMapper, ImMessage> implements IImMessageService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveMessage(ImMessage imMessage) {
        new ImMessageServiceImpl.SaveChatMessageThread(imMessage).run();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<ImMessage> getUnReadMessage(String toname) {
        QueryWrapper<ImMessage> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("to_name", toname);
        queryWrapper.eq("read_status", "1");
        List<ImMessage> messageList = baseMapper.selectList(queryWrapper);
        for (ImMessage message : messageList) {
            message.setReadStatus(ChatUtils.READ_TYPE_READ);
            this.updateById(message);
        }
        return messageList;
    }

    /**
     * 内部类
     */
    class SaveChatMessageThread implements Runnable {

        private ImMessage imMessage;

        public SaveChatMessageThread(ImMessage imMessage) {
            this.imMessage = imMessage;
        }

        @Override
        public void run() {
            save(imMessage);
        }
    }
}
