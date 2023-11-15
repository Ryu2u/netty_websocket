package com.ryu2u.obj;

import lombok.Data;

import java.io.Serializable;

/**
 * 一对一私聊
 * @author Administrator
 * @Description:
 * @date 2023/5/9 17:35
 */
@Data
public class SingleChatMessage implements Serializable {

    private static final long serialVersionUID = -5683102423048192797L;
    /**
     * 发送者id
     */
    private String senderId;
    /**
     * 发送者名称
     */
    private String senderName;
    /**
     * 接收者id
     */
    private String receiverId;
    /**
     * 接收者名称
     */
    private String receiverName;
    /**
     * 消息内容
     */
    private String content;
    /**
     * 发送时间
     */
    private String sendDate;

}
