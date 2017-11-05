package com.example.administrator.envsystem.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2017/4/13.
 */

public class PdaTaskList implements Serializable {

    @Override
    public String toString() {
        return "PdaTaskList{" +
                "businessid='" + businessid + '\'' +
                ", carcardnumber='" + carcardnumber + '\'' +
                ", carcardcolor='" + carcardcolor + '\'' +
                ", vin='" + vin + '\'' +
                ", opt='" + opt + '\'' +
                ", fueltype='" + fueltype + '\'' +
                ", fuelname='" + fuelname + '\'' +
                ", phototype=" + phototype +
                ", env=" + env +
                '}';
    }

    /**
     * businessid : //环保检测业务编号
     * carcardnumber : //车牌号码
     * carcardcolor : //车牌颜色， 例如 blue、 yellow
     * vin : //车辆 VIN 号
     * opt : //任务操作： add 增加 del 删除
     * fueltype : //燃料类型编号：
     * fuelname : //燃料类型中文名
     * phototype : //所需照片类型
     * env : //外观检查项目
     */

    private String businessid;
    private String carcardnumber;
    private String carcardcolor;
    private String vin;
    private String opt;
    private String fueltype;
    private String fuelname;
    private List<Phototype> phototype;
    private List<Env> env;

    public List<Env> getEnv() {
        return env;
    }

    public void setEnv(List<Env> env) {
        this.env = env;
    }

    public static class Env implements Serializable{

        /**
         * visCode ://编号
         * visName ://名字
         * visPCode ://父编号
         * visType ://类型
         */

        private String visCode;
        private String visName;
        private String visPCode;
        private String visType;

        public String getVisCode() {
            return visCode;
        }

        public void setVisCode(String visCode) {
            this.visCode = visCode;
        }

        public String getVisName() {
            return visName;
        }

        public void setVisName(String visName) {
            this.visName = visName;
        }

        public String getVisPCode() {
            return visPCode;
        }

        public void setVisPCode(String visPCode) {
            this.visPCode = visPCode;
        }

        public String getVisType() {
            return visType;
        }

        public void setVisType(String visType) {
            this.visType = visType;
        }

        @Override
        public String toString() {
            return "Env{" +
                    "visCode='" + visCode + '\'' +
                    ", visName='" + visName + '\'' +
                    ", visPCode='" + visPCode + '\'' +
                    ", visType='" + visType + '\'' +
                    '}';
        }
    }

    public List<Phototype> getPhototype() {
        return phototype;
    }

    public void setPhototype(List<Phototype> phototype) {
        this.phototype = phototype;
    }

    public static class Phototype implements Serializable{

        /**
         * typecode : //照片类型编号
         * typename : //图片类型中文名称
         */
        private String typecode;
        private String typename;

        public String getTypecode() {
            return typecode;
        }

        public void setTypecode(String typecode) {
            this.typecode = typecode;
        }

        public String getTypename() {
            return typename;
        }

        public void setTypename(String typename) {
            this.typename = typename;
        }

        @Override
        public String toString() {
            return "Phototype{" +
                    "typecode='" + typecode + '\'' +
                    ", typename='" + typename + '\'' +
                    '}';
        }
    }

    public String getFueltype() {
        return fueltype;
    }

    public void setFueltype(String fueltype) {
        this.fueltype = fueltype;
    }

    public String getBusinessid() {
        return businessid;
    }

    public void setBusinessid(String businessid) {
        this.businessid = businessid;
    }

    public String getCarcardnumber() {
        return carcardnumber;
    }

    public void setCarcardnumber(String carcardnumber) {
        this.carcardnumber = carcardnumber;
    }

    public String getCarcardcolor() {
        return carcardcolor;
    }

    public void setCarcardcolor(String carcardcolor) {
        this.carcardcolor = carcardcolor;
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public String getOpt() {
        return opt;
    }

    public void setOpt(String opt) {
        this.opt = opt;
    }

    public String getFuelname() {
        return fuelname;
    }

    public void setFuelname(String fuelname) {
        this.fuelname = fuelname;
    }
}
