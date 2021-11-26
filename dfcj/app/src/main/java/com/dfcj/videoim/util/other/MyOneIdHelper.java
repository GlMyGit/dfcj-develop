package com.dfcj.videoim.util.other;

import android.content.Context;

import com.bun.miitmdid.core.ErrorCode;
import com.bun.miitmdid.core.MdidSdkHelper;
import com.bun.miitmdid.interfaces.IIdentifierListener;
import com.bun.miitmdid.interfaces.IdSupplier;

public class MyOneIdHelper  implements IIdentifierListener {

    private AppIdsUpdater listener;

    public MyOneIdHelper(AppIdsUpdater callback){
        listener=callback;
    }

    public void getDeviceIds(Context cxt){
        long timeb=System.currentTimeMillis();
        // 方法调用
        int nres = CallFromReflect(cxt);

        long timee=System.currentTimeMillis();
        long offset=timee-timeb;
        if(nres == ErrorCode.INIT_ERROR_DEVICE_NOSUPPORT){//不支持的设备
        }else if( nres == ErrorCode.INIT_ERROR_LOAD_CONFIGFILE){//
            // 加载配置文件出错
        }else if(nres ==ErrorCode.INIT_ERROR_MANUFACTURER_NOSUPPORT){
            //不支持的设备厂商
        }else if(nres == ErrorCode.INIT_ERROR_RESULT_DELAY){
            //获取接口是异步的，结果会在回调中返回，回调执行的回调可能在工作线程
        }else if(nres == ErrorCode.INIT_HELPER_CALL_ERROR){
            //反射调用出错
        }

        LogUtils.logd("return value:"+String.valueOf(nres));

    }


    private int CallFromReflect(Context cxt){
        return MdidSdkHelper.InitSdk(cxt,true,this);
    }


    @Override
    public void OnSupport(boolean isSupport, IdSupplier supplier) {
        if(supplier==null) {
            return;
        }
        String oaid=supplier.getOAID();
        String vaid=supplier.getVAID();
        String aaid=supplier.getAAID();
        StringBuilder builder=new StringBuilder();
        builder.append("support: ").append(isSupport?"true":"false").append("\n");
        builder.append("OAID: ").append(oaid).append("\n");
        builder.append("VAID: ").append(vaid).append("\n");
        builder.append("AAID: ").append(aaid).append("\n");
        String idstext=builder.toString();
        if(listener!=null){
            listener.OnIdsAvalid(oaid,vaid,aaid);
        }
    }


    public interface AppIdsUpdater{
        void OnIdsAvalid(String oaid,String vaid,String aaid);
    }



}
