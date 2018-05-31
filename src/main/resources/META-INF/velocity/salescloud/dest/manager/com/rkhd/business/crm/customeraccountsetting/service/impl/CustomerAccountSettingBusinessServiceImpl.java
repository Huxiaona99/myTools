package com.rkhd.business.crm.customeraccountsetting.service.impl;

import cloud.multi.tenant.TenantParam;
import com.alibaba.fastjson.JSONObject;
import com.rkhd.aggregator.common.context.AggregatorContext;
import com.rkhd.aggregator.data.model.ObjectDataAO;
import com.rkhd.aggregator.data.service.ObjectDataOperateAggService;
import com.rkhd.aggregator.data.service.ObjectDataQueryAggService;
import com.rkhd.aggregator.exception.AggregatorException;
import com.rkhd.aggregator.metadata.model.BusiTypeAO;
import com.rkhd.aggregator.metadata.model.ItemAO;
import com.rkhd.aggregator.metadata.model.ObjectAO;
import com.rkhd.aggregator.metadata.model.layout.LayoutComponentAO;
import com.rkhd.aggregator.metadata.model.layout.PageLayoutAO;
import com.rkhd.aggregator.metadata.service.BusiTypeQueryAggService;
import com.rkhd.aggregator.metadata.service.ItemQueryAggService;
import com.rkhd.aggregator.metadata.service.ObjectQueryAggService;
import com.rkhd.aggregator.metadata.service.layout.PageLayoutQueryAggService;
import com.rkhd.business.crm.common.service.impl.BaseBusinessServiceImpl;
import com.rkhd.business.flow.service.ApprovalBusinessService;
import com.rkhd.business.flow.service.WorkflowBusinessService;
import com.rkhd.business.standardcustomize.standardcustomize.service.StandardDetailBusinessService;
import com.rkhd.ienterprise.apps.approvalprocess.service.ApprovalInstanceBindingPojoService;
import com.rkhd.ienterprise.apps.isales.contract.model.Contract;
import com.rkhd.ienterprise.apps.manager.business.base.ProcessorResult;
import com.rkhd.ienterprise.apps.manager.business.constants.ProcessorConstants;
import com.rkhd.ienterprise.apps.manager.business.utils.*;
import com.rkhd.ienterprise.apps.manager.constant.ItemConstants;
import com.rkhd.ienterprise.apps.manager.constant.ObjectConstants;
import com.rkhd.ienterprise.apps.manager.customize.component.util.*;
import com.rkhd.ienterprise.apps.manager.customize.util.ResourceUtil;
import com.rkhd.ienterprise.apps.xdata.datapermission.service.DataPermissionManagerService;
import com.rkhd.ienterprise.base.dbcustomize.constant.DBCustomizeConstants;
import com.rkhd.ienterprise.base.dbcustomize.util.DBCustomizeUtil;
import com.rkhd.ienterprise.base.group.service.GroupService;
import com.rkhd.ienterprise.base.relation.model.GroupMember;
import com.rkhd.ienterprise.base.relation.service.GroupFollowService;
import com.rkhd.ienterprise.base.relation.service.GroupMemberService;
import com.rkhd.ienterprise.base.user.model.User;
import com.rkhd.ienterprise.base.user.service.UserService;
import com.rkhd.ienterprise.constant.CommonConstancts;
import com.rkhd.ienterprise.exception.ServiceException;
import com.rkhd.ienterprise.i18n.utils.MultiLangResourceUtil;
import com.rkhd.paas.xdata.common.BatchResult;
import com.rkhd.paas.xdata.constant.DataPermissionConstant;
import com.rkhd.paas.xdata.constant.ObjectConstant;
import com.rkhd.paas.xdata.metadata.dto.XObject;
import com.rkhd.platform.customize.constant.CustomizeConstant;
import com.rkhd.platform.customize.exception.CustomizeException;
import com.rkhd.platform.customize.service.EntityFileService;
import com.rkhd.platform.customize.service.SettingCurrencyService;
import com.rkhd.platform.exception.BusinessException;
import com.rkhd.platform.exception.PaasException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.powermock.core.classloader.annotations.PrepareOnlyThisForTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;


public class CustomerAccountSettingBusinessServiceImpl extends BaseBusinessServiceImpl  {

    private Logger LOGGER = LoggerFactory.getLogger(CustomerAccountSettingBusinessServiceImpl.class);

    private UserService userService;
    private EntityUtil entityUtil;
    private DataPermissionManagerService dataPermissionManagerService;
    private GroupFollowService groupFollowService;
    private GroupMemberService groupMemberService;
    private ObjectQueryAggService objectQueryAggService;
    private EntityFileService entityFileService;
    private ApprovalProcessorUtil approvalProcessorUtil;
    private CustomDataProcessorUtil customDataProcessorUtil;
    private ObjectDataQueryAggService objectDataQueryAggService;
    private ObjectDataOperateAggService objectDataOperateAggService;
    private BusiTypeQueryAggService busiTypeQueryAggService;
    private PageLayoutQueryAggService pageLayoutQueryAggService;
    private DependencyUtil dependencyUtil;
    private ObjectDataBusinessUtil objectDataBusinessUtil;
    private DimensionProcessorUtil dimensionProcessorUtil;
    private DimensionUtil dimensionUtil;
    private EntityProcessorUtil entityProcessorUtil;
    private StandardDetailBusinessService standardDetailBusinessService;
    private ApprovalInstanceBindingPojoService approvalInstanceBindingPojoService;
    private GroupService groupService;
    private ApprovalBusinessService approvalBusinessService;
    private WorkflowBusinessService workflowBusinessService;
    private ObjectDataAOBuilderUtil objectDataAOBuilderUtil;
    private SettingCurrencyService settingCurrencyService;
    private ObjectRelationDataBuilderUtil objectRelationDataBuilderUtil;

    private static final Long objectId = ObjectConstants.OBJECT_ID_CUSTOMER_ACCOUNT_SETTING;
    public static final String UPDATE_ONE = "one";
    private static final short GROUP_MEMBER_STATUS_NORMAL = 0;
    private final static String DETAIL_DATA_KEY = "details";
    private final static String DETAIL_DELETE_DETAIL_IDS = "deleteDetailIds";
    private final static String DETAIL_OBJECT_ID = "detailObjectId";
    private final static String SYSTEM_ERROR = "100000";


    private static List<Short> itemType4Remove = new ArrayList<Short>() {{
        add(CustomizeConstant.ITEM_TYPE_FORMULA);
        add(CustomizeConstant.ITEM_TYPE_AUTONUMBER);
    }};

    private static List<String> item4Remove = new ArrayList<String>() {{
        add(ItemConstants.ObjectCommonItem.createdAt);
        add(ItemConstants.ObjectCommonItem.createdBy);
        add(ItemConstants.ObjectCommonItem.updatedAt);
        add(ItemConstants.ObjectCommonItem.updatedBy);
        add(ItemConstants.ObjectCommonItem.lockStatus);
        add(ItemConstants.ObjectCommonItem.approvalStatus);
        add(ItemConstants.ObjectCommonItem.applicantId);
        add(ItemConstants.ObjectCommonItem.workflowStageName);
    }};

    public List<String> getItem4Remove(){
        return item4Remove;
    }

    public ProcessorResult openCreate(Long busiTypeId, Long referRecordId, Long referObjectId, Long referItemId, AggregatorContext aggregatorContext) throws ServiceException {
        ProcessorResult processorResult = new ProcessorResult();
        TenantParam tenantParam = aggregatorContext.getTenantParam();
        try {
            if (null != referRecordId && null != referObjectId) {
                boolean canOpenCreate = dataPermissionManagerService.canOpenCreate(processorResult, referRecordId, referObjectId, objectId, aggregatorContext.getUserId(), tenantParam);
                if (!canOpenCreate) {
                    LOGGER.error(appendErrorMsg("当前实体父实体不能编辑，所以不能新增当前实体", tenantParam.getTenantId()));
                    return this.businessError(processorResult, aggregatorContext.getLocale(), ProcessorConstants.ERROR_CODE_NO_PERMISSION, "1002011", "没有数据权限");
                }
            }
            BusiTypeAO busiTypeAO = busiTypeQueryAggService.getById(busiTypeId, objectId, aggregatorContext);
            if (null == busiTypeAO) {
                LOGGER.error(appendErrorMsg("业务类型没查到", tenantParam.getTenantId()));
                return businessError(processorResult, aggregatorContext.getLocale(), ProcessorConstants.ERROR_CODE_SYSTEM, "sfa.object.common.busitype.invalid", null);
            }
            LayoutComponentBuilder layoutComponentBuilder = new LayoutComponentBuilder(objectId, busiTypeAO.getId(), processorResult, aggregatorContext);
            layoutComponentBuilder.initService(pageLayoutQueryAggService, dependencyUtil, settingCurrencyService, entityUtil);
            layoutComponentBuilder.getPageLayout().validatePageLayout();
            if (processorResult.getStatusCode() != ProcessorConstants.SUCCESS) {
                return processorResult;
            }
            layoutComponentBuilder.validateLayoutComponent();
            if (processorResult.getStatusCode() != ProcessorConstants.SUCCESS) {
                return processorResult;
            }
            // 需要移除的item
            List<String> openCreate4Remove = new ArrayList<String>();
            openCreate4Remove.addAll(getItem4Remove());
            //手机端过滤掉的item
            if (aggregatorContext.getDevice().equals(AggregatorContext.DEVICE.MOBILE)) {
                openCreate4Remove.add(ItemConstants.ObjectCommonItem.entityType);
                openCreate4Remove.add(ItemConstants.ObjectCommonItem.ownerId);
            }
            List<String> readOnlyItem4Remain = new ArrayList<String>();
            readOnlyItem4Remain.add(ItemConstants.ObjectCommonItem.entityType);
            readOnlyItem4Remain.add(ItemConstants.ObjectCommonItem.ownerId);
            layoutComponentBuilder.filterLayoutComponent(openCreate4Remove, itemType4Remove, readOnlyItem4Remain);
            List<LayoutComponentAO> layoutComponentAOs = (List<LayoutComponentAO>) layoutComponentBuilder.getProcessorResult().get("layoutComponentList");
            //所有者的引用需要放出来
            ItemAO ownerRelationItem = itemQueryAggService.getByApiKey(ItemConstants.ObjectCommonItem.ownerId, objectId, aggregatorContext);
            for (Iterator<LayoutComponentAO> iterator = layoutComponentAOs.iterator(); iterator.hasNext(); ) {
                LayoutComponentAO layoutComponentAO = iterator.next();
                ItemAO itemAO = layoutComponentAO.getItemAO();
                //引用型字段
                if (itemAO.getItemType().equals(CustomizeConstant.ITEM_TYPE_JOIN)) {
                    //引用字段集中营,所属部门，相关实体，随时可能选值，所以字段都要放出来
                    if (DBCustomizeConstants.ENTITY_BELONG_USER.equals(itemAO.getJoinObject())) {
                        if (null != ownerRelationItem.getReferLinkId() && ownerRelationItem.getReferLinkId().equals(itemAO.getJoinLink())) {
                            continue;
                        }
                        iterator.remove();
                        continue;
                    }
                }
                //自己新建时候关联字段都能编辑，做为子的时候，主子明细字段不能编辑，关联字段不能编辑
                if (referItemId != null && referItemId.equals(itemAO.getId())) {
                    layoutComponentAO.setReadonly(true);
                }
            }
            layoutComponentBuilder.builderReferItemAOList().builderDependencyItemIdList().builderCurrencyUnit4Create();
            ObjectDataAO objectDataAO = new ObjectDataAO();
            //构建关联引用字段
            objectRelationDataBuilderUtil.builderObjectRelationData4AO(objectId, referRecordId, referObjectId, referItemId, null, objectDataAO, ObjectRelationDataBuilderUtil.BUILDER_4_CREATE, processorResult, aggregatorContext);
            this.setUserList(objectDataAO, processorResult, aggregatorContext);
            processorResult.put("entityType", busiTypeAO);
            processorResult.put("componentAOs", processorResult.get("layoutComponentList"));
            processorResult.put("objectDataAO", objectDataAO);
            processorResult.put("dimMap", dimensionProcessorUtil.getEntityDimMap(aggregatorContext.getUserId(), objectId, tenantParam));
            processorResult.put("mainDimMap", dimensionUtil.getMainDimensions(aggregatorContext.getUserId(), tenantParam));
            processorResult.put("sourceItemsMap", getSourceItemAOsMap(objectId, aggregatorContext));
            Map<Long, JSONObject> relationDataMap = (Map<Long, JSONObject>) processorResult.get("relationMap");
            JSONObject relationData = relationDataMap == null ? null : relationDataMap.get(referItemId);
            processorResult.put("relationData", relationData);
            processorResult.put("itemMapTypes", DBCustomizeUtil.itemTypeMapping);
            processorResult.put("dummyItemMap", DBCustomizeUtil.dummyItemMapping);
            //主子明细支持
            ObjectAO objectAO = objectQueryAggService.getPlusById(objectId, aggregatorContext);
            this.getAllChildEntity(processorResult, null, aggregatorContext.getUserId(), objectAO, aggregatorContext);
        } catch (AggregatorException e) {
            LOGGER.error(e.getMessage(), e);
            processorResult.setStatusCode(Integer.parseInt(e.getCode()));
            processorResult.setMessage(e.getMessage());
            processorResult.setDevelopErrorInfo(e.getMessage());
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            processorResult = this.businessError(processorResult, aggregatorContext.getLocale(), ProcessorConstants.ERROR_CODE_SYSTEM, "sfa.object.common.openCreateFailed", e.getMessage());

        }
        return processorResult;
    }

    private ProcessorResult getAllChildEntity(ProcessorResult processorResult, Long masterDataId, Long userId, ObjectAO objectAO, AggregatorContext aggregatorContext) throws AggregatorException {
        try {
            TenantParam tenantParam = aggregatorContext.getTenantParam();
            if (null != masterDataId) {
                processorResult = standardDetailBusinessService.getAllCustomizeChildEntitySettings(processorResult, masterDataId, userId, objectAO.ao2Dto(XObject.class), tenantParam, aggregatorContext);
            } else {
                processorResult = standardDetailBusinessService.getAllCustomizeChildEntitySettings(processorResult, userId, objectAO.ao2Dto(XObject.class), tenantParam);
            }
        } catch (AggregatorException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new AggregatorException(SYSTEM_ERROR, MultiLangResourceUtil.getResString(aggregatorContext.getLocale(), "sfa.object.common.getDetailFailed"), e);
        }
        return processorResult;
    }

    public ProcessorResult openUpdate(Long recordId, AggregatorContext aggregatorContext) throws ServiceException {
        ProcessorResult processorResult = new ProcessorResult();
        TenantParam tenantParam = aggregatorContext.getTenantParam();
        try {
            if (!dataPermissionManagerService.canUpdate(recordId, objectId, aggregatorContext.getUserId(), aggregatorContext.getTenantParam())) {
                LOGGER.error(appendErrorMsg("没权限修改当前数据", tenantParam.getTenantId()));
                return this.businessError(processorResult, aggregatorContext.getLocale(), ProcessorConstants.ERROR_CODE_NO_PERMISSION, "1002011", "没有数据权限");
            }
            ObjectDataAO objectDataAO = objectDataQueryAggService.getPlusWithoutPermission(recordId, objectId, aggregatorContext);
            PageLayoutAO pageLayoutAO = pageLayoutQueryAggService.getAvailableLayoutPlusFlowStatus4User(objectId, objectDataAO.getBusiType(), recordId, aggregatorContext);
            if (pageLayoutAO == null) {
                LOGGER.error(appendErrorMsg("布局没查到", tenantParam.getTenantId()));
                return businessError(processorResult, aggregatorContext.getLocale(), ProcessorConstants.ERROR_CODE_NULL_OBJECT, "sfa.object.common.openUpdateFailed", null);
            }
            List<LayoutComponentAO> layoutComponentAOs = pageLayoutAO.getComponentList();
            Boolean hasCurrencyUnit = false;
            //获取LayoutComponentAO，过滤不需要的字段
            for (Iterator<LayoutComponentAO> iterator = layoutComponentAOs.iterator(); iterator.hasNext(); ) {
                LayoutComponentAO layoutComponentAO = iterator.next();
                ItemAO itemAO = layoutComponentAO.getItemAO();
                if (itemAO == null || itemAO.getApiKey() == null || itemAO.getItemType() == null) {
                    iterator.remove();
                    continue;
                }
                if (layoutComponentAO.getComponentType().equals(CustomizeConstant.LAYOUT_COMPONENT_TYPE_SPLIT)) {
                    iterator.remove();
                    continue;
                }
                //手机端过滤掉业务类型
                if (ItemConstants.ObjectCommonItem.entityType.equals(itemAO.getApiKey()) && aggregatorContext.getDevice().equals(AggregatorContext.DEVICE.MOBILE)) {
                    iterator.remove();
                    continue;
                }
                if (CustomizeConstant.CURRENCY_UNIT.equals(itemAO.getApiKey())) {
                    hasCurrencyUnit = true;
                }
                //过滤不需要的字段
                if (this.canRemoveComponent(layoutComponentAO)) {
                    iterator.remove();
                    continue;
                }
            }
            //如果当前存在币种字段，那么返给前台币种、汇率的关系
            if (hasCurrencyUnit) {
                processorResult.put("unitCurrencyMap", entityUtil.getUnitCurrencyMap(tenantParam));
            }
            List<Long> dependencyItemIds = dependencyUtil.setDependentOptionNew(objectId, objectDataAO, layoutComponentAOs, aggregatorContext);
            //构建关联引用字段
            objectDataBusinessUtil.setReferObjects(false, objectId, null, objectDataAO, aggregatorContext, processorResult);
            this.setUserList(objectDataAO, processorResult, aggregatorContext);
            this.buildOtherDataResult(objectDataAO, processorResult, aggregatorContext);
            processorResult.put("dependencyItemIds", dependencyItemIds);
            BusiTypeAO busiTypeAO = busiTypeQueryAggService.getById(objectDataAO.getBusiType(), objectId, aggregatorContext);
            processorResult.put("entityType", busiTypeAO);
            processorResult.put("componentAOs", layoutComponentAOs);
            processorResult.put("dimMap", dimensionProcessorUtil.getEntityDimMap(aggregatorContext.getUserId(), objectId, tenantParam));
            processorResult.put("mainDimMap", dimensionUtil.getMainDimensions(aggregatorContext.getUserId(), tenantParam));
            processorResult.put("objectDataAO", objectDataAO);
            processorResult.put("sourceItemsMap", getSourceItemAOsMap(objectId, aggregatorContext));
            processorResult.put("itemMapTypes", DBCustomizeUtil.itemTypeMapping);
            processorResult.put("dummyItemMap", DBCustomizeUtil.dummyItemMapping);
            //主子明细支持
            ObjectAO objectAO = objectQueryAggService.getPlusById(objectId, aggregatorContext);
            this.getAllChildEntity(processorResult, objectDataAO.getId(), aggregatorContext.getUserId(), objectAO, aggregatorContext);
        } catch (AggregatorException e) {
            LOGGER.error(e.getMessage(), e);
            processorResult.setStatusCode(Integer.parseInt(e.getCode()));
            processorResult.setMessage(e.getMessage());
            processorResult.setDevelopErrorInfo(e.getMessage());
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            processorResult = this.businessError(processorResult, aggregatorContext.getLocale(), ProcessorConstants.ERROR_CODE_SYSTEM, "sfa.object.common.openUpdateFailed", e.getMessage());
        }
        return processorResult;
    }

    private boolean canRemoveComponent(LayoutComponentAO componentAO) {
        ItemAO itemAO = componentAO.getItemAO();
        //移除特殊字段
        if (getItem4Remove().contains(itemAO.getApiKey())) {
            return true;
        }
        //移除只读字段,但是不能动join类型的,另外有些特殊类型（业务类型）的线上保持原有逻辑。。
        if (componentAO.isReadonly() && ObjectConstant.ITEM_TYPE_JOIN != itemAO.getItemType()
                && !ItemConstants.ObjectCommonItem.entityType.equals(itemAO.getApiKey()) && !ItemConstants.ObjectCommonItem.ownerId.equals(itemAO.getApiKey())
                ) {
            return true;
        }
        //移除计算型字段和自动编号型字段
        if (itemAO.getItemType() != null && (itemAO.getItemType().equals(CustomizeConstant.ITEM_TYPE_FORMULA) ||
                itemAO.getItemType().equals(CustomizeConstant.ITEM_TYPE_AUTONUMBER))) {
            return true;
        }
        //移除本币字段
        if (Short.valueOf("2").equals(itemAO.getCurrencyPart())) {
            return true;
        }
        return false;
    }

    private void setUserList(ObjectDataAO objectDataAO, ProcessorResult processorResult, AggregatorContext aggregatorContext) throws Exception {
        Set<Long> userIdSet = new HashSet<Long>();
        if (aggregatorContext.getUserId() != null) {
            userIdSet.add(aggregatorContext.getUserId());
        }
        if (objectDataAO.getOwnerId() != null) {
            userIdSet.add(objectDataAO.getOwnerId());
        }
        if (objectDataAO.getCreatedBy() != null) {
            userIdSet.add(objectDataAO.getCreatedBy());
        }
        if (objectDataAO.getUpdatedBy() != null) {
            userIdSet.add(objectDataAO.getUpdatedBy());
        }
        Long applicantId = objectDataAO.retrieveLong(Contract.APPLICANT_ID);
        if (applicantId != null) {
            userIdSet.add(applicantId);
        }
        processorResult.put("userList", userService.userListByIdList(new ArrayList<Long>(userIdSet), aggregatorContext.getTenantParam()));
    }

    private void buildOtherDataResult(ObjectDataAO objectDataAO, ProcessorResult processorResult, AggregatorContext aggregatorContext) throws Exception {
        TenantParam tenantParam = aggregatorContext.getTenantParam();
        List<ObjectDataAO> dataAOs = new ArrayList<ObjectDataAO>();
        dataAOs.add(objectDataAO);
        entityProcessorUtil.setDimensionsByXObjectDataNew(processorResult, dataAOs, aggregatorContext.getTenantParam());
        User user = userService.get(aggregatorContext.getUserId(), tenantParam);
        processorResult.put("userFuncPermMap", dimensionProcessorUtil.getUserFunctions(user));
    }

    public ProcessorResult transfer(List<Long> recordIdList, Long targetId, AggregatorContext aggregatorContext) throws ServiceException {
        ProcessorResult processorResult = new ProcessorResult();
        try {
            List<ObjectDataAO> objectDataAOs = objectDataQueryAggService.getPlusByIdList(recordIdList, objectId, aggregatorContext);
            if (CollectionUtils.isEmpty(objectDataAOs)) {
                return businessError(processorResult, aggregatorContext.getLocale(), ProcessorConstants.ERROR_CODE_NULL_OBJECT, "sfa.object.common.transferFailed", "sfa.object.common.transferFailed");
            }
            List<Long> transIdList = new ArrayList<Long>();
            for (ObjectDataAO objectDataAO : objectDataAOs) {
                long oldOwnerId = objectDataAO.getOwnerId();
                if (targetId.equals(oldOwnerId)) {
                    continue;
                }
                if (dataPermissionManagerService.canTransfer(objectDataAO.getId(), objectId, aggregatorContext.getUserId(), aggregatorContext.getTenantParam())) {
                    transIdList.add(objectDataAO.getId());
                } else {
                    return businessError(processorResult, aggregatorContext.getLocale(), ProcessorConstants.ERROR_CODE_NO_PERMISSION, "1002011", objectDataAO.getName());
                }
            }
            if (!transIdList.isEmpty()) {
                BatchResult batchResult = objectDataOperateAggService.transferBatch(transIdList, targetId, objectId, aggregatorContext);
                if (CollectionUtils.isNotEmpty(batchResult.getFailureResultList())) {
                    processorResult.setStatusCode(ProcessorConstants.ERROR_CODE_PART_SUCCESS);
                    processorResult.setMessage(MessageUtils.getBatchFailureMessage(batchResult.getFailureResultList(), objectDataAOs, aggregatorContext.getLocale()));
                    processorResult.setDevelopErrorInfo(processorResult.getMessage());
                } else if (recordIdList.size() == 1) {
                    JSONObject data = new JSONObject();
                    if (dataPermissionManagerService.canView(recordIdList.get(0), objectId, aggregatorContext.getUserId(), aggregatorContext.getTenantParam())) {
                        data.put("permission", true);
                    } else {
                        data.put("permission", false);
                    }
                    processorResult.put("data", data);
                }
            }
        } catch (AggregatorException e) {
            LOGGER.error(e.getMessage(), e);
            processorResult.setStatusCode(Integer.parseInt(e.getCode()));
            processorResult.setMessage(e.getMessage());
            processorResult.setDevelopErrorInfo(e.getMessage());
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            processorResult = this.businessError(processorResult, aggregatorContext.getLocale(), ProcessorConstants.ERROR_CODE_SYSTEM, "sfa.object.common.transferFailed", e.getMessage());
        }
        return processorResult;
    }

    protected ObjectDataAO initDataForMobileDetail(Long recordId, AggregatorContext aggregatorContext) throws AggregatorException {
        if (recordId == null) {
            return null;
        }
        ObjectDataAO objectDataAO = objectDataQueryAggService.getPlus(recordId, objectId, aggregatorContext);
        if (objectDataAO == null || objectDataAO.retrieveShort("deleteFlg").equals(CommonConstancts.DeleteFlag.DELETED)) {
            return null;
        }
        return objectDataAO;
    }

    public ProcessorResult detailList(Long recordId, AggregatorContext aggregatorContext) throws ServiceException {
        ProcessorResult processorResult = new ProcessorResult();
        TenantParam tenantParam = aggregatorContext.getTenantParam();
        Long userId = aggregatorContext.getUserId();
        try {
            ObjectDataAO objectDataAO = initDataForMobileDetail(recordId, aggregatorContext);
            if (objectDataAO == null) {
                processorResult.setStatusCode(ProcessorConstants.ERROR_CODE_NULL_OBJECT);
                return processorResult;
            }
            Map<String, Boolean> dataPermissionAO = dataPermissionManagerService.getAllDataPermissions(recordId, objectId, aggregatorContext.getUserId(), aggregatorContext.getTenantParam());
            if (!dataPermissionAO.get(DataPermissionConstant.OPERATE_VIEW)) {
                processorResult.setStatusCode(ProcessorConstants.ERROR_CODE_NO_PERMISSION);
                return processorResult;
            }
            PageLayoutAO pageLayoutAO = pageLayoutQueryAggService.getAvailableLayoutPlus4User(objectDataAO.getObjectId(), objectDataAO.getBusiType(), aggregatorContext);
            Map<String, String> checks = new HashMap<String, String>();
            //取出来属性判断，根据金额的属性来判断，如果是实数类型，不展示单位，如果是单币或者本币，展示本币单位；如果是多币类型，展示原币单位
            String currencyUnit = entityUtil.getCurrencyUnit(tenantParam);
            Map<Long, String> itemIdUnitMap = new HashMap<Long, String>();
            Map<Integer, String> unitSetCurrencyMap = entityUtil.getUnitSetCurrencyMapNew(tenantParam);
            List<ItemAO> relationItemAos = new ArrayList<ItemAO>();
            Map<String, ItemAO> joinItemMap = new HashMap<String, ItemAO>();
            for (Iterator<LayoutComponentAO> iterator = pageLayoutAO.getComponentList().iterator(); iterator.hasNext(); ) {
                LayoutComponentAO _component = iterator.next();
                ItemAO item = _component.getItemAO();
                if (item == null) {
                    continue;
                } else if (DBCustomizeConstants.ITEM_TYPE_CHECKBOX.equals(item.getItemType())) {
                    StringBuilder sb = new StringBuilder();
                    Integer[] tempArray = objectDataAO.retrieveCheckBox(item.getApiKey());
                    if (tempArray != null && tempArray.length > 0) {
                        for (int i : tempArray) {
                            sb.append(i).append(",");
                        }
                    }
                    if (sb.length() == 0) {
                        checks.put(item.getApiKey(), "");
                    } else {
                        checks.put(item.getApiKey(), sb.substring(0, sb.length() - 1));
                    }
                } else if (item.getItemType().equals(ObjectConstant.ITEM_TYPE_REFER) && !item.isDetail()
                        && !item.getApiKey().equals(ItemConstants.LeadItem.dimDepart)
                        && !item.getApiKey().equals(ItemConstants.LeadItem.dimArea)
                        && !item.getApiKey().equals(ItemConstants.LeadItem.dimBusiness)
                        && !item.getApiKey().equals(ItemConstants.LeadItem.dimIndustry)
                        && !item.getApiKey().equals(ItemConstants.LeadItem.dimProduct)) {
                    relationItemAos.add(item);
                } else if (item.getItemType().equals(ObjectConstant.ITEM_TYPE_JOIN)) {
                    joinItemMap.put(item.getApiKey(), item);
                }
                if (item.getApiKey().equals(Contract.APPROVAL_STATUS) || item.getApiKey().equals(Contract.APPLICANT_ID)) {
                    List appovallist = approvalInstanceBindingPojoService.getApprovalInstanceBindingByBillIdFlowType(objectId, recordId, "approval", aggregatorContext.getTenantParam());
                    if ((null == appovallist) || (appovallist.size() == 0)) {
                        iterator.remove();
                        continue;
                    }
                }
                //为itemAO拼接多币种单位
                String currencyUnitAmount = entityUtil.getCurrencyUnitAmount(item, objectDataAO, currencyUnit, unitSetCurrencyMap);
                itemIdUnitMap.put(item.getId(), currencyUnitAmount);
            }
            // 相关对象 赋值
            objectDataBusinessUtil.setRelationObjectShowValue(false, objectId, relationItemAos, joinItemMap, objectDataAO, processorResult, aggregatorContext);
            processorResult.put("canUpdate", dataPermissionAO.get(DataPermissionConstant.OPERATE_UPDATE));
            processorResult.put("compList", pageLayoutAO.getComponentList());
            processorResult.put("viewItemTypes", DBCustomizeUtil.itemTypeMapping);
            processorResult.put("values", objectDataAO.getDataMap());
            processorResult.put("checks", checks);
            processorResult.put("dimMap", dimensionProcessorUtil.getEntityDimMap(userId, objectId, tenantParam));
            processorResult.put("currencyUnit", currencyUnit);
            processorResult.put("itemIdUnitMap", itemIdUnitMap);
            processorResult.put("sourceItemsMap", getSourceItemAOsMap(objectId, aggregatorContext));
        } catch (AggregatorException e) {
            Object[] errorLogParam = new Object[]{aggregatorContext.getTenantParam().getTenantId(), aggregatorContext.getUserId(), e.getMessage()};
            LOGGER.error("[detailList] tenantId:{},userId:{},e:{}", errorLogParam);
            processorResult.setStatusCode(Integer.parseInt(e.getCode()));
            processorResult.setDevelopErrorInfo(e.getMessage());
            processorResult.setMessage(e.getMessage());
        } catch (CustomizeException e) {
            Object[] errorLogParam = new Object[]{aggregatorContext.getTenantParam().getTenantId(), aggregatorContext.getUserId(), e.getMessage()};
            LOGGER.error("[detailList] tenantId:{},userId:{},e:{}", errorLogParam);
            processorResult.setStatusCode(ProcessorConstants.BUSINESS_EXCEPTION);
            processorResult.setDevelopErrorInfo(e.getMessage());
            processorResult.setMessage(e.getMessage());
        } catch (PaasException e) {
            Object[] errorLogParam = new Object[]{aggregatorContext.getTenantParam().getTenantId(), aggregatorContext.getUserId(), e.getMessage()};
            LOGGER.error("[detailList] tenantId:{},userId:{},e:{}", errorLogParam);
            processorResult.setStatusCode(ProcessorConstants.BUSINESS_EXCEPTION);
            processorResult.setDevelopErrorInfo(e.getMessage());
            processorResult.setMessage(e.getMessage());
        } catch (BusinessException e) {
            Object[] errorLogParam = new Object[]{aggregatorContext.getTenantParam().getTenantId(), aggregatorContext.getUserId(), e.getMessage()};
            LOGGER.error("[detailList] tenantId:{},userId:{},e:{}", errorLogParam);
            processorResult.setStatusCode(ProcessorConstants.BUSINESS_EXCEPTION);
            processorResult.setDevelopErrorInfo(e.getMessage());
            processorResult.setMessage(e.getMessage());
        }
        return processorResult;
    }

    public ProcessorResult detailOthers(Long recordId, AggregatorContext aggregatorContext) throws ServiceException {
        ProcessorResult processorResult = new ProcessorResult();
        TenantParam tenantParam = aggregatorContext.getTenantParam();
        Long userId = aggregatorContext.getUserId();
        try {
            ObjectDataAO objectDataAO = initDataForMobileDetail(recordId, aggregatorContext);
            if (objectDataAO == null) {
                Object[] errorLogParam = new Object[]{tenantParam.getTenantId(), userId, MultiLangResourceUtil.getResString(aggregatorContext.getLocale(), "sfa.object.common.operateFailed")};
                LOGGER.error("[detailOthers] tenantId:{},userId:{},e:{}", errorLogParam);
                processorResult.setStatusCode(ProcessorConstants.ERROR_CODE_NULL_OBJECT);
                processorResult.setMessage(MultiLangResourceUtil.getResString(aggregatorContext.getLocale(), "sfa.object.common.operateFailed"));
                processorResult.setDevelopErrorInfo(MultiLangResourceUtil.getResString(aggregatorContext.getLocale(), "sfa.object.common.operateFailed"));
                return processorResult;
            }
            Map<String, Boolean> dataPermissionAO = dataPermissionManagerService.getAllDataPermissions(recordId, objectId, aggregatorContext.getUserId(), aggregatorContext.getTenantParam());
            if (!dataPermissionAO.get(DataPermissionConstant.OPERATE_VIEW)) {
                Object[] errorLogParam = new Object[]{tenantParam.getTenantId(), userId, MultiLangResourceUtil.getResString(aggregatorContext.getLocale(), "1002011")};
                LOGGER.error("[detailOthers] tenantId:{},userId:{},e:{}", errorLogParam);
                processorResult.setStatusCode(ProcessorConstants.ERROR_CODE_NO_PERMISSION);
                processorResult.setMessage(MultiLangResourceUtil.getResString(aggregatorContext.getLocale(), "1002011"));
                processorResult.setDevelopErrorInfo(MultiLangResourceUtil.getResString(aggregatorContext.getLocale(), "1002011"));
                return processorResult;
            }

            Long configurableProduct=objectDataAO.retrieveLong(ItemConstants.ProductFeatureItem.configurableProduct);
            if (null!=configurableProduct) {
                ObjectDataAO productDataAO=objectDataQueryAggService.getPlus(configurableProduct,DBCustomizeConstants.ENTITY_BELONG_PRODUCT,aggregatorContext);
                processorResult.put("productDataAO", productDataAO);
            }

            ObjectAO productFeatureAO=objectQueryAggService.getPlusById(ObjectConstants.OBJECT_ID_PRODUCT_FEATURE,aggregatorContext);
            processorResult.put("productFeatureAO", productFeatureAO);

            List<ItemAO> enableItems = itemQueryAggService.getEnableItems(objectId, aggregatorContext);
            for (Iterator<ItemAO> iterator = enableItems.iterator(); iterator.hasNext(); ) {
                ItemAO item = iterator.next();
                if (!Contract.START_DATE.equals(item.getApiKey()) &&
                        !Contract.END_DATE.equals(item.getApiKey()) &&
                        !Contract.STATUS.equals(item.getApiKey())) {
                    iterator.remove();
                }
            }
            processorResult.put("enableItems", enableItems);
            processorResult.put("viewItemTypes", DBCustomizeUtil.itemTypeMapping);
            processorResult.put("values", objectDataAO.getDataMap());
            processorResult.put("group", groupService.get(objectDataAO.getGroupId(), tenantParam));
            StringBuilder admins = new StringBuilder();
            for (Long id : groupMemberService.getGroupAdmins(objectDataAO.getGroupId(), tenantParam)) {
                admins.append(id).append(",");
            }
            processorResult.put("admins", admins.length() > 0 ? admins.substring(0, admins.length() - 1) : admins.toString());
            processorResult.put("owner", userService.get(objectDataAO.getOwnerId(), tenantParam));
            processorResult.put("isFollow", groupFollowService.isGroupFollow(userId, objectDataAO.getGroupId(), tenantParam));
            //负责员工和相关员工
            List<Long> ownerMemberIds = new ArrayList<Long>();
            List<Long> memberIds = new ArrayList<Long>();
            List<GroupMember> groupMemberList = groupMemberService.getMembersByGroupAndStatus(objectDataAO.getGroupId(), GROUP_MEMBER_STATUS_NORMAL, tenantParam);
            for (GroupMember groupMember : groupMemberList) {
                //过滤掉负责人
                if (groupMember.getMemberAttr() == 1) {
                    continue;
                }
                if (groupMember.getOwnerFlag() == 1) {
                    ownerMemberIds.add(groupMember.getUserId());
                } else {
                    memberIds.add(groupMember.getUserId());
                }
            }
            processorResult.put("ownerMembers", userService.getActiveUserListByIdList(ownerMemberIds, tenantParam));
            processorResult.put("members", userService.getActiveUserListByIdList(memberIds, tenantParam));
            User user = objectDataBusinessUtil.getUser(aggregatorContext);
            Map<String, Object> paramMap = new HashMap<String, Object>();
            paramMap.put("user", user);
            processorResult.put("objectItems", objectDataBusinessUtil.getAllReferObjectsForContract(objectDataAO, paramMap, aggregatorContext));
            List<BusiTypeAO> busiTypes = busiTypeQueryAggService.getAllBusiTypes(objectId, aggregatorContext);
            if (CollectionUtils.isNotEmpty(busiTypes)) {
                processorResult.put("types", busiTypes);
            }
            BusiTypeAO entityBelongType = busiTypeQueryAggService.getById(objectDataAO.getBusiType(), objectId, aggregatorContext);
            if (entityBelongType != null) {
                processorResult.put("entityTypeName", entityBelongType.getLabel());
                processorResult.put("entityTypeNameResourceKey", entityBelongType.getLabelKey());
            }
            processorResult.put("belongId", objectId);
            processorResult.put("canUpdate", dataPermissionAO.get(DataPermissionConstant.OPERATE_UPDATE));
            processorResult.put("canLock", dataPermissionAO.get(DataPermissionConstant.OPERATE_LOCK));//锁定
            processorResult.put("canUnLock", dataPermissionAO.get(DataPermissionConstant.OPERATE_UNLOCK));//解锁
            Integer lockStatusData = objectDataAO.retrieveInteger(CustomizeConstant.SYSTEM_ITEM_LOCK_STATUS);
            boolean isLocked = lockStatusData != null && CustomizeConstant.STATUS_LOCK.equals(lockStatusData);
            processorResult.put("isLocked", isLocked);//当前锁定状态
            processorResult.put("canTransfer", dataPermissionAO.get(DataPermissionConstant.OPERATE_TRANSFER));
            processorResult.put("canDelete", dataPermissionAO.get(DataPermissionConstant.OPERATE_DELETE) && !isLocked);
            processorResult.put("isShowApproval", dimensionProcessorUtil.getUserFunctions(user).get("approval").get("m"));
            ProcessorResult approvalBusinessProcessorResult = approvalBusinessService.crmMobileDetail(objectDataAO.getObjectId(), objectDataAO.getId(), aggregatorContext);
            processorResult.getData().putAll(approvalBusinessProcessorResult.getData());
            processorResult.put("taskidApproval", processorResult.get("taskid"));
            ProcessorResult workflowBusinessProcessorResult = workflowBusinessService.mobileWorkflowDetail(objectDataAO.getObjectId(), objectDataAO.getId(), aggregatorContext);
            processorResult.getData().putAll(workflowBusinessProcessorResult.getData());
            //begin 详情页头部展示商机金额的货币单位：给手机端用
            //取出来属性判断，根据金额的属性来判断，如果是实数类型，不展示单位，如果是单币或者本币，展示本币单位；如果是多币类型，展示原币单位
            String currencyUnit = entityUtil.getCurrencyUnit(tenantParam);
            ItemAO moneyItem = itemQueryAggService.getByApiKey(ItemConstants.OpportunityItem.MONEY, DBCustomizeConstants.ENTITY_BELONG_OPPORTUNITY, aggregatorContext);
            String currencyUnitAmount = currencyUnit;
            Map<Integer, String> unitSetCurrencyMap = entityUtil.getUnitSetCurrencyMapNew(tenantParam);
            if (moneyItem != null) {
                if (moneyItem.isCurrency()) {
                    if (moneyItem.isMultiCurrency() && moneyItem.getCurrencyPart() == 1) {
                        if (objectDataAO.retrieveInteger(CustomizeConstant.CURRENCY_UNIT) != null) {
                            if (unitSetCurrencyMap != null && unitSetCurrencyMap.containsKey(Integer.valueOf(objectDataAO.retrieveInteger(CustomizeConstant.CURRENCY_UNIT).toString()))) {
                                currencyUnitAmount = unitSetCurrencyMap.get(Integer.valueOf(objectDataAO.retrieveInteger(CustomizeConstant.CURRENCY_UNIT).toString()));
                            }
                        }
                    }
                } else {
                    currencyUnitAmount = "";
                }
            }
            processorResult.put("currencyUnit", currencyUnit);
            processorResult.put("currencyUnitAmount", currencyUnitAmount);
        } catch (AggregatorException e) {
            Object[] errorLogParam = new Object[]{aggregatorContext.getTenantParam().getTenantId(), aggregatorContext.getUserId(), e.getMessage()};
            LOGGER.error("[detailOthers] tenantId:{},userId:{},e:{}", errorLogParam);
            processorResult.setStatusCode(Integer.parseInt(e.getCode()));
            processorResult.setDevelopErrorInfo(e.getMessage());
            processorResult.setMessage(e.getMessage());
        } catch (CustomizeException e) {
            Object[] errorLogParam = new Object[]{aggregatorContext.getTenantParam().getTenantId(), aggregatorContext.getUserId(), e.getMessage()};
            LOGGER.error("[detailOthers] tenantId:{},userId:{},e:{}", errorLogParam);
            processorResult.setStatusCode(Integer.parseInt(e.getCode()));
            processorResult.setDevelopErrorInfo(e.getMessage());
            processorResult.setMessage(e.getMessage());
        } catch (PaasException e) {
            Object[] errorLogParam = new Object[]{aggregatorContext.getTenantParam().getTenantId(), aggregatorContext.getUserId(), e.getMessage()};
            LOGGER.error("[detailOthers] tenantId:{},userId:{},e:{}", errorLogParam);
            processorResult.setStatusCode(Integer.parseInt(e.getCode()));
            processorResult.setDevelopErrorInfo(e.getMessage());
            processorResult.setMessage(e.getMessage());
        } catch (BusinessException e) {
            Object[] errorLogParam = new Object[]{aggregatorContext.getTenantParam().getTenantId(), aggregatorContext.getUserId(), e.getMessage()};
            LOGGER.error("[detailOthers] tenantId:{},userId:{},e:{}", errorLogParam);
            processorResult.setStatusCode(Integer.parseInt(e.getCode()));
            processorResult.setDevelopErrorInfo(e.getMessage());
            processorResult.setMessage(e.getMessage());
        }
        return processorResult;
    }

    private boolean canOperate(String operateType, Long contractId, AggregatorContext aggregatorContext) throws ServiceException {
        try {
            if (operateType != null && operateType.equals("canTransfer")) {
                return dataPermissionManagerService.canTransfer(contractId, objectId, aggregatorContext.getUserId(), aggregatorContext.getTenantParam());
            } else if (operateType != null && operateType.equals("canUpdate")) {
                return dataPermissionManagerService.canUpdate(contractId, objectId, aggregatorContext.getUserId(), aggregatorContext.getTenantParam());
            } else if (operateType != null && operateType.equals("canDelete")) {
                return dataPermissionManagerService.canDelete(contractId, objectId, aggregatorContext.getUserId(), aggregatorContext.getTenantParam());
            } else {
                return false;
            }
        } catch (PaasException e) {
            Object[] errorLogParam = new Object[]{aggregatorContext.getTenantParam().getTenantId(), aggregatorContext.getUserId(), e.getMessage()};
            LOGGER.error("[ContractBusinessServiceImpl.canOperate] tenantId:{},userId:{},e:{}", errorLogParam);
        } catch (BusinessException e) {
            Object[] errorLogParam = new Object[]{aggregatorContext.getTenantParam().getTenantId(), aggregatorContext.getUserId(), e.getMessage()};
            LOGGER.error("[ContractBusinessServiceImpl.canOperate] tenantId:{},userId:{},e:{}", errorLogParam);
        }
        return false;
    }




    private String appendErrorMsg(Object... appendMsg) {
        return StringUtils.join(appendMsg, ",");
    }

    private ProcessorResult businessError(ProcessorResult processorResult, Locale locale, int businessErrorCode, String msgResourceId, String developInfo) {
        processorResult.setStatusCode(businessErrorCode);
        String message = MultiLangResourceUtil.getResString(locale, msgResourceId);
        if (null == message || message.isEmpty()) {
            LOGGER.error("locale:" + locale + "_msgResourceId:" + msgResourceId + "_can not get newMsg,so get that from old file");
            message = ResourceUtil.getResString(locale, msgResourceId);
        }
        processorResult.setMessage(message);
        if (null == developInfo || developInfo.isEmpty()) {
            processorResult.setDevelopErrorInfo(message);
        } else {
            processorResult.setDevelopErrorInfo(developInfo);
        }
        return processorResult;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public void setEntityUtil(EntityUtil entityUtil) {
        this.entityUtil = entityUtil;
    }

    public void setDataPermissionManagerService(DataPermissionManagerService dataPermissionManagerService) {
        this.dataPermissionManagerService = dataPermissionManagerService;
    }

    public void setGroupFollowService(GroupFollowService groupFollowService) {
        this.groupFollowService = groupFollowService;
    }

    public void setGroupMemberService(GroupMemberService groupMemberService) {
        this.groupMemberService = groupMemberService;
    }

    public void setObjectQueryAggService(ObjectQueryAggService objectQueryAggService) {
        this.objectQueryAggService = objectQueryAggService;
    }

    public void setEntityFileService(EntityFileService entityFileService) {
        this.entityFileService = entityFileService;
    }

    public void setApprovalProcessorUtil(ApprovalProcessorUtil approvalProcessorUtil) {
        this.approvalProcessorUtil = approvalProcessorUtil;
    }

    public void setCustomDataProcessorUtil(CustomDataProcessorUtil customDataProcessorUtil) {
        this.customDataProcessorUtil = customDataProcessorUtil;
    }

    public void setObjectDataQueryAggService(ObjectDataQueryAggService objectDataQueryAggService) {
        this.objectDataQueryAggService = objectDataQueryAggService;
    }

    public void setObjectDataOperateAggService(ObjectDataOperateAggService objectDataOperateAggService) {
        this.objectDataOperateAggService = objectDataOperateAggService;
    }

    public void setBusiTypeQueryAggService(BusiTypeQueryAggService busiTypeQueryAggService) {
        this.busiTypeQueryAggService = busiTypeQueryAggService;
    }

    public void setPageLayoutQueryAggService(PageLayoutQueryAggService pageLayoutQueryAggService) {
        this.pageLayoutQueryAggService = pageLayoutQueryAggService;
    }

    public void setDependencyUtil(DependencyUtil dependencyUtil) {
        this.dependencyUtil = dependencyUtil;
    }

    public void setObjectDataBusinessUtil(ObjectDataBusinessUtil objectDataBusinessUtil) {
        this.objectDataBusinessUtil = objectDataBusinessUtil;
    }

    public void setDimensionProcessorUtil(DimensionProcessorUtil dimensionProcessorUtil) {
        this.dimensionProcessorUtil = dimensionProcessorUtil;
    }

    public void setDimensionUtil(DimensionUtil dimensionUtil) {
        this.dimensionUtil = dimensionUtil;
    }

    public void setEntityProcessorUtil(EntityProcessorUtil entityProcessorUtil) {
        this.entityProcessorUtil = entityProcessorUtil;
    }

    public void setStandardDetailBusinessService(StandardDetailBusinessService standardDetailBusinessService) {
        this.standardDetailBusinessService = standardDetailBusinessService;
    }

    public void setApprovalInstanceBindingPojoService(ApprovalInstanceBindingPojoService approvalInstanceBindingPojoService) {
        this.approvalInstanceBindingPojoService = approvalInstanceBindingPojoService;
    }

    public void setGroupService(GroupService groupService) {
        this.groupService = groupService;
    }

    public void setApprovalBusinessService(ApprovalBusinessService approvalBusinessService) {
        this.approvalBusinessService = approvalBusinessService;
    }

    public void setWorkflowBusinessService(WorkflowBusinessService workflowBusinessService) {
        this.workflowBusinessService = workflowBusinessService;
    }

    public void setObjectDataAOBuilderUtil(ObjectDataAOBuilderUtil objectDataAOBuilderUtil) {
        this.objectDataAOBuilderUtil = objectDataAOBuilderUtil;
    }

    public void setItemQueryAggService(ItemQueryAggService itemQueryAggService) {
        this.itemQueryAggService = itemQueryAggService;
    }

    @Override
    public void setSettingCurrencyService(SettingCurrencyService settingCurrencyService) {
        this.settingCurrencyService = settingCurrencyService;
    }

    public void setObjectRelationDataBuilderUtil(ObjectRelationDataBuilderUtil objectRelationDataBuilderUtil) {
        this.objectRelationDataBuilderUtil = objectRelationDataBuilderUtil;
    }
}
