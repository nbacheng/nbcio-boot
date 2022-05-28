package com.nbcio.modules.flowable.domain.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * 基础常量
 */
@Data
//@Builder
public class FlowCommentFileDto implements Serializable {

    /**
     * 意见类别: 1-正常意见 2-退回意见 3-驳回意见 4-委派意见5-转办意见
     */
    private String type;

    /**
     * 文件url
     */
    private String fileurl;
}
