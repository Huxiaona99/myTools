package com.rkhd.ienterprise.apps.ingage.crm.customeraccountsetting.action;

import com.alibaba.fastjson.JSONObject;
import com.rkhd.aggregator.metadata.model.ItemAO;
import com.rkhd.aggregator.metadata.service.ItemQueryAggService;
import com.rkhd.business.crm.common.service.CommonBusinessService;
import com.rkhd.business.crm.customeraccountsetting.service;
import com.rkhd.ienterprise.apps.ingage.crm.base.action.BaseAction;
import com.rkhd.ienterprise.apps.ingage.restresult.MultiResult;
import com.rkhd.ienterprise.apps.ingage.restresult.SingleResult;
import com.rkhd.ienterprise.apps.manager.business.base.ProcessorRequest;
import com.rkhd.ienterprise.apps.manager.business.base.ProcessorResult;
import com.rkhd.ienterprise.apps.manager.business.constants.ProcessorConstants;
import com.rkhd.ienterprise.apps.manager.constant.ObjectConstants;
import com.rkhd.ienterprise.apps.salescloud.customeraccountsetting.proxy.CustomerAccountSettingClientProxy;
import com.rkhd.ienterprise.exception.ServiceException;
import com.rkhd.ienterprise.permission.annotation.Model;
import com.rkhd.ienterprise.permission.annotation.Permissions;
import com.rkhd.ienterprise.web.CollectionUtil;
import com.rkhd.ienterprise.web.ParamUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 账户配置功能操作
 */
@Namespace("/json/crm_531")
public class CustomerAccountSettingAction extends BaseAction {

    public static final String PAGES_CUSTOMIZE_ROOT = "/WEB-INF/pages/custom";
    private static final String PAGES_ROOT = "/WEB-INF/pages/customeraccountsetting";
    private static final String PAGES_COMMON_ROOT = "/WEB-INF/pages/common";
    private static final String CURR_OBJECT_ID_STRING = ObjectConstants.OBJECT_ID_CUSTOMER_ACCOUNT_SETTING_STRING;
    private static final String CURR_OBJECT_NAME_STRING =  ObjectConstants.OBJECT_NAME_CUSTOMER_ACCOUNT_SETTING;
    private static final Long CURR_OBJECT_ID = ObjectConstants.OBJECT_ID_CUSTOMER_ACCOUNT_SETTING;

    private Map<String, Object> paramMap;
    private Long itemId;
    private String itemApiKey;
    private String itemValue;
    private Long target;
    private Long id;
    private Long belongId;
    private Long entityId;
    private Long instanceId;
    private Long belongTypeId;
    private Long customizeId;
    private String customizeIds;
    private String businessIds;
    private Long referItemId;
    private Long referRecordId;
    private Long referObjectId;
    private String deleteDetailIds;
    private String detailEntityMap;
    private Long detailEntityId;

    @Autowired
    private CustomerAccountSettingBusinessService businessService;
    @Autowired
    private CommonBusinessService commonBusinessService;

    @Autowired
    private ItemQueryAggService itemQueryAggService;

    /**
     * 打开创建账户配置页面
     * @return
     *
     * @apiStart
     *     @apiStatus:1
     *     @apiUrl: /json/crm_531/open-create.action
     *     @businessLine:SFA
     *     @module:账户配置
     * @apiEnd
     *
     */
    @Permissions(models = {@Model(name = CURR_OBJECT_ID_STRING, operation = "create")})
    @Action(value = "open-create", results = {@Result(name = "success", location = PAGES_ROOT + "/openCreate.jsp")})
    public String openCreate() {
        try {
            ProcessorResult processorResult = businessService.openCreate(belongTypeId, referRecordId, referObjectId, referItemId, getAggregatorContext());
            if (super.isSuccess(processorResult)) {
                super.assembAtrribute(processorResult);
            }
        } catch (Exception e) {
            super.setStatus(ERROR_CODE_SYSTEM);
            baseLog.error(e.getMessage(), e);
        }
        return SUCCESS;
    }

    /**
     * 打开更新账户配置页面
     * @return
     *
     * @apiStart
     *     @apiStatus:1
     *     @apiUrl: /json/crm_531/open-update.action
     *     @businessLine:SFA
     *     @module:账户配置
     * @apiEnd
     *
     */
    @Permissions(models = {@Model(name = CURR_OBJECT_ID_STRING, operation = "update")})
    @Action(value = "open-update", results = {@Result(name = SUCCESS, location = PAGES_ROOT + "/openUpdate.jsp")})
    public String openUpdate() {
        try {
            ProcessorResult processorResult = businessService.openUpdate(customizeId, getAggregatorContext());
            if (super.isSuccess(processorResult)) {
                super.assembAtrribute(processorResult);
            }
        } catch (Exception e) {
            this.setStatus(ERROR_CODE_SYSTEM);
            baseLog.error(e.getMessage(), e);
        }
        return SUCCESS;
    }

    /**
     * 账户配置新建保存
     * @return
     *
     * @apiStart
     *     @apiStatus:1
     *     @apiUrl: /json/crm_531/save.action
     *     @businessLine:SFA
     *     @module:账户配置
     * @apiEnd
     *
     */
    @Permissions(models = {@Model(name = CURR_OBJECT_ID_STRING, operation = "create")})
    @Action(value = "save", results = {@Result(name = SUCCESS, location = PAGES_COMMON_ROOT + "/onlyData.jsp")})
    public String save() {
        ProcessorResult processorResult = new ProcessorResult();
        try {
            ParamUtil.filterParamMapForAdd(paramMap);
            paramMap.put(ProcessorRequest.OBJECTID, belongId);
            paramMap.put(ProcessorRequest.USER_ID, getUserId());
            paramMap.put("entityType", belongTypeId);// 这里业务类型很任性，得用entityType才行
            // 组装调用API接口的数据
            com.alibaba.fastjson.JSONObject jsonObject = new com.alibaba.fastjson.JSONObject();
            jsonObject.put("data", paramMap);
            // 获取FeignClient
            CustomerAccountSettingClientProxy restApi = xsyServiceLocator.lookup(CustomerAccountSettingClientProxy.class);
            // 调用API接口
            SingleResult singleResult = restApi.create(CURR_OBJECT_NAME_STRING, jsonObject);
            // TODO 异常处理待完善
            if (!singleResult.getCode().equals("200")) {
                this.assembErrorAttribute(processorResult, singleResult);
                return SUCCESS;
            }
            processorResult.put("objectId", belongId);
            processorResult.put("name", paramMap.get("name"));
            Map resultMap = processorResult.getData();
            if (resultMap != null) {
                net.sf.json.JSONObject resultJson = net.sf.json.JSONObject.fromObject(resultMap);
                getRequest().setAttribute("data", resultJson);
            }

        } catch (Exception e) {
            setStatus(ERROR_CODE_SYSTEM);
            baseLog.error(e.getMessage());
            e.printStackTrace();
        }
        return SUCCESS;
    }

    /**
     * 账户配置更新保存
     * @return
     *
     * @apiStart
     *     @apiStatus:1
     *     @apiUrl: /json/crm_531/update.action
     *     @businessLine:SFA
     *     @module:账户配置
     * @apiEnd
     *
     */
    @Permissions(models = {@Model(name = CURR_OBJECT_ID_STRING, operation = "update")})
    @Action(value = "update", results = {@Result(name = "success", location = PAGES_COMMON_ROOT + "/saveResult.jsp")})
    public String update() {
        ProcessorResult processorResult = new ProcessorResult();
        try {
            if (entityId == null || itemId == null) {
                processorResult.setStatusCode(ProcessorConstants.ERROR_CODE_NULL_OBJECT);
                return SUCCESS;
            }
            ItemAO item = itemQueryAggService.getById(itemId, CURR_OBJECT_ID, getAggregatorContext());
            if (item == null) {
                processorResult.setStatusCode(ProcessorConstants.ERROR_CODE_NULL_OBJECT);
                return SUCCESS;
            } else {
                itemApiKey = item.getApiKey();
            }
            Map<String, Object> params = new HashMap<>();
            params.put("id", entityId);
            params.put(itemApiKey, itemValue);
            // 组装调用API接口的数据
            com.alibaba.fastjson.JSONObject jsonObject = new com.alibaba.fastjson.JSONObject();
            jsonObject.put("data", params);
            // 获取FeignClient
            CustomerAccountSettingClientProxy restApi = xsyServiceLocator.lookup(CustomerAccountSettingClientProxy.class);
            // 调用API接口
            SingleResult singleResult = restApi.update(CURR_OBJECT_NAME_STRING, jsonObject);
            if (!singleResult.getCode().equals("200")) {
                this.assembErrorAttribute(processorResult, singleResult);
                return SUCCESS;
            }

            Map resultMap = (Map) processorResult.getData().get("customEntity");
            net.sf.json.JSONObject resultJson = net.sf.json.JSONObject.fromObject(resultMap);
            getRequest().setAttribute("data", resultJson);
            getRequest().setAttribute(ProcessorRequest.RELATE_COMPUTE_ITEMS, processorResult.get(ProcessorRequest.RELATE_COMPUTE_ITEMS));
        } catch (Exception e) {
            setStatus(ERROR_CODE_SYSTEM);
            setDevelopInfo(e + e.getMessage());
            baseLog.error(e.getMessage(), e);
        }
        return SUCCESS;
    }

    /**
     * 账户配置批量更新保存
     * @return
     *
     * @apiStart
     *     @apiStatus:1
     *     @apiUrl: /json/crm_531/update-all.action
     *     @businessLine:SFA
     *     @module:账账户配置
     * @apiEnd
     *
     */
    @Permissions(models = {@Model(name = CURR_OBJECT_ID_STRING, operation = "update")})
    @Action(value = "update-all", results = {@Result(name = "success", location = PAGES_COMMON_ROOT + "/saveResult.jsp")})
    public String updateAll() {
        ProcessorResult processorResult = new ProcessorResult();
        try {
            ParamUtil.filterParamMapForEdit(paramMap);
            paramMap.put(ProcessorRequest.OBJECTID, belongId);
            paramMap.put(ProcessorRequest.USER_ID, getUserId());
            paramMap.put("id", customizeId);

            // 组装调用API接口的数据
            com.alibaba.fastjson.JSONObject jsonObject = new com.alibaba.fastjson.JSONObject();
            jsonObject.put("data", paramMap);
            // 获取FeignClient
            CustomerAccountSettingClientProxy restApi = xsyServiceLocator.lookup(CustomerAccountSettingClientProxy.class);
            // 调用API接口
            SingleResult singleResult = restApi.update(CURR_OBJECT_NAME_STRING, jsonObject);
            // TODO 异常处理待完善
            if (!singleResult.getCode().equals("200")) {
                this.assembErrorAttribute(processorResult, singleResult);
                return SUCCESS;
            }

            Map resultMap = (Map) processorResult.getData().get("customEntity");
            net.sf.json.JSONObject resultJson = net.sf.json.JSONObject.fromObject(resultMap);
            getRequest().setAttribute("data", resultJson);
            getRequest().setAttribute(ProcessorRequest.RELATE_COMPUTE_ITEMS, processorResult.get(ProcessorRequest.RELATE_COMPUTE_ITEMS));
        } catch (Exception e) {
            setStatus(ERROR_CODE_SYSTEM);
            setDevelopInfo(e + e.getMessage());
            baseLog.error(e.getMessage(), e);
        }
        return SUCCESS;
    }

    /**
     * 验证账户配置是否可删除
     * @return
     *
     * @apiStart
     *     @apiStatus:1
     *     @apiUrl: /json/crm_531/validate-delete.action
     *     @businessLine:SFA
     *     @module:账户配置
     * @apiEnd
     *
     */
    @Permissions(models = {@Model(name = CURR_OBJECT_ID_STRING, operation = "delete")})
    @Action(value = "validate-delete", results = {@Result(name = SUCCESS, location = PAGES_COMMON_ROOT + "/onlyData.jsp")})
    public String validateDelete() {
        if (StringUtils.isEmpty(customizeIds)) {
            setStatus(ERROR_CODE_SYSTEM);
            return SUCCESS;
        }
    }

    /**
     * 删除账户配置
     * @return
     *
     * @apiStart
     *     @apiStatus:1
     *     @apiUrl: /json/crm_531/delete.action
     *     @businessLine:SFA
     *     @module:账户配置
     * @apiEnd
     *
     */
    @Permissions(models = {@Model(name = CURR_OBJECT_ID_STRING, operation = "delete")})
    @Action(value = "delete", results = {@Result(name = "success", location = PAGES_COMMON_ROOT + "/onlyData.jsp")})
    public String deleteByProcessor() {
        ProcessorResult processorResult = new ProcessorResult();
        paramMap = new HashMap<>();
        paramMap.put(ProcessorRequest.OBJECTID, belongId);
        try {
            // 获取FeignClient
            CustomerAccountSettingClientProxy restAPI = xsyServiceLocator.lookup(CustomerAccountSettingClientProxy.class);
            MultiResult multiResult = restAPI.deleteByGet(CURR_OBJECT_NAME_STRING, customizeIds);
            if (!this.checkResult(multiResult)) {
                assembMultiResultErrorAttribute(processorResult, multiResult);
                return SUCCESS;
            }
        } catch (Exception e) {
            setStatus(ERROR_CODE_SYSTEM);
            baseLog.error(e.getMessage(), e);
        }
        return SUCCESS;
    }

    /**
     * 锁定账户配置
     * @return
     *
     * @apiStart
     *     @apiStatus:1
     *     @apiUrl: /json/crm_531/lock-data.action
     *     @businessLine:SFA
     *     @module:账户配置
     * @apiEnd
     *
     */
    @Permissions(models = {@Model(name = CURR_OBJECT_ID_STRING, operation = "lock")})
    @Action(value = "lock-data", results = {@Result(name = "success", location = PAGES_COMMON_ROOT + "/noData.jsp")})
    public String lockData() {
        if (belongId == null || id == null) {
            setStatus(ERROR_CODE_SYSTEM);
            return SUCCESS;
        }
        ProcessorResult processorResult = new ProcessorResult();
        try {
            // 获取FeignClient
            CustomerAccountSettingClientProxy restApi = xsyServiceLocator.lookup(CustomerAccountSettingClientProxy.class);
            // 调用API接口
            MultiResult multiResult = restApi.lock(CURR_OBJECT_NAME_STRING,id);
            if (!this.checkResult(multiResult)) {
                assembMultiResultErrorAttribute(processorResult, multiResult);
                return SUCCESS;
            }
        }catch (Exception e) {
            setStatus(ERROR_CODE_SYSTEM);
            setDevelopInfo(e+e.getMessage());
            baseLog.error(e.getMessage(), e);
        }
        return SUCCESS;
    }

    /**
     * 解锁账户配置
     * @return
     *
     * @apiStart
     *     @apiStatus:1
     *     @apiUrl: /json/crm_531/unlock-data.action
     *     @businessLine:SFA
     *     @module:账户配置
     * @apiEnd
     *
     */
    @Permissions(models = {@Model(name = CURR_OBJECT_ID_STRING, operation = "unlock")})
    @Action(value = "unlock-data", results = {@Result(name = "success", location = PAGES_COMMON_ROOT + "/noData.jsp")})
    public String unlockData() {
        if (belongId == null || id == null) {
            setStatus(ERROR_CODE_SYSTEM);
            return SUCCESS;
        }
        ProcessorResult processorResult = new ProcessorResult();
        try {
            // 获取FeignClient
            CustomerAccountSettingClientProxy restApi = xsyServiceLocator.lookup(CustomerAccountSettingClientProxy.class);
            // 调用API接口
            MultiResult multiResult = restApi.unLock(CURR_OBJECT_NAME_STRING,id);
            if (!this.checkResult(multiResult)) {
                assembMultiResultErrorAttribute(processorResult, multiResult);
                return SUCCESS;
            }
        }catch (Exception e) {
            setStatus(ERROR_CODE_SYSTEM);
            setDevelopInfo(e+e.getMessage());
            baseLog.error(e.getMessage(), e);
        }
        return SUCCESS;
    }

    /**
     * 转移账户配置
     * @return
     *
     * @apiStart
     *     @apiStatus:1
     *     @apiUrl: /json/crm_531/transfer.action
     *     @businessLine:SFA
     *     @module:账户配置
     * @apiEnd
     *
     */
    @Permissions(models = {@Model(name = CURR_OBJECT_ID_STRING, operation = "transfer")})
    @Action(value = "transfer", results = {@Result(name = SUCCESS, location = PAGES_COMMON_ROOT + "/onlyData.jsp")})
    public String transferByProcessor() {
        if (StringUtils.isEmpty(customizeIds) || target == null) {
            setStatus(ERROR_CODE_SYSTEM);
            return SUCCESS;
        }
        ProcessorResult processorResult = new ProcessorResult();
        try {

            // 组装调用API接口的数据
            com.alibaba.fastjson.JSONObject jsonObject = assembeParamJsonForIds(customizeIds);
            // 获取FeignClient
            CustomerAccountSettingClientProxy restApi = xsyServiceLocator.lookup(CustomerAccountSettingClientProxy.class);
            // 调用API接口
            MultiResult multiResult =  restApi.transferBatch(CURR_OBJECT_NAME_STRING,jsonObject,target);
            if (!multiResult.getCode().equals("200")) {
                this.assembErrorAttribute(processorResult,multiResult);
                return SUCCESS;
            }
        }catch (Exception e) {
            setStatus(ERROR_CODE_SYSTEM);
            setDevelopInfo(e+e.getMessage());
            baseLog.error(e.getMessage(), e);
        }
        return SUCCESS;
    }

    /**
     * 账户配置操作记录
     * @return
     *
     * @apiStart
     *     @apiStatus:1
     *     @apiUrl: /json/crm_531/entity-logs.action
     *     @businessLine:SFA
     *     @module:账户配置
     * @apiEnd
     *
     */
    @Permissions(models = {@Model(name = CURR_OBJECT_ID_STRING, operation = "detail")})
    @Action(value = "entity-logs", results = {@Result(name = "success", location = PAGES_CUSTOMIZE_ROOT + "/entityLogs.jsp")})
    public String entityLogs() {
        if (instanceId == null || belongId == null) {
            setStatus(ERROR_CODE_SYSTEM);
            return SUCCESS;
        }
        try {
            ProcessorResult processorResult = commonBusinessService.getOperationLogs(instanceId, belongId, getAggregatorContext());
            if (super.isSuccess(processorResult)) {
                super.assembAtrribute(processorResult);
            }
        } catch (ServiceException e) {
            setStatus(ERROR_CODE_SYSTEM);
            baseLog.error(e.getMessage(), e);
        }
        return SUCCESS;
    }


    public com.alibaba.fastjson.JSONObject assembeParamJsonForIds(String customizeIds){
        com.alibaba.fastjson.JSONObject jsonObject = new com.alibaba.fastjson.JSONObject();
        List<Map<String,Map<String,Long>>> datas = new ArrayList<Map<String,Map<String,Long>>>();
        jsonObject.put("batchData", datas);
        List<Long> ids = ParamUtil.parseIdsToList(customizeIds);
        if(!CollectionUtils.isEmpty(ids)){
            for(Long id:ids){
                Map<String,Map<String,Long>> data = new HashMap<String,Map<String,Long>>();
                Map<String,Long> idMap = new HashMap<String,Long>();
                idMap.put("id",id);
                data.put("data",idMap);
                datas.add(data);
            }
        }
        return jsonObject;
    }
    private void assembMultiResultErrorAttribute(ProcessorResult processorResult,MultiResult multiResult){
        List<SingleResult> list = multiResult.getBatchData();
        if(!CollectionUtils.isEmpty(list)){
            for(SingleResult result : list) {
                if (!result.getCode().equals("200")) {
                    this.assembErrorAttribute(processorResult, result);
                    return;
                }
            }
        }

    }

    public Map<String, Object> getParamMap() {
        return paramMap;
    }

    public void setParamMap(Map<String, Object> paramMap) {
        this.paramMap = paramMap;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public String getItemApiKey() {
        return itemApiKey;
    }

    public void setItemApiKey(String itemApiKey) {
        this.itemApiKey = itemApiKey;
    }

    public String getItemValue() {
        return itemValue;
    }

    public void setItemValue(String itemValue) {
        this.itemValue = itemValue;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTarget() {
        return target;
    }

    public void setTarget(Long target) {
        this.target = target;
    }

    public Long getBelongId() {
        return belongId;
    }

    public void setBelongId(Long belongId) {
        this.belongId = belongId;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public Long getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(Long instanceId) {
        this.instanceId = instanceId;
    }

    public Long getBelongTypeId() {
        return belongTypeId;
    }

    public void setBelongTypeId(Long belongTypeId) {
        this.belongTypeId = belongTypeId;
    }

    public Long getCustomizeId() {
        return customizeId;
    }

    public void setCustomizeId(Long customizeId) {
        this.customizeId = customizeId;
    }

    public String getCustomizeIds() {
        return customizeIds;
    }

    public void setCustomizeIds(String customizeIds) {
        this.customizeIds = customizeIds;
    }

    public void setBusinessIds(String businessIds) {
        this.businessIds = businessIds;
    }

    public Long getReferItemId() {
        return referItemId;
    }

    public void setReferItemId(Long referItemId) {
        this.referItemId = referItemId;
    }

    public Long getReferRecordId() {
        return referRecordId;
    }

    public void setReferRecordId(Long referRecordId) {
        this.referRecordId = referRecordId;
    }

    public Long getReferObjectId() {
        return referObjectId;
    }

    public void setReferObjectId(Long referObjectId) {
        this.referObjectId = referObjectId;
    }

    public String getDeleteDetailIds() {
        return deleteDetailIds;
    }

    public void setDeleteDetailIds(String deleteDetailIds) {
        this.deleteDetailIds = deleteDetailIds;
    }

    public String getDetailEntityMap() {
        return detailEntityMap;
    }

    public void setDetailEntityMap(String detailEntityMap) {
        this.detailEntityMap = detailEntityMap;
    }

    public Long getDetailEntityId() {
        return detailEntityId;
    }

    public void setDetailEntityId(Long detailEntityId) {
        this.detailEntityId = detailEntityId;
    }
}
