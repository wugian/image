/*
 *  Copyright (c)  2017.  wugian
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package com.wugian.sissi.utils;

/**
 * Created by wugian on 2017/4/25
 */
public class UpdateMsg {

    /**
     * code : 0
     * msg :
     * data : {"verCode":"12","verName":"v2.0","description":"1.3423423#2.ffffffffff","appUrl":"url","forceType":true}
     */

    private int code;
    private String msg;
    private DataEntity data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public DataEntity getData() {
        return data;
    }

    public void setData(DataEntity data) {
        this.data = data;
    }

    public static class DataEntity {
        /**
         * verCode : 12
         * verName : v2.0
         * description : 1.3423423#2.ffffffffff
         * appUrl : url
         * forceType : true
         */

        private int verCode;
        private String verName;
        private String description;
        private String appUrl;
        private boolean forceType;

        public int getVerCode() {
            return verCode;
        }

        public void setVerCode(int verCode) {
            this.verCode = verCode;
        }

        public String getVerName() {
            return verName;
        }

        public void setVerName(String verName) {
            this.verName = verName;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getAppUrl() {
            return appUrl;
        }

        public void setAppUrl(String appUrl) {
            this.appUrl = appUrl;
        }

        public boolean isForceType() {
            return forceType;
        }

        public void setForceType(boolean forceType) {
            this.forceType = forceType;
        }
    }
}
