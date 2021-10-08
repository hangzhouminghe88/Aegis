package com.mingheinfo.maxdata.manage.utils;

import com.mingheinfo.maxdata.manage.entity.Permission;
import com.mingheinfo.maxdata.manage.vo.MenuVo;

import cn.hutool.core.bean.BeanUtil;

public class VoUtil {

    public static MenuVo permissionToMenuVo(Permission p){

        MenuVo menuVo = new MenuVo();
        BeanUtil.copyProperties(p, menuVo);
        return menuVo;
    }
}
