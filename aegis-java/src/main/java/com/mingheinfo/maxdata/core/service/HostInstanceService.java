package com.mingheinfo.maxdata.core.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mingheinfo.common.vo.PageVo;
import com.mingheinfo.maxdata.core.entity.HostInstance;
import com.mingheinfo.maxdata.core.mapper.HostInstanceMapper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class HostInstanceService extends ServiceImpl<HostInstanceMapper, HostInstance> {

}