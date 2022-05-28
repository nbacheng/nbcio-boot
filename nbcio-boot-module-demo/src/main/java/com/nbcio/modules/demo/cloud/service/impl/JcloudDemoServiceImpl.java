package com.nbcio.modules.demo.cloud.service.impl;

import org.springframework.stereotype.Service;

import org.jeecg.common.api.vo.Result;
import com.nbcio.modules.demo.cloud.service.JcloudDemoService;

@Service
public class JcloudDemoServiceImpl implements JcloudDemoService {
    @Override
    public Result<String> getMessage(String name) {
        return Result.OK("Hello，" + name);
    }
}
