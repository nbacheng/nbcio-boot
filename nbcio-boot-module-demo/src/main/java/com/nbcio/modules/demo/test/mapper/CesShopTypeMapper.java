package com.nbcio.modules.demo.test.mapper;

import org.apache.ibatis.annotations.Param;
import com.nbcio.modules.demo.test.entity.CesShopType;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 商品分类
 * @Author: nbacheng
 * @Date:   2022-05-15
 * @Version: V1.0
 */
public interface CesShopTypeMapper extends BaseMapper<CesShopType> {

	/**
	 * 编辑节点状态
	 * @param id
	 * @param status
	 */
	void updateTreeNodeStatus(@Param("id") String id,@Param("status") String status);

}
