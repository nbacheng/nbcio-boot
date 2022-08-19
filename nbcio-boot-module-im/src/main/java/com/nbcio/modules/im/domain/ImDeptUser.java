package com.nbcio.modules.im.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

import com.nbcio.modules.im.utils.BaseEntity;

/**
 * <p>
 * 组织部门 用户关联表
 * </p>
 *
 * @author nbacheng
 * @since 2018-10-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ImDeptUser extends BaseEntity<ImDeptUser> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 部门id
     */
    private String deptId;

    /**
     * 用户id
     */
    private String userId;




}
