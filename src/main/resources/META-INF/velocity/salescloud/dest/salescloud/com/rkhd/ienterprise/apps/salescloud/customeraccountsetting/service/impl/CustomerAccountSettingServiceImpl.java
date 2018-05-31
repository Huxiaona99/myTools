package com.rkhd.ienterprise.apps.salescloud.customeraccountsetting.service.impl;

import com.rkhd.ienterprise.apps.salescloud.base.feign.PaasAggObjectDataFeignClient;
import com.rkhd.ienterprise.apps.salescloud.base.service.SalesCloudServiceAdapter;
import com.rkhd.ienterprise.apps.salescloud.customeraccountsetting.processor.CustomerAccountSettingCreateProcessor;
import com.rkhd.ienterprise.apps.salescloud.customeraccountsetting.processor.CustomerAccountSettingDeleteProcessor;
import com.rkhd.ienterprise.apps.salescloud.customeraccountsetting.processor.CustomerAccountSettingGetProcessor;
import com.rkhd.ienterprise.apps.salescloud.customeraccountsetting.processor.CustomerAccountSettingUpdateProcessor;
import com.rkhd.ienterprise.apps.salescloud.customeraccountsetting.service.CustomerAccountSettingService;
import com.rkhd.ienterprise.apps.salescloud.customeraccountsetting.validator.CustomerAccountSettingCreateValidator;
import com.rkhd.ienterprise.apps.salescloud.customeraccountsetting.validator.CustomerAccountSettingDeleteValidator;
import com.rkhd.ienterprise.apps.salescloud.customeraccountsetting.validator.CustomerAccountSettingGetValidator;
import com.rkhd.ienterprise.apps.salescloud.customeraccountsetting.validator.CustomerAccountSettingUpdateValidator;
import com.rkhd.ienterprise.base.feign.paasagg.BaseObjectDataFeignClient;
import com.rkhd.ienterprise.model.EngagerRequest;
import com.rkhd.ienterprise.model.MultiResult;
import com.rkhd.ienterprise.model.xobject.ObjectDataAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service("customerAccountSettingService")
public class CustomerAccountSettingServiceImpl extends SalesCloudServiceAdapter implements CustomerAccountSettingService {

    @Autowired
    private PaasAggObjectDataFeignClient paasAggObjectDataFeignClient;


    @Autowired(required = false)
    private CustomerAccountSettingCreateProcessor}    createProcessor ;

    @Autowired(required = false)
    private CustomerAccountSettingUpdateProcessor    updateProcessor ;

    @Autowired(required = false)
    private CustomerAccountSettingDeleteProcessor    deleteProcessor ;

    @Autowired(required = false)
    private CustomerAccountSettingGetProcessor       getProcessor ;

    @Autowired(required = false)
    private CustomerAccountSettingCreateValidator    createValidator ;

    @Autowired(required = false)
    private CustomerAccountSettingUpdateValidator updateValidator;

    @Autowired(required = false)
    private CustomerAccountSettingDeleteValidator  deleteValidator ;

    @Autowired(required = false)
    private CustomerAccountSettingGetValidator   getValidator ;

    @Override
    public BaseObjectDataFeignClient getPassAggObjectDataFeignClient() {
    return this.paasAggObjectDataFeignClient;
    }


    @Override
    protected void createBefore(String s, List<ObjectDataAO> list, EngagerRequest engagerRequest, Map map, MultiResult multiResult) {
        super.createBefore(s, list, engagerRequest, map, multiResult);

        if (createValidator != null) {
            this.createValidator.validate(s, list, engagerRequest, map, multiResult);
        }

        if (createProcessor != null) {
            this.createProcessor.processBefore(s, list, engagerRequest, map, multiResult);
        }
    }

    @Override
    protected void createAfter(MultiResult multiResult, Map map) {
        super.createAfter(multiResult, map);
        if (createProcessor != null) {
            this.createProcessor.processAfter(multiResult, map);
        }
    }

    @Override
    protected void updateAfter(MultiResult multiResult, Map map) {
        super.updateAfter(multiResult, map);
        if (this.updateProcessor != null) {
            this.updateProcessor.processAfter(multiResult,map);
        }
    }

    @Override
    protected void updateBefore(String s, List<ObjectDataAO> list, EngagerRequest engagerRequest, Map map , MultiResult multiResult) {

        super.updateBefore(s, list, engagerRequest, map, multiResult);
        if (updateValidator != null) {
            this.updateValidator.validate(s, list, engagerRequest, map, multiResult);
        }
        if (this.updateProcessor != null) {
            this.updateProcessor.processBefore(s,list,engagerRequest,map, multiResult);
        }

    }

    @Override
    protected void deleteBefore(String s, List<Long> list, EngagerRequest engagerRequest, Map map , MultiResult multiResult) {
        super.deleteBefore(s, list, engagerRequest, map, multiResult);
        if (this.deleteValidator != null) {
            this.deleteValidator.validate(s, list, engagerRequest, map, multiResult);
        }
        if (this.deleteProcessor != null) {
            this.deleteProcessor.processBefore(s, list, engagerRequest, map, multiResult);
        }
    }

    @Override
    protected void deleteAfter(MultiResult multiResult, Map map ) {
        super.deleteAfter(multiResult, map);
        if (this.deleteProcessor != null) {
            this.deleteProcessor.processAfter(multiResult, map);
        }

    }

    @Override
    protected void getBefore(String s, List<Long> list, EngagerRequest engagerRequest, Map map , MultiResult multiResult) {
        super.getBefore(s, list, engagerRequest, map, multiResult);
        if (this.getValidator != null) {
            this.getValidator.validate(s,list,engagerRequest,map, multiResult);
        }
        if (this.getProcessor != null) {
            this.getProcessor.processBefore(s, list, engagerRequest, map, multiResult);
        }
    }

    @Override
    protected void getAfter(MultiResult multiResult, Map map) {
        super.getAfter(multiResult, map);
        if (this.getProcessor != null) {
            this.getProcessor.processAfter(multiResult, map);
        }
    }
}
