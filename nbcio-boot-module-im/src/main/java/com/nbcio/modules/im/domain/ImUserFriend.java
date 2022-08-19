package com.nbcio.modules.im.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.nbcio.modules.im.utils.BaseEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 好友实体
 *
 * @author nbacheng
 * @since 2018-12-31
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("im_user_friend")
public class ImUserFriend extends BaseEntity<ImUserFriend> implements Serializable  {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 好友ID
     */
    private String friendId;



}
