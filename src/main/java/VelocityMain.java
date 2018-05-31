import ch.qos.logback.access.filter.StatisticalView;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import utils.FileUtils;
import utils.VelocityUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by fangjun on 2018/5/8.
 */
public class VelocityMain {
    //实体相关信息
    private static String entityApikey = "customerAccountSetting";
    private static String entityName = "账户配置";
    private static String constant_object_id = "ObjectConstants.OBJECT_ID_CUSTOMER_ACCOUNT_SETTING";  //实体id常量
    private static String constant_object_name = "ObjectConstants.OBJECT_NAME_CUSTOMER_ACCOUNT_SETTING"; //实体apiKey常量
    private static String constant_object_id_String = "ObjectConstants.OBJECT_ID_CUSTOMER_ACCOUNT_SETTING_STRING"; //实体id常量(String类型)
    private static String crm_url = "crm_531";


    //apps-manager-service的相关路径
    private static String servicePackage = "com.rkhd.business.crm.customeraccountsetting.service";
    private static String componentPackage = "com.rkhd.ienterprise.apps.manager.customize.component.customeraccountsetting";

    //crm项目的相关路径
    private static String crmActionPackage = "com.rkhd.ienterprise.apps.ingage.crm.customeraccountsetting.action";
    private static String crmSalesCloudProxyPackage = "com.rkhd.ienterprise.apps.salescloud.customeraccountsetting.proxy";

    //salescloud的相关路径
    private static String salesCloudPackage = "com.rkhd.ienterprise.apps.salescloud.customeraccountsetting";

    public static void main(String[] args){

        FileUtils.delFolder(getDestBaseUrl());
        VelocityUtils.getInstance().setFileProperties(getSourceBaseUrl());
        Map map = assembeParam();

        try{
            String entityName_U = entityApikey.substring(0,1).toUpperCase()+entityApikey.substring(1);
            generateJavaDir("\\java\\crm\\action","crm",crmActionPackage, map);
            generateJavaDir("\\java\\crm\\salescloud","crm",crmSalesCloudProxyPackage, map);
            generateJavaDir("\\java\\dependency\\service","dependency",servicePackage, map);
            generateJavaDir("\\java\\manager\\service","manager",servicePackage,map);
            generateJavaDir("\\java\\manager\\component","manager",componentPackage, map);
            generateJavaDir("\\java\\salescloud","salescloud",salesCloudPackage, map);

            generateConfDir("\\xml\\crm","crm", map,"xml");
            generateConfDir("\\xml\\manager","manager",map,"xml");

            generateConfDir("\\composite\\crm","crm", map,"composite");
            generateConfDir("\\composite\\manager","manager", map,"composite");



        }catch (Exception e){
            e.printStackTrace();;
        }
    }



    public static String getDestUrl(String baseUrl,String packUrl,String baseDestUrl,String split){

        baseDestUrl = baseDestUrl+baseUrl+"\\";
        String[] packUrl_  = null;
        if(".".equals(split)){
           packUrl_ = packUrl.split("\\.");
        }else if("\\".equals(split)){
            packUrl_ = packUrl.split("\\\\");
        }else{
            packUrl_ = packUrl.split(split);
        }
        for(String s : packUrl_){
            baseDestUrl = baseDestUrl+s+"\\";
        }
        return baseDestUrl;
    }

    private static Map assembeParam(){
        Map map = new HashMap();
        String entityName_U = entityApikey.substring(0,1).toUpperCase()+entityApikey.substring(1);
        String entityName_L = entityApikey.toLowerCase();
        map.put("entity",entityApikey);
        map.put("entityName",entityName);
        map.put("entity_U",entityName_U);
        map.put("entity_L",entityApikey.toLowerCase());
        map.put("constant_object_id",constant_object_id);
        map.put("constant_object_name",constant_object_name);
        map.put("constant_object_id_String",constant_object_id_String);
        map.put("crm_url",crm_url);

        map.put("servicePackage",servicePackage);
        map.put("componentPackage",componentPackage);
        map.put("crmActionPackage",crmActionPackage);
        map.put("crmSalesCloudProxyPackage",crmSalesCloudProxyPackage);
        map.put("salesCloudPackage",salesCloudPackage);
        return map;
    }

    private static void generateOtherDir(String sourcedir,String projectName, String packageName,Map param,String suffix,String split)throws FileNotFoundException, UnsupportedEncodingException{

        generateDir(sourcedir,projectName,"","",param,suffix,split);
    }

    private static void generateConfDir(String sourcedir,String projectName, Map param,String suffix)throws FileNotFoundException, UnsupportedEncodingException{

        generateDir(sourcedir,projectName,"","",param,suffix,".");
    }

    private static void generateJavaDir(String sourcedir,String projectName,String packageName, Map param)throws FileNotFoundException, UnsupportedEncodingException{
        String entityName_U = entityApikey.substring(0,1).toUpperCase()+entityApikey.substring(1);
        generateDir(sourcedir,projectName,packageName,entityName_U,param,"java",".");
    }

    private static void generateDir(String sourcedir,String projectName,String packageName,String filePrefix, Map param,String suffix,String split) throws FileNotFoundException, UnsupportedEncodingException {
        File files = new File(getSourceBaseUrl()+sourcedir);
        recursionGenerate(files,sourcedir,getDestUrl(projectName,packageName,getDestBaseUrl(),split),param,filePrefix,suffix);
    }

    private static String getSourceBaseUrl(){
        return System.getProperty("user.dir")+"\\src\\main\\resources\\META-INF\\velocity\\salescloud\\source\\";
    }

    private static String getDestBaseUrl(){
        return System.getProperty("user.dir")+"\\src\\main\\resources\\META-INF\\velocity\\salescloud\\dest\\";
    }
    private static void recursionGenerate(File files,String sourcedir, String destdir, Map param,String entityU,String suffix) throws FileNotFoundException, UnsupportedEncodingException {
        if(files.isDirectory()){
            for(File file : files.listFiles()){
                if(file.isDirectory()){
                    String destdir_c = destdir;
                    destdir_c = generateDestDir(destdir_c,file.getName(),entityU);
                    recursionGenerate(file,sourcedir+"\\"+file.getName(),destdir_c,param,entityU,suffix);
                }else{
                    generateFile(file,sourcedir,destdir,param,entityU,suffix);
                }
            }
        }
    }
    private static String generateDestDir(String destdir,String fileName,String entityU) {

        if("salescloud".equals(fileName)||"dependency".equals(fileName)||"manager".equals(fileName)){
            return destdir+"\\"+fileName+"\\"+entityU.toLowerCase();
        }
        if("action".equals(fileName)){
            return destdir+"\\"+entityU.toLowerCase()+"\\"+fileName;
        }
        return destdir+"\\"+fileName;
    }


    private static void  generateFile(File file,String sourcedir, String destdir, Map param,String entityU,String suffix) throws FileNotFoundException, UnsupportedEncodingException {

        if(file.isDirectory() ||file.getName().indexOf(".vm")<0)return;
        String destName = entityU+file.getName().substring(0,file.getName().indexOf("."));
        VelocityUtils.getInstance().generateFile(sourcedir+"\\"+file.getName(),destdir,destName,suffix,param);

    }

}
