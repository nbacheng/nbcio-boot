package com.nbcio.modules.im.utils;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.Date;

/**
 * 基础类别
 *
 * @author nbacheng
 * @since 2018-10-07
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BaseEntity<T> {

    /**
     * 说明
     */
    private String remarks;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createDate;

    private String createBy;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateDate;

    private String updateBy;

    @TableLogic(value="0",delval="1")
    private String delFlag;

    public void preInsert() {
        this.createDate = new Date();
        this.updateDate = new Date();
        this.delFlag = "0";
    }
}
