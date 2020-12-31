package com.king.project.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.king.project.entity.Role;
import com.king.project.mapper.RoleMapper;
import com.king.project.service.RoleService;
import org.springframework.stereotype.Service;

import java.util.Collection;

/**
 * @author 金振林
 * @version v1.0
 * @date 2020/12/25 15:29
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

}
