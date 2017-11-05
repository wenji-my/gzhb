package com.example.administrator.envsystem.bean;

import java.util.List;

/**
 * Created by Administrator on 2017/4/13.
 */

public class PdaTaskResult {

    /**
     * businessid : z
     * checkresult : true
     * env : [{"visCode":"sa","visValue":"as"}]
     * photolist : [{"path":"afaf","phototype":"dsff"}]
     */

    private String businessid;
    private boolean checkresult;
    private List<EnvBean> env;
    private List<PhotolistBean> photolist;

    public String getBusinessid() {
        return businessid;
    }

    public void setBusinessid(String businessid) {
        this.businessid = businessid;
    }

    public boolean isCheckresult() {
        return checkresult;
    }

    public void setCheckresult(boolean checkresult) {
        this.checkresult = checkresult;
    }

    public List<EnvBean> getEnv() {
        return env;
    }

    public void setEnv(List<EnvBean> env) {
        this.env = env;
    }

    public List<PhotolistBean> getPhotolist() {
        return photolist;
    }

    public void setPhotolist(List<PhotolistBean> photolist) {
        this.photolist = photolist;
    }

    public static class EnvBean {
        /**
         * visCode : sa
         * visValue : as
         */

        private String visCode;
        private String visValue;

        public String getVisCode() {
            return visCode;
        }

        public void setVisCode(String visCode) {
            this.visCode = visCode;
        }

        public String getVisValue() {
            return visValue;
        }

        public void setVisValue(String visValue) {
            this.visValue = visValue;
        }

        @Override
        public String toString() {
            return "EnvBean{" +
                    "visCode='" + visCode + '\'' +
                    ", visValue='" + visValue + '\'' +
                    '}';
        }
    }

    public static class PhotolistBean {
        @Override
        public String toString() {
            return "PhotolistBean{" +
                    "path='" + path + '\'' +
                    ", phototype='" + phototype + '\'' +
                    '}';
        }

        /**
         * path : afaf
         * phototype : dsff
         */

        private String path;
        private String phototype;

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public String getPhototype() {
            return phototype;
        }

        public void setPhototype(String phototype) {
            this.phototype = phototype;
        }

    }

    @Override
    public String toString() {
        return "PdaTaskResult{" +
                "businessid='" + businessid + '\'' +
                ", checkresult=" + checkresult +
                ", env=" + env +
                ", photolist=" + photolist +
                '}';
    }
}
