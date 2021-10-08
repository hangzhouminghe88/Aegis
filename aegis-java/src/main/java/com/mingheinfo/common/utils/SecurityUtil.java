package com.mingheinfo.common.utils;

import cn.hutool.core.util.StrUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mingheinfo.common.constant.CommonConstant;
import com.mingheinfo.maxdata.manage.entity.Permission;
import com.mingheinfo.maxdata.manage.entity.Role;
import com.mingheinfo.maxdata.manage.entity.User;
import com.mingheinfo.maxdata.manage.service.UserRoleService;
import com.mingheinfo.maxdata.manage.service.UserService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;


@Component
@Slf4j
public class SecurityUtil {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 获取当前登录用户
     * @return
     */
    public User getCurrUser(){

        UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userService.findByUsername(user.getUsername());
    }

    /**
     * 获取当前用户数据权限 null代表具有所有权限 包含值为-1的数据代表无任何权限
     */
    public List<String> getDeparmentIds(){

        List<String> deparmentIds = new ArrayList<>();
        User u = getCurrUser();
        
        // 读取缓存
        String key = "userRole::depIds:" + u.getId();
        String v = redisTemplate.opsForValue().get(key);
        if(StrUtil.isNotBlank(v)){
            deparmentIds = new Gson().fromJson(v, new TypeToken<List<String>>(){}.getType());
            return deparmentIds;
        }
        // 当前用户拥有角色
        List<Role> roles = userRoleService.findByUserId(u.getId());
        // 判断有无全部数据的角色
        Boolean flagAll = false;
        for(Role r : roles){
            if(r.getDataType()==null||r.getDataType().equals(CommonConstant.DATA_TYPE_ALL)){
                flagAll = true;
                break;
            }
        }
        // 包含全部权限返回null
        if(flagAll){
            return null;
        }
        // 每个角色判断 求并集
        for(Role r : roles) {
        	/*
            if (r.getDataType().equals(CommonConstant.DATA_TYPE_UNDER)) {
                // 本部门及以下
                if (StrUtil.isBlank(u.getDepartmentId())) {
                    // 用户无部门
                    deparmentIds.add("-1");
                } else {
                    // 递归获取自己与子级
                    List<String> ids = new ArrayList<>();
                    getRecursion(u.getDepartmentId(), ids);
                    deparmentIds.addAll(ids);
                }
            } else if (r.getDataType().equals(CommonConstant.DATA_TYPE_SAME)) {
                // 本部门
                if (StrUtil.isBlank(u.getDepartmentId())) {
                    // 用户无部门
                    deparmentIds.add("-1");
                } else {
                    deparmentIds.add(u.getDepartmentId());
                }
            } else if (r.getDataType().equals(CommonConstant.DATA_TYPE_CUSTOM)) {
                // 自定义
                List<String> depIds = iUserRoleService.findDepIdsByUserId(u.getId());
                if (depIds == null || depIds.size() == 0) {
                    deparmentIds.add("-1");
                } else {
                    deparmentIds.addAll(depIds);
                }
            }*/
        }
        // 去重
        LinkedHashSet<String> set = new LinkedHashSet<>(deparmentIds.size());
        set.addAll(deparmentIds);
        deparmentIds.clear();
        deparmentIds.addAll(set);
        // 缓存
        redisTemplate.opsForValue().set(key, new Gson().toJson(deparmentIds));
        return deparmentIds;
    }

    private void getRecursion(String departmentId, List<String> ids){

    	/*
        Department department = departmentService.get(departmentId);
        ids.add(department.getId());
        if(department.getIsParent()!=null&&department.getIsParent()){
            // 获取其下级
            List<Department> departments = departmentService.findByParentIdAndStatusOrderBySortOrder(departmentId, CommonConstant.STATUS_NORMAL);
            departments.forEach(d->{
                getRecursion(d.getId(), ids);
            });
        }*/
    }

    /**
     * 通过用户名获取用户拥有权限
     * @param username
     */
    public List<GrantedAuthority> getCurrUserPerms(String username){

        List<GrantedAuthority> authorities = new ArrayList<>();
        for(Permission p : userService.findByUsername(username).getPermissions()){
            authorities.add(new SimpleGrantedAuthority(p.getTitle()));
        }
        return authorities;
    }
}
