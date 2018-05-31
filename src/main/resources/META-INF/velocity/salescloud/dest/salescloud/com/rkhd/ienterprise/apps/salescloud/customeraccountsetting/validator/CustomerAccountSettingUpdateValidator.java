package com.rkhd.ienterprise.apps.salescloud.customeraccountsetting.validator;

import com.rkhd.ienterprise.apps.salescloud.base.validator.BaseUpdateValidator;
import com.rkhd.ienterprise.model.EngagerRequest;
import com.rkhd.ienterprise.model.MultiResult;
import com.rkhd.ienterprise.model.xobject.ObjectDataAO;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;


@Component("customerAccountSettingUpdateValidator")
public class CustomerAccountSettingUpdateValidator extends BaseUpdateValidator {
    @Override
    protected void processValidate(String s, List<ObjectDataAO> list, EngagerRequest engagerRequest, Map map, MultiResult multiResult) {

    }
}
