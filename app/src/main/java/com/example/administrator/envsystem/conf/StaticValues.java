package com.example.administrator.envsystem.conf;

import com.example.administrator.envsystem.bean.PdaTaskResult;

import java.util.List;

/**
 * Created by Administrator on 2017/4/10.
 */

public class StaticValues {
    public static String ip="10.111.102.92:8160";
    public static String namespace="http://webservice.gzjdc.grkj.com/";
    public static String ftpPath="";
    public static int cs=0;
    //方法名称
    public static String token="";
    public static String getToken="getToken";
    public static String getPdaTaskList="getPdaTaskList";
    public static String getPdaTaskByCarNumber="getPdaTaskByCarNumber";
    public static String setPdaTaskResult="setPdaTaskResult";

    //V02
    public static List<PdaTaskResult.EnvBean> v02;
}
