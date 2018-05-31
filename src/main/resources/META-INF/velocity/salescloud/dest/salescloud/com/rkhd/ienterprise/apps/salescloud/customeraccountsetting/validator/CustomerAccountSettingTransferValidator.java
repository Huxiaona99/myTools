package com.rkhd.ienterprise.apps.salescloud.customeraccountsetting.validator;

import com.rkhd.ienterprise.apps.salescloud.base.validator.BaseTransferValidator;
import com.rkhd.ienterprise.model.EngagerRequest;
import com.rkhd.ienterprise.model.MultiResult;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;


@Component("customerAccountSettingTransferValidator")
public class CustomerAccountSettingTransferValidator extends BaseTransferValidator {
    @Override
    protected void processValidate(String s, List<Long> list, EngagerRequest engagerRequest, Map map, MultiResult multiResult) {

    }
}
