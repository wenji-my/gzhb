package com.example.administrator.envsystem.utils;

import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.example.administrator.envsystem.conf.StaticValues.namespace;

/**
 * Created by Administrator on 2017/4/13.
 */

public class WebServiceUtils {
    /**
     * 连接WebService
     * @param ip ip
     * @param methodName 名称
     * @param map 参数
     * @return
     */
    public static String connectWebService(String ip, String methodName, HashMap<String,String> map){
        String WSDL_URI = "http://"+ip+"/pda/services/pdaTaskService?wsdl";//wsdl 的uri
        SoapObject request = new SoapObject(namespace, methodName);
        if (map!=null){
            for (Map.Entry<String,String> entry:map.entrySet()){
                request.addProperty(entry.getKey(),entry.getValue());
            }
        }
        //创建SoapSerializationEnvelope 对象，同时指定soap版本号(之前在wsdl中看到的)
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapSerializationEnvelope.VER11);
        envelope.bodyOut = request;//由于是发送请求，所以是设置bodyOut
        envelope.dotNet = false;//由于是.net开发的webservice，所以这里要设置为true
        envelope.setOutputSoapObject(request);// 处理结果保存对象设定
        HttpTransportSE httpTransportSE = new HttpTransportSE(WSDL_URI,3000);
        httpTransportSE.debug=true;
        try {
            httpTransportSE.call(null, envelope);//调用
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (XmlPullParserException e) {
            e.printStackTrace();
            return null;
        }
        // 获取返回的数据
        SoapObject object = (SoapObject) envelope.bodyIn;
        // 获取返回的结果
       String result = object.getProperty(0).toString();
//        try {
//            String s = envelope.getResponse().toString();
//        } catch (SoapFault soapFault) {
//            soapFault.printStackTrace();
//        }
        return result;
    }
}
