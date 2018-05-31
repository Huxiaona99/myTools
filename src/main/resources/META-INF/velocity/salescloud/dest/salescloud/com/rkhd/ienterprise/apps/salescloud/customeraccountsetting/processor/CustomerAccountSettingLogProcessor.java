package com.rkhd.ienterprise.apps.salescloud.customeraccountsetting.processor;

import com.rkhd.ienterprise.apps.salescloud.base.processor.BaseLogProcessor;
import com.rkhd.ienterprise.model.EngagerRequest;
import com.rkhd.ienterprise.model.MultiResult;
import org.springframework.stereotype.Component;

import java.util.Map;


@Component("customerAccountSettingLogProcessor")
public class CustomerAccountSettingLogProcessor extends BaseLogProcessor {
    @Override
    protected void logBefore(String objectApiKey, Long aLong, EngagerRequest engagerRequest, Map businessExt, MultiResult multiResult) {

    }

    @Override
    protected void logAfter(MultiResult multiResult, Map businessExt) {

    }
}
