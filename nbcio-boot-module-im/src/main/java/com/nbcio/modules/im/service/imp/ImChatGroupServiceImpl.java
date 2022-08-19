package com.nbcio.modules.im.service.imp;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nbcio.modules.im.domain.ImChatGroup;
import com.nbcio.modules.im.mapper.ImChatGroupMapper;
import com.nbcio.modules.im.service.IImChatGroupService;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 群 服务实现类
 * </p>
 *
 * @author nbacheng
 * @since 2018-10-28
 */
@Service
@Qualifier("imChatGroupServiceImpl")
public class ImChatGroupServiceImpl extends ServiceImpl<ImChatGroupMapper, ImChatGroup> implements IImChatGroupService {

}
