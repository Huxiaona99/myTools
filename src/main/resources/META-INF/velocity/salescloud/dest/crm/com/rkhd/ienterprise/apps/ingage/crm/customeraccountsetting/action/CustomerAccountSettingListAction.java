package com.rkhd.ienterprise.apps.ingage.crm.customeraccountsetting.action;

import com.rkhd.ienterprise.apps.ingage.crm.customize.action.CustomEntityListAction;
import com.rkhd.ienterprise.apps.manager.constant.ObjectConstants;
import com.rkhd.ienterprise.permission.annotation.Model;
import com.rkhd.ienterprise.permission.annotation.Permissions;
import com.rkhd.ienterprise.permission.standardization.StandardCutomize;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.Result;

/**
 * 账户配置列表操作
 */
@Namespace("/json/crm_531")
public class CustomerAccountSettingListAction extends CustomEntityListAction implements StandardCutomize {

    /**
     * 账户配置列表
     * @return
     *
     * @apiStart
     *     @apiStatus:1
     *     @apiUrl: /json/crm_531/listNew.action
     *     @businessLine:SFA
     *     @module:账户配置
     * @apiEnd
     *
     */
    @Permissions(models = {@Model(name = ObjectConstants.OBJECT_ID_CUSTOMER_ACCOUNT_SETTING_STRING, operation = "list")})
    @Action(value = "listNew", results = {@Result(name = "success", location = PAGES_ROOT + "/indexNew.jsp")})
    public String indexNew() {
        setBelongId(getStandardObjectId());
        super.indexNew();
        return SUCCESS;
    }

    @Override
    public Long getStandardObjectId() {
        return ObjectConstants.OBJECT_ID_CUSTOMER_ACCOUNT_SETTING;
    }
}
