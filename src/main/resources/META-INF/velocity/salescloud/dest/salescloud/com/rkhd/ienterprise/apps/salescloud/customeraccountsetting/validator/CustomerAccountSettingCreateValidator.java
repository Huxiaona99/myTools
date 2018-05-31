package com.rkhd.ienterprise.apps.salescloud.customeraccountsetting.validator;

import com.rkhd.ienterprise.apps.salescloud.base.validator.BaseCreateValidator;
import com.rkhd.ienterprise.model.EngagerRequest;
import com.rkhd.ienterprise.model.MultiResult;
import com.rkhd.ienterprise.model.xobject.ObjectDataAO;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;


@Component("customerAccountSettingCreateValidator")
public class CustomerAccountSettingCreateValidator extends BaseCreateValidator {
    @Override
    protected void processValidate(String s, List<ObjectDataAO> list, EngagerRequest engagerRequest, Map map, MultiResult multiResult) {

    }
}
