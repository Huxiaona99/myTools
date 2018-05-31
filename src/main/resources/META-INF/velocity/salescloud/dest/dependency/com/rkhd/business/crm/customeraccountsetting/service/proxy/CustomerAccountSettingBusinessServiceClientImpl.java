package com.rkhd.business.crm.customeraccountsetting.service.proxy;

import com.rkhd.business.crm.customeraccountsetting.service.CustomerAccountSettingBusinessService;
import com.rkhd.ienterprise.common.sca.service.AbstractServiceProxy;
import org.osoa.sca.annotations.Reference;

public class CustomerAccountSettingBusinessServiceClientImpl extends AbstractServiceProxy<CustomerAccountSettingBusinessService> {
    @Reference
    protected CustomerAccountSettingBusinessService rmiService;

    @Override
    public CustomerAccountSettingBusinessService getRmiService() {
    return rmiService;
    }
    }

