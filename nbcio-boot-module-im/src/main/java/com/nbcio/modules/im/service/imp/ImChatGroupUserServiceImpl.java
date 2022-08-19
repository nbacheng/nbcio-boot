package com.nbcio.modules.im.service.imp;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nbcio.modules.im.domain.ImChatGroupUser;
import com.nbcio.modules.im.mapper.ImChatGroupUserMapper;
import com.nbcio.modules.im.service.IImChatGroupUserService;

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
@Qualifier("imChatGroupUserService")
public class ImChatGroupUserServiceImpl extends ServiceImpl<ImChatGroupUserMapper, ImChatGroupUser> implements IImChatGroupUserService {

}
