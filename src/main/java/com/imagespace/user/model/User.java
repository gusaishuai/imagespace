package com.imagespace.user.model;

import lombok.Getter;
import lombok.Setter;

/**
 * @author gusaishuai
 * @since 18/12/16
 */
@Setter
@Getter
public class User {

    private Long id;
    private String loginName;
    /**
     * MD5编码的密码
     */
    private String password;
    /**
     * 昵称
     */
    private String nick;

}
