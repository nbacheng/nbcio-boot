package com.nbcio.modules.demo.test_demo.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nbcio.modules.demo.test_demo.entity.TestDemo;

import org.apache.ibatis.annotations.Param;

/**
 * @Description: 测试用户表
 * @Author: nbacheng
 * @Date:   2021-11-30
 * @Version: V1.0
 */
public interface TestDemoMapper extends BaseMapper<TestDemo> {

    IPage<TestDemo> myPage(Page<TestDemo> page, @Param(Constants.WRAPPER) QueryWrapper<TestDemo> queryWrapper);
}
