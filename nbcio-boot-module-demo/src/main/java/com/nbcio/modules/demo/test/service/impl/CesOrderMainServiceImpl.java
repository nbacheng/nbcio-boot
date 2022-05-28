package com.nbcio.modules.demo.test.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.nbcio.modules.demo.test.entity.CesOrderCustomer;
import com.nbcio.modules.demo.test.entity.CesOrderGoods;
import com.nbcio.modules.demo.test.entity.CesOrderMain;
import com.nbcio.modules.demo.test.mapper.CesOrderCustomerMapper;
import com.nbcio.modules.demo.test.mapper.CesOrderGoodsMapper;
import com.nbcio.modules.demo.test.mapper.CesOrderMainMapper;
import com.nbcio.modules.demo.test.service.ICesOrderMainService;
import com.nbcio.modules.flowable.apithird.business.entity.FlowMyBusiness;
import com.nbcio.modules.flowable.apithird.service.FlowCallBackServiceI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * @Description: 商城订单表
 * @Author: nbacheng
 * @Date:   2022-04-25
 * @Version: V1.0
 */
@Service("cesOrderMainService")
public class CesOrderMainServiceImpl extends ServiceImpl<CesOrderMainMapper, CesOrderMain> implements ICesOrderMainService, FlowCallBackServiceI {

	@Autowired
	private CesOrderMainMapper cesOrderMainMapper;
	@Autowired
	private CesOrderGoodsMapper cesOrderGoodsMapper;
	@Autowired
	private CesOrderCustomerMapper cesOrderCustomerMapper;
	
    @Override
    public IPage<CesOrderMain> myPage(Page<CesOrderMain> page, QueryWrapper<CesOrderMain> queryWrapper) {
        return this.baseMapper.myPage(page, queryWrapper);
    }
    
	@Override
	@Transactional
	public void saveMain(CesOrderMain cesOrderMain, List<CesOrderGoods> cesOrderGoodsList,List<CesOrderCustomer> cesOrderCustomerList) {
		cesOrderMainMapper.insert(cesOrderMain);
		if(cesOrderGoodsList!=null && cesOrderGoodsList.size()>0) {
			for(CesOrderGoods entity:cesOrderGoodsList) {
				//外键设置
				entity.setOrderMainId(cesOrderMain.getId());
				cesOrderGoodsMapper.insert(entity);
			}
		}
		if(cesOrderCustomerList!=null && cesOrderCustomerList.size()>0) {
			for(CesOrderCustomer entity:cesOrderCustomerList) {
				//外键设置
				entity.setOrderMainId(cesOrderMain.getId());
				cesOrderCustomerMapper.insert(entity);
			}
		}
	}

	@Override
	@Transactional
	public void updateMain(CesOrderMain cesOrderMain,List<CesOrderGoods> cesOrderGoodsList,List<CesOrderCustomer> cesOrderCustomerList) {
		cesOrderMainMapper.updateById(cesOrderMain);
		
		//1.先删除子表数据
		cesOrderGoodsMapper.deleteByMainId(cesOrderMain.getId());
		cesOrderCustomerMapper.deleteByMainId(cesOrderMain.getId());
		
		//2.子表数据重新插入
		if(cesOrderGoodsList!=null && cesOrderGoodsList.size()>0) {
			for(CesOrderGoods entity:cesOrderGoodsList) {
				//外键设置
				entity.setOrderMainId(cesOrderMain.getId());
				cesOrderGoodsMapper.insert(entity);
			}
		}
		if(cesOrderCustomerList!=null && cesOrderCustomerList.size()>0) {
			for(CesOrderCustomer entity:cesOrderCustomerList) {
				//外键设置
				entity.setOrderMainId(cesOrderMain.getId());
				cesOrderCustomerMapper.insert(entity);
			}
		}
	}

	@Override
	@Transactional
	public void delMain(String id) {
		cesOrderGoodsMapper.deleteByMainId(id);
		cesOrderCustomerMapper.deleteByMainId(id);
		cesOrderMainMapper.deleteById(id);
	}

	@Override
	@Transactional
	public void delBatchMain(Collection<? extends Serializable> idList) {
		for(Serializable id:idList) {
			cesOrderGoodsMapper.deleteByMainId(id.toString());
			cesOrderCustomerMapper.deleteByMainId(id.toString());
			cesOrderMainMapper.deleteById(id);
		}
	}

	@Override
	public void afterFlowHandle(FlowMyBusiness business) {
		// TODO Auto-generated method stub
		 //流程操作后做些什么
        business.getTaskNameId();//接下来审批的节点
        business.getValues();//前端传进来的参数
        business.getActStatus();//流程状态 ActStatus.java
        //....其他
	}

	@Override
	public Object getBusinessDataById(String dataId) {
		// TODO Auto-generated method stub
		return this.getById(dataId);
	}

	@Override
	public Map<String, Object> flowValuesOfTask(String taskNameId, Map<String, Object> values) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> flowCandidateUsernamesOfTask(String taskNameId, Map<String, Object> values) {
		// TODO Auto-generated method stub
		// 案例，写死了jeecg，实际业务中通过当前节点来判断下一个节点的候选人并写回到反参中，如果为null，流程模块会根据默认设置处理
        return Lists.newArrayList("jeecg");
	}
	
}
