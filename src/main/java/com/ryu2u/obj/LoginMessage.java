package com.ryu2u.obj;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Administrator
 * @Description:
 * @date 2023/5/10 9:34
 */
@Data
public class LoginMessage implements Serializable {
    private static final long serialVersionUID = 8949563892738551697L;

    private Integer userId;
    private String username;

}
