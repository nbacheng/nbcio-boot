package com.nbcio.modules.flowable.domain.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * 基础常量
 */
@Data
//@Builder
public class FlowCommentDto implements Serializable {

    /**
     * 意见类别: 1-正常意见 2-退回意见 3-驳回意见 4-委派意见5-转办意见
     */
    private String type;

    /**
     * 意见内容
     */
    private String comment;
}
