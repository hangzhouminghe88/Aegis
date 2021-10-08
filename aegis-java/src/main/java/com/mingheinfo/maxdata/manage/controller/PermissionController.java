package com.mingheinfo.maxdata.manage.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mingheinfo.common.constant.CommonConstant;
import com.mingheinfo.common.json.ApiJson;
import com.mingheinfo.common.utils.SecurityUtil;
import com.mingheinfo.config.security.permission.MySecurityMetadataSource;
import com.mingheinfo.maxdata.manage.entity.Permission;
import com.mingheinfo.maxdata.manage.entity.RolePermission;
import com.mingheinfo.maxdata.manage.entity.User;
import com.mingheinfo.maxdata.manage.service.PermissionService;
import com.mingheinfo.maxdata.manage.service.RolePermissionService;
import com.mingheinfo.maxdata.manage.utils.VoUtil;
import com.mingheinfo.maxdata.manage.vo.MenuVo;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/api/permission")
@CacheConfig(cacheNames = "permission")
@Transactional
public class PermissionController {
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private PermissionService permissionService;
    @Autowired
    private SecurityUtil securityUtil;
    @Autowired
    private RolePermissionService rolePermissionService;
    @Autowired
    private MySecurityMetadataSource mySecurityMetadataSource;

    //获取用户页面菜单数据
    @RequestMapping(value = "/getMenuList", method = RequestMethod.GET)
    public ApiJson getAllMenuList(){
    	ApiJson ajaxJson = new ApiJson();
    	
        List<MenuVo> menuList = new ArrayList<>();
        // 读取缓存
        User u = securityUtil.getCurrUser();
        String key = "permission::userMenuList:" + u.getId();
        
        
        String v = redisTemplate.opsForValue().get(key);
        if(StrUtil.isNotBlank(v)){
            menuList = new Gson().fromJson(v, new TypeToken<List<MenuVo>>(){}.getType());
            
            ajaxJson.setResult(menuList);
            return ajaxJson;
        }

        // 用户所有权限 已排序去重
        List<Permission> list = permissionService.findByUserId(u.getId());

        // 筛选0级页面
        for(Permission p : list){
            if(CommonConstant.PERMISSION_NAV.equals(p.getType())&&CommonConstant.LEVEL_ZERO.equals(p.getLevel())){
                menuList.add(VoUtil.permissionToMenuVo(p));
            }
        }
        // 筛选一级页面
        List<MenuVo> firstMenuList = new ArrayList<>();
        for(Permission p : list){
            if(CommonConstant.PERMISSION_PAGE.equals(p.getType())&&CommonConstant.LEVEL_ONE.equals(p.getLevel())){
                firstMenuList.add(VoUtil.permissionToMenuVo(p));
            }
        }
        // 筛选二级页面
        List<MenuVo> secondMenuList = new ArrayList<>();
        for(Permission p : list){
            if(CommonConstant.PERMISSION_PAGE.equals(p.getType())&&CommonConstant.LEVEL_TWO.equals(p.getLevel())){
                secondMenuList.add(VoUtil.permissionToMenuVo(p));
            }
        }
        // 筛选二级页面拥有的按钮权限
        List<MenuVo> buttonPermissions = new ArrayList<>();
        for(Permission p : list){
            if(CommonConstant.PERMISSION_OPERATION.equals(p.getType())&&CommonConstant.LEVEL_THREE.equals(p.getLevel())){
                buttonPermissions.add(VoUtil.permissionToMenuVo(p));
            }
        }

        // 匹配二级页面拥有权限
        for(MenuVo m : secondMenuList){
            List<String> permTypes = new ArrayList<>();
            for(MenuVo me : buttonPermissions){
                if(m.getId()==me.getParentId()){
                    permTypes.add(me.getButtonType());
                }
            }
            m.setPermTypes(permTypes);
        }
        // 匹配一级页面拥有二级页面
        for(MenuVo m : firstMenuList){
            List<MenuVo> secondMenu = new ArrayList<>();
            for(MenuVo me : secondMenuList){
                if(m.getId()==me.getParentId()){
                    secondMenu.add(me);
                }
            }
            m.setChildren(secondMenu);
        }
        // 匹配0级页面拥有一级页面
        for(MenuVo m : menuList){
            List<MenuVo> firstMenu = new ArrayList<>();
            for(MenuVo me : firstMenuList){
                if(m.getId()==me.getParentId()){
                    firstMenu.add(me);
                }
            }
            m.setChildren(firstMenu);
        }

        // 缓存
        redisTemplate.opsForValue().set(key, new Gson().toJson(menuList));
        
        ajaxJson.setResult(menuList);
        return ajaxJson;
    }

    //获取权限菜单树
    @RequestMapping(value = "/getAllList", method = RequestMethod.GET)
    @Cacheable(key = "'allList'")
    public ApiJson getAllList(){
    	ApiJson ajaxJson = new ApiJson();
    	
        // 0级
        List<Permission> list0 = permissionService.findByLevelOrderBySortOrder(CommonConstant.LEVEL_ZERO);
        for(Permission p0 : list0){
            // 一级
            List<Permission> list1 = permissionService.findByParentIdOrderBySortOrder(p0.getId());
            p0.setChildren(list1);
            // 二级
            for(Permission p1 : list1){
                List<Permission> children1 = permissionService.findByParentIdOrderBySortOrder(p1.getId());
                p1.setChildren(children1);
                // 三级
                for(Permission p2 : children1){
                    List<Permission> children2 = permissionService.findByParentIdOrderBySortOrder(p2.getId());
                    p2.setChildren(children2);
                }
            }
        }
        
        ajaxJson.setResult(list0);
        return ajaxJson;        
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @CacheEvict(key = "'menuList'")
    public ApiJson add(Permission permission){
    	ApiJson ajaxJson = new ApiJson();
    	
        // 判断拦截请求的操作权限按钮名是否已存在
        if(CommonConstant.PERMISSION_OPERATION.equals(permission.getType())){
            List<Permission> list = permissionService.findByTitle(permission.getTitle());
            if(list!=null&&list.size()>0){
            	ajaxJson.setError("-1", "名称已存在");
                return ajaxJson;
            }
        }
        permissionService.save(permission);
        
        //重新加载权限
        mySecurityMetadataSource.loadResourceDefine();
        
        //手动删除缓存
        redisTemplate.delete("permission::allList");
        
        ajaxJson.setResult(permission);
        
        return ajaxJson;
    }

    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public ApiJson edit(Permission permission){
    	ApiJson ajaxJson = new ApiJson();
    	
        // 判断拦截请求的操作权限按钮名是否已存在
        if(CommonConstant.PERMISSION_OPERATION.equals(permission.getType())){
            // 若名称修改
            Permission p = permissionService.getById(permission.getId());
            if(!p.getTitle().equals(permission.getTitle())){
                List<Permission> list = permissionService.findByTitle(permission.getTitle());
                if(list!=null&&list.size()>0){
                	ajaxJson.setError("-1", "名称已存在");
                    return ajaxJson;
                }
            }
        }
        permissionService.updateById(permission);
        
        //重新加载权限
        mySecurityMetadataSource.loadResourceDefine();
        //手动批量删除缓存
        Set<String> keys = redisTemplate.keys("userPermission:" + "*");
        redisTemplate.delete(keys);
        Set<String> keysUser = redisTemplate.keys("user:" + "*");
        redisTemplate.delete(keysUser);
        Set<String> keysUserMenu = redisTemplate.keys("permission::userMenuList:*");
        redisTemplate.delete(keysUserMenu);
        redisTemplate.delete("permission::allList");
        
        ajaxJson.setResult(permission);
        return ajaxJson;

    }

    @RequestMapping(value = "/delByIds/{ids}", method = RequestMethod.DELETE)
    @CacheEvict(key = "'menuList'")
    public ApiJson delByIds(@PathVariable String[] ids){
    	ApiJson ajaxJson = new ApiJson();
    	
        for(String id:ids){
        	
            List<RolePermission> list = rolePermissionService.findByPermissionId(Integer.parseInt(id));
            if(list!=null&&list.size()>0){
            	ajaxJson.setError("-1", "删除失败，包含正被角色使用关联的菜单或权限");
                return ajaxJson;
            }
        }
        for(String id:ids){
            permissionService.removeById(id);
        }
        //重新加载权限
        mySecurityMetadataSource.loadResourceDefine();
        //手动删除缓存
        redisTemplate.delete("permission::allList");
        
        return ajaxJson;
    }

    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public ApiJson searchPermissionList(@RequestParam String title){
    	ApiJson ajaxJson = new ApiJson();
    	
        List<Permission> list = permissionService.findByTitleLikeOrderBySortOrder("%"+title+"%");
        
        ajaxJson.setResult(list);
        return ajaxJson;
    }
}
