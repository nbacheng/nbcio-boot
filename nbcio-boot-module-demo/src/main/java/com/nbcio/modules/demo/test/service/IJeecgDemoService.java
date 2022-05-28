package com.nbcio.modules.demo.test.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.jeecg.common.system.base.service.JeecgService;
import com.nbcio.modules.demo.test.entity.JeecgDemo;

/**
 * @Description: jeecg 测试demo
 * @Author: nbacheng
 * @Date:  2018-12-29
 * @Version: V1.0
 */
public interface IJeecgDemoService extends JeecgService<JeecgDemo> {
	
	public void testTran();
	
	public JeecgDemo getByIdCacheable(String id);
	
	/**
	 * 查询列表数据 在service中获取数据权限sql信息
	 * @param pageSize
	 * @param pageNo
	 * @return
	 */
	IPage<JeecgDemo> queryListWithPermission(int pageSize,int pageNo);

	/**
	 * 根据用户权限获取导出字段
	 * @return
	 */
	String getExportFields();
}
