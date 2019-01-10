package com.imagespace.user.model.vo;

import lombok.Getter;
import lombok.Setter;

/**
 * @author gusaishuai
 * @since 2019/1/10
 */
@Setter
@Getter
public class UserVo {

    private Long id;
    private String loginName;
    /**
     * 昵称
     */
    private String nick;

}
