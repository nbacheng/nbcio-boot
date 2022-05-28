package com.nbcio.modules.demo.test.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nbcio.modules.demo.test.entity.JoaDemo;
import com.nbcio.modules.demo.test.mapper.JoaDemoMapper;
import com.nbcio.modules.demo.test.service.IJoaDemoService;

/**
 * @Description: 流程测试
 * @Author: nbacheng
 * @Date:   2019-05-14
 * @Version: V1.0
 */
@Service
public class JoaDemoServiceImpl extends ServiceImpl<JoaDemoMapper, JoaDemo> implements IJoaDemoService {

}
