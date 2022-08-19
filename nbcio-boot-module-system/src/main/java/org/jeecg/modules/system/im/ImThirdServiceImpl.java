package org.jeecg.modules.system.im;

import java.util.ArrayList;

import java.util.List;
import java.util.Map;

import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;

import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.oConvertUtils;

import org.jeecg.modules.system.service.ISysDepartService;
import org.jeecg.modules.system.service.impl.SysRoleServiceImpl;
import org.jeecg.modules.system.service.impl.SysUserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nbcio.modules.im.apithird.entity.SysDepart;
import com.nbcio.modules.im.apithird.entity.SysDepartTreeModel;
import com.nbcio.modules.im.apithird.entity.SysRole;
import com.nbcio.modules.im.apithird.entity.SysUser;
import com.nbcio.modules.im.apithird.service.IImThirdService;

import cn.hutool.core.bean.BeanUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * im模块必需实现类
 *@author nbacheng
 *@date 2022/08/03
 *@version 1.0
 */
@Service
@Slf4j
public class ImThirdServiceImpl implements IImThirdService {
    @Autowired
    ISysBaseAPI sysBaseAPI;
    @Autowired
    SysUserServiceImpl sysUserService;
    @Autowired
    SysRoleServiceImpl sysRoleService;
    @Autowired
	private ISysDepartService sysDepartService;

    
    
    @Override
    public SysUser getLoginUser() {
        LoginUser sysUser = null;
        SysUser copyProperties = null;
        try {
            sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
            copyProperties = BeanUtil.copyProperties(sysUser, SysUser.class);
        } catch (Exception e) {
            //e.printStackTrace();
        }
        return copyProperties;
    }

    @Override
    public List<SysUser> getAllUser() {
        List<org.jeecg.modules.system.entity.SysUser> list = sysUserService.list();
        List<SysUser> userList = list.stream().map(o -> BeanUtil.copyProperties(o, SysUser.class)).collect(Collectors.toList());
        return userList;
    }

    @Override
    public List<SysUser> getUsersByRoleId(String roleId) {
        Page<org.jeecg.modules.system.entity.SysUser> page = new Page<>(1,Integer.MAX_VALUE);
        IPage<org.jeecg.modules.system.entity.SysUser> userByRoleId = sysUserService.getUserByRoleId(page, roleId, null);
        List<org.jeecg.modules.system.entity.SysUser> records = userByRoleId.getRecords();
        List<SysUser> userList = records.stream().map(o -> BeanUtil.copyProperties(o, SysUser.class)).collect(Collectors.toList());
        return userList;
    }


    @Override
    public SysUser getUserByUsername(String username) {
        LoginUser userByName = sysBaseAPI.getUserByName(username);
        return userByName==null?null:BeanUtil.copyProperties(userByName, SysUser.class);
    }

    @Override
    public List<SysRole> getAllRole() {
        List<org.jeecg.modules.system.entity.SysRole> list = sysRoleService.list();
        List<SysRole> roleList = list.stream().map(o -> BeanUtil.copyProperties(o, SysRole.class)).collect(Collectors.toList());
        return roleList;
    }


    @Override
    public List<String> getDepartNamesByUsername(String username) {
        List<String> departNamesByUsername = sysBaseAPI.getDepartNamesByUsername(username);
        return departNamesByUsername;
    }

	@Override
	public void sendSysAnnouncement(String fromUser, String toUser, String title, String msgContent,
			String setMsgCategory) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getBaseUrl() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SysDepart selectDeptById(long deptId) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public JSONObject queryAllParentIdByDepartId(String departId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<SysUser> selectUserList(String departId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<SysUser> search(String mobile) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SysUser getUserByName(String username) {
		 LoginUser userByName = sysBaseAPI.getUserByName(username);
	     return userByName==null?null:BeanUtil.copyProperties(userByName, SysUser.class);
	}

	@Override
	public int updateUser(SysUser user) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<SysDepartTreeModel> queryMyDeptTreeList() {
		LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		try {
				String departIds = user.getDepartIds();
				if(StringUtils.isNotBlank(departIds)){
					List<org.jeecg.modules.system.model.SysDepartTreeModel> list = sysDepartService.queryMyDeptTreeList(departIds);
					List<SysDepartTreeModel> deptlist = list.stream().map(o -> BeanUtil.copyProperties(o, SysDepartTreeModel.class)).collect(Collectors.toList());
					return deptlist;
				}
			
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}
		return null;
	}

	@Override
	public List<SysUser> queryDeptUserList(String deptId) {
        //根据部门ID查询,当前和下级所有的部门IDS
        List<String> subDepids = new ArrayList<>();
        //部门id为空时，查询我的部门下所有用户
        if(oConvertUtils.isEmpty(deptId)){
            LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
            int userIdentity = user.getUserIdentity() != null?user.getUserIdentity():CommonConstant.USER_IDENTITY_1;
            if(oConvertUtils.isNotEmpty(userIdentity) && userIdentity == CommonConstant.USER_IDENTITY_2 ){
                subDepids = sysDepartService.getMySubDepIdsByDepId(user.getDepartIds());
            }
        }else{
            subDepids = sysDepartService.getSubDepIdsByDepId(deptId);
        }
        Page<org.jeecg.modules.system.entity.SysUser> page = new Page<>(1,Integer.MAX_VALUE);
        if(subDepids != null && subDepids.size()>0){
            IPage<org.jeecg.modules.system.entity.SysUser> pageList = sysUserService.getUsersByDepIds(page,subDepids);

            //批量查询用户的所属部门
            //step.1 先拿到全部的 useids
            //step.2 通过 useids，一次性查询用户的所属部门名字
            List<String> userIds = pageList.getRecords().stream().map(org.jeecg.modules.system.entity.SysUser::getId).collect(Collectors.toList());
            if(userIds!=null && userIds.size()>0){
                Map<String, String> useDepNames = sysUserService.getDepNamesByUserIds(userIds);
                pageList.getRecords().forEach(item -> {
                    //批量查询用户的所属部门
                    item.setOrgCode(useDepNames.get(item.getId()));
                });
            }
            List<SysUser> listuser = pageList.getRecords().stream().map(o -> BeanUtil.copyProperties(o, SysUser.class)).collect(Collectors.toList());
            return listuser;
        }else{
            return null;
        }
	}
}
