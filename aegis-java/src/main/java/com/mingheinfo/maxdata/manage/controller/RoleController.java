package com.mingheinfo.maxdata.manage.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mingheinfo.common.json.ApiJson;
import com.mingheinfo.common.utils.ResultUtil;
import com.mingheinfo.common.vo.PageVo;
import com.mingheinfo.maxdata.manage.entity.Role;
import com.mingheinfo.maxdata.manage.entity.RolePermission;
import com.mingheinfo.maxdata.manage.entity.User;
import com.mingheinfo.maxdata.manage.entity.UserRole;
import com.mingheinfo.maxdata.manage.service.RolePermissionService;
import com.mingheinfo.maxdata.manage.service.RoleService;
import com.mingheinfo.maxdata.manage.service.UserRoleService;

import java.util.List;
import java.util.Set;


/**
 * 角色管理接口
 */
@Slf4j
@RestController
@RequestMapping("/api/role")
@Transactional
public class RoleController {

    @Autowired
    private RoleService roleService;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private RolePermissionService rolePermissionService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    //获取全部角色
    @RequestMapping(value = "/getAllList",method = RequestMethod.GET)
    public ApiJson roleGetAll(){

        List<Role> list = roleService.getAll();
        return ResultUtil.data(list);
    }

    //分页获取角色
    @RequestMapping(value = "/getAllByPage",method = RequestMethod.GET)
    public ApiJson getRoleByPage(PageVo pageVo){

    	IPage<Role> page = new Page<Role>(pageVo.getPageNumber(),pageVo.getPageSize());
    	
    	QueryWrapper<Role> queryWrapper = new QueryWrapper<Role>();
    	
    	page = roleService.page(page, queryWrapper);
     
        
        for(Role role : page.getRecords()){
            // 角色拥有权限
            List<RolePermission> permissions = rolePermissionService.findByRoleId(role.getId());
            role.setPermissions(permissions);
            // 角色拥有数据权限
            //List<RoleDepartment> departments = roleDepartmentService.findByRoleId(role.getId());
            //role.setDepartments(departments);
        }
        return ResultUtil.data(page);
    }

    //设置或取消默认角色
    @RequestMapping(value = "/setDefault",method = RequestMethod.POST)
    public ApiJson setDefault(@RequestParam String id,
                                     @RequestParam Boolean isDefault){

        Role role = roleService.getById(id);
        if(role==null){
            return ResultUtil.error("角色不存在");
        }
        role.setDefaultRole(isDefault);
        roleService.updateById(role);
        return ResultUtil.success("设置成功");
    }

    //编辑角色分配菜单权限
    @RequestMapping(value = "/editRolePerm",method = RequestMethod.POST)
    public ApiJson editRolePerm(@RequestParam Integer roleId,
                                       @RequestParam(required = false) Integer[] permIds){

        //删除其关联权限
        rolePermissionService.deleteByRoleId(roleId);
        //分配新权限
        for(Integer permId : permIds){
            RolePermission rolePermission = new RolePermission();
            rolePermission.setRoleId(roleId);
            rolePermission.setPermissionId(permId);
            rolePermissionService.save(rolePermission);
        }
        //手动批量删除缓存
        Set<String> keysUser = redisTemplate.keys("user:" + "*");
        redisTemplate.delete(keysUser);
        Set<String> keysUserRole = redisTemplate.keys("userRole:" + "*");
        redisTemplate.delete(keysUserRole);
        Set<String> keysUserPerm = redisTemplate.keys("userPermission:" + "*");
        redisTemplate.delete(keysUserPerm);
        Set<String> keysUserMenu = redisTemplate.keys("permission::userMenuList:*");
        redisTemplate.delete(keysUserMenu);
        return ResultUtil.data(null);
    }

    //编辑角色分配数据权限
    @RequestMapping(value = "/editRoleDep",method = RequestMethod.POST)
    public ApiJson editRoleDep(@RequestParam String roleId,
                                      @RequestParam Integer dataType,
                                      @RequestParam(required = false) String[] depIds){

        Role r = roleService.getById(roleId);
        r.setDataType(dataType);
        roleService.updateById(r);
        // 删除其关联数据权限
        /*roleDepartmentService.deleteByRoleId(roleId);
        // 分配新数据权限
        for(String depId : depIds){
            RoleDepartment roleDepartment = new RoleDepartment();
            roleDepartment.setRoleId(roleId);
            roleDepartment.setDepartmentId(depId);
            roleDepartmentService.save(roleDepartment);
        }*/
        
        // 手动删除相关缓存
        Set<String> keys = redisTemplate.keys("department:" + "*");
        redisTemplate.delete(keys);
        Set<String> keysUserRole = redisTemplate.keys("userRole:" + "*");
        redisTemplate.delete(keysUserRole);

        return ResultUtil.data(null);
    }

    //保存数据
    @RequestMapping(value = "/save",method = RequestMethod.POST)
    public ApiJson save(Role role){

    	roleService.save(role);
        return ResultUtil.data(role);
    }

    //更新数据
    @RequestMapping(value = "/edit",method = RequestMethod.POST)
    public ApiJson edit(Role entity){

        roleService.updateById(entity);
        //手动批量删除缓存
        Set<String> keysUser = redisTemplate.keys("user:" + "*");
        redisTemplate.delete(keysUser);
        Set<String> keysUserRole = redisTemplate.keys("userRole:" + "*");
        redisTemplate.delete(keysUserRole);
        return ResultUtil.data(entity);
    }

    //批量通过ids删除
    @RequestMapping(value = "/delAllByIds/{ids}",method = RequestMethod.DELETE)
    public ApiJson delByIds(@PathVariable Integer[] ids){

        for(Integer id:ids){
            List<UserRole> list = userRoleService.findByRoleId(id);
            if(list!=null&&list.size()>0){
                return ResultUtil.error("删除失败，包含正被用户使用关联的角色");
            }
        }
        for(Integer id:ids){
            roleService.removeById(id);
            //删除关联菜单权限
            rolePermissionService.deleteByRoleId(id);
            //删除关联数据权限
            //roleDepartmentService.deleteByRoleId(id);
        }
        return ResultUtil.success("批量通过id删除数据成功");
    }

}
