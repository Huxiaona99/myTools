package com.rkhd.ienterprise.apps.salescloud.customeraccountsetting.processor;

import com.rkhd.ienterprise.apps.salescloud.base.processor.BaseUpdateProcessor;
import com.rkhd.ienterprise.model.EngagerRequest;
import com.rkhd.ienterprise.model.MultiResult;
import com.rkhd.ienterprise.model.xobject.ObjectDataAO;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
* Created by chencd on 2018/3/6.
*/
@Component("customerAccountSettingUpdateProcessor")
public class CustomerAccountSettingUpdateProcessor extends BaseUpdateProcessor {
    @Override
    protected void updateBefore(String s, List<ObjectDataAO> list, EngagerRequest engagerRequest, Map map, MultiResult multiResult) {
        this.convertParamMap(list);
    }

    @Override
    protected void updateAfter(MultiResult multiResult, Map map) {

    }
}
