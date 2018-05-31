package com.rkhd.ienterprise.apps.salescloud.customeraccountsetting.processor;

import com.rkhd.ienterprise.apps.salescloud.base.processor.BaseUnLockProcessor;
import com.rkhd.ienterprise.model.EngagerRequest;
import com.rkhd.ienterprise.model.MultiResult;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
* Created by chencd on 2018/3/6.
*/
@Component("customerAccountSettingUnLockProcessor")
public class CustomerAccountSettingUnLockProcessor extends BaseUnLockProcessor {
@Override
protected void unLockBefore(String s, List<Long> list, EngagerRequest engagerRequest, Map map, MultiResult multiResult) {

    }

    @Override
    protected void unLockAfter(MultiResult multiResult, Map map) {

    }
}
