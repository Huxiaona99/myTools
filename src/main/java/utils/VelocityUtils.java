package utils;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.view.velocity.VelocityConfigurer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Properties;

/**
 * Created by fangjun on 2018/2/13.
 */
public class VelocityUtils {
    private Logger logger = LoggerFactory.getLogger(VelocityUtils.class);
    private static VelocityUtils utils = null;
    private VelocityEngine engine;
    private Properties properties = new Properties();
    private VelocityUtils(){
        engine =  new VelocityEngine();
    }
    public  static synchronized VelocityUtils getInstance(){
        if(utils == null){
            utils = new VelocityUtils();
        }
        return utils;
    }

   public void setFileProperties(String templateUrl){
       //设置velocity资源加载方式为file
       properties.setProperty("resource.loader", "file");
       //设置velocity资源加载方式为file时的处理类
       properties.setProperty("file.resource.loader.class","org.apache.velocity.runtime.resource.loader.FileResourceLoader");
       properties.put("input.encoding", "UTF-8");
       properties.put("output.encoding", "UTF-8");
       properties.put(VelocityEngine.FILE_RESOURCE_LOADER_PATH, templateUrl);
       engine.init(properties);
   }

    public VelocityEngine getEngine(){
        return engine;
    }

    public Template getTemplate(String templateName){
        if(StringUtils.isEmpty(templateName)){
            return null;
        }
        return engine.getTemplate(templateName);
    }

    private String getTemplateUrl(String templateName){
        if(!templateName.endsWith(".vm")){
            templateName += ".vm";
        }
        String path =  (String)engine.getProperty(RuntimeConstants.FILE_RESOURCE_LOADER_PATH);
        path = (path.endsWith("/")|| path.endsWith("\\")) ? path : path+"/";
        return path+templateName;
    }
    public String generateXmlFile(String templateName, String destUrl, String destName, Map<String,Object> params)throws FileNotFoundException, UnsupportedEncodingException{
        return generateFile(templateName,destUrl,destName,"xml",params);
    }
    public String generateSQLFile(String templateName, String destUrl, String destName, Map<String,Object> params)throws FileNotFoundException, UnsupportedEncodingException{
        return generateFile(templateName,destUrl,destName,"sql",params);
    }

    public String generateJavaFile(String templateName, String destUrl, String destName, Map<String,Object> params)throws FileNotFoundException, UnsupportedEncodingException{
        return generateFile(templateName,destUrl,destName,"java",params);
    }

    public String generateFile(String templateName, String destUrl,String destName, String suffix, Map<String,Object> params) throws FileNotFoundException, UnsupportedEncodingException {
        Template template = getTemplate(templateName);
        if(template == null){
            return null;
        }
        destUrl=(destUrl.endsWith("/")|| destUrl.endsWith("\\")) ? destUrl : destUrl+"\\";
        FileUtils.createDir(destUrl);
        destName = destUrl+destName+"."+suffix;
        //1.数据填入上下文
        VelocityContext root = new VelocityContext();
        if(params !=null && !params.isEmpty()){
            for(Map.Entry<String,Object> entry : params.entrySet()){
                root.put(entry.getKey(),entry.getValue());
            }
        }
        //2.生成文件
        PrintWriter wt = null;
        try{
            wt = new PrintWriter(destName, "UTF-8");
            template.merge(root, wt);
            wt.flush();
        }catch(Exception e){
            logger.error("方法[VelocityUtils.generateFile]报错,destUrl:{},entity:{}",destName,params ,e);
            throw e;
        }finally {
            IOUtils.closeQuietly(wt);
            wt = null;
        }
        return templateName;
    }


}
