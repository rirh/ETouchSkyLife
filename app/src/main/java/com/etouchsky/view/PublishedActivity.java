package com.etouchsky.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.etouchsky.activity.AFragmentAdviceActivity;
import com.etouchsky.data.DataHelper;
import com.etouchsky.pojo.AFragmentAdviceNew;
import com.etouchsky.pojo.SimpleHouseInfo;
import com.etouchsky.util.CacheUtils;
import com.etouchsky.util.HttpUtil;
import com.etouchsky.util.OkAndGsonUtil;
import com.etouchsky.wisdom.R;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PublishedActivity extends Activity {

    private GridView noScrollgridview;
    private GridAdapter adapter;
    private TextView activity_selectimg_send, selecting_title_tv;
    private int choicePage;
    private ImageView back;
    private Intent intent = new Intent();
    private RequestBody fileBody;
    private String url;
    private EditText title, message;
    private Message message1;
    private ProgressDialog dialog;
//    private SimpleHouseInfo info;
    private String jsonObject1;
    private SimpleHouseInfo info;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selectimg);
        choicePage = getIntent().getIntExtra("INDEX", 0x00);
        if (Bimp.bmp.size() > 0) {
            Bimp.bmp.clear();
            Bimp.drr.clear();
            Bimp.max = 0;
        }
        Toast.makeText(PublishedActivity.this, "界面选择" + choicePage, Toast.LENGTH_SHORT).show();
        Init();
    }

    public void Init() {
        selecting_title_tv = (TextView) findViewById(R.id.selecting_title_tv);
        title = (EditText) findViewById(R.id.selecting_title);
        message = (EditText) findViewById(R.id.selecting_message);
        back = (ImageView) findViewById(R.id.activity_selectimg_back);
        noScrollgridview = (GridView) findViewById(R.id.noScrollgridview);
        noScrollgridview.setSelector(new ColorDrawable(Color.TRANSPARENT));
        adapter = new GridAdapter(this);
        adapter.update();
        noScrollgridview.setAdapter(adapter);
        back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.setClass(PublishedActivity.this, AFragmentAdviceActivity.class);
                intent.putExtra("AFragment", choicePage);
                startActivity(intent);
                finish();
            }
        });
        noScrollgridview.setOnItemClickListener(new OnItemClickListener() {

            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                if (arg2 == Bimp.bmp.size()) {
                    new PopupWindows(PublishedActivity.this, noScrollgridview);
                } else {
                    intent.setClass(PublishedActivity.this, PhotoActivity.class);
                    intent.putExtra("ID", arg2);
                    startActivity(intent);
                }
            }
        });
        activity_selectimg_send = (TextView) findViewById(R.id.activity_selectimg_send);
        activity_selectimg_send.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {

                if (title.getText().toString().trim().equals("") && message.getText().toString().trim().equals("")) {
                    Toast.makeText(PublishedActivity.this, "标题与内容不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    dialog = OkAndGsonUtil.showProgressDialog(PublishedActivity.this);
                    List<String> list = new ArrayList<String>();
                    for (int i = 0; i < Bimp.drr.size(); i++) {
                        String Str = Bimp.drr.get(i).substring(
                                Bimp.drr.get(i).lastIndexOf("/") + 1,
                                Bimp.drr.get(i).lastIndexOf("."));
                        list.add(FileUtils.SDPATH + Str + ".JPEG");
                    }
                    MultipartBody.Builder bodyBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);
                    File file;
                    for (int i = 0; i < list.size(); i++) {
                        file = new File(list.get(i));
                        fileBody = RequestBody.create(MediaType.parse("image/jpeg"), file);
                        Log.e("*****" + i, file.getName());
                        bodyBuilder.addPart(Headers.of("Content-Disposition", "multipart/form-data; name=" + "File[]" + ";filename=" + file.getName()), fileBody);
                    }
                    DataHelper dbHelper = new DataHelper(PublishedActivity.this);
                    SQLiteDatabase db = dbHelper.getReadableDatabase();
                    Cursor cursor = db.rawQuery("select * from CommunitiesInfo where Id = 1",null);

                    switch (choicePage) {
                        case 0x01:   //0x01为投诉按钮
//                            selecting_title_tv.setText("投诉");
                            intent.putExtra("AFragment", 0x01); //0x01为投诉按钮
                            url = HttpUtil.HOUSE_ADVICE_SAVE;
                            cursor.moveToNext();
                            bodyBuilder.addFormDataPart("unitno", cursor.getString(cursor.getColumnIndex("UnitNo")));
                            bodyBuilder.addFormDataPart("community_id", cursor.getString(cursor.getColumnIndex("CmtID")));
                            bodyBuilder.addFormDataPart("roomno", cursor.getString(cursor.getColumnIndex("RoomNo")));
                            bodyBuilder.addFormDataPart("account", CacheUtils.getAccount(PublishedActivity.this, CacheUtils.USER_ACCOUNT, "000"));
                            bodyBuilder.addFormDataPart("advice_title", title.getText().toString());
                            bodyBuilder.addFormDataPart("remark", message.getText().toString());
                            Log.e("****",cursor.getString(cursor.getColumnIndex("RoomNo")));
                            //关闭数据库
                            db.close();

                            break;
                        case 0x02:  //0x02为报修按钮
//                            selecting_title_tv.setText("报修");
                            intent.putExtra("AFragment", 0x02); //0x02为报修按钮
                            url = HttpUtil.HOUSE_TROUBLE_SAVE;
                            cursor.moveToNext();
                            bodyBuilder.addFormDataPart("unitno", cursor.getString(cursor.getColumnIndex("UnitNo")));
                            bodyBuilder.addFormDataPart("community_id", cursor.getString(cursor.getColumnIndex("CmtID")));
                            bodyBuilder.addFormDataPart("roomno", cursor.getString(cursor.getColumnIndex("RoomNo")));
                            bodyBuilder.addFormDataPart("account", CacheUtils.getAccount(PublishedActivity.this, CacheUtils.USER_ACCOUNT, "000"));
                            bodyBuilder.addFormDataPart("trouble_title", title.getText().toString());
                            bodyBuilder.addFormDataPart("remark", message.getText().toString());
                            Log.e("****",cursor.getString(cursor.getColumnIndex("RoomNo")));
                            //关闭数据库
                            db.close();
                            break;
                    }
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url(url)
                            .post(bodyBuilder.build())
                            .build();
                    Log.e("****","这是提交参数"+bodyBuilder.build());
                    Call call = client.newCall(request);
                    call.enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Log.e("*****","程序错误");
                            e.printStackTrace();
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            jsonObject1 = response.body().string();
                            Log.e("*****",jsonObject1);
                            info = OkAndGsonUtil.gson.fromJson(jsonObject1, SimpleHouseInfo.class);
                            if (info.getStatus() == 0) {
                                message1 = Message.obtain();
                                message1.what = 0x02;
                                hand.sendMessage(message1);
                            }else {
                                message1 = Message.obtain();
                                message1.what = 0x03;
                                hand.sendMessage(message1);
                            }

                        }
                    });


                }
                // 高清的压缩图片全部就在 list 路径里面了
                // 高清的压缩过的 bmp 对象 都在 Bimp.bmp里面
                // 完成上传服务器后 .........

            }
        });
    }

    private Handler hand = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0x02) {
                OkAndGsonUtil.closeProgressDialog(dialog);
                Toast.makeText(PublishedActivity.this, "提交成功", Toast.LENGTH_SHORT).show();
                intent.setClass(PublishedActivity.this, AFragmentAdviceActivity.class);
                startActivity(intent);
                finish();
            }else if(msg.what == 0x03){
                OkAndGsonUtil.closeProgressDialog(dialog);
                Toast.makeText(PublishedActivity.this, info.getDesc(), Toast.LENGTH_SHORT).show();
            }
        }
    };

    @SuppressLint("HandlerLeak")
    public class GridAdapter extends BaseAdapter {
        private LayoutInflater inflater; // 视图容器
        private int selectedPosition = -1;// 选中的位置
        private boolean shape;

        public boolean isShape() {
            return shape;
        }

        public void setShape(boolean shape) {
            this.shape = shape;
        }

        public GridAdapter(Context context) {
            inflater = LayoutInflater.from(context);
        }

        public void update() {
            loading();
        }

        public int getCount() {
            return (Bimp.bmp.size() + 1);
        }

        public Object getItem(int arg0) {

            return null;
        }

        public long getItemId(int arg0) {

            return 0;
        }

        public void setSelectedPosition(int position) {
            selectedPosition = position;
        }

        public int getSelectedPosition() {
            return selectedPosition;
        }

        /**
         * ListView Item设置
         */
        public View getView(int position, View convertView, ViewGroup parent) {
            final int coord = position;
            ViewHolder holder = null;
            if (convertView == null) {

                convertView = inflater.inflate(R.layout.item_published_grida,
                        parent, false);
                holder = new ViewHolder();
                holder.image = (ImageView) convertView
                        .findViewById(R.id.item_grida_image);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            if (position == Bimp.bmp.size()) {
                holder.image.setImageBitmap(BitmapFactory.decodeResource(
                        getResources(), R.mipmap.icon_addpic_unfocused));
                if (position == 9) {
                    holder.image.setVisibility(View.GONE);
                }
            } else {
                holder.image.setImageBitmap(Bimp.bmp.get(position));
            }

            return convertView;
        }

        private class ViewHolder {
            public ImageView image;
        }

        Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        adapter.notifyDataSetChanged();
                        break;
                }
                super.handleMessage(msg);
            }
        };

        public void loading() {
            new Thread(new Runnable() {
                public void run() {
                    while (true) {
                        if (Bimp.max == Bimp.drr.size()) {
                            Message message = new Message();
                            message.what = 1;
                            handler.sendMessage(message);
                            break;
                        } else {
                            try {
                                String path = Bimp.drr.get(Bimp.max);
                                System.out.println(path);
                                Bitmap bm = Bimp.revitionImageSize(path);
                                Bimp.bmp.add(bm);
                                String newStr = path.substring(
                                        path.lastIndexOf("/") + 1,
                                        path.lastIndexOf("."));
                                FileUtils.saveBitmap(bm, "" + newStr);
                                Bimp.max += 1;
                                Message message = new Message();
                                message.what = 1;
                                handler.sendMessage(message);
                            } catch (IOException e) {

                                e.printStackTrace();
                            }
                        }
                    }
                }
            }).start();
        }
    }

    public String getString(String s) {
        String path = null;
        if (s == null)
            return "";
        for (int i = s.length() - 1; i > 0; i++) {
            s.charAt(i);
        }
        return path;
    }

    protected void onRestart() {
        FileUtils.deleteDir();
        adapter.update();
        super.onRestart();
    }

    public class PopupWindows extends PopupWindow {

        public PopupWindows(Context mContext, View parent) {

            View view = View
                    .inflate(mContext, R.layout.item_popupwindows, null);
            view.startAnimation(AnimationUtils.loadAnimation(mContext,
                    R.anim.fade_ins));
            LinearLayout ll_popup = (LinearLayout) view
                    .findViewById(R.id.ll_popup);
            ll_popup.startAnimation(AnimationUtils.loadAnimation(mContext,
                    R.anim.push_bottom_in_2));

            setWidth(LayoutParams.FILL_PARENT);
            setHeight(LayoutParams.FILL_PARENT);
            setBackgroundDrawable(new BitmapDrawable());
            setFocusable(true);
            setOutsideTouchable(true);
            setContentView(view);
            showAtLocation(parent, Gravity.BOTTOM, 0, 0);
            update();

            Button bt1 = (Button) view
                    .findViewById(R.id.item_popupwindows_camera);
            Button bt2 = (Button) view
                    .findViewById(R.id.item_popupwindows_Photo);
            Button bt3 = (Button) view
                    .findViewById(R.id.item_popupwindows_cancel);
            bt1.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    photo();
                    dismiss();
                }
            });
            bt2.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(PublishedActivity.this,
                            TestPicActivity.class);
                    startActivity(intent);
                    dismiss();
                }
            });
            bt3.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    dismiss();
                }
            });

        }
    }

    private static final int TAKE_PICTURE = 0x000000;
    private String path = "";

    public void photo() {
        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        openCameraIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);

        StringBuffer sDir = new StringBuffer();
        if (hasSDcard()) {
            sDir.append(Environment.getExternalStorageDirectory()
                    + "/MyPicture/");
        } else {
            String dataPath = Environment.getRootDirectory().getPath();
            sDir.append(dataPath + "/MyPicture/");
        }

        File fileDir = new File(sDir.toString());
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }
        File file = new File(fileDir,
                String.valueOf(System.currentTimeMillis()) + ".jpg");

        path = file.getPath();
        Uri imageUri = Uri.fromFile(file);
        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(openCameraIntent, TAKE_PICTURE);
    }

    public static boolean hasSDcard() {
        String status = Environment.getExternalStorageState();
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PICTURE:
                if (Bimp.drr.size() < 9 && resultCode == -1) {
                    Bimp.drr.add(path);
                }
                break;
        }
    }

}
