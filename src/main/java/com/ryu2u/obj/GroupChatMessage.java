package com.ryu2u.obj;

import java.io.Serializable;

/**
 * 多对多群聊
 * @author Administrator
 * @Description:
 * @date 2023/5/10 10:50
 */
public class GroupChatMessage implements Serializable {
    private static final long serialVersionUID = -1627658139592649971L;

    /**
     * 消息类型，例如文本，图片
     */
    private String messageType;
    /**
     * 群组id
     * 需要发送广播的群组
     */
    private String groupId;
    /**
     * 发送者的id
     */
    private String senderId;
    /**
     * 消息内容
     */
    private String content;
    /**
     * 发送时间
     */
    private String sendDate;

}
