package com.rkhd.ienterprise.apps.salescloud.customeraccountsetting.validator;

import com.rkhd.ienterprise.apps.salescloud.base.validator.BaseLockValidator;
import com.rkhd.ienterprise.model.EngagerRequest;
import com.rkhd.ienterprise.model.MultiResult;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;


@Component("customerAccountSettingLockValidator")
public class CustomerAccountSettingLockValidator extends BaseLockValidator {
    @Override
    protected void processValidate(String s, List<Long> list, EngagerRequest engagerRequest, Map map, MultiResult multiResult) {

    }
}
