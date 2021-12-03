package com.dfcj.videoim.api;


import com.dfcj.videoim.base.BaseRespose;
import com.dfcj.videoim.entity.ChangeCustomerServiceEntity;
import com.dfcj.videoim.entity.DemoBean;
import com.dfcj.videoim.entity.LoginBean;
import com.dfcj.videoim.entity.SendOffineMsgEntity;
import com.dfcj.videoim.entity.TrtcRoomEntity;
import com.dfcj.videoim.entity.upLoadImgEntity;
import com.wzq.mvvmsmart.http.BaseResponse;

import java.util.Map;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

/**
 * Created by Riven
 * 服务器接口
 */
public interface ApiService {


    //登录
    @POST("/api/newMedia/fcm/customerRead/login")
    Observable<LoginBean> requestLogin(
            @Body Map<String, Object> map
    );



    //智能客服
    @POST("/api/newMedia/fcm/xiaoIRead/ask")
    Observable<SendOffineMsgEntity> requestSendOffineMsg(
            @Body Map<String, Object> map
    );


    //转人工客服 分配客服
    @POST("/api/newMedia/fcm/customerRead/getImStaff")
    Observable<ChangeCustomerServiceEntity> requestChangeCustomerService(
            @Body Map<String, Object> map
    );



    //获取视频房间号
    @POST("/api/newMedia/fcm/customerRead/getTrtcRoomId")
    Observable<TrtcRoomEntity> requestTrtcRoomId(
            @Body Map<String, Object> map
    );


    //文件上传
    @Multipart
    @POST("/api/newMedia/fcm/fileOper/uploadFile")
    Observable<upLoadImgEntity> requestUploadImg(
            @Part MultipartBody.Part file
    );


    //顾客根据日期查询历史单聊消息接口
    @POST("/api/newMedia/fcm/eventRead/queryCustImRecord")
    Observable<TrtcRoomEntity> requestQueryCustImRecord(
            @Body Map<String, Object> map
    );




    @GET("action/apiv2/banner")
    Observable<BaseResponse<DemoBean>> demoGet(@Query("catalog") int pageNum);

    @FormUrlEncoded
    @POST("action/apiv2/banner")
    Observable<BaseResponse<DemoBean>> demoPost(@Field("catalog") String catalog);

    @GET("getJsonFile")
    Observable<BaseResponse<Object>> getJsonFile();






   /* //获取客服
    @GET("/sys/config/v1/getKf")
    Observable<CustomerEntity> requestCustomer(
            @Header("Cache-Control") String cacheControl,
            @QueryMap Map<String, Object> map

    );



    //成语填空-领取体力
    @POST("/idiom/idiomUser/v1/power")
    Observable<IdiomPowerEntity> requestIdiomPower(
            @Header("Cache-Control") String cacheControl,
            @Body Map<String, Object> map
    );



    //成语填空-获取提示
    @POST("/idiom/idiomUser/v1/hint")
    Observable<BaseRespose> requestIdiomHint(
            @Header("Cache-Control") String cacheControl,
            @Body Map<String, Object> map
    );



    //成语获取用户信息
    @GET("/idiom/idiomUser/v1/info")
    Observable<IdiomUserEntity> requestIdiomUserInfo(
            @Header("Cache-Control") String cacheControl,
            @QueryMap Map<String, Object> map

    );


    //成语是否答对
    @POST("/idiom/keyword/v1/winOrLose")
    Observable<CheckWinOrLoseEntity> requestCheckWinOrLose(
            @Header("Cache-Control") String cacheControl,
            @Body Map<String, Object> map
    );

    //获取成语配置的广告信息
    @GET("/idiom/idiom/v1/info")
    Observable<IdiomAdEntity> requestIdiomAd(
            @Header("Cache-Control") String cacheControl,
            @QueryMap Map<String, Object> map

    );


    //保存统计-广告
    @POST("/statistic/statisticAd/v1/save")
    Observable<BaseRespose> requestStatisticsAd(
            @Header("Cache-Control") String cacheControl,
            @Body Map<String, Object> map
    );

    //点击泡泡
    @POST("/operation/bubble/v1/click")
    Observable<PaoPaoEntity> requestClickPaoPao(
            @Header("Cache-Control") String cacheControl,
            @Body Map<String, Object> map
    );

    //点击宝箱领取
    @POST("/operation/bubble/v1/clickBox")
    Observable<ClickBoxEntity> requestClickBox(
            @Header("Cache-Control") String cacheControl,
            @Body Map<String, Object> map
    );

    //查询泡泡信息
    @GET("/operation/bubble/v1/info")
    Observable<BubbleInfoEntity> requestBubbleInfo(
            @Header("Cache-Control") String cacheControl,
            @QueryMap Map<String, Object> map

    );

    //查询泡泡信息v2
    @GET("/operation/bubble/v2/info")
    Observable<BubbleInfoEntity> requestBubbleInfoV2(
            @Header("Cache-Control") String cacheControl,
            @QueryMap Map<String, Object> map

    );


    //关卡
    @POST("/idiom/v1/level")
    Observable<GuanKaEntity> requestGuanKa(
            @Header("Cache-Control") String cacheControl,
            @Body Map<String, Object> map
    );



    //领取体力
    @POST("/idiom/v1/incrLives")
    Observable<BaseRespose> requestIncrLives(
            @Header("Cache-Control") String cacheControl,
            @Body Map<String, Object> map
    );



    //领取俸禄
    @POST("/idiom/idiomUser/v1/salary")
    Observable<ReceiveSalaryEntity> requestReceiveSalary(
            @Header("Cache-Control") String cacheControl,
            @Body Map<String, Object> map
    );




    //成语填空-俸禄列表
    @GET("/idiom/idiomUser/v1/getSalaryList")
    Observable<ShowSalaryEntity> requestShowSalary(
            @Header("Cache-Control") String cacheControl,
            @QueryMap Map<String, Object> map

    );




    //获取跑马灯
    @GET("/sys/config/v1/getHorseRaceLamp")
    Observable<HorseRaceLampEntity> requestHorseRaceLamp(
            @Header("Cache-Control") String cacheControl,
            @QueryMap Map<String, Object> map

    );

    //获取跑马灯 提现
    @GET("/sys/config/v1/getHorseRaceLamp/withdraw")
    Observable<HorseRaceLampEntity> requestHorseRaceLampWitharaw(
            @Header("Cache-Control") String cacheControl,
            @QueryMap Map<String, Object> map

    );




    //抽奖-刮刮卡奖品
    @POST("/operation/scratchCard/v1/start")
    Observable<ScratchCardResultEntity> requestScratchCardResult(
            @Header("Cache-Control") String cacheControl,
            @Body Map<String, Object> map
    );



    //抽奖-奖品信息
    @GET("/operation/scratchCard/v1/prizeInfo")
    Observable<PrizeInfoEntity> requestPrizeInfo(
            @Header("Cache-Control") String cacheControl,
            @QueryMap Map<String, Object> map

    );

    //查询刮刮卡信息
    @GET("/operation/scratchCard/v1/info")
    Observable<ScratchCardInfoEntity> requestScratchCardInfo(
            @Header("Cache-Control") String cacheControl,
            @QueryMap Map<String, Object> map

    );


    //抽手机开始
    @POST("/operation/takePhone/v1/start")
    Observable<StartLotteryPhoneEntity> requestStartLotteryPhone(
            @Header("Cache-Control") String cacheControl,
            @Body Map<String, Object> map
    );


    //抽手机
    @GET("/operation/takePhone/v1/info")
    Observable<LotterPhoneEntity> requestLotterPhoneInfo(
            @Header("Cache-Control") String cacheControl,
            @QueryMap Map<String, Object> map

    );


    //看完广告、给用户发钱
    @POST("/operation/money/v1/save")
    Observable<AddMoneyEntity> requestAddMoney(
            @Header("Cache-Control") String cacheControl,
            @Body Map<String, Object> map
    );

    //看完广告、给用户发钱 v2
    @POST("/operation/money/v2/save")
    Observable<AddMoneyEntity> requestAddMoneyV2(
            @Header("Cache-Control") String cacheControl,
            @Body Map<String, Object> map
    );



    //用户现金签到
    @POST("/operation/money/v1/doSign")
    Observable<DoSignEntity> requestDoSign(
            @Header("Cache-Control") String cacheControl,
            @Body Map<String, Object> map
    );



    //100元现金查询
    @GET("/operation/money/v1/info")
    Observable<MoneyInfoEntity> requestMoneyInfo(
            @Header("Cache-Control") String cacheControl,
            @QueryMap Map<String, Object> map

    );


    //关卡
    @POST("/idiom/v1/level")
    Observable<OfficialLevelEntity> requestOfficialLevel(
            @Header("Cache-Control") String cacheControl,
            @Body Map<String, Object> map
    );


    //官阶列表
    @GET("/idiom/idiomRank/v1/officialRank")
    Observable<OfficialRankEntity> requestOfficialRank(
            @Header("Cache-Control") String cacheControl,
            @QueryMap Map<String, Object> map

    );


    //默认广告
    @GET("/operation/defaultAd/v1/info")
    Observable<DefaultAdEntity> requestDefaultAd(
            @Header("Cache-Control") String cacheControl,
            @QueryMap Map<String, Object> map

    );


    //用户签到
    @POST("/operation/sign/v1/doSign")
    Observable<SignEntity> requestSign(
            @Header("Cache-Control") String cacheControl,
            @Body Map<String, Object> map

    );

    //用户签到v2
    @POST("/operation/sign/v2/sign")
    Observable<SignEntity> requestSignV2(
            @Header("Cache-Control") String cacheControl,
            @Body Map<String, Object> map

    );

    //查询用户签到情况
    @GET("/operation/sign/v1/info")
    Observable<SignStatusEntity> requestSignStatus(
            @Header("Cache-Control") String cacheControl,
            @QueryMap Map<String, Object> map

    );

    //V2领取新人红包 判断是否显示新人福利
    @POST("/user/user/v2/receive")
    Observable<NewUserReceive> requestNewUserReceive(
            @Header("Cache-Control") String cacheControl,
            @Body Map<String, Object> map

    );

    //V2 用户绑定微信
    @POST("/v2/login/binding")
    Observable<LoginBindingEntity> requestLoginBinding(
            @Header("Cache-Control") String cacheControl,
            @Body Map<String, Object> map

    );

    //V2 用户提现
    @POST("/user/user/v2/cash")
    Observable<UserWatchEntity> requestUserWatch(
            @Header("Cache-Control") String cacheControl,
            @Body Map<String, Object> map

    );



    //首页各种广告查询
    @GET("/operation/otherAd/v1/info")
    Observable<OperationAdEntity> requestAdInfos(
            @Header("Cache-Control") String cacheControl,
            @QueryMap Map<String, Object> map

    );


    //打款列表分页查询v2
    @GET("/pay/payRecord/v2/page")
    Observable<PayRecordV2Entity> requestPayRecordV2(
            @Header("Cache-Control") String cacheControl,
            @QueryMap Map<String, Object> map

    );


    //步数兑换-奖励
    @POST("/operation/otherAd/v1/convert")
    Observable<mainAddGoldEntity> requestMainAddGold(
            @Header("Cache-Control") String cacheControl,
            @Body Map<String, Object> map

    );

    //步数兑换-奖励v2
    @POST("/user/user/v2/exchange")
    Observable<mainAddGoldEntity> requestMainAddGoldV2(
            @Header("Cache-Control") String cacheControl,
            @Body Map<String, Object> map

    );


    //成语填空
    @POST("/idiom/keyword/v1/idiom")
    Observable<GameInfoEntity> requestGameInfo(
            @Header("Cache-Control") String cacheControl,
            @Body Map<String, Object> map
    );

    //翻倍
    @GET("/user/userRecord/v1/double")
    Observable<DouableMoneyEntity> requestDouableMoney(
            @Header("Cache-Control") String cacheControl,
            @QueryMap Map<String, Object> map

    );


    //开始抽奖
    @POST("/operation/luckyDraw/v1/start")
    Observable<StartLotteryEntity> requestStartLottery(
            @Header("Cache-Control") String cacheControl,
            @Body Map<String, Object> map
    );

    //开始抽奖v2
    @POST("/operation/luckyDraw/v2/start")
    Observable<StartLotteryEntity> requestStartLotteryV2(
            @Header("Cache-Control") String cacheControl,
            @Body Map<String, Object> map
    );


    //意见反馈
    @POST("/operation/feedback/v1/save")
    Observable<BaseRespose> requestFeedBack(
            @Header("Cache-Control") String cacheControl,
            @Body Map<String, Object> map
    );



    //签到状态
    @GET("/operation/sign/v2/info")
    Observable<signStatusV2Entity> requestSignStatusV2(
            @Header("Cache-Control") String cacheControl,
            @QueryMap Map<String, Object> map

    );


    //抽奖信息
    @GET("/operation/luckyDraw/v1/info")
    Observable<LotterNumEntity> requestLotteryNum(
            @Header("Cache-Control") String cacheControl,
            @QueryMap Map<String, Object> map

    );
    //抽奖信息v2
    @GET("/operation/luckyDraw/v2/info")
    Observable<LotterNumEntity> requestLotteryNumV2(
            @Header("Cache-Control") String cacheControl,
            @QueryMap Map<String, Object> map

    );

    //用户信息
    @GET("/user/user/v1/info")
    Observable<MineEntity> requestMineInfo(
            @Header("Cache-Control") String cacheControl,
            @QueryMap Map<String, Object> map

    );

    //获取步数
    @GET("/user/user/v2/step/info")
    Observable<StepEntity> requestUserStep(
            @Header("Cache-Control") String cacheControl,
            @QueryMap Map<String, Object> map

    );


    //更新
    @GET("/app/appVersion/v1/page")
    Observable<UpdateEntity> requestUppApp(
            @Header("Cache-Control") String cacheControl,
            @QueryMap Map<String, Object> map

    );

    //@FormUrlEncoded
    @POST("/v1/login")
    Observable<LoginEntity> requestLogin(
            @Header("Cache-Control") String cacheControl,
            //@PartMap Map<String, RequestBody> requestBodyMap
           // @FieldMap Map<String, Object> map
            @Body Map<String, Object> map
    );

    //修改用户信息
    @POST("/user/user/v1/update")
    Observable<UppUserEntity> requestUppLogin(
            @Header("Cache-Control") String cacheControl,
            @Body Map<String, Object> map
    );


    @POST("/v1/wxlogin")
    Observable<WxLoginEntity> requestWxLogin(
            @Header("Cache-Control") String cacheControl,
            //@PartMap Map<String, RequestBody> requestBodyMap
            // @FieldMap Map<String, Object> map
            @Body Map<String, Object> map
    );
*/


}