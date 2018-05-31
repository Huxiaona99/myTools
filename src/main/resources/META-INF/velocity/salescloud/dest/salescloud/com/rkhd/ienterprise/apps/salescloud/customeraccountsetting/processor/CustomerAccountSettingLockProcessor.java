package com.rkhd.ienterprise.apps.salescloud.customeraccountsetting.processor;

import com.rkhd.ienterprise.apps.salescloud.base.processor.BaseLockProcessor;
import com.rkhd.ienterprise.model.EngagerRequest;
import com.rkhd.ienterprise.model.MultiResult;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;


@Component("customerAccountSettingLockProcessor")
public class CustomerAccountSettingLockProcessor extends BaseLockProcessor {
@Override
protected void lockBefore(String s, List<Long> list, EngagerRequest engagerRequest, Map map, MultiResult multiResult) {

    }

    @Override
    protected void lockAfter(MultiResult multiResult, Map map) {

    }
}
