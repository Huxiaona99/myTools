package com.rkhd.ienterprise.apps.salescloud.customeraccountsetting.processor;

import com.rkhd.ienterprise.apps.salescloud.base.processor.BaseGetProcessor;
import com.rkhd.ienterprise.model.EngagerRequest;
import com.rkhd.ienterprise.model.MultiResult;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;


@Component("customerAccountSettingGetProcessor")
public class CustomerAccountSettingGetProcessor extends BaseGetProcessor {
@Override
protected void getBefore(String s, List<Long> list, EngagerRequest engagerRequest, Map map, MultiResult multiResult) {

    }

    @Override
    protected void getAfter(MultiResult multiResult, Map map) {

    }
}
