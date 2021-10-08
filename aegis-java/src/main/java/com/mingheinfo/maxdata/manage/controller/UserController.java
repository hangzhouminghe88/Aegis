package com.mingheinfo.maxdata.manage.controller;

import cn.hutool.core.util.StrUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mingheinfo.common.constant.CommonConstant;
import com.mingheinfo.common.json.ApiJson;
import com.mingheinfo.common.utils.ResultUtil;
import com.mingheinfo.common.utils.SecurityUtil;
import com.mingheinfo.common.vo.PageVo;
import com.mingheinfo.common.vo.SearchVo;
import com.mingheinfo.maxdata.manage.entity.Department;
import com.mingheinfo.maxdata.manage.entity.Role;
import com.mingheinfo.maxdata.manage.entity.User;
import com.mingheinfo.maxdata.manage.entity.UserRole;
import com.mingheinfo.maxdata.manage.service.DepartmentService;
import com.mingheinfo.maxdata.manage.service.RoleService;
import com.mingheinfo.maxdata.manage.service.UserRoleService;
import com.mingheinfo.maxdata.manage.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Set;


@Slf4j
@RestController
@RequestMapping("/api/user")
@CacheConfig(cacheNames = "user")
@Transactional
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private SecurityUtil securityUtil;

    @PersistenceContext
    private EntityManager entityManager;
    
    @Autowired
    private DepartmentService departmentService;

    @RequestMapping(value = "/regist",method = RequestMethod.POST)
    @ApiOperation(value = "注册用户")
    public ApiJson regist(User u){

        if(StrUtil.isBlank(u.getUsername()) || StrUtil.isBlank(u.getPassword())){
            return ResultUtil.error("缺少必需表单字段");
        }

        if(userService.findByUsername(u.getUsername())!=null){
            return ResultUtil.error("该用户名已被注册");
        }

        String encryptPass = new BCryptPasswordEncoder().encode(u.getPassword());
        u.setPassword(encryptPass);
        u.setType(CommonConstant.USER_TYPE_NORMAL);
        
        boolean ret = userService.save(u);
        
        if(!ret){
            return ResultUtil.error("注册失败");
        }
        // 默认角色
        List<Role> roleList = roleService.findByDefaultRole(true);
        if(roleList!=null&&roleList.size()>0){
            for(Role role : roleList){
                UserRole ur = new UserRole();
                ur.setUserId(u.getId());
                ur.setRoleId(role.getId());
                userRoleService.save(ur);
            }
        }

        return ResultUtil.data(u);
    }

    @RequestMapping(value = "/info",method = RequestMethod.GET)
    @ApiOperation(value = "获取当前登录用户接口")
    public ApiJson getUserInfo(){

        User u = securityUtil.getCurrUser();
        // 清除持久上下文环境 避免后面语句导致持久化
        entityManager.clear();
        u.setPassword(null);
        return ResultUtil.data(u);
    }

    @RequestMapping(value = "/unlock",method = RequestMethod.POST)
    @ApiOperation(value = "解锁验证密码")
    public ApiJson unLock(@RequestParam String password){

        User u = securityUtil.getCurrUser();
        if(!new BCryptPasswordEncoder().matches(password, u.getPassword())){
            return ResultUtil.error("密码不正确");
        }
        return ResultUtil.data(null);
    }

    @RequestMapping(value = "/resetPass", method = RequestMethod.POST)
    @ApiOperation(value = "重置密码")
    public ApiJson resetPass(@RequestParam String[] ids){

        for(String id:ids){
            User u = userService.getById(id);
            u.setPassword(new BCryptPasswordEncoder().encode("123456"));
            userService.updateById(u);
            redisTemplate.delete("user::"+u.getUsername());
        }
        return ResultUtil.success("操作成功");
    }

    @RequestMapping(value = "/edit",method = RequestMethod.POST)
    @ApiOperation(value = "修改用户自己资料",notes = "用户名密码不会修改 需要username更新缓存")
    @CacheEvict(key = "#u.username")
    public ApiJson editOwn(User u){

        User old = securityUtil.getCurrUser();
        u.setUsername(old.getUsername());
        u.setPassword(old.getPassword());
        boolean ret = userService.updateById(u);
        if(!ret){
            return ResultUtil.error("修改失败");
        }
        return ResultUtil.success("修改成功");
    }

    /**
     * @param u
     * @param roles
     * @return
     */
    @RequestMapping(value = "/admin/edit",method = RequestMethod.POST)
    @ApiOperation(value = "管理员修改资料",notes = "需要通过id获取原用户信息 需要username更新缓存")
    @CacheEvict(key = "#u.username")
    public ApiJson edit(User u,
                               @RequestParam(required = false) String[] userRoles){

        User old = userService.getById(u.getId());
        //若修改了用户名
        if(!old.getUsername().equals(u.getUsername())){
            //若修改用户名删除原用户名缓存
            redisTemplate.delete("user::"+old.getUsername());
            //判断新用户名是否存在
            if(userService.findByUsername(u.getUsername())!=null){
                return ResultUtil.error("该用户名已被存在");
            }
        }

        // 若修改了手机和邮箱判断是否唯一
        if(!old.getMobile().equals(u.getMobile())&&userService.findByMobile(u.getMobile())!=null){
            return ResultUtil.error("该手机号已绑定其他账户");
        }
        if(!old.getEmail().equals(u.getEmail())&&userService.findByMobile(u.getEmail())!=null){
            return ResultUtil.error("该邮箱已绑定其他账户");
        }

        u.setPassword(old.getPassword());
        boolean ret=userService.updateById(u);
        if(!ret){
            return ResultUtil.error("修改失败");
        }
        //删除该用户角色
        userRoleService.deleteByUserId(u.getId());
        if(userRoles!=null&&userRoles.length>0){
            //新角色
            for(String roleId : userRoles){
                UserRole ur = new UserRole();
                ur.setRoleId(Integer.parseInt(roleId));
                ur.setUserId(u.getId());
                userRoleService.save(ur);
            }
        }
        //手动删除缓存
        redisTemplate.delete("userRole::"+u.getId());
        redisTemplate.delete("userRole::depIds:"+u.getId());
        redisTemplate.delete("userPermission::"+u.getId());
        redisTemplate.delete("permission::userMenuList:"+u.getId());
        return ResultUtil.success("修改成功");
    }

    /**
     * 线上demo不允许测试账号改密码
     * @param password
     * @param newPass
     * @return
     */
    @RequestMapping(value = "/modifyPass",method = RequestMethod.POST)
    @ApiOperation(value = "修改密码")
    public ApiJson modifyPass(@ApiParam("旧密码") @RequestParam String password,
                                     @ApiParam("新密码") @RequestParam String newPass){

        User user = securityUtil.getCurrUser();

        if(!new BCryptPasswordEncoder().matches(password, user.getPassword())){
            return ResultUtil.error("旧密码不正确");
        }

        String newEncryptPass= new BCryptPasswordEncoder().encode(newPass);
        user.setPassword(newEncryptPass);
        userService.updateById(user);

        //手动更新缓存
        redisTemplate.delete("user::"+user.getUsername());

        return ResultUtil.success("修改密码成功");
    }

    
    @RequestMapping(value = "/getByCondition",method = RequestMethod.GET)
    @ApiOperation(value = "多条件分页获取用户列表")
    public ApiJson getByCondition(User user,
                                             SearchVo searchVo,
                                             PageVo pageVo){
    	
    	IPage<User> page = new Page<User>(pageVo.getPageNumber(),pageVo.getPageSize());
    	
    	QueryWrapper<User> queryWrapper = new QueryWrapper<User>();
    	
        if(StrUtil.isNotBlank(user.getUsername())){
        	queryWrapper.like("username", user.getUsername());
        }
        
        if(StrUtil.isNotBlank(user.getMobile())){
        	queryWrapper.like("mobile", user.getMobile());
        }
        if(StrUtil.isNotBlank(user.getEmail())){
        	queryWrapper.like("email", user.getEmail());
        }

        //部门
        if(user.getDepartmentId()!=null){
        	queryWrapper.eq("department_id", user.getDepartmentId());
        }

        //性别
        if(StrUtil.isNotBlank(user.getSex())){
        	queryWrapper.eq("sex", user.getSex());
        }
        //类型
        if(user.getType()!=null){
        	queryWrapper.eq("type", user.getType());
        }
        //状态
        if(user.getStatus()!=null){
        	queryWrapper.eq("status", user.getStatus());
        }
        
        //创建时间
        if(StrUtil.isNotBlank(searchVo.getStartDate())&&StrUtil.isNotBlank(searchVo.getEndDate())){
    		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
    		String startDate = sf.format(searchVo.getStartDate());
    		String endDate = sf.format(searchVo.getEndDate());
    		
    		queryWrapper.between("create_ime", startDate, endDate);
        }
        
        page = userService.page(page, queryWrapper);
        
        //Page<User> page = userService.findByCondition(user, searchVo, PageUtil.initPage(pageVo));
        for(User u: page.getRecords()){
            // 关联部门
            if(u.getDepartmentId()!=null){
                Department department = departmentService.getById(u.getDepartmentId());
                if(department!=null){
                    u.setDepartmentTitle(department.getTitle());
                }
            }
            // 关联角色
            List<Role> list = userRoleService.findByUserId(u.getId());
            u.setRoles(list);
            // 清除持久上下文环境 避免后面语句导致持久化
            entityManager.clear();
            u.setPassword(null);
        }
        return ResultUtil.data(page);
    }


    //多条件分页获取用户列表
    @RequestMapping(value = "/getByDepartmentId/{departmentId}",method = RequestMethod.GET)
    public ApiJson getByCondition(@PathVariable Integer departmentId){

        List<User> list = userService.findByDepartmentId(departmentId);
        entityManager.clear();
        list.forEach(u -> {
            u.setPassword(null);
        });
        return ResultUtil.data(list);
    }

    /*
    @RequestMapping(value = "/getAll",method = RequestMethod.GET)
    @ApiOperation(value = "获取全部用户数据")
    public ApiJson getByCondition(){

        List<User> list = userService.getAll();
        for(User u: list){
            // 关联部门
            if(StrUtil.isNotBlank(u.getDepartmentId())){
                Department department = departmentService.get(u.getDepartmentId());
                if(department!=null){
                    u.setDepartmentTitle(department.getTitle());
                }
            }
            // 清除持久上下文环境 避免后面语句导致持久化
            entityManager.clear();
            u.setPassword(null);
        }
        return ResultUtil.data(list);
    }*/

    @RequestMapping(value = "/admin/add",method = RequestMethod.POST)
    @ApiOperation(value = "添加用户")
    public ApiJson regist(User u,
    		@RequestParam(required = false) String[] userRoles){

        if(StrUtil.isBlank(u.getUsername()) || StrUtil.isBlank(u.getPassword())){
            return ResultUtil.error("缺少必需表单字段");
        }

        if(userService.findByUsername(u.getUsername())!=null){
            return ResultUtil.error("该用户名已被注册");
        }

        String encryptPass = new BCryptPasswordEncoder().encode(u.getPassword());
        u.setPassword(encryptPass);
        boolean ret= userService.save(u);
        if(!ret){
            return ResultUtil.error("添加失败");
        }
        if(userRoles!=null&&userRoles.length>0){
            //添加角色
            for(String roleId : userRoles){
                UserRole ur = new UserRole();
                ur.setUserId(u.getId());
                ur.setRoleId(Integer.parseInt(roleId));
                userRoleService.save(ur);
            }
        }

        return ResultUtil.data(u);
    }

    @RequestMapping(value = "/admin/disable/{userId}",method = RequestMethod.POST)
    @ApiOperation(value = "后台禁用用户")
    public ApiJson disable(@ApiParam("用户唯一id标识") @PathVariable String userId){

        User user = userService.getById(userId);
        if(user==null){
            return ResultUtil.error("通过userId获取用户失败");
        }
        user.setStatus(CommonConstant.USER_STATUS_LOCK);
        userService.updateById(user);
        //手动更新缓存
        redisTemplate.delete("user::"+user.getUsername());
        return ResultUtil.data(null);
    }

    @RequestMapping(value = "/admin/enable/{userId}",method = RequestMethod.POST)
    @ApiOperation(value = "后台启用用户")
    public ApiJson enable(@ApiParam("用户唯一id标识") @PathVariable String userId){

        User user = userService.getById(userId);
        if(user==null){
            return ResultUtil.error("通过userId获取用户失败");
        }
        user.setStatus(CommonConstant.USER_STATUS_NORMAL);
        userService.updateById(user);
        //手动更新缓存
        redisTemplate.delete("user::"+user.getUsername());
        return ResultUtil.data(null);
    }

    @RequestMapping(value = "/delByIds/{ids}",method = RequestMethod.DELETE)
    @ApiOperation(value = "批量通过ids删除")
    public ApiJson delAllByIds(@PathVariable String[] ids){

        for(String id:ids){
            User u = userService.getById(id);
            //删除缓存
            redisTemplate.delete("user::" + u.getUsername());
            redisTemplate.delete("userRole::" + u.getId());
            redisTemplate.delete("userRole::depIds:" + u.getId());
            redisTemplate.delete("permission::userMenuList:" + u.getId());
            Set<String> keys = redisTemplate.keys("department::*");
            redisTemplate.delete(keys);
            userService.removeById(id);
            //删除关联角色
            userRoleService.deleteByUserId(Integer.parseInt(id));
            //删除关联部门负责人
            //departmentHeaderService.deleteByUserId(id);
        }
        return ResultUtil.success("批量通过id删除数据成功");
    }

}
