package com.nbcio.modules.im.apithird.entity;

import com.fasterxml.jackson.annotation.JsonFormat;


import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

/**
 * 用户对象 <br/>
 *  //todo 暂时支持用于jeecg，如需迁移其他框架，需要改动
 * @author pmc
 */
@Data
public class SysUser {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    private String id;

    /**
     * 登录账号
     */
    private String username;

    /**
     * 真实姓名
     */
    private String realname;

    /**
     * 密码
     */
    private String password;

    /**
     * md5密码盐
     */
    private String salt;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 生日
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date birthday;

    /**
     * 性别（1：男 2：女）
     */
    private Integer sex;

    /**
     * 电子邮件
     */
    private String email;

    /**
     * 电话
     */
    private String phone;

    /**
     * 部门code(当前选择登录部门)
     */
    private String orgCode;

    /**部门名称*/
    private transient String orgCodeTxt;

    /**
     * 状态(1：正常  2：冻结 ）
     */
    private Integer status;

    /**
     * 删除状态（0，正常，1已删除）
     */
    private Integer delFlag;

    /**
     * 工号，唯一键
     */
    private String workNo;

    /**
     * 职务，关联职务表
     */
    private String post;

    /**
     * 座机号
     */
    private String telephone;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新人
     */
    private String updateBy;

    /**
     * 更新时间
     */
    private Date updateTime;
    /**
     * 同步工作流引擎1同步0不同步
     */
    private Integer activitiSync;

    /**
     * 身份（0 普通成员 1 上级）
     */
    private Integer userIdentity;

    /**
     * 负责部门
     */
    private String departIds;
    
    private List<SysUser> friends;

    public SysUser() {
    }

    public SysUser(String id, String username, String avatar, String phone, Integer sex,String orgCode,String email) {
        this.id = id;
        this.username = username;
        this.avatar = avatar;
        this.phone = phone;
        this.sex = sex;
        this.orgCode = orgCode;
        this.email = email;
    }

}
