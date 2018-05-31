package com.rkhd.ienterprise.apps.salescloud.customeraccountsetting.proxy;

import com.alibaba.fastjson.JSONObject;
import com.rkhd.ienterprise.apps.ingage.restresult.MultiResult;
import com.rkhd.ienterprise.apps.ingage.restresult.SingleResult;
import com.rkhd.ienterprise.apps.salescloud.base.proxy.SalescloudBaseClientProxy;
import com.rkhd.xsy.client.FeignClient;
import feign.Headers;
import feign.Param;
import feign.RequestLine;


@FeignClient("apps-salescloud-service")
public interface CustomerAccountSettingClientProxy extends SalescloudBaseClientProxy{

}


