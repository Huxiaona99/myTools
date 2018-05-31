package com.rkhd.ienterprise.apps.salescloud.customeraccountsetting.processor;

import com.rkhd.ienterprise.apps.salescloud.base.processor.BaseFollowProcessor;
import com.rkhd.ienterprise.model.EngagerRequest;
import com.rkhd.ienterprise.model.MultiResult;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;


@Component("customerAccountSettingFollowProcessor")
public class CustomerAccountSettingFollowProcessor extends BaseFollowProcessor {
@Override
protected void followBefore(String s, List<Long> list, EngagerRequest engagerRequest, Map map, MultiResult multiResult) {

    }

    @Override
    protected void followAfter(MultiResult multiResult, Map map) {

    }
}
