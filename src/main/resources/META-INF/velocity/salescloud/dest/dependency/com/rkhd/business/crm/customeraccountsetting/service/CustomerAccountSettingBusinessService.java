package com.rkhd.business.crm.customeraccountsetting.service;

import com.rkhd.aggregator.common.context.AggregatorContext;
import com.rkhd.aggregator.data.model.ObjectDataAO;
import com.rkhd.ienterprise.apps.manager.business.base.ProcessorResult;
import com.rkhd.ienterprise.exception.ServiceException;

import java.util.List;
import java.util.Map;

public interface CustomerAccountSettingBusinessService {

    public ProcessorResult openCreate(Long busiTypeId, Long referRecordId, Long referObjectId, Long referItemId, AggregatorContext aggregatorContext) throws ServiceException;

    public ProcessorResult openUpdate(Long recordId, AggregatorContext var3) throws ServiceException;

    public ProcessorResult detailList(Long recordId, AggregatorContext aggregatorContext) throws ServiceException;

    public ProcessorResult detailOthers(Long recordId, AggregatorContext aggregatorContext) throws ServiceException;

}