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
    @ApiOperation(value = "????????????")
    public ApiJson regist(User u){

        if(StrUtil.isBlank(u.getUsername()) || StrUtil.isBlank(u.getPassword())){
            return ResultUtil.error("????????????????????????");
        }

        if(userService.findByUsername(u.getUsername())!=null){
            return ResultUtil.error("????????????????????????");
        }

        String encryptPass = new BCryptPasswordEncoder().encode(u.getPassword());
        u.setPassword(encryptPass);
        u.setType(CommonConstant.USER_TYPE_NORMAL);
        
        boolean ret = userService.save(u);
        
        if(!ret){
            return ResultUtil.error("????????????");
        }
        // ????????????
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
    @ApiOperation(value = "??????????????????????????????")
    public ApiJson getUserInfo(){

        User u = securityUtil.getCurrUser();
        // ??????????????????????????? ?????????????????????????????????
        entityManager.clear();
        u.setPassword(null);
        return ResultUtil.data(u);
    }

    @RequestMapping(value = "/unlock",method = RequestMethod.POST)
    @ApiOperation(value = "??????????????????")
    public ApiJson unLock(@RequestParam String password){

        User u = securityUtil.getCurrUser();
        if(!new BCryptPasswordEncoder().matches(password, u.getPassword())){
            return ResultUtil.error("???????????????");
        }
        return ResultUtil.data(null);
    }

    @RequestMapping(value = "/resetPass", method = RequestMethod.POST)
    @ApiOperation(value = "????????????")
    public ApiJson resetPass(@RequestParam String[] ids){

        for(String id:ids){
            User u = userService.getById(id);
            u.setPassword(new BCryptPasswordEncoder().encode("123456"));
            userService.updateById(u);
            redisTemplate.delete("user::"+u.getUsername());
        }
        return ResultUtil.success("????????????");
    }

    @RequestMapping(value = "/edit",method = RequestMethod.POST)
    @ApiOperation(value = "????????????????????????",notes = "??????????????????????????? ??????username????????????")
    @CacheEvict(key = "#u.username")
    public ApiJson editOwn(User u){

        User old = securityUtil.getCurrUser();
        u.setUsername(old.getUsername());
        u.setPassword(old.getPassword());
        boolean ret = userService.updateById(u);
        if(!ret){
            return ResultUtil.error("????????????");
        }
        return ResultUtil.success("????????????");
    }

    /**
     * @param u
     * @param roles
     * @return
     */
    @RequestMapping(value = "/admin/edit",method = RequestMethod.POST)
    @ApiOperation(value = "?????????????????????",notes = "????????????id????????????????????? ??????username????????????")
    @CacheEvict(key = "#u.username")
    public ApiJson edit(User u,
                               @RequestParam(required = false) String[] userRoles){

        User old = userService.getById(u.getId());
        //?????????????????????
        if(!old.getUsername().equals(u.getUsername())){
            //??????????????????????????????????????????
            redisTemplate.delete("user::"+old.getUsername());
            //??????????????????????????????
            if(userService.findByUsername(u.getUsername())!=null){
                return ResultUtil.error("????????????????????????");
            }
        }

        // ?????????????????????????????????????????????
        if(!old.getMobile().equals(u.getMobile())&&userService.findByMobile(u.getMobile())!=null){
            return ResultUtil.error("?????????????????????????????????");
        }
        if(!old.getEmail().equals(u.getEmail())&&userService.findByMobile(u.getEmail())!=null){
            return ResultUtil.error("??????????????????????????????");
        }

        u.setPassword(old.getPassword());
        boolean ret=userService.updateById(u);
        if(!ret){
            return ResultUtil.error("????????????");
        }
        //?????????????????????
        userRoleService.deleteByUserId(u.getId());
        if(userRoles!=null&&userRoles.length>0){
            //?????????
            for(String roleId : userRoles){
                UserRole ur = new UserRole();
                ur.setRoleId(Integer.parseInt(roleId));
                ur.setUserId(u.getId());
                userRoleService.save(ur);
            }
        }
        //??????????????????
        redisTemplate.delete("userRole::"+u.getId());
        redisTemplate.delete("userRole::depIds:"+u.getId());
        redisTemplate.delete("userPermission::"+u.getId());
        redisTemplate.delete("permission::userMenuList:"+u.getId());
        return ResultUtil.success("????????????");
    }

    /**
     * ??????demo??????????????????????????????
     * @param password
     * @param newPass
     * @return
     */
    @RequestMapping(value = "/modifyPass",method = RequestMethod.POST)
    @ApiOperation(value = "????????????")
    public ApiJson modifyPass(@ApiParam("?????????") @RequestParam String password,
                                     @ApiParam("?????????") @RequestParam String newPass){

        User user = securityUtil.getCurrUser();

        if(!new BCryptPasswordEncoder().matches(password, user.getPassword())){
            return ResultUtil.error("??????????????????");
        }

        String newEncryptPass= new BCryptPasswordEncoder().encode(newPass);
        user.setPassword(newEncryptPass);
        userService.updateById(user);

        //??????????????????
        redisTemplate.delete("user::"+user.getUsername());

        return ResultUtil.success("??????????????????");
    }

    
    @RequestMapping(value = "/getByCondition",method = RequestMethod.GET)
    @ApiOperation(value = "?????????????????????????????????")
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

        //??????
        if(user.getDepartmentId()!=null){
        	queryWrapper.eq("department_id", user.getDepartmentId());
        }

        //??????
        if(StrUtil.isNotBlank(user.getSex())){
        	queryWrapper.eq("sex", user.getSex());
        }
        //??????
        if(user.getType()!=null){
        	queryWrapper.eq("type", user.getType());
        }
        //??????
        if(user.getStatus()!=null){
        	queryWrapper.eq("status", user.getStatus());
        }
        
        //????????????
        if(StrUtil.isNotBlank(searchVo.getStartDate())&&StrUtil.isNotBlank(searchVo.getEndDate())){
    		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
    		String startDate = sf.format(searchVo.getStartDate());
    		String endDate = sf.format(searchVo.getEndDate());
    		
    		queryWrapper.between("create_ime", startDate, endDate);
        }
        
        page = userService.page(page, queryWrapper);
        
        //Page<User> page = userService.findByCondition(user, searchVo, PageUtil.initPage(pageVo));
        for(User u: page.getRecords()){
            // ????????????
            if(u.getDepartmentId()!=null){
                Department department = departmentService.getById(u.getDepartmentId());
                if(department!=null){
                    u.setDepartmentTitle(department.getTitle());
                }
            }
            // ????????????
            List<Role> list = userRoleService.findByUserId(u.getId());
            u.setRoles(list);
            // ??????????????????????????? ?????????????????????????????????
            entityManager.clear();
            u.setPassword(null);
        }
        return ResultUtil.data(page);
    }


    //?????????????????????????????????
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
    @ApiOperation(value = "????????????????????????")
    public ApiJson getByCondition(){

        List<User> list = userService.getAll();
        for(User u: list){
            // ????????????
            if(StrUtil.isNotBlank(u.getDepartmentId())){
                Department department = departmentService.get(u.getDepartmentId());
                if(department!=null){
                    u.setDepartmentTitle(department.getTitle());
                }
            }
            // ??????????????????????????? ?????????????????????????????????
            entityManager.clear();
            u.setPassword(null);
        }
        return ResultUtil.data(list);
    }*/

    @RequestMapping(value = "/admin/add",method = RequestMethod.POST)
    @ApiOperation(value = "????????????")
    public ApiJson regist(User u,
    		@RequestParam(required = false) String[] userRoles){

        if(StrUtil.isBlank(u.getUsername()) || StrUtil.isBlank(u.getPassword())){
            return ResultUtil.error("????????????????????????");
        }

        if(userService.findByUsername(u.getUsername())!=null){
            return ResultUtil.error("????????????????????????");
        }

        String encryptPass = new BCryptPasswordEncoder().encode(u.getPassword());
        u.setPassword(encryptPass);
        boolean ret= userService.save(u);
        if(!ret){
            return ResultUtil.error("????????????");
        }
        if(userRoles!=null&&userRoles.length>0){
            //????????????
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
    @ApiOperation(value = "??????????????????")
    public ApiJson disable(@ApiParam("????????????id??????") @PathVariable String userId){

        User user = userService.getById(userId);
        if(user==null){
            return ResultUtil.error("??????userId??????????????????");
        }
        user.setStatus(CommonConstant.USER_STATUS_LOCK);
        userService.updateById(user);
        //??????????????????
        redisTemplate.delete("user::"+user.getUsername());
        return ResultUtil.data(null);
    }

    @RequestMapping(value = "/admin/enable/{userId}",method = RequestMethod.POST)
    @ApiOperation(value = "??????????????????")
    public ApiJson enable(@ApiParam("????????????id??????") @PathVariable String userId){

        User user = userService.getById(userId);
        if(user==null){
            return ResultUtil.error("??????userId??????????????????");
        }
        user.setStatus(CommonConstant.USER_STATUS_NORMAL);
        userService.updateById(user);
        //??????????????????
        redisTemplate.delete("user::"+user.getUsername());
        return ResultUtil.data(null);
    }

    @RequestMapping(value = "/delByIds/{ids}",method = RequestMethod.DELETE)
    @ApiOperation(value = "????????????ids??????")
    public ApiJson delAllByIds(@PathVariable String[] ids){

        for(String id:ids){
            User u = userService.getById(id);
            //????????????
            redisTemplate.delete("user::" + u.getUsername());
            redisTemplate.delete("userRole::" + u.getId());
            redisTemplate.delete("userRole::depIds:" + u.getId());
            redisTemplate.delete("permission::userMenuList:" + u.getId());
            Set<String> keys = redisTemplate.keys("department::*");
            redisTemplate.delete(keys);
            userService.removeById(id);
            //??????????????????
            userRoleService.deleteByUserId(Integer.parseInt(id));
            //???????????????????????????
            //departmentHeaderService.deleteByUserId(id);
        }
        return ResultUtil.success("????????????id??????????????????");
    }

}
