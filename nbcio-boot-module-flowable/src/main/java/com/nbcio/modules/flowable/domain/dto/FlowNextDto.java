package com.nbcio.modules.flowable.domain.dto;

import lombok.Data;
import org.flowable.bpmn.model.UserTask;

import com.nbcio.modules.flowable.apithird.entity.SysRole;
import com.nbcio.modules.flowable.apithird.entity.SysUser;

import java.io.Serializable;
import java.util.List;

/**
 * 人员、组
 */
@Data
public class FlowNextDto implements Serializable {
	

    private String type;

    private String vars;
	
    /**
     * 节点对象
     */
    private UserTask userTask;
    /**
     * 待办人员
     */
    private List<SysUser> userList;
    
    private List<SysRole> roleList;

}
