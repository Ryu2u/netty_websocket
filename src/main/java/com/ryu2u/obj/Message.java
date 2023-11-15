package com.ryu2u.obj;

import lombok.Data;

import java.io.Serializable;

/**
 * 接收信息
 * @author Administrator
 * @Description:
 * @date 2023/5/10 9:09
 */
@Data
public class Message implements Serializable {
    private static final long serialVersionUID = 5658389284308527960L;
    public static final Integer LOGIN_MESSAGE = 1;
    public static final Integer SINGLE_MESSAGE = 2;
    public static final Integer GROUP_MESSAGE = 3;
    /**
     * 信息头，判断是哪种类型的信息
     * 1: LoginMessage
     * 2: SingleChatMessage
     * 3: GroupChatMessage
     * @see LoginMessage
     * @see SingleChatMessage
     * @see GroupChatMessage
     */
    private Integer type;
    /**
     * 身份验证token
     */
    private String token;
    /**
     * 传输数据
     */
    private Object data;
}
