package com.rkhd.ienterprise.apps.salescloud.customeraccountsetting.processor;

import com.rkhd.ienterprise.apps.salescloud.base.processor.BaseActivityProcessor;
import com.rkhd.ienterprise.model.EngagerRequest;
import com.rkhd.ienterprise.model.MultiResult;
import org.springframework.stereotype.Component;

import java.util.Map;


@Component("customerAccountSettingActivityProcessor")
public class CustomerAccountSettingActivityProcessor extends BaseActivityProcessor {
    @Override
    protected void activitiesBefore(String s, EngagerRequest engagerRequest, Map map, MultiResult multiResult) {

    }

    @Override
    protected void activitiesAfter(MultiResult multiResult, Map map) {

    }
}