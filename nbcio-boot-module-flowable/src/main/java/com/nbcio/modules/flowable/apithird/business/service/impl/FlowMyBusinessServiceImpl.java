package com.nbcio.modules.flowable.apithird.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nbcio.modules.flowable.apithird.business.entity.FlowMyBusiness;
import com.nbcio.modules.flowable.apithird.business.mapper.FlowMyBusinessMapper;
import com.nbcio.modules.flowable.apithird.business.service.IFlowMyBusinessService;

import org.springframework.stereotype.Service;

/**
 * @Description: 流程业务扩展表
 * @Author: nbacheng
 * @Date:   2021-11-25
 * @Version: V1.0
 */
@Service
public class FlowMyBusinessServiceImpl extends ServiceImpl<FlowMyBusinessMapper, FlowMyBusiness> implements IFlowMyBusinessService {

    public FlowMyBusiness getByDataId(String dataId) {
        LambdaQueryWrapper<FlowMyBusiness> flowMyBusinessLambdaQueryWrapper = new LambdaQueryWrapper<>();
        flowMyBusinessLambdaQueryWrapper.eq(FlowMyBusiness::getDataId,dataId)
        ;
        //如果保存数据前未调用必调的FlowCommonService.initActBusiness方法，就会有问题
        FlowMyBusiness business = this.getOne(flowMyBusinessLambdaQueryWrapper);
        return business;
    }
}
