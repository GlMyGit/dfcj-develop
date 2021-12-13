package com.dfcj.videoim.entity;

import java.util.List;
import java.util.Map;


public class HistoryMsgEntity {


    private Boolean success;
    private DataDTO data;

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

    public static class DataDTO {
        private Object extra;
        private Integer total;
        private List<DataDTO.DataDTO2> data;

        public Object getExtra() {
            return extra;
        }

        public void setExtra(Object extra) {
            this.extra = extra;
        }

        public Integer getTotal() {
            return total;
        }

        public void setTotal(Integer total) {
            this.total = total;
        }

        public List<DataDTO.DataDTO2> getData() {
            return data;
        }

        public void setData(List<DataDTO.DataDTO2> data) {
            this.data = data;
        }

        public static class DataDTO2 {
            private Integer id;
            private String callbackCommand;
            private String fromAccount;
            private String toAccount;
            private Integer msgSeq;
            private Integer msgRandom;
            private Integer msgTime;
            private String msgKey;
            private Integer onlineOnlyFlag;
            private Integer sendMsgResult;
            private String errorInfo;
            private Integer unreadMsgNum;
            private String msgBody;
            private String cloudCustomData;
            private Integer eventId;
            private String staffCode;
            private String custCode;
            private String custNick;
            private String custFaceUrl;
            private String staffFaceUrl;
            private String staffNick;

            public Integer getId() {
                return id;
            }

            public void setId(Integer id) {
                this.id = id;
            }

            public String getCallbackCommand() {
                return callbackCommand;
            }

            public void setCallbackCommand(String callbackCommand) {
                this.callbackCommand = callbackCommand;
            }

            public String getFromAccount() {
                return fromAccount;
            }

            public void setFromAccount(String fromAccount) {
                this.fromAccount = fromAccount;
            }

            public String getToAccount() {
                return toAccount;
            }

            public void setToAccount(String toAccount) {
                this.toAccount = toAccount;
            }

            public Integer getMsgSeq() {
                return msgSeq;
            }

            public void setMsgSeq(Integer msgSeq) {
                this.msgSeq = msgSeq;
            }

            public Integer getMsgRandom() {
                return msgRandom;
            }

            public void setMsgRandom(Integer msgRandom) {
                this.msgRandom = msgRandom;
            }

            public Integer getMsgTime() {
                return msgTime;
            }

            public void setMsgTime(Integer msgTime) {
                this.msgTime = msgTime;
            }

            public String getMsgKey() {
                return msgKey;
            }

            public void setMsgKey(String msgKey) {
                this.msgKey = msgKey;
            }

            public Integer getOnlineOnlyFlag() {
                return onlineOnlyFlag;
            }

            public void setOnlineOnlyFlag(Integer onlineOnlyFlag) {
                this.onlineOnlyFlag = onlineOnlyFlag;
            }

            public Integer getSendMsgResult() {
                return sendMsgResult;
            }

            public void setSendMsgResult(Integer sendMsgResult) {
                this.sendMsgResult = sendMsgResult;
            }

            public String getErrorInfo() {
                return errorInfo;
            }

            public void setErrorInfo(String errorInfo) {
                this.errorInfo = errorInfo;
            }

            public Integer getUnreadMsgNum() {
                return unreadMsgNum;
            }

            public void setUnreadMsgNum(Integer unreadMsgNum) {
                this.unreadMsgNum = unreadMsgNum;
            }

            public String getMsgBody() {
                return msgBody;
            }

            public void setMsgBody(String msgBody) {
                this.msgBody = msgBody;
            }

            public String getCloudCustomData() {
                return cloudCustomData;
            }

            public void setCloudCustomData(String cloudCustomData) {
                this.cloudCustomData = cloudCustomData;
            }

            public Integer getEventId() {
                return eventId;
            }

            public void setEventId(Integer eventId) {
                this.eventId = eventId;
            }

            public String getStaffCode() {
                return staffCode;
            }

            public void setStaffCode(String staffCode) {
                this.staffCode = staffCode;
            }

            public String getCustCode() {
                return custCode;
            }

            public void setCustCode(String custCode) {
                this.custCode = custCode;
            }

            public String getCustNick() {
                return custNick;
            }

            public void setCustNick(String custNick) {
                this.custNick = custNick;
            }

            public String getCustFaceUrl() {
                return custFaceUrl;
            }

            public void setCustFaceUrl(String custFaceUrl) {
                this.custFaceUrl = custFaceUrl;
            }

            public String getStaffFaceUrl() {
                return staffFaceUrl;
            }

            public void setStaffFaceUrl(String staffFaceUrl) {
                this.staffFaceUrl = staffFaceUrl;
            }

            public String getStaffNick() {
                return staffNick;
            }

            public void setStaffNick(String staffNick) {
                this.staffNick = staffNick;
            }

            public static class MsgBody {
                private String MsgType;
                private MsgContent MsgContent;

                public String getMsgType() {
                    return MsgType;
                }

                public void setMsgType(String msgType) {
                    MsgType = msgType;
                }

                public MsgBody.MsgContent getMsgContent() {
                    return MsgContent;
                }

                public void setMsgContent(MsgBody.MsgContent msgContent) {
                    MsgContent = msgContent;
                }

                public static class MsgContent {
                    private String Data;

                    public String getData() {
                        return Data;
                    }

                    public void setData(String data) {
                        Data = data;
                    }
                }
            }
        }
    }
}