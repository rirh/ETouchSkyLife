package com.etouchsky.gs;

import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

import com.ddclient.configuration.DongConfiguration;
import com.ddclient.dongsdk.AbstractDongCallbackProxy;
import com.ddclient.dongsdk.AbstractDongCallbackProxy.DongAccountCallbackImp;
import com.ddclient.dongsdk.DeviceInfo;
import com.ddclient.dongsdk.DongSDK;
import com.ddclient.dongsdk.DongSDKProxy;
import com.ddclient.dongsdk.PushInfo;
import com.ddclient.jnisdk.InfoUser;
import com.ddclient.push.DongPushMsgManager;
import com.etouchsky.GViewerXApplication;
import com.etouchsky.activity.VideoViewActivity;
import com.etouchsky.baseble.ViseBluetooth;
import com.etouchsky.baseble.callback.IConnectCallback;
import com.etouchsky.baseble.callback.data.ICharacteristicCallback;
import com.etouchsky.baseble.callback.scan.PeriodLScanCallback;
import com.etouchsky.baseble.callback.scan.PeriodScanCallback;
import com.etouchsky.baseble.exception.BleException;
import com.etouchsky.baseble.model.BluetoothLeDevice;
import com.etouchsky.baseble.utils.BleUtil;
import com.etouchsky.baseble.utils.HexUtil;
import com.etouchsky.serialprot.SerialPortSdk;
import com.etouchsky.util.ClsUtils;
import com.etouchsky.util.PlayVedio;
import com.etouchsky.wisdom.R;
import com.gViewerX.util.LogUtils;
import com.vise.log.ViseLog;
import com.vise.log.inner.LogcatTree;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

import static android.bluetooth.BluetoothDevice.ACTION_FOUND;
import static android.bluetooth.BluetoothDevice.ACTION_PAIRING_REQUEST;
import static com.etouchsky.activity.VideoViewActivity.VideoViewActivityFlag;
import static com.etouchsky.wisdom.DeviceControlAct.DeviceControlActFlag;
import static com.etouchsky.wisdom.MainActivity.callFlag;
import static com.etouchsky.wisdom.MainActivity.dwDeviceID;


public class ETService extends Service
{
  private String TAG = "MyService";
  private SensorManager sensorManager;
  private Vibrator vibrator;
  private static final int SENSOR_SHAKE = 10;
  private static final int ble_connect_timeout = 1; //蓝牙连接handle标志
  public static boolean ble_connect_timeout_flag=true; //连接超时标志设置，防止同一时间多次连接蓝牙,为true表示已经超时，可以进行再次连接
  public int connectBel=0;//蓝牙连接次数，连接失败后connectBel加1，自动连接一次
  public int connectBel_timeout=0;//蓝牙连接超时次数，当连续2次超时则提示用户蓝牙异常
  private Queue<byte[]> dataInfoQueue = new LinkedList<>();
  private BluetoothGattCharacteristic mCharacteristic;
  public static final String sensor_shake="com.etouchsky.sensor_shake";
  LoginActivityDongAccountProxy mDongAccountProxy = new LoginActivityDongAccountProxy();
  ListActivityDongAccountProxy mAccountProxy = new ListActivityDongAccountProxy();
  public void onStart(Intent intent, int startId)
  {
    System.out.println("onStart");
    super.onStart(intent, startId);
  }
  BluetoothLeDevice mbluetoothLeDevice=null;
  public void onCreate()
  {
    sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
    vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
    IntentFilter myFilter = new IntentFilter();
    myFilter.addAction(BluetoothDevice.ACTION_FOUND);
    myFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
    myFilter.addAction(ACTION_PAIRING_REQUEST);
    this.registerReceiver(myBroadCast, myFilter);
    ViseLog.getLogConfig().configAllowLog(true);
    ViseLog.plant(new LogcatTree());
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      periodLScanCallback = new PeriodLScanCallback() {
        @Override
        public void scanTimeout() {
          Log.i("guosong", "scan timeout");
        }

        @Override
        public void onDeviceFound(BluetoothLeDevice bluetoothLeDevice) {
          LogUtils.i("onDeviceFound"+bluetoothLeDevice.getAddress());
          if (GViewerXApplication.preferences_userInfo.getString("ble_address", "").equals(bluetoothLeDevice.getAddress())) {
            connectBel(bluetoothLeDevice);
            stopScan();
          }
        }
      };
    }else if(Build.VERSION.SDK_INT >=18){
      periodScanCallback = new PeriodScanCallback() {
        @Override
        public void scanTimeout() {
          Log.i("guosong","scan timeout");
        }

        @Override
        public void onDeviceFound(BluetoothLeDevice bluetoothLeDevice) {
          String ble_address=GViewerXApplication.preferences_userInfo.getString("ble_address","");
          LogUtils.i("guosong="+ble_address+"===="+bluetoothLeDevice.getAddress());
          if(ble_address.equals(bluetoothLeDevice.getAddress())) {
            stopScan();
            connectBel(bluetoothLeDevice);
          }
        }
      };
    }
    if (sensorManager != null) {// 注册监听器
      sensorManager.registerListener(sensorEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
      // 第一个参数是Listener，第二个参数是所得传感器类型，第三个参数值获取传感器信息的频率
    }

    LogUtils.i("ETService onCreate"+GViewerXApplication.preferences_userInfo.getBoolean("etservice_destroy",false));
    setInitPhone();
    super.onCreate();
  }
   public void setInitPhone(){
     if(GViewerXApplication.preferences_userInfo.getBoolean("flagLogin",false)){
       DongSDK.initializePush(this, DongPushMsgManager.PUSH_TYPE_ALL);
       boolean initDongAccount = DongSDKProxy.initCompleteDongAccount();
       LogUtils.d("initDongAccount=="+initDongAccount);
       if (!initDongAccount) {
         DongSDKProxy.initDongAccount(mAccountProxy);
       }
       DongSDKProxy.login(GViewerXApplication.preferences_userInfo.getString("account","15914063041"),GViewerXApplication.preferences_userInfo.getString("pwd","123456"));
     }
   }

  Long lastSensor=0l;
  /**
   * 重力感应监听
   */
  private SensorEventListener sensorEventListener = new SensorEventListener() {

    @Override
    public void onSensorChanged(SensorEvent event) {
      // 传感器信息改变时执行该方法
      float[] values = event.values;
      float x = values[0]; // x轴方向的重力加速度，向右为正
      float y = values[1]; // y轴方向的重力加速度，向前为正
      float z = values[2]; // z轴方向的重力加速度，向上为正
      //Log.i("VideoViewActivity", "x轴方向的重力加速度" + x +  "；y轴方向的重力加速度" + y +  "；z轴方向的重力加速度" + z);
      // 一般在这三个方向的重力加速度达到40就达到了摇晃手机的状态。
      int medumValue = 17;// 如果不敏感请自行调低该数值,低于10的话就不行了,因为z轴上的加速度本身就已经达到10了
      if (Math.abs(x) > medumValue || Math.abs(y) > medumValue || Math.abs(z) > medumValue) {
        Long time = System.currentTimeMillis();
        if ((time - lastSensor) > 3000) {
          lastSensor = time;
          vibrator.vibrate(200);
          Message msg = new Message();
          msg.what = SENSOR_SHAKE;
          handlerble.sendMessage(msg);
        }
      }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
  };

  Long lastClickTime=0l;
  /**
   * 动作执行
   */
  Handler handlerble = new Handler() {

    @Override
    public void handleMessage(Message msg) {
      super.handleMessage(msg);
      switch (msg.what) {
        case SENSOR_SHAKE:
          Log.i("ETService", "检测到摇晃，执行操作！falg=+++++++"+DeviceControlActFlag);
          Long time = System.currentTimeMillis();
          if ((time - lastClickTime) > 3000) {
            Intent mIntent=new Intent(sensor_shake);
            sendBroadcast(mIntent);
            if(!DeviceControlActFlag & !VideoViewActivityFlag) {
              openBle();
            }
          }
          break;
        case ble_connect_timeout:
          ble_connect_timeout_flag=true;
          break;
      }
    }
  };
  public void openBle(){

    if(Build.VERSION.SDK_INT <18){
      return;
    }
    String ble_address=GViewerXApplication.preferences_userInfo.getString("ble_address","");
    LogUtils.i("ble_address="+ble_address);
    if(ble_address!=null) {
      Log.i("ETService", "检测到摇晃，执行操作！"+ViseBluetooth.getInstance().isConnected());
      if(mbluetoothLeDevice==null){
        scanBluetooth();
      }
      else if (!ViseBluetooth.getInstance().isConnected()) {
        connectBel=0;//摇一次置为0，防止连接失败后，程序进行再次连接，增加连接成功率
        connectBel(mbluetoothLeDevice);
      }else{
        sendfailTimes=0;
        sendBleUnlock();
      }
    }
  }

  public void connectBel(BluetoothLeDevice mDevice){
    mbluetoothLeDevice=mDevice;
    if (!ViseBluetooth.getInstance().isConnected() & ble_connect_timeout_flag) {
      ViseBluetooth.getInstance().connect(mDevice, false, connectCallback);
      handlerble.sendEmptyMessageDelayed(ble_connect_timeout,10000);
      LogUtils.i("connectBel start ++++++");
      ble_connect_timeout_flag=false;//表示正在连接蓝牙还未超时
    }
  }
  public int onStartCommand(Intent intent, int flags, int startId)
  {
    System.out.println("ETservice onstartcommand1");
    DongSDKProxy.registerAccountCallback(mDongAccountProxy);
    DongSDKProxy.registerAccountCallback(mAccountProxy);
    return 3;
  }

  public void onDestroy()
  {
    LogUtils.i("EtService onDestroy");
    GViewerXApplication.preferences_editor.putBoolean("etservice_destroy",true).commit();
    Intent intent = new Intent();
    intent.setClass(this, ETService.class);
    startService(intent);
    super.onDestroy();
  }

  /*
	 * (non-Javadoc)
	 *
	 * @see android.app.Service#onBind( )
	 */
	/* 创建广播接收器 */
  public BroadcastReceiver myBroadCast = new BroadcastReceiver() {
    public void onReceive(Context context, Intent intent) {
      String action = intent.getAction();
      if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
          setInitPhone();//网络状态改变时并且登录失败时重新登录一次
      }
      else if(ACTION_FOUND.equals(action)){
        BluetoothDevice btDevice=null;  //创建一个蓝牙device对象
        // 从Intent中获取设备对象
        btDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        Log.e("发现设备:", "["+btDevice.getName()+"]"+":"+btDevice.getAddress());
        if(btDevice.getAddress().contains(GViewerXApplication.preferences_userInfo.getString("ble_address", "")))//HC-05设备如果有多个，第一个搜到的那个会被尝试。
        {
          if (btDevice.getBondState() == BluetoothDevice.BOND_NONE) {

            Log.e("ywq", "attemp to bond:"+"["+btDevice.getName()+"]");
            try {
              //通过工具类ClsUtils,调用createBond方法
              ClsUtils.createBond(btDevice.getClass(), btDevice);
            } catch (Exception e) {
              // TODO Auto-generated catch block
              e.printStackTrace();
            }
          }
        }else
          Log.e("error", "Is faild");
      }else if (ACTION_PAIRING_REQUEST.equals(action)){
        BluetoothDevice btDevice=null;  //创建一个蓝牙device对象
        // 从Intent中获取设备对象
        btDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        Log.e("action2=", action);
        if(btDevice.getAddress().contains(GViewerXApplication.preferences_userInfo.getString("ble_address", "")))
        {
          Log.e("here", "OKOKOK");
          try {

            //1.确认配对
            ClsUtils.setPairingConfirmation(btDevice.getClass(), btDevice, true);
            //2.终止有序广播
            Log.i("order...", "isOrderedBroadcast:"+isOrderedBroadcast()+",isInitialStickyBroadcast:"+isInitialStickyBroadcast());
            abortBroadcast();//如果没有将广播终止，则会出现一个一闪而过的配对框。
            //3.调用setPin方法进行配对...
            boolean ret = ClsUtils.setPin(btDevice.getClass(), btDevice, "123456");

          } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
        }else
          Log.e("提示信息", "这个设备不是目标蓝牙设备");

      }
    }
  };

  public IBinder onBind(Intent intent)
  {
    return null;
  }


  private void scanBluetooth() {
    if (BleUtil.isBleEnable(ETService.this)) {
      if(mbluetoothLeDevice==null || "".equals(mbluetoothLeDevice)) {
        LogUtils.i("scanBluetooth==1");
        startScan();
      }
      else {
        LogUtils.i("scanBluetooth==2");
        connectBel(mbluetoothLeDevice);
      }
    } else {
        BleUtil.enableBluetooth(ETService.this);
    }
  }
    Timer scanTimer = new Timer();
    TimerTask task = new TimerTask() {

        @Override
        public void run() {
          // 需要做的事:发送消息
          stopScan();
          try {
            scanTimer.cancel();
            scanTimer = null;
          }catch (Exception e){
          }
        }
    };
  private PeriodLScanCallback periodLScanCallback;
  private PeriodScanCallback periodScanCallback;
  private void startScan() {
    ViseBluetooth.getInstance().init(getApplicationContext());
    LogUtils.i("startScan");
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      ViseBluetooth.getInstance().setScanTimeout(-1).startScan(periodLScanCallback);
      LogUtils.i(periodLScanCallback+"startScan1"+(periodLScanCallback==null));
    } else {
      ViseBluetooth.getInstance().setScanTimeout(-1).startScan(periodScanCallback);
      LogUtils.i("startScan2");
    }
  }

  private void stopScan() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      ViseBluetooth.getInstance().stopScan(periodLScanCallback);
    } else {
      ViseBluetooth.getInstance().stopScan(periodScanCallback);
    }
  }

  private IConnectCallback connectCallback = new IConnectCallback() {
    @Override
    public void onConnectSuccess(BluetoothGatt gatt, int status) {
      connectBel_timeout=0;
      ViseLog.i("Connect Sucfdasadscess!");
      if (gatt != null) {
        String uuid;
        for (final BluetoothGattService gattService : gatt.getServices()) {
          final List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();
          for (final BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
            uuid = gattCharacteristic.getUuid().toString();
            final BluetoothGattCharacteristic characteristic = gattCharacteristic;
            final int charaProp = characteristic.getProperties();
            if ((charaProp & BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE) > 0 & "00010203-0405-0607-0809-0a0b0c0d2b11".equals(uuid)) {
              mCharacteristic = characteristic;
              sendfailTimes=0;
              sendBleUnlock();
            } else if ((charaProp & BluetoothGattCharacteristic.PROPERTY_READ) > 0 & "00010203-0405-0607-0809-0a0b0c0d2b10".equals(uuid)) {
              ViseBluetooth.getInstance().readCharacteristic(characteristic, new ICharacteristicCallback() {
                @Override
                public void onSuccess(final BluetoothGattCharacteristic characteristic) {
                  if (characteristic == null) {
                    return;
                  }
                }
                @Override
                public void onFailure(BleException exception) {
                  if (exception == null) {
                    return;
                  }
                  ViseLog.i("readCharacteristic onFailure:" + exception.getDescription());
                }
              });
            }
            if ((charaProp & BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
              ViseBluetooth.getInstance().enableCharacteristicNotification(characteristic, bleCallback, false);
            } else if ((charaProp & BluetoothGattCharacteristic.PROPERTY_INDICATE) > 0) {
              ViseBluetooth.getInstance().enableCharacteristicNotification(characteristic, bleCallback, true);
            }
          }
        }
      }
    }

    @Override
    public void onConnectFailure(BleException exception) {
      ViseBluetooth.getInstance().disconnect();
      ViseLog.i("Connect Failure!++++++++++++++++++++"+exception.getCode());
      String code=exception.getCode().toString();
      if("TIMEOUT".equals(code))//连接超时,很有可能设备不在线
      {
        if (connectBel_timeout>2){//连续连接2次失败则提示用户蓝牙连接失败！
          PlayVedio.playVedio(ETService.this, R.raw.bleconnectfail);
          Toast.makeText(ETService.this, "Connect Failure!", Toast.LENGTH_SHORT).show();
        }
        connectBel_timeout++;
      }else if ("CONNECT_ERR".equals(code)){
        connectBel_timeout=0;
        if (connectBel==0) {
          connectBel=1;
          new Thread(){
            @Override
            public void run() {
              super.run();
              try {
                ble_connect_timeout_flag=true;
                LogUtils.i("CONNECT_ERR++++++++");
                Thread.sleep(2000);
                connectBel(mbluetoothLeDevice);//连接失败后，自动连接一次，增加开门机会
              } catch (InterruptedException e) {
                e.printStackTrace();
              }
            }
          }.start();
        }
      }else{
        connectBel_timeout=0;
        PlayVedio.playVedio(ETService.this, R.raw.bleconnectfail);
        Toast.makeText(ETService.this, "Connect Failure!", Toast.LENGTH_SHORT).show();
      }
    }

    @Override
    public void onDisconnect() {
      ViseLog.i("Disconnect!");
      //Toast.makeText(ETService.this, "Disconnect!", Toast.LENGTH_SHORT).show();
    }
  };
  public void sendBleUnlock(){
    byte cmd=0x43;
    byte [] ack= SerialPortSdk.Packdata(cmd, "F4BC3C2D","F4BC3C2D".length()/2);
    Log.w("TAG","ack="+HexUtil.encodeHexStr(ack));
    send(ack);
  }
  private Handler handler = new Handler(){
    @Override
    public void handleMessage(Message msg) {
      super.handleMessage(msg);
    }
  };
  private Runnable runnable = new Runnable() {
    @Override
    public void run() {
      send();
    }
  };

  private void send(byte[] data) {
    if (dataInfoQueue != null) {
      dataInfoQueue.clear();
      dataInfoQueue = splitPacketFor20Byte(data);
      handler.post(runnable);
    }
  }
  int sendfailTimes=0;//发送失败次数，最多发送3次，3次失败后不再发送，防止卡死
  public void repeatSend(){
    new Thread(){
      @Override
      public void run() {
        super.run();
        try {
          Thread.sleep(1000);
          if (sendfailTimes<3) {
            sendBleUnlock();
            sendfailTimes++;
          }
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }.start();
  }
  private void send() {
    if (dataInfoQueue != null && !dataInfoQueue.isEmpty()) {
      if (dataInfoQueue.peek() != null) {
        ViseBluetooth.getInstance().writeCharacteristic(mCharacteristic, dataInfoQueue.poll(), new ICharacteristicCallback() {
          @Override
          public void onSuccess(BluetoothGattCharacteristic characteristic) {
            ViseLog.i("Send onSuccess!");
            sendfailTimes=0;
          }
          @Override
          public void onFailure(BleException exception) {
            repeatSend();
            ViseLog.i("Send onFail!");
          }
        });
      }
      if (dataInfoQueue.peek() != null) {
        handler.postDelayed(runnable, 100);
      }
    }
  }
  private Queue<byte[]> splitPacketFor20Byte(byte[] data) {
    Queue<byte[]> dataInfoQueue = new LinkedList<>();
    if (data != null) {
      int index = 0;
      do {
        byte[] surplusData = new byte[data.length - index];
        byte[] currentData;
        System.arraycopy(data, index, surplusData, 0, data.length - index);
        if (surplusData.length <= 20) {
          currentData = new byte[surplusData.length];
          System.arraycopy(surplusData, 0, currentData, 0, surplusData.length);
          index += surplusData.length;
        } else {
          currentData = new byte[20];
          System.arraycopy(data, index, currentData, 0, 20);
          index += 20;
        }
        dataInfoQueue.offer(currentData);
      } while (index < data.length);
    }
    return dataInfoQueue;
  }
  private ICharacteristicCallback bleCallback = new ICharacteristicCallback() {
    @Override
    public void onSuccess(BluetoothGattCharacteristic characteristic) {
      if (characteristic == null || characteristic.getValue() == null) {
        return;
      }
      ViseLog.i("notify success:" + HexUtil.encodeHexStr(characteristic.getValue()));
      String result= HexUtil.encodeHexStr(characteristic.getValue());
      String cmd="";
      String statu="";
      if (result.length()>=16) {
        cmd=result.substring(12,14);
        statu=result.substring(15,16);
      }
      if("1".equals(statu)){
        Toast.makeText(ETService.this,"门开了",3000).show();
        PlayVedio.playVedio(ETService.this, R.raw.opensucc);
      }else{
        Toast.makeText(ETService.this,"开门失败，请核对是否已授权！",3000).show();
        PlayVedio.playVedio(ETService.this, R.raw.authorityfail);
      }
      ViseBluetooth.getInstance().disconnect();
    }

    @Override
    public void onFailure(BleException exception) {
      if (exception == null) {
        return;
      }
      ViseLog.i("notify fail:" + exception.getDescription());
    }
  };

  private class ListActivityDongAccountProxy extends AbstractDongCallbackProxy.DongAccountCallbackImp {
    @Override
    public int onAuthenticate(InfoUser tInfo) {
      LogUtils.i("ETService.clazz--->>>onAuthenticate........tInfo:ListActivityDongAccountProxy"
              + tInfo+"callFlag=="+callFlag);
      //一般情况是消息推送执才会执行此处
      if (callFlag) {
        try {
          ArrayList<DeviceInfo> deviceList = DongSDKProxy.requestGetDeviceListFromCache();
          LogUtils.d("guosong=" + deviceList.size());
          if(deviceList.size()==0){
            Thread.sleep(300);
            deviceList = DongSDKProxy.requestGetDeviceListFromCache();
          }
          for (int i = 0; i < deviceList.size(); i++) {
            if (deviceList.get(i).dwDeviceID == Integer.parseInt(dwDeviceID)) {
              DongConfiguration.mDeviceInfo = deviceList.get(i);
              call(deviceList.get(i));
              //MainActivity.this.finish();
              break;
            }
          }

        }catch (Exception e){
          e.printStackTrace();
        }
      }
      return 0;
    }

    @Override
    public int onUserError(int nErrNo) {
      LogUtils.i("ETService.clazz--->>>onUserError........nErrNo:"
              + nErrNo);
      return 0;
    }

    /**
     * 平台在线推送时回调该方法
     */
    @Override
    public int onCall(ArrayList<DeviceInfo> list) {
      LogUtils.i("ETService.clazz--->>>onCall........list.size():" + list.size());
      int size = list.size();
      if (size > 0) {
        call(list.get(0));
      }
      return 0;
    }
  }
    private class LoginActivityDongAccountProxy extends DongAccountCallbackImp {

        @Override
        public int onAuthenticate(InfoUser tInfo) {
            DongConfiguration.mUserInfo = tInfo;
            LogUtils.i("ETService.clazz--->>>onAuthenticate........tInfo:"
                    + tInfo+"callFlag=="+callFlag);
            DongSDKProxy.requestSetPushInfo(PushInfo.PUSHTYPE_FORCE_ADD);
            DongSDKProxy.requestGetDeviceListFromPlatform();

            return 0;
        }

        @Override
        public int onUserError(int nErrNo) {
            LogUtils.i("ETService.clazz--->>>onUserError........nErrNo:"
                    + nErrNo);
            DongSDK.reInitDongSDK();
            return 0;
        }

    }
    public void call(DeviceInfo deviceInfo){
        String message = deviceInfo.deviceName + deviceInfo.dwDeviceID
                + deviceInfo.msg;
        DongConfiguration.mDeviceInfo = deviceInfo;
        //getApplicationContext().startActivity(new Intent(ETService.this,
        //       VideoViewActivity.class));
        Intent mintent = new Intent(ETService.this, VideoViewActivity.class);
        mintent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        //mintent.setClassName(ETService.this, VideoViewActivity.class);
        mintent.putExtra("call","");
        //startActivityForResult(mintent,1);
        startActivity(mintent);
    }
}