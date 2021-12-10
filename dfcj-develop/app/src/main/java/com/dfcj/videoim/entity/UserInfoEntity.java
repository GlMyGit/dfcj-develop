package com.dfcj.videoim.entity;

public class UserInfoEntity {

    private Boolean success;
    private DataDTO data;
    private String code;
    private String message;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public DataDTO getData() {
        return data;
    }

    public void setData(DataDTO data) {
        this.data = data;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static class DataDTO {
        private String custNo;
        private String custName;
        private Boolean vvipYn;
        private String custGradeCode;
        private String custGradeName;
        private String custFaceUrl;
        private String custCharCode;
        private String custCharName;
        private String custGbCode;
        private String custGb;
        private String custHp;
        private Boolean empYn;
        private String custBirthday;
        private String custAddress;
        private String custSaveAmt;
        private String custCardTypeCode;
        private String custCardType;
        private String custRate;
        private String remark;
        private Object custDepositAmt;

        public String getCustNo() {
            return custNo;
        }

        public void setCustNo(String custNo) {
            this.custNo = custNo;
        }

        public String getCustName() {
            return custName;
        }

        public void setCustName(String custName) {
            this.custName = custName;
        }

        public Boolean getVvipYn() {
            return vvipYn;
        }

        public void setVvipYn(Boolean vvipYn) {
            this.vvipYn = vvipYn;
        }

        public String getCustGradeCode() {
            return custGradeCode;
        }

        public void setCustGradeCode(String custGradeCode) {
            this.custGradeCode = custGradeCode;
        }

        public String getCustGradeName() {
            return custGradeName;
        }

        public void setCustGradeName(String custGradeName) {
            this.custGradeName = custGradeName;
        }

        public String getCustFaceUrl() {
            return custFaceUrl;
        }

        public void setCustFaceUrl(String custFaceUrl) {
            this.custFaceUrl = custFaceUrl;
        }

        public String getCustCharCode() {
            return custCharCode;
        }

        public void setCustCharCode(String custCharCode) {
            this.custCharCode = custCharCode;
        }

        public String getCustCharName() {
            return custCharName;
        }

        public void setCustCharName(String custCharName) {
            this.custCharName = custCharName;
        }

        public String getCustGbCode() {
            return custGbCode;
        }

        public void setCustGbCode(String custGbCode) {
            this.custGbCode = custGbCode;
        }

        public String getCustGb() {
            return custGb;
        }

        public void setCustGb(String custGb) {
            this.custGb = custGb;
        }

        public String getCustHp() {
            return custHp;
        }

        public void setCustHp(String custHp) {
            this.custHp = custHp;
        }

        public Boolean getEmpYn() {
            return empYn;
        }

        public void setEmpYn(Boolean empYn) {
            this.empYn = empYn;
        }

        public String getCustBirthday() {
            return custBirthday;
        }

        public void setCustBirthday(String custBirthday) {
            this.custBirthday = custBirthday;
        }

        public String getCustAddress() {
            return custAddress;
        }

        public void setCustAddress(String custAddress) {
            this.custAddress = custAddress;
        }

        public String getCustSaveAmt() {
            return custSaveAmt;
        }

        public void setCustSaveAmt(String custSaveAmt) {
            this.custSaveAmt = custSaveAmt;
        }

        public String getCustCardTypeCode() {
            return custCardTypeCode;
        }

        public void setCustCardTypeCode(String custCardTypeCode) {
            this.custCardTypeCode = custCardTypeCode;
        }

        public String getCustCardType() {
            return custCardType;
        }

        public void setCustCardType(String custCardType) {
            this.custCardType = custCardType;
        }

        public String getCustRate() {
            return custRate;
        }

        public void setCustRate(String custRate) {
            this.custRate = custRate;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

        public Object getCustDepositAmt() {
            return custDepositAmt;
        }

        public void setCustDepositAmt(Object custDepositAmt) {
            this.custDepositAmt = custDepositAmt;
        }
    }
}
