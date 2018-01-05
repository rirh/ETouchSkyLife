package com.etouchsky.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.Vibrator;
import android.provider.Settings;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ddclient.configuration.DongConfiguration;
import com.ddclient.dongsdk.AbstractDongCallbackProxy.DongAccountCallbackImp;
import com.ddclient.dongsdk.AbstractDongCallbackProxy.DongDeviceCallbackImp;
import com.ddclient.dongsdk.AbstractDongCallbackProxy.DongDeviceSettingCallbackImp;
import com.ddclient.dongsdk.DeviceInfo;
import com.ddclient.dongsdk.DongSDKProxy;
import com.ddclient.jnisdk.InfoUser;
import com.etouchsky.util.Config;
import com.etouchsky.util.FileUtils;
import com.etouchsky.util.HttpUtil;
import com.etouchsky.util.OkAndGsonUtil;
import com.etouchsky.util.PlayVedio;
import com.etouchsky.widget.WarnDialog;
import com.etouchsky.wisdom.MainActivity;
import com.etouchsky.wisdom.R;
import com.gViewerX.util.LogUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

import org.apache.http.entity.StringEntity;

import java.io.UnsupportedEncodingException;

import static com.etouchsky.gs.ETService.sensor_shake;

public class VideoViewActivity extends Activity implements OnClickListener {
    public static boolean VideoViewActivityFlag = false;
    private TextView mTySpk;
    private TextView mTvVideo;
    private TextView mTvStop;
    private ImageView mIvHangup;
    private ImageView iv_unlock;
    private ImageView iv_talk;
    private ImageView iv_audio;
    private ImageView mIvAccept;
    private ImageView iv_snap;
    private TextView mTvAudio;
    private TextView tv_unlock;
    private TextView tv_accpt;
    LinearLayout accept_layout;
    private SurfaceView mSurfaceView;
    private boolean isVideoOn;
    private boolean isSoundOn;
    private boolean isMicroOn;
    public static MediaPlayer bell = null;
    // 正在播放的设备
    private DeviceInfo mDeviceInfo;
    PowerManager pm;
    private PowerManager.WakeLock wl;
    private VideoViewActivityDongDeviceCallBackImpl mDeviceCallBackImpl
            = new VideoViewActivityDongDeviceCallBackImpl();
    private VideoViewActivityDongDeviceSettingImpl mDeviceSettingImpl
            = new VideoViewActivityDongDeviceSettingImpl();
    private SensorManager sensorManager;
    private Vibrator vibrator;
    boolean managercall;
    private static final int SENSOR_SHAKE = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("****","activity");
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDeviceInfo = DongConfiguration.mDeviceInfo;
        if (mDeviceInfo == null && this.getIntent().hasExtra("call")) { //by gs 2017/8/28 防止后台运行时，挂断了还会出现继续被叫和通话状态
            Intent mintent = new Intent(VideoViewActivity.this, MainActivity.class);
            mintent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(mintent);
            this.finish();
            return;
        }
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Config.setNoTheme(this);
        setContentView(R.layout.activity_video_view);
//        对讲
        mTySpk = (TextView) findViewById(R.id.tv_spk);
        iv_talk = (ImageView) findViewById(R.id.iv_talk);

        mTvAudio = (TextView) findViewById(R.id.tv_audio);
        iv_audio = (ImageView) findViewById(R.id.iv_audio);

        mTvVideo = (TextView) findViewById(R.id.tv_video);
        iv_snap = (ImageView) findViewById(R.id.iv_snap);
//        挂断
        mTvStop = (TextView) findViewById(R.id.tv_stop);
        mIvHangup = (ImageView) findViewById(R.id.iv_hangup);
//        开锁
        tv_unlock = (TextView) findViewById(R.id.tv_unlock);
        iv_unlock = (ImageView) findViewById(R.id.iv_unlock);
//        接听
        tv_accpt = (TextView) findViewById(R.id.tv_accpt);
        mIvAccept = (ImageView) findViewById(R.id.iv_accept);


        mSurfaceView = (SurfaceView) findViewById(R.id.sfv_play);
        mTySpk.setOnClickListener(this);
        iv_talk.setOnClickListener(this);

        mTvAudio.setOnClickListener(this);
        iv_audio.setOnClickListener(this);

        mTvVideo.setOnClickListener(this);
        iv_snap.setOnClickListener(this);

        mTvStop.setOnClickListener(this);
        mIvHangup.setOnClickListener(this);

        tv_unlock.setOnClickListener(this);
        iv_unlock.setOnClickListener(this);

        tv_accpt.setOnClickListener(this);
        mIvAccept.setOnClickListener(this);
        accept_layout = (LinearLayout) findViewById(R.id.accept_layout);

        pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        if (wl == null) {
            wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "PhoneWindowManager.mBroadcastWakeLock");
        }
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        if (this.getIntent().hasExtra("call")) {
            accept_layout.setVisibility(View.VISIBLE);
            initializeSounds();
            playSound(bell);
            wl.acquire();
        }
        IntentFilter myFilter = new IntentFilter();
        myFilter.addAction(sensor_shake);//摇一摇
        this.registerReceiver(myBroadCast, myFilter);
        managercall = this.getIntent().hasExtra("managercall");
        if (managercall) {
            findViewById(R.id.activity_video_view_text_open).setVisibility(View.GONE);
            findViewById(R.id.iv_unlock).setVisibility(View.GONE);
            findViewById(R.id.tv_unlock).setVisibility(View.GONE);
            findViewById(R.id.iv_snap).setVisibility(View.GONE);
            findViewById(R.id.tv_video).setVisibility(View.GONE);
            findViewById(R.id.iv_talk).setVisibility(View.GONE);
            findViewById(R.id.tv_spk).setVisibility(View.GONE);
        }
    }

    public BroadcastReceiver myBroadCast = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (sensor_shake.equals(action)) {
                unlock();
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        VideoViewActivityFlag = true;
        mDeviceInfo = DongConfiguration.mDeviceInfo;
        if (mDeviceInfo == null) {
            this.finish();
        }
        LogUtils.i("VideoViewActivity.clazz--->>>onResume.... mDeviceInfo:" + mDeviceInfo);
        videoPlay();
    }

    @Override
    protected void onPause() {
        super.onPause();
        DongSDKProxy.unRegisterDongDeviceCallback(mDeviceCallBackImpl);
        DongSDKProxy.unRegisterDongDeviceSettingCallback(mDeviceSettingImpl);
        LogUtils.i("VideoViewActivity.clazz--->>>onPause .... mDeviceInfo:" + mDeviceInfo);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.e("****","销毁了activity");
        VideoViewActivityFlag = false;
        Log.d("", "VideoViewActity onDestroy");
        DongConfiguration.mDeviceInfo = null;
        stopVideo();
        try {
            this.unregisterReceiver(myBroadCast);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void wakeUp() {
        if (wl == null) {
            wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP,
                    "PhoneWindowManager.mBroadcastWakeLock");
        }
        int timeOut = -1;
        try {
            timeOut = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            wl.setReferenceCounted(false);
            wl.release();
            wl.acquire((long) timeOut);
        } catch (Exception e) {
            e.printStackTrace();
            // TODO: handle exception
        }
        // wl.release();
        // wl=null;
    }

    private ProgressDialog dialog;

    public void showProgressDialog() {
            if (dialog == null)
                dialog = ProgressDialog.show(VideoViewActivity.this, "", "正在缓冲数据数据30%", false, true);

    }

    public void closeProgressDialog() {
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }

    private void videoPlay() {
        showProgressDialog();
        Log.e("****","执行了videoplay");
        LogUtils.i("VideoViewActivity.clazz--->>>videoPlay.... mDeviceInfo:" + mDeviceInfo);
        if (mDeviceInfo == null) {
            Toast.makeText(VideoViewActivity.this, getString(R.string.PLEASE_FIRST_SELECT_CAMERA),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        boolean initDongAccountLan = DongSDKProxy.initCompleteDongAccountLan();
        LogUtils.i("VideoViewActivity.clazz--->>>videoPlay ..... registerAccountCallback");
        boolean initCompleteDongDevice = DongSDKProxy.initCompleteDongDevice();
        if (!initCompleteDongDevice) {
            DongSDKProxy.initDongDevice(mDeviceCallBackImpl);
        } else {
            DongSDKProxy.registerDongDeviceCallback(mDeviceCallBackImpl);
        }
        LogUtils.i("VideoViewActivity.clazz--->>>videoPlay ..... initDongDevice");
        boolean completeDongDeviceSetting = DongSDKProxy.initCompleteDongDeviceSetting();
        if (!completeDongDeviceSetting) {
            DongSDKProxy.initDongDeviceSetting(mDeviceSettingImpl);
        } else {
            DongSDKProxy.registerDongDeviceSettingCallback(mDeviceSettingImpl);
        }
        LogUtils.i("VideoViewActivity.clazz--->>>videoPlay ..... initDongDeviceSetting");
        // ////////////////////////////////////////////
        DongSDKProxy.requestStartPlayDevice(this, mSurfaceView, mDeviceInfo);
        // mTvVideo.setBackgroundResource(R.color.blue);
        DongSDKProxy.requestRealtimePlay(DongSDKProxy.PLAY_TYPE_VIDEO);
        LogUtils.i("VideoViewActivity.clazz--->>>videoPlay ..... initCompleteDongDevice:"
                + initCompleteDongDevice + ",completeDongDeviceSetting:" + completeDongDeviceSetting);
    }

    private void stopVideo() {
        //mTySpk.setBackgroundResource(R.color.gray);
        // mTvVideo.setBackgroundResource(R.color.gray);
        //mTvAudio.setBackgroundResource(R.color.gray);
        // if (isSoundOn) {
        CloseSpeaker();
        DongSDKProxy.requestClosePhoneSound();// 关闭手机音响
        //}
        //if (isMicroOn) {
        DongSDKProxy.requestClosePhoneMic();// 关闭 手机麦克风
        // }
        DongSDKProxy.requestStop(DongSDKProxy.PLAY_TYPE_VIDEO);// 关闭设备摄像头
        DongSDKProxy.requestRealtimePlay(DongSDKProxy.PLAY_TYPE_AUDIO_USER);// 关闭设备音响
        if (mDeviceCallBackImpl != null) {
            isSoundOn = false;
            isMicroOn = false;
            isVideoOn = false;
        }
        DongSDKProxy.requestStopDeice();
        destroySounds();
        if (pm != null && !pm.isScreenOn()) {
            wakeUp();
        }
    }

    private void initializeSounds() {
        if (bell == null) {
            Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            // 使用此方法创建MediaPlayer,PrePare()已经被调用，MediaPlayer进入PrePared状态
            // bell= MediaPlayer.create(this, R.raw.ring);
            if (alert == null || "".equals(alert)) {

            } else {
                bell = new MediaPlayer();
                try {
                    bell.setDataSource(this, alert);
                    bell.setAudioStreamType(AudioManager.STREAM_RING);
                    bell.setLooping(true); // 循环播放开
                    bell.prepare();
                } catch (Exception e) {
                    // TODO: handle exception
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * {一句话功能简述} 循环播放 {功能详细描述}
     *
     * @param mp
     * @hide
     */
    private void playSound(MediaPlayer mp) {
        mp.seekTo(0);
        mp.start();
        bell.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            // @Override
            public void onCompletion(MediaPlayer arg0) {
                try {
                    // bell.seekTo(0);
                    bell.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 停止播放音频
     */
    public static void destroySounds() {
        try {
            if (!"".equals(bell)) {
                bell.stop();
                bell.release();
                bell = null;
            }
        } catch (Exception e) {

        }
    }

    public int currVolume;

    //打开扬声器
    public void OpenSpeaker() {

        try {
            AudioManager audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
            audioManager.setMode(AudioManager.ROUTE_SPEAKER);
            currVolume = audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);

            if (!audioManager.isSpeakerphoneOn()) {
                audioManager.setSpeakerphoneOn(true);

                audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,
                        audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL),
                        AudioManager.STREAM_VOICE_CALL);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //关闭扬声器
    public void CloseSpeaker() {

        try {
            AudioManager audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
            if (audioManager != null) {
                if (audioManager.isSpeakerphoneOn()) {
                    audioManager.setSpeakerphoneOn(false);
                    audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, currVolume,
                            AudioManager.STREAM_VOICE_CALL);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    Long lastClickTime = 0l;

    public void unlock() {
        Long time = System.currentTimeMillis();
        if ((time - lastClickTime) > 3000) {
            lastClickTime = time;
            int result = DongSDKProxy.requestDOControl();
            if (result == 0 && !managercall) {
                Toast.makeText(this, "门开了", Toast.LENGTH_SHORT).show();
            }
        } else if (!managercall) {
            Toast.makeText(this, "您的操作太频繁了，请稍后在试。。。", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.tv_audio:
            case R.id.iv_audio:
                if (!isSoundOn) {
                    OpenSpeaker();
                    DongSDKProxy.requestOpenPhoneSound();// 打开手机音响
                    DongSDKProxy.requestRealtimePlay(DongSDKProxy.PLAY_TYPE_AUDIO);// 打开设备麦克风
                    //mTvAudio.setBackgroundResource(R.color.blue);
                    iv_audio.setBackgroundResource(R.mipmap.hand_free_pressed);
                    isSoundOn = true;
                } else {
                    iv_audio.setBackgroundResource(R.mipmap.hand_free_normal);
                    CloseSpeaker();
                    //if (isMicroOn) {
                    //  mTySpk.performClick();
                    // }
                    //DongSDKProxy.requestClosePhoneSound();// 关闭手机音响
                    // DongSDKProxy.requestStop(DongSDKProxy.PLAY_TYPE_AUDIO);// 关闭设备麦克风
                    // mTvAudio.setBackgroundResource(R.color.gray);
                    isSoundOn = false;
                }
                break;
            case R.id.tv_spk:
            case R.id.iv_talk:
                accpt();
                break;
            case R.id.tv_video:
            case R.id.iv_snap:
                /*if (!isVideoOn) {
                    DongSDKProxy.requestRealtimePlay(DongSDKProxy.PLAY_TYPE_VIDEO);// 打开设备摄像头
                    mTvVideo.setBackgroundResource(R.color.blue);
                    isVideoOn = true;
                } else {
                    DongSDKProxy.requestStop(DongSDKProxy.PLAY_TYPE_VIDEO);// 关闭设备摄像头
                    mTvVideo.setBackgroundResource(R.color.gray);
                    isVideoOn = false;
                }*/
                DongSDKProxy.requestTakeOnePicture("etouchsky", mDeviceInfo);
                PlayVedio.playVedio(VideoViewActivity.this, R.raw.picture_sound);
                break;
            case R.id.tv_stop:
            case R.id.iv_hangup:
                DongSDKProxy.requestTakePicture("Viewer", mDeviceInfo);// 拍照截图
                // PS:Viewer手机根目录下的Viewer文件夹
                stopVideo();
                //正在响铃时挂断

                if (this.getIntent().hasExtra("call")) {
                    sendOutdoorHandup();
                }
                VideoViewActivity.this.finish();
                break;
            case R.id.tv_unlock:
            case R.id.iv_unlock:
                unlock();
                break;
            case R.id.tv_accpt:
            case R.id.iv_accept:
                accept_layout.setVisibility(View.GONE);
                destroySounds();
                OpenSpeaker();
                isMicroOn = true;
                isSoundOn = true;
                iv_audio.setPressed(true);
                iv_talk.setBackgroundResource(R.mipmap.talk_pressed);

                //tv_accpt.setBackgroundResource(R.color.blue);
                //mTvAudio.setBackgroundResource(R.color.blue);
                DongSDKProxy.requestOpenPhoneSound();// 打开手机音响
                DongSDKProxy.requestOpenPhoneMic();// 打开手机麦克风
                DongSDKProxy.requestRealtimePlay(DongSDKProxy.PLAY_TYPE_AUDIO);// 打开设备音响
                break;
        }
    }

    public void accpt() {
        if (isMicroOn) return;
        if (!isSoundOn) {
            //Toast.makeText(VideoViewActivity.this, "请先打开语音按钮！",
            //   Toast.LENGTH_SHORT).show();
            //return;
            DongSDKProxy.requestOpenPhoneSound();// 打开手机音响
            DongSDKProxy.requestRealtimePlay(DongSDKProxy.PLAY_TYPE_AUDIO);// 打开设备麦克风
            OpenSpeaker();
            isSoundOn = true;
            iv_audio.setPressed(true);
            iv_talk.setBackgroundResource(R.mipmap.talk_pressed);
        }
        if (!isMicroOn) {
            DongSDKProxy.requestOpenPhoneMic();// 打开手机麦克风
            DongSDKProxy.requestRealtimePlay(DongSDKProxy.PLAY_TYPE_AUDIO);// 打开设备音响
            //mTySpk.setBackgroundResource(R.color.blue);
            isMicroOn = true;
        }
    }

    public void sendOutdoorHandup() {
        final String url = HttpUtil.handup;
        Log.e("****", "挂断地址:" + mDeviceInfo.deviceSerialNO);
        RequestParams params = new RequestParams("UTF-8");
        try {
            params.setBodyEntity(new StringEntity("{\"cameraID\":\"" + mDeviceInfo.deviceSerialNO + "\"}", "UTF-8"));
            params.setContentType("applicatin/json");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

//        params.setAsJsonContent(true);
//        params.setBodyContent("{\"username\":\"test\",\"password\":\"test\"}");
//        params.addHeader("Content-Type", "application/json");
        Config.http.send(HttpMethod.POST, url, params,
                new RequestCallBack<String>() {
                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onLoading(long total, long current,
                                          boolean isUploading) {
                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        try {
//                            System.out.println("http---onSuccess");
                            Log.e("****", "http---onFailure");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(
                            com.lidroid.xutils.exception.HttpException arg0,
                            String msg) {
//                        System.out.println("http---onFailure"+msg);
                        Log.e("****", "http---onFailure" + "msg:" + msg);
                    }
                });
    }

    private class VideoViewActivityDongAccountCallbackImp extends
            DongAccountCallbackImp {

        @Override
        public int onAuthenticate(InfoUser tInfo) {
            LogUtils.i("VideoViewActivityDongAccountCallbackImp.clazz--->>>onAuthenticate........tInfo:"
                    + tInfo);
            return 0;
        }

        @Override
        public int onUserError(int nErrNo) {
            LogUtils.i("VideoViewActivityDongAccountCallbackImp.clazz--->>>onUserError........nErrNo:"
                    + nErrNo);
            return 0;
        }
    }

    private class VideoViewActivityDongDeviceSettingImpl extends
            DongDeviceSettingCallbackImp {

        @Override
        public int onOpenDoor(int result) {
            return 0;
        }
    }

    private class VideoViewActivityDongDeviceCallBackImpl extends
            DongDeviceCallbackImp {

        @Override
        public int onConnect(int nType) {
            LogUtils.i("VideoViewActivityDongDeviceCallBackImpl.clazz--->>>onConnect nType:"
                    + nType);
            return 0;
        }

        int curtimes = 0;

        @Override
        public int onAuthenticate(int nType) {// 认证成功会回调两次-----音频认证成功，视频认证成功
            // 获取音频大小
            int audioSize = DongSDKProxy.requestGetAudioQuality();
            // 获取设备亮度
            int bCHS = DongSDKProxy.requestGetBCHS();
            // 获取视频品质
            int quality = DongSDKProxy.requestGetQuality();
            LogUtils.i("VideoViewActivityDongDeviceCallBackImpl.clazz--->>>onAuthenticate nType:"
                    + nType + ",audioSize:" + audioSize + ",bCHS:" + bCHS + ",quality:" + quality);
            if (curtimes == 0) {
                dialog.setMessage("正在缓冲数据60%");
                curtimes = 1;
            } else if (curtimes == 1) {
                dialog.setMessage("正在缓冲数据90%");
            }
            return 0;
        }

        @Override
        public int onVideoSucc() {
            LogUtils.i("VideoViewActivityDongDeviceCallBackImpl.clazz--->>>onVideoSucc");
            closeProgressDialog();
            if (managercall) {
                accpt();
            }
            isVideoOn = true;
            return 0;
        }

        @Override
        public int onViewError(int nErrNo) {
            LogUtils.i("VideoViewActivityDongDeviceCallBackImpl.clazz--->>>onViewError...nErrNo:"
                    + nErrNo);
            if (isVideoOn) {
                stopVideo();
            }
            WarnDialog.showDialog(VideoViewActivity.this, null, nErrNo);
            return 0;
        }

        public int num;

        @Override
        public int onTrafficStatistics(float upload, float download) {
            LogUtils.i("VideoViewActivityDongDeviceCallBackImpl.clazz--->>>onTrafficStatistics upload:"
                    + upload + ";download:" + download + "=");
            float minDownload = 0;
            if (minDownload == minDownload && num == 10) {//重新请求一次视频
                videoPlay();
            } else if (download <= minDownload && num == 42) {
                //Toast.makeText(VideoViewActivity.this,"网络异常！请检查网络",2000).show();
                FileUtils.alertTextNoCancel(VideoViewActivity.this, "", "当前网络不佳请稍后再试！", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                dialog.dismiss();
                                stopVideo();
                                VideoViewActivity.this.finish();
                                break;
                        }
                    }
                });
            } else if (download <= minDownload && num > 50) {//超时后自动退出
                stopVideo();
                VideoViewActivity.this.finish();
            }
            if (download > minDownload) {
                num = 0;
            } else {
                num += 1;
            }
            return 0;
        }

        @Override
        public int onPlayError(int nReason, String username) {
            LogUtils.i("VideoViewActivityDongDeviceCallBackImpl.clazz--->>>onPlayError nReason:"
                    + nReason + ";username:" + username);
            if (isVideoOn && nReason == 2) {
                stopVideo();
                Toast.makeText(VideoViewActivity.this, "对方挂断", Toast.LENGTH_SHORT).show();
                VideoViewActivity.this.finish();
            }
            return 0;
        }
    }
}
