package com.nbcio.modules.im.entity;

/**
 * 消息已读回执
 * @author nbacheng
 */
public class Receipt {

    /**
     * 聊天室id
     */
    private String chatId;

    /**
     * 消息读取人
     */
    private String userId;

    /**
     * 最后一条消息读取时间
     */
    private Long timestamp;

    /**
     * 消息类型
     */
    private String type;

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
