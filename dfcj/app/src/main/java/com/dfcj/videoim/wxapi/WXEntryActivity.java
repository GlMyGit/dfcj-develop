package com.dfcj.videoim.wxapi;

public class WXEntryActivity {}

//		extends AppCompatActivity implements IWXAPIEventHandler{
//	private static String TAG = "DebugLog";
//
//    private IWXAPI api;
//    private MyHandler handler;
//
//	private class MyHandler extends Handler {
//
//		private final WeakReference<WXEntryActivity> wxEntryActivityWeakReference;
//		private String openId="", accessToken="", refreshToken="", scope="",unionid="",refresh_token="";
//
//		public MyHandler(WXEntryActivity wxEntryActivity){
//			wxEntryActivityWeakReference = new WeakReference<WXEntryActivity>(wxEntryActivity);
//		}
//
//		@Override
//		public void handleMessage(Message msg) {
//			//LogUtils.logd("微信登录返回22：");  新版本来临了，升级获取更多金币哦
//			//头条    https://oss.jibu100.com/niu/2021/04/2bbfad38b3e449b097e32fecd41d4b45.apk
//			//360   https://oss.jibu100.com/niu/2021/04/17cc1e91531242c795c2c8860462c810.apk
//			//华为  https://oss.jibu100.com/niu/2021/04/8a47415d45a34e25ba75be2de80be698.apk
//			int tag = msg.what;
//			switch (tag) {
//				case NetworkUtil.GET_TOKEN: {
//					LogUtils.logd("微信登录返回11：");
//					Bundle data = msg.getData();
//
//					JSONObject json = null;
//					try {
//
//						String result = data.getString("result");
//						LogUtils.logd("微信登录返回："+result);
//
//						if(!TextUtils.isEmpty(result)){
//
//							json = new JSONObject(result);
//							openId = json.getString("openid");
//							unionid = json.getString("unionid");
//							accessToken = json.getString("access_token");
//							refresh_token = json.getString("refresh_token");
//
//							wxToLogin(""+openId,""+unionid,""+accessToken,""+refresh_token);
//
//						}
//
//						//accessToken = json.getString("access_token");
//						//refreshToken = json.getString("refresh_token");
//						//scope = json.getString("scope");
//
//
//					/*	Intent intent = new Intent(wxEntryActivityWeakReference.get(), SendToWXActivity.class);
//						intent.putExtra("openId", openId);
//						intent.putExtra("accessToken", accessToken);
//						intent.putExtra("refreshToken", refreshToken);
//						intent.putExtra("scope", scope);
//						wxEntryActivityWeakReference.get().startActivity(intent);*/
//
//
//
//
//					} catch (JSONException e) {
//						LogUtils.logd("错误："+ e.getMessage());
//					}
//				}
//			}
//		}
//	}
//
//
//	public  void wxToLogin(String openId,String unionid,String access_token,String refresh_token){
//
//
//		//用户绑定微信
//
//		TreeMap<String, Object> map = new TreeMap<>();
//		map.put("openid", "" + openId);//
//		map.put("unionid", "" + unionid);//
//		map.put("sysVersion", "" + AppUtils.getVersionNameInfo(this));//
//		map.put("accessToken", "" + access_token);//
//		map.put("refreshToken", "" + refresh_token);//
//
///*
//
//		new RxManager().add(Api.getDefault(HostType.lOAN_STEWARD_MAIN_HOST)
//				.requestLoginBinding(Api.getCacheControl(),map)
//				.compose(RxSchedulers.<LoginBindingEntity>io_main())
//				.subscribe(new MyRxSubscriber<LoginBindingEntity>(MyAppliaction.mContext,"加载中",true){
//
//					@Override
//					protected void _onNext(LoginBindingEntity wxLoginEntity) {
//						if(wxLoginEntity!=null && wxLoginEntity.getCode()==1){
//
//							SharedPrefsUtils.putValue(AppConstant.USERopenid,""+openId);
//
//							EventBusUtils.post(new EventMessage("wxloginback"));
//							//finish();
//						}else{
//							EventBusUtils.post(new EventMessage("wxloginbackerror",""+wxLoginEntity.getMsg()));
//							//EToastUtils.show(""+wxLoginEntity.getMsg());
//						}
//						finish();
//					}
//
//					@Override
//					protected void _onError(String message) {
//
//					}
//				}));
//*/
//
//
//	}
//
//	@Subscribe
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//    	api = WXAPIFactory.createWXAPI(this, Constants.APP_ID, false);
//		handler = new MyHandler(this);
//
//		EventBusUtils.register(this);
//
//		//LogUtils.logd("微信登录返回33：");
//
//
//        try {
//            Intent intent = getIntent();
//        	api.handleIntent(intent, this);
//        } catch (Exception e) {
//        	e.printStackTrace();
//        }
//	}
//
//	@Override
//	protected void onDestroy() {
//		super.onDestroy();
//		EventBusUtils.unregister(this);
//	}
//
//	@Override
//	protected void onNewIntent(Intent intent) {
//		super.onNewIntent(intent);
//		//LogUtils.logd("微信登录返回44：");
//		setIntent(intent);
//        api.handleIntent(intent, this);
//	}
//
//	@Override
//	public void onReq(BaseReq req) {
//		//LogUtils.logd("微信登录返回55：");
//
//
//		switch (req.getType()) {
//		case ConstantsAPI.COMMAND_GETMESSAGE_FROM_WX:
//			goToGetMsg();
//			break;
//		case ConstantsAPI.COMMAND_SHOWMESSAGE_FROM_WX:
//			goToShowMsg((ShowMessageFromWX.Req) req);
//			break;
//		default:
//			break;
//		}
//        finish();
//	}
//
//	@Override
//	public void onResp(BaseResp resp) {
//		String result = "";
//		//LogUtils.logd("微信登录返回66：");
//		switch (resp.errCode) {
//		case BaseResp.ErrCode.ERR_OK:
//			result = getResources().getString(R.string.errcode_success);
//			break;
//		case BaseResp.ErrCode.ERR_USER_CANCEL:
//			result =  getResources().getString(R.string.errcode_cancel);
//			break;
//		case BaseResp.ErrCode.ERR_AUTH_DENIED:
//			result =  getResources().getString(R.string.errcode_deny);
//			break;
//		case BaseResp.ErrCode.ERR_UNSUPPORT:
//			result =  getResources().getString(R.string.errcode_unsupported);
//			break;
//		default:
//			result =  getResources().getString(R.string.errcode_unknown);
//			break;
//		}
//
//		//LogUtils.logd("微信登录返回661："+result);
//		//Toast.makeText(this, getString(result) + ", type=" + resp.getType(), Toast.LENGTH_SHORT).show();
//
//		if(WechatUtils.getInstance().getListener()!=null){
//			WechatUtils.getInstance().setResponse(resp);
//		}
//
//		if (resp.getType() == ConstantsAPI.COMMAND_SUBSCRIBE_MESSAGE) {
//			SubscribeMessage.Resp subscribeMsgResp = (SubscribeMessage.Resp) resp;
//			String text = String.format("openid=%s\ntemplate_id=%s\nscene=%d\naction=%s\nreserved=%s",
//					subscribeMsgResp.openId, subscribeMsgResp.templateID, subscribeMsgResp.scene, subscribeMsgResp.action, subscribeMsgResp.reserved);
//
//		}
//
//        if (resp.getType() == ConstantsAPI.COMMAND_LAUNCH_WX_MINIPROGRAM) {
//            WXLaunchMiniProgram.Resp launchMiniProgramResp = (WXLaunchMiniProgram.Resp) resp;
//            String text = String.format("openid=%s\nextMsg=%s\nerrStr=%s",
//                    launchMiniProgramResp.openId, launchMiniProgramResp.extMsg,launchMiniProgramResp.errStr);
//
//        }
//
//        if (resp.getType() == ConstantsAPI.COMMAND_OPEN_BUSINESS_VIEW) {
//            WXOpenBusinessView.Resp launchMiniProgramResp = (WXOpenBusinessView.Resp) resp;
//            String text = String.format("openid=%s\nextMsg=%s\nerrStr=%s\nbusinessType=%s",
//                    launchMiniProgramResp.openId, launchMiniProgramResp.extMsg,launchMiniProgramResp.errStr,launchMiniProgramResp.businessType);
//
//        }
//
//        if (resp.getType() == ConstantsAPI.COMMAND_OPEN_BUSINESS_WEBVIEW) {
//            WXOpenBusinessWebview.Resp response = (WXOpenBusinessWebview.Resp) resp;
//            String text = String.format("businessType=%d\nresultInfo=%s\nret=%d",response.businessType,response.resultInfo,response.errCode);
//
//        }
//
//		if (resp.getType() == ConstantsAPI.COMMAND_SENDAUTH) {
//			SendAuth.Resp authResp = (SendAuth.Resp)resp;
//			final String code = authResp.code;
//			NetworkUtil.sendWxAPI(handler, String.format("https://api.weixin.qq.com/sns/oauth2/access_token?" +
//							"appid=%s&secret=%s&code=%s&grant_type=authorization_code", ""+ com.dfcj.videoshop.wx.Constants.APP_ID,
//					""+ com.dfcj.videoshop.wx.Constants.APP_Sc, code), NetworkUtil.GET_TOKEN);
//		}
//
//
//
//
//        finish();
//
//	}
//
//	private void goToGetMsg() {
//		/*Intent intent = new Intent(this, GetFromWXActivity.class);
//		intent.putExtras(getIntent());
//		startActivity(intent);
//		finish();*/
//	}
//
//	private void goToShowMsg(ShowMessageFromWX.Req showReq) {
//		/*WXMediaMessage wxMsg = showReq.message;
//		WXAppExtendObject obj = (WXAppExtendObject) wxMsg.mediaObject;
//
//		StringBuffer msg = new StringBuffer();
//		msg.append("description: ");
//		msg.append(wxMsg.description);
//		msg.append("\n");
//		msg.append("extInfo: ");
//		msg.append(obj.extInfo);
//		msg.append("\n");
//		msg.append("filePath: ");
//		msg.append(obj.filePath);
//
//		Intent intent = new Intent(this, ShowFromWXActivity.class);
//		intent.putExtra(Constants.ShowMsgActivity.STitle, wxMsg.title);
//		intent.putExtra(Constants.ShowMsgActivity.SMessage, msg.toString());
//		intent.putExtra(Constants.ShowMsgActivity.BAThumbData, wxMsg.thumbData);
//		startActivity(intent);
//		finish();*/
//	}
//}