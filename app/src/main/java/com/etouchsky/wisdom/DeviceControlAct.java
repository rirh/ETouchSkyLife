package com.etouchsky.wisdom;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.etouchsky.baseble.ViseBluetooth;
import com.etouchsky.baseble.callback.IConnectCallback;
import com.etouchsky.baseble.callback.data.ICharacteristicCallback;
import com.etouchsky.baseble.common.State;
import com.etouchsky.baseble.exception.BleException;
import com.etouchsky.baseble.model.BluetoothLeDevice;
import com.etouchsky.baseble.utils.BleUtil;
import com.etouchsky.baseble.utils.HexUtil;
import com.etouchsky.serialprot.SerialPortSdk;
import com.etouchsky.util.PlayVedio;
import com.gViewerX.util.LogUtils;
import com.vise.log.ViseLog;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import static android.bluetooth.BluetoothDevice.ACTION_FOUND;
import static android.bluetooth.BluetoothDevice.ACTION_PAIRING_REQUEST;
import static com.etouchsky.gs.ETService.sensor_shake;

public class DeviceControlAct extends AppCompatActivity {
    private TextView mConnectionState;
    private BluetoothLeDevice mDevice;
    private BluetoothGattCharacteristic mCharacteristic;
    public static boolean DeviceControlActFlag=false;
    private Queue<byte[]> dataInfoQueue = new LinkedList<>();
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

    private IConnectCallback connectCallback = new IConnectCallback() {
        @Override
        public void onConnectSuccess(BluetoothGatt gatt, int status) {
            ViseLog.i("Connect Success!");
            Toast.makeText(DeviceControlAct.this, "Connect Success!", Toast.LENGTH_SHORT).show();
            mConnectionState.setText("已连接");
            invalidateOptionsMenu();
            if (gatt != null) {
                String uuid;
                for (final BluetoothGattService gattService : gatt.getServices()) {
                    final List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();

                    // Loops through available Characteristics.
                    for (final BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                        uuid = gattCharacteristic.getUuid().toString();
                        final BluetoothGattCharacteristic characteristic = gattCharacteristic;
                        final int charaProp = characteristic.getProperties();
                        if ((charaProp & BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE) > 0 & "00010203-0405-0607-0809-0a0b0c0d2b11".equals(uuid)) {
                            mCharacteristic = characteristic;
                            if(flagSend){
                                sendfailTimes=0;
                                sendBleUnlock();
                            }
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
            ViseLog.i("Connect Failure!");
            Toast.makeText(DeviceControlAct.this, "Connect Failure!", 1000).show();
            mConnectionState.setText("未连接");
            invalidateOptionsMenu();
            clearUI();
        }

        @Override
        public void onDisconnect() {
            ViseLog.i("Disconnect!");
            Toast.makeText(DeviceControlAct.this, "Disconnect!", 1000).show();
            mConnectionState.setText("未连接");
            invalidateOptionsMenu();
            clearUI();
        }
    };

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
                Toast.makeText(DeviceControlAct.this,"门开了",1000).show();
                PlayVedio.playVedio(DeviceControlAct.this, R.raw.opensucc);
            }else{
                Toast.makeText(DeviceControlAct.this,"开门失败，请核对是否已授权！",2000).show();
                PlayVedio.playVedio(DeviceControlAct.this, R.raw.authorityfail);
            }
        }

        @Override
        public void onFailure(BleException exception) {
            if (exception == null) {
                return;
            }
            ViseLog.i("notify fail:" + exception.getDescription());
        }
    };
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_control);
        init();
    }
    private byte cmd;
    private void init() {
        mConnectionState = (TextView) findViewById(R.id.connection_state);
        //param=getIntent().getStringExtra("param");
        mDevice = getIntent().getParcelableExtra(DeviceScanActivity.EXTRA_DEVICE);
        if (mDevice != null) {
            ((TextView) findViewById(R.id.device_address)).setText(mDevice.getAddress());
        }
        findViewById(R.id.unlock_ble).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    sendfailTimes=0;
                    sendBleUnlock();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        IntentFilter myFilter = new IntentFilter();
        myFilter.addAction(sensor_shake);//摇一摇
        this.registerReceiver(myBroadCast, myFilter);
    }
    boolean flagSend=false;
    public void sendBleUnlock(){
        if (!BleUtil.isBleEnable(this)) {
            flagSend=true;
            boolean enableBluetooth=BleUtil.enableBluetooth(this);
            LogUtils.i("enableBluetooth"+enableBluetooth);
            if(enableBluetooth) {
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        try {
                            Thread.sleep(3000);
                            ViseBluetooth.getInstance().connect(mDevice, true, connectCallback);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
            return;
        }
        else if (!ViseBluetooth.getInstance().isConnected()) {
            ViseBluetooth.getInstance().connect(mDevice, false, connectCallback);
            flagSend=true;
            //Toast.makeText(DeviceControlAct.this, "请连接蓝牙设备！", 2000).show();
            return;
        }
        flagSend=false;
        byte cmd=0x43;
        byte [] ack= SerialPortSdk.Packdata(cmd, "F4BC3C2D","F4BC3C2D".length()/2);
        Log.w("TAG","ack="+HexUtil.encodeHexStr(ack));
        send(ack);
    }
    public BroadcastReceiver myBroadCast = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (sensor_shake.equals(action)) {
                sendBleUnlock();
            }
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        LogUtils.i("DeviceControlActFlag onpause");
        DeviceControlActFlag=false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        ViseBluetooth.getInstance().connect(mDevice, false, connectCallback);
        DeviceControlActFlag=true;
    }
    @Override
    protected void onStop() {
        super.onStop();
        ViseBluetooth.getInstance().disconnect();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ViseBluetooth.getInstance().clear();
        try{
            this.unregisterReceiver(myBroadCast);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.connect, menu);
        if (ViseBluetooth.getInstance().isConnected()) {
            menu.findItem(R.id.menu_connect).setVisible(false);
            menu.findItem(R.id.menu_disconnect).setVisible(true);
        } else {
            menu.findItem(R.id.menu_connect).setVisible(true);
            menu.findItem(R.id.menu_disconnect).setVisible(false);
        }
        if (ViseBluetooth.getInstance().getState() == State.CONNECT_PROCESS) {
            menu.findItem(R.id.menu_refresh).setActionView(R.layout.actionbar_progress_indeterminate);
        } else {
            menu.findItem(R.id.menu_refresh).setActionView(null);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_connect:
                invalidateOptionsMenu();
                if (!ViseBluetooth.getInstance().isConnected()) {
                    ViseBluetooth.getInstance().connect(mDevice, false, connectCallback);
                }
                break;
            case R.id.menu_disconnect:
                invalidateOptionsMenu();
                if (ViseBluetooth.getInstance().isConnected()) {
                    ViseBluetooth.getInstance().disconnect();
                }
                break;
        }
        return true;
    }

    private void clearUI() {
        //mInput.setText("");
        //mOutput.setText("");
        mCharacteristic = null;
    }

    private boolean isHexData(String str) {
        if (str == null) {
            return false;
        }
        char[] chars = str.toCharArray();
        for (char ch : chars) {
            if (ch >= '0' && ch <= '9') continue;
            if (ch >= 'A' && ch <= 'F') continue;
            if (ch >= 'a' && ch <= 'f') continue;
            return false;
        }
        return true;
    }

}
