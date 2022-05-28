package com.nbcio.modules.flowable.apithird.entity;

import lombok.Data;

/**
 *流程分类
 *@author PanMeiCheng
 *@date 2021/11/25
 *@version 1.0
 */
@Data
public class SysCategory {
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    private String id;

    /**
     * 分类名称
     */
    private String name;
}
