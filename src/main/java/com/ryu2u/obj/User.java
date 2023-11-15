package com.ryu2u.obj;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Administrator
 * @Description:
 * @date 2023/5/9 17:33
 */
@Data
public class User implements Serializable {
    private static final long serialVersionUID = 2178929175554042896L;
    private Integer id;
    private String name;
}
