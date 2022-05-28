package com.nbcio.modules.demo.test_demo.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.nbcio.modules.demo.test_demo.entity.TestDemo;
import com.nbcio.modules.demo.test_demo.mapper.TestDemoMapper;
import com.nbcio.modules.demo.test_demo.service.ITestDemoService;
import com.nbcio.modules.flowable.apithird.business.entity.FlowMyBusiness;
import com.nbcio.modules.flowable.apithird.service.FlowCallBackServiceI;
import com.nbcio.modules.flowable.apithird.service.FlowCommonService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @Description: 测试用户表
 * @Author: nbacheng
 * @Date:   2021-11-30
 * @Version: V1.0
 */
@Service("testDemoService")
public class TestDemoServiceImpl extends ServiceImpl<TestDemoMapper, TestDemo> implements ITestDemoService, FlowCallBackServiceI {
    @Autowired
    FlowCommonService flowCommonService;
    @Override
    public void afterFlowHandle(FlowMyBusiness business) {
        //流程操作后做些什么
        business.getTaskNameId();//接下来审批的节点
        business.getValues();//前端传进来的参数
        business.getActStatus();//流程状态 ActStatus.java
        //....其他

    }



    @Override
    public Object getBusinessDataById(String dataId) {
        return this.getById(dataId);
    }

    @Override
    public Map<String, Object> flowValuesOfTask(String taskNameId, Map<String, Object> values) {
        return null;
    }

    @Override
    public List<String> flowCandidateUsernamesOfTask(String taskNameId, Map<String, Object> values) {
        // 通过当前节点来判断下一个节点的候选人并写回到反参中，如果为null，流程模块会根据默认设置处理
    	
        return null;
    }

    @Override
    public IPage<TestDemo> myPage(Page<TestDemo> page, QueryWrapper<TestDemo> queryWrapper) {
        return this.baseMapper.myPage(page, queryWrapper);
    }
    
    @Override
    public boolean save(TestDemo testDemo) {
        /**新增数据**/
        testDemo.setId(IdUtil.fastSimpleUUID());
        return super.save(testDemo);
    }
    @Override
    public boolean removeById(Serializable id) {
        /**删除数据，移除流程关联信息**/
        flowCommonService.delActBusiness(id.toString());
        return super.removeById(id);
    }

}
