package com.nbcio.modules.im.apithird.entity;

import lombok.Data;

/**
 * 角色
 * @author pmc
 */
@Data
public class SysRole {
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    private String id;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 角色编码
     */
    private String roleCode;
}
