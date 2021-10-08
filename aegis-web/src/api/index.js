// 统一请求路径前缀在libs/axios.js中修改
import { getRequest, postRequest, putRequest, deleteRequest, getRequestWithNoToken } from '@/libs/axios';



// 文件上传接口
export const uploadFile = "/api/upload/file"
// 验证码渲染图片接口
export const drawCodeImage = "/api/common/captcha/draw/"
// 获取菜单
export const getMenuList = "/api/permission/getMenuList"
// 获取数据字典
export const getDictData = "/api/dictData/getByType/"



// 登陆
export const login = (params) => {
    return postRequest('/login', params)
}
// 获取用户登录信息
export const userInfo = (params) => {
    return getRequest('/user/info', params)
}
// 注册
export const regist = (params) => {
    return postRequest('/user/regist', params)
}
// 初始化验证码
export const initCaptcha = (params) => {
    return getRequestWithNoToken('/common/captcha/init', params)
}
// 发送短信验证码
export const sendSms = (mobile, params) => {
    return getRequest(`/common/captcha/sendSms/${mobile}`, params)
}
// 短信验证码登录
export const smsLogin = (params) => {
    return postRequest('/user/smsLogin', params)
}
// IP天气信息
export const ipInfo = (params) => {
    return getRequest('/common/ip/info', params)
}
// 个人中心编辑
export const userInfoEdit = (params) => {
    return postRequest('/user/edit', params)
}
// 个人中心修改密码
export const changePass = (params) => {
    return postRequest('/user/modifyPass', params)
}
// 解锁
export const unlock = (params) => {
    return postRequest('/user/unlock', params)
}



// github登录
export const githubLogin = (params) => {
    return getRequest('/social/github/login', params)
}
// qq登录
export const qqLogin = (params) => {
    return getRequest('/social/qq/login', params)
}
// 微博登录
export const weiboLogin = (params) => {
    return getRequest('/social/weibo/login', params)
}
// 绑定账号
export const relate = (params) => {
    return postRequest('/social/relate', params)
}
// 获取JWT
export const getJWT = (params) => {
    return getRequest('/social/getJWT', params)
}



// 获取绑定账号信息
export const relatedInfo = (username, params) => {
    return getRequest(`/relate/getRelatedInfo/${username}`, params)
}
// 解绑账号
export const unRelate = (params) => {
    return postRequest('/relate/delByIds', params)
}
// 分页获取绑定账号信息
export const getRelatedListData = (params) => {
    return getRequest('/relate/findByCondition', params)
}



// 获取用户数据 多条件
export const getUserListData = (params) => {
    return getRequest('/user/getByCondition', params)
}
// 获取全部用户数据
export const getAllUserData = (params) => {
    return getRequest('/user/getAll', params)
}
// 添加用户
export const addUser = (params) => {
    return postRequest('/user/admin/add', params)
}
// 编辑用户
export const editUser = (params) => {
    return postRequest('/user/admin/edit', params)
}
// 启用用户
export const enableUser = (id, params) => {
    return postRequest(`/user/admin/enable/${id}`, params)
}
// 禁用用户
export const disableUser = (id, params) => {
    return postRequest(`/user/admin/disable/${id}`, params)
}
// 删除用户
export const deleteUser = (ids, params) => {
    return deleteRequest(`/user/delByIds/${ids}`, params)
}
// 重置用户密码
export const resetUserPass = (params) => {
    return postRequest('/user/resetPass', params)
}



// 获取一级部门
export const initDepartment = (params) => {
    return getRequest('/department/getByParentId/0', params)
}
// 加载部门子级数据
export const loadDepartment = (id, params) => {
    return getRequest(`/department/getByParentId/${id}`, params)
}
// 通过部门获取全部用户数据
export const getUserByDepartmentId = (id, params) => {
    return getRequest(`/user/getByDepartmentId/${id}`, params)
}
// 添加部门
export const addDepartment = (params) => {
    return postRequest('/department/add', params)
}
// 编辑部门
export const editDepartment = (params) => {
    return postRequest('/department/edit', params)
}
// 删除部门
export const deleteDepartment = (ids, params) => {
    return deleteRequest(`/department/delByIds/${ids}`, params)
}
// 搜索部门
export const searchDepartment = (params) => {
    return getRequest('/department/search', params)
}



// 获取全部角色数据
export const getAllRoleList = (params) => {
    return getRequest('/role/getAllList', params)
}
// 分页获取角色数据
export const getRoleList = (params) => {
    return getRequest('/role/getAllByPage', params)
}
// 添加角色
export const addRole = (params) => {
    return postRequest('/role/save', params)
}
// 编辑角色
export const editRole = (params) => {
    return postRequest('/role/edit', params)
}
// 设为或取消注册角色
export const setDefaultRole = (params) => {
    return postRequest('/role/setDefault', params)
}
// 分配角色权限
export const editRolePerm = (params) => {
    return postRequest('/role/editRolePerm', params)
}
// 分配角色数据权限
export const editRoleDep = (params) => {
    return postRequest('/role/editRoleDep', params)
}
// 删除角色
export const deleteRole = (ids, params) => {
    return deleteRequest(`/role/delAllByIds/${ids}`, params)
}



// 获取全部字典
export const getAllDictList = (params) => {
    return getRequest('/dict/getAll', params)
}
// 添加字典
export const addDict = (params) => {
    return postRequest('/dict/add', params)
}
// 编辑字典
export const editDict = (params) => {
    return postRequest('/dict/edit', params)
}
// 删除字典
export const deleteDict = (ids, params) => {
    return deleteRequest(`/dict/delByIds/${ids}`, params)
}
// 搜索字典
export const searchDict = (params) => {
    return getRequest('/dict/search', params)
}
// 获取全部字典数据
export const getAllDictDataList = (params) => {
    return getRequest('/dictData/getByCondition', params)
}
// 添加字典数据
export const addDictData = (params) => {
    return postRequest('/dictData/add', params)
}
// 编辑字典数据
export const editDictData = (params) => {
    return postRequest('/dictData/edit', params)
}
// 删除字典数据
export const deleteData = (ids, params) => {
    return deleteRequest(`/dictData/delByIds/${ids}`, params)
}
// 通过类型获取字典数据
export const getDictDataByType = (type, params) => {
    return getRequest(`/dictData/getByType/${type}`, params)
}



// 获取全部权限数据
export const getAllPermissionList = (params) => {
    return getRequest('/permission/getAllList', params)
}
// 添加权限
export const addPermission = (params) => {
    return postRequest('/permission/add', params)
}
// 编辑权限
export const editPermission = (params) => {
    return postRequest('/permission/edit', params)
}
// 删除权限
export const deletePermission = (ids, params) => {
    return deleteRequest(`/permission/delByIds/${ids}`, params)
}
// 搜索权限
export const searchPermission = (params) => {
    return getRequest('/permission/search', params)
}


// 分页获取日志数据
export const getLogListData = (params) => {
    return getRequest('/log/getAllByPage', params)
}
// 删除日志
export const deleteLog = (ids, params) => {
    return deleteRequest(`/log/delByIds/${ids}`, params)
}
// 清空日志
export const deleteAllLog = (params) => {
    return deleteRequest('/log/delAll', params)
}



// 分页获取Redis数据
export const getRedisData = (params) => {
    return getRequest('/redis/getAllByPage', params)
}
// 通过key获取Redis信息
export const getRedisByKey = (key, params) => {
    return getRequest(`/redis/getByKey/${key}`, params)
}
// 获取Redis键值数量
export const getRedisKeySize = (params) => {
    return getRequest('/redis/getKeySize', params)
}
// 获取Redis内存
export const getRedisMemory = (params) => {
    return getRequest('/redis/getMemory', params)
}
// 获取Redis信息
export const getRedisInfo = (params) => {
    return getRequest('/redis/info', params)
}
// 添加编辑Redis
export const saveRedis = (params) => {
    return postRequest('/redis/save', params)
}
// 删除Redis
export const deleteRedis = (params) => {
    return deleteRequest('/redis/delByKeys', params)
}
// 清空Redis
export const deleteAllRedis = (params) => {
    return deleteRequest('/redis/delAll', params)
}



// 分页获取定时任务数据
export const getQuartzListData = (params) => {
    return getRequest('/quartzJob/getAllByPage', params)
}
// 添加定时任务
export const addQuartz = (params) => {
    return postRequest('/quartzJob/add', params)
}
// 编辑定时任务
export const editQuartz = (params) => {
    return postRequest('/quartzJob/edit', params)
}
// 暂停定时任务
export const pauseQuartz = (params) => {
    return postRequest('/quartzJob/pause', params)
}
// 恢复定时任务
export const resumeQuartz = (params) => {
    return postRequest('/quartzJob/resume', params)
}
// 删除定时任务
export const deleteQuartz = (ids, params) => {
    return deleteRequest(`/quartzJob/delByIds/${ids}`, params)
}



// 分页获取消息数据
export const getMessageData = (params) => {
    return getRequest('/message/getByCondition', params)
}
// 添加消息
export const addMessage = (params) => {
    return postRequest('/message/add', params)
}
// 编辑消息
export const editMessage = (params) => {
    return postRequest('/message/edit', params)
}
// 删除消息
export const deleteMessage = (ids, params) => {
    return deleteRequest(`/message/delByIds/${ids}`, params)
}
// 分页获取消息推送数据
export const getMessageSendData = (params) => {
    return getRequest('/messageSend/getByCondition', params)
}
// 编辑发送消息
export const editMessageSend = (params) => {
    return putRequest('/messageSend/update', params)
}
// 删除发送消息
export const deleteMessageSend = (ids, params) => {
    return deleteRequest(`/messageSend/delByIds/${ids}`, params)
}



// base64上传
export const base64Upload = (params) => {
    return postRequest('/upload/file', params)
}



// 检查oss配置
export const checkOssSet = (params) => {
    return getRequest('/setting/oss/check', params)
}
// 获取oss配置
export const getOssSet = (serviceName, params) => {
    return getRequest(`/setting/oss/${serviceName}`, params)
}
// 编辑oss配置
export const editOssSet = (params) => {
    return postRequest('/setting/oss/set', params)
}
// 获取sms配置
export const getSmsSet = (serviceName, params) => {
    return getRequest(`/setting/sms/${serviceName}`, params)
}
// 获取sms模板code
export const getSmsTemplateCode = (type, params) => {
    return getRequest(`/setting/sms/templateCode/${type}`, params)
}
// 编辑sms配置
export const editSmsSet = (params) => {
    return postRequest('/setting/sms/set', params)
}
// 获取email配置
export const getEmailSet = (serviceName, params) => {
    return getRequest('/setting/email', params)
}
// 编辑email配置
export const editEmailSet = (params) => {
    return postRequest('/setting/email/set', params)
}
// 获取vaptcha配置
export const getVaptchaSet = (params) => {
    return getRequest('/setting/vaptcha', params)
}
// 编辑vaptcha配置
export const editVaptchaSet = (params) => {
    return postRequest('/setting/vaptcha/set', params)
}
// 获取vaptcha配置
export const getOtherSet = (params) => {
    return getRequest('/setting/other', params)
}
// 编辑other配置
export const editOtherSet = (params) => {
    return postRequest('/setting/other/set', params)
}
// 查看私密配置
export const seeSecretSet = (settingName, params) => {
    return getRequest(`/setting/seeSecret/${settingName}`, params)
}

//=====================主机信息================
// 分页获取
export const getHostList = (params) => {
    return getRequest('/host/getAllByPage', params)
}

// 获取全部
export const getAllHostList = () => {
    return getRequest('/host/getAll', {})
}

// 添加
export const addHost = (params) => {
    return postRequest('/host/save', params)
}

// 编辑
export const editHost = (params) => {
    return postRequest('/host/edit', params)
}

// 删除
export const deleteHost = (ids, params) => {
    return deleteRequest(`/host/delAllByIds/${ids}`, params)
}

//=====================主机组信息================
// 分页获取
export const getHostGroupList = (params) => {
    return getRequest('/hostGroup/getAllByPage', params)
}

// 获取全部
export const getAllHostGroupList = (params) => {
    return getRequest('/hostGroup/getAll', params)
}

// 添加
export const addHostGroup = (params) => {
    return postRequest('/hostGroup/save', params)
}

// 编辑
export const editHostGroup = (params) => {
    return postRequest('/hostGroup/edit', params)
}

// 删除
export const deleteHostGroup = (ids, params) => {
    return deleteRequest(`/hostGroup/delAllByIds/${ids}`, params)
}


//=====================模板管理================
// 分页获取
export const getTemplateList = (params) => {
    return getRequest('/template/getAllByPage', params)
}


// 添加
export const addTemplate = (params) => {
    return postRequest('/template/save', params)
}

// 编辑
export const editTemplate = (params) => {
    return postRequest('/template/edit', params)
}

// 删除
export const deleteTemplate = (ids, params) => {
    return deleteRequest(`/template/delAllByIds/${ids}`, params)
}

//=====================告警明细================
// 分页获取
export const getAlertList = (params,action) => {
    return getRequest(`/alert/${action}`, params)
}

// 删除
export const deleteAlert = (ids, params) => {
    return deleteRequest(`/alert/delAllByIds/${ids}`, params)
}

//=====================表空间预警================
// 分页获取
export const getTableSpaceSumList = (params) => {
    return getRequest('/tableSpaceSum/getAllByPage', params)
}

//=====================段空间预警================
// 分页获取
export const getSegmentSpaceSumList = (params) => {
    return getRequest('/segmentSpaceSum/getAllByPage', params)
}

//=====================SQL区段报表================
// 分页获取
export const getSqlSummaryReportList = (params) => {
    return getRequest('/sqlSummaryReport/getAllByPage', params)
}

// 分页获取
export const getSqlSummaryList = (params) => {
    return getRequest('/sqlSummary/getAllByPage', params)
}

//=====================SQL绑定变量报表================
// 分页获取
export const getSqlBindList = (params) => {
    return getRequest('/sqlBind/getAllByPage', params)
}

//=====================活动会话历史================
// 分页获取
export const getSysAshWaitList = (params) => {
    return getRequest('/sysAshWait/getAllByPage', params)
}

//=====================awr报表================
// 分页获取
export const getSnapshotList = (params) => {
   // return getRequest('/snapshot/getAllByPage', params)
   return postRequest('/snapshot/create');
}

//=====================索引访问报表================
// 分页获取
export const getTableMonIndsList = (params) => {
    return getRequest('/tableMonInds/getAllByPage', params)
}

//=====================序列监控报表================
// 分页获取
export const getSequenceReportList = (params) => {
    return getRequest('/sequenceReport/getAllByPage', params)
}


// 分页获取
export const getTableMonList = (params) => {
    return getRequest('/tableMon/getAllByPage', params)
}

//=====================记录变更报表================
// 分页获取
export const getTableMonSumList = (params) => {
    return getRequest('/tableMonSum/getAllByPage', params)
}

//=====================用户操作日志管理================
// 分页获取
export const getUserOperLogList = (params) => {
    return getRequest('/userOperLog/getAllByPage', params)
}
// 删除
export const deleteUserOperLog = (ids, params) => {
    return deleteRequest(`/userOperLog/delAllByIds/${ids}`, params)
}

//=====================告警短信管理================
// 分页获取数据
export const getAlarmSmsList = (params) => {
    return getRequest('/alarmSms/getAllByPage', params)
}

// 获取全部
export const getAllAlarmSmsList = (params) => {
    return getRequest('/alarmSms/getAll', params)
}

// 添加
export const addAlarmSms = (params) => {
    return postRequest('/alarmSms/save', params)
}
// 编辑
export const editAlarmSms = (params) => {
    return postRequest('/alarmSms/edit', params)
}
// 删除
export const deleteAlarmSms = (ids, params) => {
    return deleteRequest(`/alarmSms/delAllByIds/${ids}`, params)
}

// 切换通知状态
export const switchAlarmSmsInformStatus = (params) => {
    return postRequest('/alarmSms/switchInformStatus', params)
}

//=====================告警邮箱管理================
// 分页获取数据
export const getAlarmEmailList = (params) => {
    return getRequest('/alarmEmail/getAllByPage', params)
}

// 获取全部
export const getAllAlarmEmailList = (params) => {
    return getRequest('/alarmEmail/getAll', params)
}

// 添加
export const addAlarmEmail = (params) => {
    return postRequest('/alarmEmail/save', params)
}
// 编辑
export const editAlarmEmail = (params) => {
    return postRequest('/alarmEmail/edit', params)
}
// 删除
export const deleteAlarmEmail = (ids, params) => {
    return deleteRequest(`/alarmEmail/delAllByIds/${ids}`, params)
}

// 切换通知状态
export const switchAlarmEmailInformStatus = (params) => {
    return postRequest('/alarmEmail/switchInformStatus', params)
}

//==========数据字典报表============
export const getDictReportList = (params) => {
    return getRequest('/dictReport/getAllByPage', params);
}

//列信息列表
export const  getDictColumnInfoList = (params) => {
    return getRequest('/dictColumns/getAllByPage', params);
}

//索引信息列表
export const getDictIndexesInfoList = (params) => {
    return getRequest('/dictIndex/getAllByPage', params);
}

//sqlPlan列表
export const getSqlPlanList = (params) => {
    return getRequest('/sqlplan/getAllByPage', params)
}

export const getSqlSummaryDetailList = (params) => {
    return getRequest('/sqlSummary/detail', params)
}

//表空间报表
export const getTableSpaceSumReportList = (params) => {
    return getRequest('/tableSpaceSum/report', params);
}

//段空间报表
export const getSegmentSpaceSumReportList = (params) => {
    return getRequest('/segmentSpaceSum/report', params);
}

//表空间历史数据
export const getTableSpaceSumHistoryList = (params) => {
    return getRequest('/tableSpaceSum/history', params);
}

 //段空间查看历史
 export const getSqlSummaryReportHistoryList = (params) => {
     return getRequest('/sqlSummaryReport/history',  params);
 }

 //获得sqlText
 export const getSqlTextList = (sqlId, params) => {
     return getRequest(`/sqlText/getAll/${sqlId}`, params);
 }

 //查看段空间历史
 export const getSegmentSpaceSumHistoryList = (params) => {
    return getRequest('/segmentSpaceSum/history', params);
 }

 //关键指标列表查询
 export const getCurvDispbyhList = (params) => {
     return getRequest('/curv/dispbyh', params);
 }
 //关键指标历史数据
 export const getCurvDispbyhDetailList = (params) => {
     return getRequest( '/curv/detail', params);
 }

 //活动会话历史历史数据
 export const getSysAshWaitHistoryList =  (params) => {
     return getRequest( '/sysAshWait/history', params);
 }

 //获得topSql
 export const getSqlStatTopSqlList = (params) => {
     return getRequest('/sqlstat/topsql', params);
 }

 //或得系统metric
 export const getMetricReportList = (params) => {
     return getRequest('/sqlstat/metric', params);
 }

 export const getTableMonHistoryList = (params) => {
     return getRequest('/tableMon/history', params);
 }

 //获得SQL区段分析报表
 export const getSqlSummaryListActionBySummary = (params) => {
     return getRequest('/sqlSummary/summary', params);
 }

 //获得sqlBind列表
 export const getSqlSummaryListActionBySqlBind = (params) => {
     return getRequest('/sqlSummary/sqlbind', params);
 }

 //获得趋势图预警数据
 export const getTrendMetricList = (params) => {
     return getRequest('/curv/trend', params);
 }

 //获得关键指标趋势中Alert数据
 export const getCurvAlertList = (params) => {
     return getRequest('/curv/alert', params);
 }

 //关键指标趋势中Alert详情
 export const getAlertHisList = (params) => {
     return getRequest('/curv/alerthis', params);
 }

 //relation
 export const sysAshWaitRelationList = (params) => {
     return getRequest('/sysAshWait/relation', params);
 }

 //趋势图详情
 export const getCurvDispbyhDetailMetricList = (params) => {
     return getRequest('/curv/dispbyhdetail', params);
 }
 //获得所有历史
export const getAllCurVesDetailList = (params) => {
    return getRequest('/curv/detailAll',  params);
}
//获得主机模板
export const getHostTemplate = (params) => {
    return getRequest('/host/template', params);
}

//获得所有top event
export const getAllAshList = (params) => {
    return getRequest('/sysAshWait/all', params);
}

//注册新故障
export const registerError = (params) => {
    return postRequest('/alert/register', params);
}

//查询处理故障结果
export const getAlertsResultList = (params) => {
    return getRequest('/result/list',params);
}

//修改故障处理结果
export  const alertsResultEdit = (params) => {
  return postRequest('/result/edit',params);
}

//添加故障
export  const alertsResultSave = (params) => {
  return postRequest('/result/save',params);
}

//批量删除
export const alertsDelete = (params) => {
    return deleteRequest(`/result/delAllByIds/${params.ids}`);
}

//查询indexes
export const getDictIndex = (params) => {
    return getRequest('/dictIndex/indexes', params);
}

//查看历史
export const getIndexeMonHistory = (params) => {
    return getRequest('/tableMonHistory/history', params);
}

//查看include
export const getIncludeList = (params) => {
    return getRequest('/segmentSpaceSum/include', params);
}

//查找groupName
export const getGroupName =  (params) => {
    return getRequest('/host/group', params);
}

//查找模板名称
export const getTemplateName = (params) => {
    return getRequest('/curv/template', params);
}

// 获取license请求码
export const getLicenseRequestCode = (params) => {
    return getRequest('/license/getRequestCode', params)
}

export const getLicenseInfo = (params) => {
    return getRequest('/license/getLicenseInfo', params)
}


export const getLicenseText = (params) => {
    return postRequest('/license/setLicense', params)
}

export const getSegmentSpaceSumAlertList = (params) => {
  return getRequest('/segmentSpaceSum/alert', params);
}

export const getTemplateByPagination = (params) => {
  return getRequest('/curv/template_pagination', params);
}
