package com.nbcio.modules.im.controller;

import com.nbcio.modules.im.apithird.entity.SysDepartTreeModel;
import com.nbcio.modules.im.apithird.entity.SysUser;
import com.nbcio.modules.im.apithird.service.IImThirdService;
import com.nbcio.modules.im.service.IImUserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author nbacheng
 * @date 2022-8-07
 */
@RestController
@RequestMapping("/im/dept")
public class DeptController {

    /**
     * 顶级的 PARENT_ID
     */
    public static final String DEFAULT_PARENT_ID = "0";

    private final Logger logger = LoggerFactory.getLogger(DeptController.class);


    @Resource
    @Qualifier(value = "imUserService")
    private IImUserService iImUserService;

    @Resource
    private IImThirdService iImThirdService;


    /**
     * 获取我的部门 list
     *
     * @param 
     * @return mylist
     */
    @PostMapping("mylist")
    public List<SysDepartTreeModel> mylist() {
        
        return iImThirdService.queryMyDeptTreeList();
    }

    
    /**
     * 获取部门 用户
     *
     * @param 
     * @return users
     */
    @PostMapping("users")
    public List<SysUser> users(String deptId) {
        
        return iImThirdService.queryDeptUserList(deptId);
    }
    
}
