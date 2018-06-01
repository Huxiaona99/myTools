package excel;

import excel.util.ExcelUtils;
import zhconvertor.util.ZHConverter;

import java.util.List;

/**
 * Created by fangjun on 2018/5/31.
 */
public class ExcelMain {
    public static void main(String[] args){
        //String excelUrl = "E:\\workspace\\InitSalesCloud\\src\\main\\resources\\test1.xls";
        String excelUrl = "E:\\workspace\\InitSalesCloud\\src\\main\\resources\\销售云_v1807_账户_移动端&Web端_胡晓娜_20180530.xls";
        try {
            List<List<Object>> list = ExcelUtils.getBankListByExcel(excelUrl);
            System.out.println(generateI18nSQL(list));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String generateI18nSQL(List<List<Object>> list){
        StringBuffer buffer = new StringBuffer();
        ZHConverter converter = ZHConverter.getInstance(ZHConverter.TRADITIONAL);
        buffer.append("SET @id := (SELECT IFNULL(MAX(id),0) FROM p_i18n_multi_lang_resource WHERE id <1000000);\n" )
                .append("INSERT INTO p_i18n_multi_lang_resource (id, tenant_id, object_id, i18n_type, sub_type, resource_key, lang_code, resource_value, resource_plural_value, ENABLE, del_flg, terminal, description, created_at, created_by, updated_at,updated_by)VALUES \n");
        for(List<Object> children : list){
            buffer.append("((select @id := @id + 1 as id), '-101',NULL, '2', '5' ,'"+children.get(0)+"', 'zh', '"+children.get(4)+"', '"+children.get(4)+"',1, 0, NULL, '', unix_timestamp(now())*1000, -101, unix_timestamp(now())*1000, -101),\n");
            buffer.append("((select @id := @id + 1 as id), '-101',NULL, '2', '5' ,'"+children.get(0)+"', 'zh-TW', '"+converter.convert(String.valueOf(children.get(4)))+"', '"+converter.convert(String.valueOf(children.get(4)))+"',1, 0, NULL, '', unix_timestamp(now())*1000, -101, unix_timestamp(now())*1000, -101),\n");
            if(children.size()<8){
                continue;
            }
            buffer.append("((select @id := @id + 1 as id), '-101',NULL, '2', '5' ,'"+children.get(0)+"', 'en', '"+children.get(7)+"', '"+children.get(7)+"',1, 0, NULL, '', unix_timestamp(now())*1000, -101, unix_timestamp(now())*1000, -101),\n");
        }
        return buffer.toString();
    }
}
