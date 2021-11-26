package com.dfcj.videoim.im.ocr;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;

import com.dfcj.videoim.base.BaseActivity;
import com.tencent.aai.AAIClient;
import com.tencent.aai.audio.data.AudioRecordDataSource;
import com.tencent.aai.audio.utils.WavCache;
import com.tencent.aai.auth.AbsCredentialProvider;
import com.tencent.aai.auth.LocalCredentialProvider;
import com.tencent.aai.config.ClientConfiguration;
import com.tencent.aai.exception.ClientException;
import com.tencent.aai.exception.ServerException;
import com.tencent.aai.listener.AudioRecognizeResultListener;
import com.tencent.aai.listener.AudioRecognizeStateListener;
import com.tencent.aai.listener.AudioRecognizeTimeoutListener;
import com.tencent.aai.model.AudioRecognizeRequest;
import com.tencent.aai.model.AudioRecognizeResult;
import com.tencent.aai.model.type.AudioRecognizeConfiguration;
import com.tencent.aai.model.type.AudioRecognizeTemplate;
import com.tencent.aai.model.type.EngineModelType;
import com.tencent.iot.speech.asr.listener.MessageListener;
import com.wzq.mvvmsmart.utils.KLog;

import java.io.DataOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class OcrUtil {

    //语音识别问题
    public boolean switchToDeviceAuth = false;
    public AbsCredentialProvider credentialProvider;
    public Handler handler;
    public LinkedHashMap<String, String> resMap = new LinkedHashMap<>();
    public int currentRequestId = 0;
    boolean isSaveAudioRecordFiles=true;
    public AAIClient aaiClient;
    public AudioRecognizeStateListener audioRecognizeStateListener;
    public AudioRecognizeResultListener audioRecognizeResultlistener;
    public AudioRecognizeTimeoutListener audioRecognizeTimeoutListener;
    private Context context;
    public String ocrSbResult;

    private MessageListener messageListener;

    public String myLanguageTypt=EngineModelType.EngineModelType16K.getType();


    //设置ProjectId 不设置默认使用0，说明：项目功能用于按项目管理云资源，可以对云资源进行分项目管理，详情见 https://console.cloud.tencent.com/project
    final int projectId = 0;


    public OcrUtil(Context context){
        this.context=context;
    }

    public void initInfo(BaseActivity my){

        handler = new Handler(my.getMainLooper());

    }

    public void setMessageList(MessageListener messageListener){

         this.messageListener=messageListener;

    }


    //开始录音
    public void startRecording(){


        if (aaiClient!=null) {
            boolean taskExist = aaiClient.cancelAudioRecognize(currentRequestId);
            // AAILogger.info(logger, "taskExist=" + taskExist);
        }

        KLog.d("the start button has clicked..");
        resMap.clear();
        handler.post(new Runnable() {
            @Override
            public void run() {
                //   start.setEnabled(false);
            }
        });
        AudioRecognizeRequest.Builder builder = new AudioRecognizeRequest.Builder();
        isSaveAudioRecordFiles=false;//默认是关的 false
        // 初始化识别请求
        final AudioRecognizeRequest audioRecognizeRequest = builder
//                        .pcmAudioDataSource(new AudioRecordDataSource()) // 设置数据源
                .pcmAudioDataSource(new AudioRecordDataSource(isSaveAudioRecordFiles)) // 设置数据源
                //.templateName(templateName) // 设置模板
               // .template(new AudioRecognizeTemplate(EngineModelType.EngineModelType16K.getType(),0)) // 设置自定义模板
                .template(new AudioRecognizeTemplate(myLanguageTypt,0)) // 设置自定义模板
                .setFilterDirty(0)  // 0 ：默认状态 不过滤脏话 1：过滤脏话
                .setFilterModal(0) // 0 ：默认状态 不过滤语气词  1：过滤部分语气词 2:严格过滤
                .setFilterPunc(0) // 0 ：默认状态 不过滤句末的句号 1：滤句末的句号
                .setConvert_num_mode(1) //1：默认状态 根据场景智能转换为阿拉伯数字；0：全部转为中文数字。
//                        .setVadSilenceTime(1000) // 语音断句检测阈值，静音时长超过该阈值会被认为断句（多用在智能客服场景，需配合 needvad = 1 使用） 默认不传递该参数
                .setNeedvad(1) //0：关闭 vad，1：默认状态 开启 vad。
//                        .setHotWordId("")//热词 id。用于调用对应的热词表，如果在调用语音识别服务时，不进行单独的热词 id 设置，自动生效默认热词；如果进行了单独的热词 id 设置，那么将生效单独设置的热词 id。
                .build();

        // 自定义识别配置
        final AudioRecognizeConfiguration audioRecognizeConfiguration = new AudioRecognizeConfiguration.Builder()
                .setSilentDetectTimeOut(true)// 是否使能静音检测，true表示不检查静音部分
                .audioFlowSilenceTimeOut(5000) // 静音检测超时停止录音
                .minAudioFlowSilenceTime(2000) // 语音流识别时的间隔时间
                .minVolumeCallbackTime(80) // 音量回调时间
                .build();


        //currentRequestId = audioRecognizeRequest.getRequestId();
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (switchToDeviceAuth) {
                    aaiClient.startAudioRecognizeForDevice(audioRecognizeRequest,
                            audioRecognizeResultlistener,
                            audioRecognizeStateListener,
                            audioRecognizeTimeoutListener,
                            audioRecognizeConfiguration);
                } else {
                    aaiClient.startAudioRecognize(audioRecognizeRequest,
                            audioRecognizeResultlistener,
                            audioRecognizeStateListener,
                            audioRecognizeTimeoutListener,
                            audioRecognizeConfiguration);
                }
            }
        }).start();

    }




    public void stopOcr(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean taskExist = false;
                if (aaiClient!=null) {
                    taskExist = aaiClient.stopAudioRecognize(currentRequestId);
                }
                if (!taskExist) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            KLog.d("停止");
                            // recognizeState.setText(getString(R.string.cant_stop));
                        }
                    });
                }
            }
        }).start();
    }

    public void cancelOcr(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean taskExist = false;
                if (aaiClient!=null) {
                    taskExist = aaiClient.cancelAudioRecognize(currentRequestId);
                }
               /* if (!taskExist) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            KLog.d("取消");
                            // recognizeState.setText(getString(R.string.cant_cancel));
                        }
                    });
                }*/
            }
        }).start();
    }




    public void initOcr(){


        if (!switchToDeviceAuth) {
            credentialProvider = new LocalCredentialProvider(OcrConfig.secretKey);
        } else {
            credentialProvider = new LocalCredentialProvider(OcrConfig.secretKeyForDeviceAuth);
        }

        // 用户配置
        ClientConfiguration.setMaxAudioRecognizeConcurrentNumber(2); // 语音识别的请求的最大并发数  默认2  有效1 - 5
        ClientConfiguration.setMaxRecognizeSliceConcurrentNumber(5); // 单个请求的分片最大并发数  默认5   1 - 5有效

        // 识别结果回调监听器
        audioRecognizeResultlistener = new AudioRecognizeResultListener() {

            boolean dontHaveResult = true;

            /**
             * 返回分片的识别结果
             * @param request 相应的请求
             * @param result 识别结果
             * @param seq 该分片所在语音流的序号 (0, 1, 2...)
             */
            @Override
            public void onSliceSuccess(AudioRecognizeRequest request, AudioRecognizeResult result, int seq) {

                if (dontHaveResult && !TextUtils.isEmpty(result.getText())) {
                    dontHaveResult = false;
                    Date date=new Date();
                    DateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
                    String time=format.format(date);
                    String message = String.format("voice flow order = %d, receive first response in %s, result is = %s", seq, time, result.getText());
                    KLog.d( message);
                }

               // KLog.d("分片on slice success..seq:"+seq);
              //  KLog.d( "分片slice seq = {}, voiceid = {}, result = {},startTime = {},endTime = {}"+ seq+ result.getVoiceId()+
                      //  result.getText()+result.getStartTime()+result.getEndTime());
                resMap.put(String.valueOf(seq), result.getText());
                final String msg = buildMessage(resMap);
               // KLog.d( "分片slice msg="+msg);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                      //  KLog.d("结果11："+msg);

                       if (finishedListener != null) {
                            finishedListener.onFinishedRecord(msg,2);
                        }
                        //setOcrEditVal(msg);

                        // recognizeResult.setText(msg);
                    }
                });

            }

            /**
             * 返回语音流的识别结果
             * @param request 相应的请求
             * @param result 识别结果
             * @param seq 该语音流的序号 (1, 2, 3...)
             */
            @Override
            public void onSegmentSuccess(AudioRecognizeRequest request, AudioRecognizeResult result, int seq) {
                dontHaveResult = true;
                //KLog.d( "语音流on segment success");
                // KLog.d( "语音流segment seq = {}, voiceid = {}, result = {},startTime = {},endTime = {}", seq, result.getVoiceId(), result.getText(),result.getStartTime(),result.getEndTime());
                resMap.put(String.valueOf(seq), result.getText().toString());
                final String msg = buildMessage(resMap);
                //KLog.d( "语音流segment msg="+msg);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        KLog.d("语音流结果22："+msg);
                        if (finishedListener != null) {
                            finishedListener.onFinishedRecord(msg,3);
                        }
                        //  setOcrEditVal(msg);
                        //recognizeResult.setText(msg);
                    }
                });
            }

            /**
             * 识别结束回调，返回所有的识别结果
             * @param request 相应的请求
             * @param result 识别结果
             */
            @Override
            public void onSuccess(AudioRecognizeRequest request, String result) {
                KLog.d( "识别结束, onSuccess..");
                KLog.d( "识别结束, result = {}", result);


                ocrSbResult=result;
                if (finishedListener != null) {
                    finishedListener.onFinishedRecord(result,1);
                }

            }

            /**
             * 识别失败
             * @param request 相应的请求
             * @param clientException 客户端异常
             * @param serverException 服务端异常
             * @param response   服务端返回的json字符串
             */
            @Override
            public void onFailure(AudioRecognizeRequest request, final ClientException clientException, final ServerException serverException, String response) {
                if(response != null){
                    KLog.d( "onFailure response.. :"+response);
                }
                if (clientException!=null) {
                    KLog.d("onFailure..:"+clientException.toString());
                }
                if (serverException!=null) {
                    KLog.d( "onFailure..:"+serverException.toString());
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {

                        if (clientException!=null) {
                            // recognizeState.setText("识别状态：失败,  "+clientException.toString());
                            KLog.d("识别状态：失败,  "+clientException.toString());

                        } else if (serverException!=null) {
                            // recognizeState.setText("识别状态：失败,  "+serverException.toString());
                        }
                    }
                });
            }
        };




        /**
         * 识别状态监听器
         */
        audioRecognizeStateListener = new AudioRecognizeStateListener() {
            DataOutputStream dataOutputStream;
            String fileName = null;
            String filePath = null;
            ExecutorService mExecutorService;
            /**
             * 开始录音
             * @param request
             */
            @Override
            public void onStartRecord(AudioRecognizeRequest request) {
                currentRequestId = request.getRequestId();
                KLog.d("onStartRecord..");
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        KLog.d("识别状态：开始录音");
                        // recognizeState.setText(getString(R.string.start_record));
                        //recognizeResult.setText("");
                    }
                });
                //为本次录音创建缓存一个文件
                if(isSaveAudioRecordFiles) {
                    if(mExecutorService == null){
                        mExecutorService = Executors.newSingleThreadExecutor();
                    }
                    filePath = Environment.getExternalStorageDirectory().toString() + "/tencent_audio_sdk_cache";
                    fileName = System.currentTimeMillis() + ".pcm";
                    dataOutputStream = WavCache.creatPmcFileByPath(filePath, fileName);
                }
            }

            /**
             * 结束录音
             * @param request
             */
            @Override
            public void onStopRecord(AudioRecognizeRequest request) {
                KLog.d( "onStopRecord..");
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        KLog.d("识别状态：停止录音");
                        // recognizeState.setText(getString(R.string.end_record));
                        // start.setEnabled(true);
                    }
                });
                if(isSaveAudioRecordFiles) {
                    mExecutorService.execute(new Runnable() {
                        @Override
                        public void run() {
                            WavCache.closeDataOutputStream(dataOutputStream);
                            WavCache.makePCMFileToWAVFile(filePath, fileName);
                        }
                    });

                }
            }

            /**
             * 返回音频流，
             * 用于返回宿主层做录音缓存业务。
             * 由于方法跑在sdk线程上，这里多用于文件操作，宿主需要新开一条线程专门用于实现业务逻辑
             * @param audioDatas
             */
            @Override
            public void onNextAudioData(final short[] audioDatas, final int readBufferLength) {
                if(isSaveAudioRecordFiles) {
                    mExecutorService.execute(new Runnable() {
                        @Override
                        public void run() {
                            WavCache.savePcmData(dataOutputStream, audioDatas, readBufferLength);
                        }
                    });
                }
            }

            /**
             * 第seq个语音流开始识别
             * @param request
             * @param seq
             */
            @Override
            public void onVoiceFlowStartRecognize(AudioRecognizeRequest request, int seq) {


                KLog.d( "onVoiceFlowStartRecognize.. seq = {}"+seq);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        KLog.d( "语音流识别开始");
                        //recognizeState.setText(getString(R.string.start_recognize));
                    }
                });
            }

            /**
             * 第seq个语音流结束识别
             * @param request
             * @param seq
             */
            @Override
            public void onVoiceFlowFinishRecognize(AudioRecognizeRequest request, int seq) {

                Date date=new Date();
                DateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
                String time=format.format(date);
                String message = String.format("voice flow order = %d, recognize finish in %s", seq, time);
                KLog.d( message);

                KLog.d( "onVoiceFlowFinishRecognize.. seq = {}"+ seq);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        // recognizeState.setText(getString(R.string.end_recognize));
                        KLog.d("识别状态：语音流识别结束");
                    }
                });
            }

            /**
             * 第seq个语音流开始
             * @param request
             * @param seq
             */
            @Override
            public void onVoiceFlowStart(AudioRecognizeRequest request, int seq) {

                Date date=new Date();
                DateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
                String time=format.format(date);
                String message = String.format("voice flow order = %d, start in %s", seq, time);
                KLog.d(message);

                KLog.d("onVoiceFlowStart.. seq = {}",+seq);

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        KLog.d("语音流开始");
                        // recognizeState.setText(getString(R.string.start_voice));
                    }
                });
            }

            /**
             * 第seq个语音流结束
             * @param request
             * @param seq
             */
            @Override
            public void onVoiceFlowFinish(AudioRecognizeRequest request, int seq) {

                Date date=new Date();
                DateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
                String time=format.format(date);
                String message = String.format("voice flow order = %d, stop in %s", seq, time);
                KLog.d( message);

                KLog.d("onVoiceFlowFinish.. seq = {}", seq);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        KLog.d("语音流结束");
                        // recognizeState.setText(getString(R.string.end_voice));
                    }
                });
            }

            /**
             * 语音音量回调
             * @param request
             * @param volume
             */
            @Override
            public void onVoiceVolume(AudioRecognizeRequest request, final int volume) {
                KLog.d("onVoiceVolume..");
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        KLog.d("音量:"+volume);
                        //  MainActivity.this.volume.setText(getString(R.string.volume)+volume);
                    }
                });
            }

        };


        /**
         * 识别超时监听器
         */

        audioRecognizeTimeoutListener = new AudioRecognizeTimeoutListener() {

            /**
             * 检测第一个语音流超时
             * @param request
             */
            @Override
            public void onFirstVoiceFlowTimeout(AudioRecognizeRequest request) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        KLog.d("识别状态：开始说话超时");
                        // recognizeState.setText(getString(R.string.start_voice_timeout));
                    }
                });
            }

            /**
             * 检测下一个语音流超时
             * @param request
             */
            @Override
            public void onNextVoiceFlowTimeout(AudioRecognizeRequest request) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        KLog.d("识别状态：结束说话超时");
                        //  recognizeState.setText(getString(R.string.end_voice_timeout));
                    }
                });
            }
        };


        if (aaiClient==null) {
            try {
//                        aaiClient = new AAIClient(MainActivity.this, appid, projectId, secretId, credentialProvider);
                //sdk crash 上传
                if (switchToDeviceAuth) {
                    aaiClient = new AAIClient(context, Integer.valueOf(OcrConfig.appIdForDeviceAuth), projectId,
                            OcrConfig.secretIdForDeviceAuth, OcrConfig.secretKeyForDeviceAuth,
                            OcrConfig.serialNumForDeviceAuth, OcrConfig.deviceNumForDeviceAuth,
                            credentialProvider, messageListener);
                } else {
                    /**直接鉴权**/
                    aaiClient = new AAIClient(context, OcrConfig.apppIds,projectId,  OcrConfig.secretId,
                            OcrConfig.secretKey ,credentialProvider);
                    /**使用临时密钥鉴权
                     * * 1.通过sts 获取到临时证书 （secretId secretKey  token） ,此步骤应在您的服务器端实现，见https://cloud.tencent.com/document/product/598/33416
                     *   2.通过临时密钥调用接口
                     * **/

//                    aaiClient = new AAIClient(MainActivity.this, OcrConfig.apppIds, projectId,
//                            OcrConfig.secretId,  OcrConfig.secretKey,"对应的token" ,credentialProvider);
                }
            } catch (ClientException e) {
                e.printStackTrace();
                KLog.d( e.toString());
            }
        }


    }



    //格式还字符
    private String buildMessage(Map<String, String> msg) {

        StringBuffer stringBuffer = new StringBuffer();
        Iterator<Map.Entry<String, String>> iter = msg.entrySet().iterator();
        while (iter.hasNext()) {
            String value = iter.next().getValue();
            stringBuffer.append(value+"");
            //stringBuffer.append(value+"\r\n");
        }
        return stringBuffer.toString();
    }


    private OnStartRecordListener startListener;
    public void setOnStartRecordListener(OnStartRecordListener listener) {
        startListener = listener;
    }
    public interface OnStartRecordListener {
        public void onSrartRecord(String audioPath, int time);
    }




    private OnFinishedRecordListener finishedListener;
    public interface OnFinishedRecordListener {
        public void onFinishedRecord(String audioPath, int time);
    }

    public void setOnFinishedRecordListener(OnFinishedRecordListener listener) {
        finishedListener = listener;
    }

}
