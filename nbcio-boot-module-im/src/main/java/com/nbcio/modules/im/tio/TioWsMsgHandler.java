package com.nbcio.modules.im.tio;


import cn.hutool.core.util.ObjectUtil;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nbcio.modules.im.apithird.entity.SysUser;
import com.nbcio.modules.im.apithird.service.IImThirdService;
import com.nbcio.modules.im.domain.ImChatGroup;
import com.nbcio.modules.im.domain.ImMessage;
import com.nbcio.modules.im.entity.Message;
import com.nbcio.modules.im.entity.SendInfo;
import com.nbcio.modules.im.service.IImMessageService;
import com.nbcio.modules.im.service.IImUserService;
import com.nbcio.modules.im.utils.ChatUtils;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.tio.core.ChannelContext;
import org.tio.core.Tio;
import org.tio.core.TioConfig;
import org.tio.http.common.HttpRequest;
import org.tio.http.common.HttpResponse;
import org.tio.utils.lock.SetWithLock;
import org.tio.websocket.common.WsRequest;
import org.tio.websocket.common.WsResponse;
import org.tio.websocket.server.handler.IWsMsgHandler;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import javax.annotation.Resource;


/**
 * websocket 处理函数
 *
 * @author nbacheng
 * @since 2018-10-08
 */
@Component
public class TioWsMsgHandler implements IWsMsgHandler {

    private static final Logger log = LoggerFactory.getLogger(TioWsMsgHandler.class);

    public static TioConfig tioConfig;

    @Resource
    private IImUserService imUserService;

    @Resource
    private IImMessageService iImMessageService;
    
    @Resource
    private IImThirdService iImThirdService;


    /**
     * 握手时走这个方法，业务可以在这里获取cookie，request参数等
     *
     * @param request        request
     * @param httpResponse   httpResponse
     * @param channelContext channelContext
     * @return HttpResponse
     */
    @Override
    public HttpResponse handshake(HttpRequest request, HttpResponse httpResponse, ChannelContext channelContext) {
		tioConfig = channelContext.tioConfig;
		String userName = request.getParam("name");
		SysUser loginUser = iImThirdService.getUserByName(userName);
		if(Objects.isNull(loginUser)) {
			log.info("handshake:" + "无法获得登录用户信息！");
			return httpResponse;
		}
		//String userName = loginUser.getUsername();
		// 先关闭原先的连接
		Tio.closeUser(tioConfig, userName, null);
		// 绑定用户
		Tio.bindUser(channelContext, userName);
		// 在线用户绑定到上下文 用于发送在线消息
		WsOnlineContext.bindUser(userName, channelContext);
		// 绑定群组
		List<ImChatGroup> groups = imUserService.getChatGroups(userName);
		for (ImChatGroup group : groups) {
			Tio.bindGroup(channelContext, group.getId());
		}
		return httpResponse;
    }

    /**
     * @param httpRequest    httpRequest
     * @param httpResponse   httpResponse
     * @param channelContext channelContext
     * @throws Exception Exception
     * @author tanyaowu tanyaowu
     */
    @Override
    public void onAfterHandshaked(HttpRequest httpRequest, HttpResponse httpResponse, ChannelContext channelContext) throws Exception {

    }

    /**
     * 字节消息（binaryType = arraybuffer）过来后会走这个方法
     */
    @Override
    public Object onBytes(WsRequest wsRequest, byte[] bytes, ChannelContext channelContext) throws Exception {
        return null;
    }

    /**
     * 当客户端发close flag时，会走这个方法
     */
    @Override
    public Object onClose(WsRequest wsRequest, byte[] bytes, ChannelContext channelContext) throws Exception {
        Tio.remove(channelContext, "receive close flag");
        return null;
    }

    /**
     * 字符消息（binaryType = blob）过来后会走这个方法
     *
     * @param wsRequest      wsRequest
     * @param text           text
     * @param channelContext channelContext
     * @return obj
     */
    @Override
    public Object onText(WsRequest wsRequest, String text, ChannelContext channelContext) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            SendInfo sendInfo = objectMapper.readValue(text, SendInfo.class);
            //System.out.println("onText " + text); //调试时可以打开
            //心跳检测包
            if (ChatUtils.MSG_PING.equals(sendInfo.getCode())) {
                WsResponse wsResponse = WsResponse.fromText(text, TioServerConfig.CHARSET);
                Tio.send(channelContext, wsResponse);
            }
            //真正的消息
            else if (ChatUtils.MSG_MESSAGE.equals(sendInfo.getCode())) {
                Message message = sendInfo.getMessage();
                message.setMine(false);
                WsResponse wsResponse = WsResponse.fromText(objectMapper.writeValueAsString(sendInfo), TioServerConfig.CHARSET);
                //单聊
                if (ChatUtils.MESSAGE_TYPE_FRIEND.equals(message.getType())) {
                    SetWithLock<ChannelContext> channelContextSetWithLock = Tio.getByUserid(channelContext.tioConfig, message.getId());
                    //用户没有登录，存储到离线文件
                    if (channelContextSetWithLock == null || channelContextSetWithLock.size() == 0) {
                        saveMessage(message, ChatUtils.READ_TYPE_UNREAD);
                    } else {
                        Tio.sendToUser(channelContext.tioConfig, message.getId(), wsResponse);

                        //入库操作
                        saveMessage(message, ChatUtils.READ_TYPE_READ);
                    }

                    if("admin".equals(message.getId())){
                        SendInfo sendInfo1 = ObjectUtil.cloneByStream (sendInfo);
                        Message message1 = sendInfo1.getMessage();
                        message1.setUsername("管理员");
                        message1.setAvatar("http://192.168.199.152:9010/nbcio/temp/nbcio_1660480947146.png");
                        message1.setMine(false);
                        message1.setId(message.getFromname());
                        message1.setFromname("admin");
                        message1.setContent("欢迎使用NBCIO亿事达企业管理平台！https://blog.csdn.net/qq_40032778");
                        sendInfo1.setMessage(message1);
                        WsResponse wsResponse1 = WsResponse.fromText(objectMapper.writeValueAsString(sendInfo1), TioServerConfig.CHARSET);
                        Tio.sendToUser(channelContext.tioConfig, message.getFromname(), wsResponse1);
                    }
                } else {
                    Tio.sendToGroup(channelContext.tioConfig, message.getId(), wsResponse);
                    //入库操作
                    saveMessage(message, ChatUtils.READ_TYPE_READ);
                }
            }
            //准备就绪，需要发送离线消息
            else if (ChatUtils.MSG_READY.equals(sendInfo.getCode())) {
                //未读消息
                sendOffLineMessage(channelContext, objectMapper);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        //返回值是要发送给客户端的内容，一般都是返回null
        return null;
    }

    /**
     * 未读消息
     *
     * @param channelContext channelContext
     * @param objectMapper   objectMapper
     * @throws IOException 抛出异常
     */
    private void sendOffLineMessage(ChannelContext channelContext, ObjectMapper objectMapper) throws IOException {
        List<ImMessage> imMessageList = iImMessageService.getUnReadMessage(channelContext.userid);
        for (ImMessage imMessage : imMessageList) {
            Message message = new Message();
            message.setId(imMessage.getToName());
            message.setMine(false);
            message.setType(imMessage.getType());
            SysUser imUser = imUserService.getByName(imMessage.getFromName());
            message.setUsername(imUser.getUsername());
            message.setCid(String.valueOf(imMessage.getId()));
            message.setContent(imMessage.getContent());
            message.setTimestamp(System.currentTimeMillis());
            message.setFromname(imMessage.getFromName());
            message.setAvatar(imUser.getAvatar());
            SendInfo sendInfo1 = new SendInfo();
            sendInfo1.setCode(ChatUtils.MSG_MESSAGE);
            sendInfo1.setMessage(message);
            WsResponse wsResponse = WsResponse.fromText(objectMapper.writeValueAsString(sendInfo1), TioServerConfig.CHARSET);
            Tio.sendToUser(channelContext.tioConfig, message.getId(), wsResponse);
        }
    }

    /**
     * 保存信息
     *
     * @param message    信息
     * @param readStatus 是否已读
     */
    private void saveMessage(Message message, String readStatus) {
        ImMessage imMessage = new ImMessage();
        imMessage.setToName(message.getUsername());
        imMessage.setFromName(message.getFromname());
        imMessage.setSendTime(System.currentTimeMillis());
        imMessage.setContent(message.getContent());
        imMessage.setReadStatus(readStatus);
        imMessage.setType(message.getType());
        iImMessageService.saveMessage(imMessage);
    }
}
