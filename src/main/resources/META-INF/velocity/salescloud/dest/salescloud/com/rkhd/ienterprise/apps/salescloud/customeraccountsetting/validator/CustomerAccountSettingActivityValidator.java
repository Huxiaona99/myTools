package com.rkhd.ienterprise.apps.salescloud.customeraccountsetting.validator;

import com.rkhd.ienterprise.apps.salescloud.base.validator.BaseActivityValidator;
import com.rkhd.ienterprise.model.EngagerRequest;
import com.rkhd.ienterprise.model.MultiResult;
import org.springframework.stereotype.Component;

import java.util.Map;


@Component("customerAccountSettingActivityValidator")
public class CustomerAccountSettingActivityValidator extends BaseActivityValidator {
    @Override
    protected void processValidate(String s, EngagerRequest engagerRequest, Map map, MultiResult multiResult) {

    }
}
