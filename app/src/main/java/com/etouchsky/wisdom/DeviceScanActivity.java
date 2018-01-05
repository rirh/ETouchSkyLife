package com.etouchsky.wisdom;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.etouchsky.GViewerXApplication;
import com.etouchsky.adapter.DeviceAdapter;
import com.etouchsky.baseble.ViseBluetooth;
import com.etouchsky.baseble.callback.scan.PeriodLScanCallback;
import com.etouchsky.baseble.callback.scan.PeriodScanCallback;
import com.etouchsky.baseble.model.BluetoothLeDevice;
import com.etouchsky.baseble.model.BluetoothLeDeviceStore;
import com.etouchsky.baseble.utils.BleUtil;
import com.vise.log.ViseLog;
import com.vise.log.inner.LogcatTree;

import java.util.ArrayList;
import java.util.List;

/**
 * 该类为蓝牙一键开门业务的列表类*/

public class DeviceScanActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 100;
    public static final String EXTRA_DEVICE = "extra_device";
    //界面控件
    private TextView supportTv;
    private TextView statusTv;
    private ListView deviceLv;
    private TextView scanCountTv;

    //蓝牙控件
    private BluetoothLeDeviceStore bluetoothLeDeviceStore;
    private volatile List<BluetoothLeDevice> bluetoothLeDeviceList = new ArrayList<>();
    private DeviceAdapter adapter;

    private PeriodLScanCallback periodLScanCallback;
    private PeriodScanCallback periodScanCallback = new PeriodScanCallback() {
        @Override
        public void scanTimeout() {
            Log.i("guosong","scan timeout");
        }

        @Override
        public void onDeviceFound(BluetoothLeDevice bluetoothLeDevice) {
            if (bluetoothLeDeviceStore != null) {
                bluetoothLeDeviceStore.addDevice(bluetoothLeDevice);
                bluetoothLeDeviceList = bluetoothLeDeviceStore.getDeviceList();
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.setDeviceList(bluetoothLeDeviceList);
                    updateItemCount(adapter.getCount());
                }
            });
        }
    };  //5.0之下回调该接口

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_scan);
        //日志类，用于保存蓝牙生成的日志
        ViseLog.getLogConfig().configAllowLog(true);
        ViseLog.plant(new LogcatTree());
        ViseBluetooth.getInstance().init(getApplicationContext());//蓝牙初始化方法
        init();
    }

    /**
     * 初始化了蓝牙界面控件
     */
    private void init() {
        supportTv = (TextView) findViewById(R.id.scan_ble_support);
        statusTv = (TextView) findViewById(R.id.scan_ble_status);
        deviceLv = (ListView) findViewById(android.R.id.list);
        scanCountTv = (TextView) findViewById(R.id.scan_device_count);

        //创建蓝牙管理控件，并初始化列表
        bluetoothLeDeviceStore = new BluetoothLeDeviceStore();
        adapter = new DeviceAdapter(this);
        deviceLv.setAdapter(adapter);

        deviceLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                BluetoothLeDevice device = (BluetoothLeDevice) adapter.getItem(position);
                if (device == null) return;
                Intent intent = new Intent(DeviceScanActivity.this, DeviceControlAct.class);
                GViewerXApplication.preferences_editor.putString("ble_address",device.getAddress()).commit();//向SharedPreference传入蓝牙地址
                intent.putExtra(EXTRA_DEVICE, device);   //将蓝牙装置传给下一个activity
                startActivity(intent);
            }
        });

        /**
         * 注：增加这个扫描回调处理是为了演示在使用5.0新API上的使用示例
         * 以前的扫描方式完全可以在5.0以上系统使用，此项目中也做了一定的封装，
         * 基本也能达到新API中扫描方式的大部分功能。
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            periodLScanCallback = new PeriodLScanCallback() {
                @Override
                public void scanTimeout() {
                    Log.i("guosong","scan timeout");
                }

                @Override
                public void onDeviceFound(BluetoothLeDevice bluetoothLeDevice) {
                    if (bluetoothLeDeviceStore != null) {
                        bluetoothLeDeviceStore.addDevice(bluetoothLeDevice);
                        bluetoothLeDeviceList = bluetoothLeDeviceStore.getDeviceList();
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.setDeviceList(bluetoothLeDeviceList);
                            updateItemCount(adapter.getCount());
                        }
                    });
                }
            };
        }
    }

    //系统是否开启蓝牙检测
    @Override
    protected void onResume() {
        super.onResume();
        boolean isSupport = BleUtil.isSupportBle(this);
        boolean isOpenBle = BleUtil.isBleEnable(this);
        if (isSupport) {
            supportTv.setText(getString(R.string.supported));
        } else {
            supportTv.setText(getString(R.string.not_supported));
        }
        if (isOpenBle) {
            statusTv.setText(getString(R.string.on));
        } else {
            statusTv.setText(getString(R.string.off));
        }
        invalidateOptionsMenu();  //自定义标题栏刷新
        checkBluetoothPermission();  //校验蓝牙权限
    }

    //停止蓝牙扫描操作
    @Override
    protected void onPause() {
        super.onPause();
        stopScan();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    /**
     *  创建了表单布局
     */
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.scan, menu);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (periodLScanCallback != null && !periodLScanCallback.isScanning()) {
                menu.findItem(R.id.menu_stop).setVisible(false);
                menu.findItem(R.id.menu_scan).setVisible(true);
                menu.findItem(R.id.menu_refresh).setActionView(null);
            } else {
                menu.findItem(R.id.menu_stop).setVisible(true);
                menu.findItem(R.id.menu_scan).setVisible(false);
                menu.findItem(R.id.menu_refresh).setActionView(R.layout.actionbar_progress_indeterminate);
            }
        } else {
            if (periodScanCallback != null && !periodScanCallback.isScanning()) {
                menu.findItem(R.id.menu_stop).setVisible(false);
                menu.findItem(R.id.menu_scan).setVisible(true);
                menu.findItem(R.id.menu_refresh).setActionView(null);
            } else {
                menu.findItem(R.id.menu_stop).setVisible(true);
                menu.findItem(R.id.menu_scan).setVisible(false);
                menu.findItem(R.id.menu_refresh).setActionView(R.layout.actionbar_progress_indeterminate);
            }
        }
        return true;
    }

    /**
     * 标题栏的控制事件*/
    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_scan:
                checkBluetoothPermission();
                break;
            case R.id.menu_stop:
                stopScan();
                break;
            case R.id.menu_about:
                displayAboutDialog();
                break;
        }
        return true;
    }


    /**
     * 调用方法后传入返回码后调用该方法
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            statusTv.setText(getString(R.string.on));
            if (requestCode == 1) {
                startScan();
            }
        } else if (resultCode == RESULT_CANCELED) {
            finish();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /*  校验蓝牙Android6.0权限  */
    private void checkBluetoothPermission() {
        //M为API等级23
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //校验是否已具有模糊定位权限
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
            } else {
                //具有权限
                scanBluetooth();
            }
        } else {
            //系统不高于6.0直接执行
            scanBluetooth();
        }
    }

    /**
     * 对返回的值进行处理，相当于StartActivityForResult
     */
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        doNext(requestCode, grantResults);
    }

    /**
     * 权限操作
     */
    private void doNext(int requestCode, int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //同意权限
                scanBluetooth();
            } else {
                // 权限拒绝，提示用户开启权限
                denyPermission();
            }
        }
    }

    /**
     * 只调用了一个finish方法
     */
    private void denyPermission() {
        finish();
    }

    /**
     * 扫描蓝牙
     */
    private void scanBluetooth() {
        if (BleUtil.isBleEnable(this)) {
            startScan();
        } else {
            BleUtil.enableBluetooth(this, 1);  //提示用户开启蓝牙功能，第一个参数为上下文对象，第二为StartActivityForResult返回码
        }
    }

    /**
     * 开始扫描
     */
    private void startScan() {
        updateItemCount(0);
        if (bluetoothLeDeviceStore != null) {
            bluetoothLeDeviceStore.clear();
        }
        if (adapter != null && bluetoothLeDeviceList != null) {
            bluetoothLeDeviceList.clear();
            adapter.setDeviceList(bluetoothLeDeviceList);
        }
        //LOLLIPOP为API等级21
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ViseBluetooth.getInstance().setScanTimeout(-1).startScan(periodLScanCallback);  //封装的扫描方法
        } else {
            ViseBluetooth.getInstance().setScanTimeout(-1).startScan(periodScanCallback);
        }
        invalidateOptionsMenu();
    }

    /**
     * 停止扫描
     */
    private void stopScan() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ViseBluetooth.getInstance().stopScan(periodLScanCallback);
        } else {
            ViseBluetooth.getInstance().stopScan(periodScanCallback);
        }
        invalidateOptionsMenu();
    }

    /**
     * 更新设备总数方法
     */
    private void updateItemCount(final int count) {
        scanCountTv.setText(getString(R.string.formatter_item_count, String.valueOf(count)));
    }

    /**
     * 显示对话框
     */
    private void displayAboutDialog() {
        final int paddingSizeDp = 5;
        final float scale = getResources().getDisplayMetrics().density;
        final int dpAsPixels = (int) (paddingSizeDp * scale + 0.5f);

        final TextView textView = new TextView(this);
        final SpannableString text = new SpannableString(getString(R.string.about_dialog_text));

        textView.setText(text);
        textView.setAutoLinkMask(RESULT_OK);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setPadding(dpAsPixels, dpAsPixels, dpAsPixels, dpAsPixels);

        Linkify.addLinks(text, Linkify.ALL);
        new AlertDialog.Builder(this).setTitle(R.string.menu_about).setCancelable(false).setPositiveButton(android.R.string.ok, null)
                .setView(textView).show();
    }
}
