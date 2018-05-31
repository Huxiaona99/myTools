package com.rkhd.ienterprise.apps.salescloud.customeraccountsetting.processor;

import com.rkhd.ienterprise.apps.salescloud.base.processor.BaseCreateProcessor;
import com.rkhd.ienterprise.model.EngagerRequest;
import com.rkhd.ienterprise.model.MultiResult;
import com.rkhd.ienterprise.model.xobject.ObjectDataAO;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component("customerAccountSettingCreateProcessor")
public class CustomerAccountSettingCreateProcessor extends BaseCreateProcessor {
@Override
protected void createBefore(String s, List<ObjectDataAO> list, EngagerRequest engagerRequest, Map map, MultiResult multiResult) {
    this.convertParamMap(list);
    }

    @Override
    protected void createAfter(MultiResult multiResult, Map map) {

    }
}