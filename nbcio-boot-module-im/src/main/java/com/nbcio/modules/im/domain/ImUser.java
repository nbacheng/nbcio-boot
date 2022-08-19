package com.nbcio.modules.im.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.nbcio.modules.im.utils.BaseEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 用户实体
 *
 * @author nbacheng
 * @since 2018-10-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ImUser  extends BaseEntity<ImUser> implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId
    private String id;

    private String avatar;

    private String name;

    private String sign;

    private String mobile;

    private String email;

    private String password;


    @TableField("username")
    private String loginName;

    @TableField("dept_id")
    private String deptId;
}
