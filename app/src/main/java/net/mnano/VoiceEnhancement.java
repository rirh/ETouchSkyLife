package net.mnano;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.AudioTrack.OnPlaybackPositionUpdateListener;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/*
1.初始化
有五个参数：
a.回声消除时长，1表示128ms，2表示256ms，3表示512ms。
b.噪音抑制级别，1表示低，2表示中，3表示高。
c.编码类型，可以选择G729、ADPCM、G711或者不编码。
d.音量放大倍数，一般置为1，1.5表示放大1.5倍。
e.放音方式，一般选择扬声器。
f.是否写声音文件。这是用于调试的，一般不写文件，置为false。
private VoiceEnhancement voiceEnhancement = new VoiceEnhancement(AudioEncodeType.AUDIO_ENCODE_TYPE_RAW, 1, AudioManager.STREAM_MUSIC, false){

    @Override
    public void VoiceOutput(short sFrameSeq, short sFrameSize, byte[] bFrameData) {
        //增加自己的处理，通过网络发送音频数据包给对方
    }
};

2.输出没有回音的音频数据包。这是个回调函数，需要用户自己重载，增加网络发送处理，
有三个参数：
a.音频帧序号。
b.音频帧大小，字节数。
c.音频帧数据。
public abstract void VoiceOutput(short sFrameSeq, short sFrameSize, byte[] bFrameData);

3.输入从网络上接收到的音频数据包
有三个参数：
a.音频帧序号。
b.音频帧大小，字节数。
c.音频帧数据。
public int VoiceInput(short sFrameSeq, short sFrameSize, byte[] bFrameData);

4.停止处理
无参数，结束通话时一定要调用，以释放资源。
public void Stop();
*/
public abstract class VoiceEnhancement {
    protected static final String TAG = "log6";

    //录音变量

    //回声消除时长
    int iAecLevel = 1;
    //噪音抑制级别
    int iNsLevel = 2;
    //音频编码类型
    int iEncodeType = AudioEncodeType.AUDIO_ENCODE_TYPE_RAW;
    //音量放大倍数
    int iAgcLevel = 0;
    //声音输出类型:AudioManager.STREAM_VOICE_CALL，听筒; AudioManager.STREAM_MUSIC，喇叭
    int iStreamType = AudioManager.STREAM_MUSIC;

    boolean bWriteFile = false;//是否写文件
    boolean bNormalStreamVolume = false;//是否正常音量

    public static final int iSampleFrequency = 44100;// 48000, 44100, 16000, 8000
    int iSamplesPoint = 2 * iSampleFrequency / 100; // 10ms为一帧，两帧的采样点数
    int iSamplesBytes = 2 * iSamplesPoint; // 10ms为一帧，每个采样点2个字节，两帧的采样字节数
    int iOneG729Length = ConstantDefine.SAMPLE_NUM_G729 * 2;
    int iTwoG729Length = iOneG729Length * 2;
    byte[] bufferVad = new byte[1];// 是否是语音
    byte[] bufferRead = null;
    byte[] buffer8K = null;// 2帧
    byte[] farSpeech = null;// 2帧
    byte[] bufferSpeechTwo = null;// 2帧
    byte[] bufferSpeechOne = null;// 1帧
    byte[] bufferEncode = null; // 10
    int iRecordResult = 0;
    int iReSampleResult = 0;
    short sEncodeResult = 0, sMaxEncodeResult;
    int iAudioFrameCount = 0, iAECCount;
    long lRecordTime, lTimeInterval;
    int iToClearFar;

    long lTimeTestStart, lTimeTestStop, lTimeReSample, lTimeSQE,
            lTimeEncode, lTimeDecodePlay;// Time Test
    long lTimeRecordStart;//开始录音的时间点
    boolean bSendOK = true;
    AudioRawData8K audioDataEcho = null;
    short sFrameSeq = 0;//1
    short sInputFrameSeq = 1;
    int iEncodeFor;

    AudioRecord audioRecord = null;
    //G729 codecG729 = null;
    //ADPCM codecADPCM = null;
    //G711 codecG711 = null;
    SQE cntSQE = null;

    //long lTrafficQuantity = 0; //流量统计，byte
    //int iUdpSentSize;// 发送UDP包的大小

    boolean isRecording = true;//是否录音的标记
    boolean isReceiveAudio = false;//是否接收到音频数据的标记
    //boolean isEchoCancel  = true;//是否消除回音的标记
    int EchoCancelStatus = AECStatus.AEC_STATUS_START;//回音消除的状态
    boolean isEchoArrived = false;//是否接收到回音的标记
    boolean isPlayAudio = false;//是否开始播放音频数据的标记
    boolean isStartAEC = false;//是否已经开始AEC处理的标记
    //记录所有待播放的音频帧数据
    ArrayList<AudioRawData8K> listPlaySpeech = new ArrayList<AudioRawData8K>();
    //记录所有播放的音频帧数据，用于回音消除
    ArrayList<AudioRawData8K> listFarSpeech = new ArrayList<AudioRawData8K>();
    int recBufSize, playBufSize;

    // 音频文件
    File fileMic = null;
    //File fileEncode = null;
    File fileDecode = null;
    File fileAEC = null;
    FileOutputStream outMIC = null;
    //FileOutputStream outEncode = null;
    FileOutputStream outDecode = null;
    FileOutputStream outAEC = null;

    //放音变量
    long lStartPlayTime = 0;
    long lTimeJBGet, lTimeDecode, lTimeJB, lTimePlay;//Time Test
    int iPlaySize = 0;

    byte[] bufferDecodeInTemp = null;
    byte[] bufferDecodeOutTemp = new byte[160];

    JitterBuffer jb = null;
    JBState jbState = null;
    int iFrameType;//从jitter buffer取出的音频数据包的类型
    int lostframe = 0;
    int iPutJBResult;

    //音频解码
    int iAudioDecodeResult = 0;//单帧语音解码后的样本数
    int iSampleBytes = 2 * iSampleFrequency / 100;//单帧语音的字节数
    byte[] SpeechData44K = new byte[iSampleBytes];
    byte[] PlayData48K = new byte[2 * iSampleBytes];
    int iLen8K = 2 * ConstantDefine.SAMPLE_NUM_G729;
    int iDataPacketCount = 0;  //数据包计数器
    public AudioTrack audioTrack = null;
    AudioFrame audioFrameFromJB = null;

    //记录最后放音的一帧数据，用于回音消除
    AudioRawData8K[] audioDataFarArray = new AudioRawData8K[ConstantDefine.MAX_FAR_SPEECH_NUM];
    AudioRawData8K audioRawDataFar = null;
    long lFarAudioDataSize = 0;

    //记录待播放的音频帧数据
    AudioRawData8K[] audioDataPlayArray = new AudioRawData8K[ConstantDefine.MAX_PLAY_SPEECH_NUM];
    AudioRawData8K audioRawDataPlay = null;
    long lPlayAudioDataSize = 0;

    //空白帧
    AudioRawData8K audioRawData8KNull = new AudioRawData8K();

    /* 构造函数
    有六个参数：
    a.回声消除时长，1表示128ms，2表示256ms，3表示512ms。
    b.噪音抑制级别，1表示低，2表示中，3表示高。
    c.音量放大倍数，一般置为1，1.5表示放大1.5倍。
    d.编码类型，可以选择G729、ADPCM、G711或者不编码。
    e.放音方式，一般选择扬声器。
    f.是否写声音文件。这是用于调试的，一般不写文件，置为false。
     * */
    public VoiceEnhancement(int aecLevel, int nsLevel, int agcLevel, int audioEncodeType, int streamType, boolean writeFile) {
        //回声消除时长
        iAecLevel = aecLevel;
        //噪音抑制级别
        iNsLevel = nsLevel;
        //音量放大倍数
        iAgcLevel = agcLevel;
        //音频编码类型
        iEncodeType = audioEncodeType;
        //声音输出类型:AudioManager.STREAM_VOICE_CALL，听筒; AudioManager.STREAM_MUSIC，喇叭
        iStreamType = streamType;
        //是否写文件
        bWriteFile = writeFile;

        //初始化空白帧
        for (int i = 0; i < 160; i++)
            audioRawData8KNull.bData[i] = 0;

        //录音初始化
        //RAW
        bufferDecodeInTemp = new byte[ConstantDefine.FRAME_SIZE_RAW_8K];//160
        //没有音频帧时，播放空白帧
        for (int n = 0; n < ConstantDefine.FRAME_SIZE_RAW_8K; n++)
            bufferDecodeInTemp[n] = 0;

        //创建Jitter Buffer
        //FrameSize   	该通道数据包长度（字节）最大值
        //iFrameTime   	该通道数据包帧长（ms），如g.729帧长为10ms，nFrameTime=10
        jb = new JitterBuffer(ConstantDefine.PACKET_SIZE_RAW_8K);
        if (jb.JitterBufferCreate(ConstantDefine.PACKET_SIZE_RAW_8K, ConstantDefine.FRAME_TIME) < 0) {
            Log.e(TAG, "Failed to Create Jitter Buffer");
            jb = null;
        }


        //JB状态
        jbState = new JBState();

        //从JB取出的音频帧
        audioFrameFromJB = new AudioFrame(iEncodeType);

        //初始化待播放音频数据队列
        for (int i = 0; i < ConstantDefine.MAX_PLAY_SPEECH_NUM; i++)
            audioDataPlayArray[i] = new AudioRawData8K();

        //初始化远端音频数据队列，用于回音消除
        for (int j = 0; j < ConstantDefine.MAX_FAR_SPEECH_NUM; j++)
            audioDataFarArray[j] = new AudioRawData8K();

        // 初始化SQE
        cntSQE = new SQE();
        // 初始化SQE, 8k采样, aec=on, ns=on,参数2为中等降噪, agc=off;
        //cntSQE.cntSqeInit(1, 2, 2, 1);
        //cntSQE.cntSqeInit(1, iAecDuration, iNsLevel, 0);
        //cntSQE.cntSqeInit(1, iAecDuration, iNsLevel, 2);
        cntSQE.cntSqeInit(1, iAecLevel, iNsLevel, iAgcLevel, 1);//48K_AECM库

        // 分配音频处理空间
        bufferRead = new byte[iSamplesBytes];
        buffer8K = new byte[iTwoG729Length];// 2帧
        farSpeech = new byte[iTwoG729Length];// 2帧
        bufferSpeechTwo = new byte[iTwoG729Length];// 2帧
        bufferSpeechOne = new byte[iOneG729Length];// 1帧

        // 创建麦克风对象
        recBufSize = 10 * AudioRecord.getMinBufferSize(
                iSampleFrequency, AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT);
        Log.i(TAG, "Microphone received buffer size: " + recBufSize);// 30720
        audioRecord = new AudioRecord(
                MediaRecorder.AudioSource.MIC, iSampleFrequency,
                AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, recBufSize);

        //放音初始化
        //初始化音响，一个缓冲区被填满之后，才开始播放
        //缓冲区大小应该合适，太小会导致回音太近不好消除，太大会导致声音延时太长
        playBufSize = 1 * AudioTrack.getMinBufferSize(iSampleFrequency,
                AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);//2228 * 5
        //playBufSize = 10 * iSampleFrequency / 50;//10帧， 8820
        Log.i(TAG, "AudioTrack play buffer size: " + playBufSize);//30720
        audioTrack = new AudioTrack(iStreamType, iSampleFrequency,
                AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT,
                playBufSize, AudioTrack.MODE_STREAM);

        //audioTrack.setStereoVolume(1.0f, 1.0f);//设置当前音量大小, 0.6f
        audioTrack.setStereoVolume(0.9f, 0.9f);//设置当前音量大小, 0.6f

        //设置通知
        audioTrack.setNotificationMarkerPosition(1);//播放缓冲区的第1个音频帧之后，发起事件通知
        audioTrack.setPlaybackPositionUpdateListener(new OnPlaybackPositionUpdateListener() {
            @Override
            public void onMarkerReached(AudioTrack audioTrack) {
                //通知第一帧播放完成
                Log.i(TAG, "Echo Arrived!");
                //设置接收到回音的标记
                isEchoArrived = true;
            }

            @Override
            public void onPeriodicNotification(AudioTrack audioTrack) {
                // TODO Auto-generated method stub

            }
        });

        //启动录音线程
        new AudioRecordThread().start();

        //启动放线程
        new AudioPlayThread().start();
    }

    /* 停止处理
      无参数，结束通话时一定要调用，以释放资源。
       * */
    public void Stop() {
        //停止录音
        isRecording = false;
        Log.e(TAG, "Audio record Stop, isRecording " + isRecording);
        //延时0.5秒，等待录音线程关闭，确保关闭麦克风、杨扬声器
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //销毁Jitter Buffer
        jb.JitterBufferDestroy();
        jb = null;
    }

    /* 输出录制的没有回音的音频数据包
      有三个参数：
      a.音频帧序号。
    b.音频帧大小，字节数。
    c.音频帧数据。
       * */
    public abstract void VoiceOutput(short sFrameSeq, short sFrameSize, byte[] bFrameData);

    /* 输入从网络上接收到的音频数据包
      有三个参数：
      a.音频帧序号。
    b.音频帧大小，字节数。
    c.音频帧数据。
       * */
    public int VoiceInput(short sFrameSeq, short sFrameSize, byte[] bFrameData) {
        //如果帧序号为0，则表示是空帧，不用处理
        if (0 == sFrameSeq) {
            isReceiveAudio = true;//设置是否接收到音频数据的标记
            return 0;
        }

        //Log.i(TAG, "VoiceInput: Receive a audio frame, Frame Seq " + sFrameSeq + ", Frame Size " + sFrameSize);

		/*
        // 获取音频帧头和数据
		AudioFrame audioFrame = new AudioFrame(iEncodeType);
		audioFrame.sFrameSeq  = sFrameSeq;
		audioFrame.sFrameSize = sFrameSize;
		System.arraycopy(bFrameData, 0, audioFrame.data, 0, audioFrame.sFrameSize);
		*/

        //为了处理DTX，使用自己的input音频帧序号
        // Java中short是有符号的，最大只能表示到32767，否则会出现负数
        // 为了方便，限定input音频帧序号上限为18000
        if (sInputFrameSeq >= ConstantDefine.MAX_FRAME_SEQUENCE)
            sInputFrameSeq = 1;

        // 把一包编码后的音频数据加入Jitter Buffer
        //iPutJBResult = jb.PutIntoJitterBuffer(audioFrame);
        //iPutJBResult = jb.PutIntoJitterBuffer(sFrameSeq, sFrameSize, bFrameData);
        iPutJBResult = jb.PutIntoJitterBuffer(sInputFrameSeq, sFrameSize, bFrameData);
        if (iPutJBResult < 0) {
            //Log.e(TAG, "VoiceInput: Failed to put into JB, return " + iPutJBResult + ", FrameSeq " + sFrameSeq);
            Log.e(TAG, "VoiceInput: Failed to put into JB, return " + iPutJBResult + ", FrameSeq " + sInputFrameSeq);
        }

        // input音频帧序号增加1
        sInputFrameSeq++;

        //查询JB状态
        //jb.GetJitterBufferState(jbState);
        //Log.i(TAG, "VoiceInput: Put a audio frame into JB, No." + sFrameSeq + ", JB size " + jbState.size);
        //Log.i(TAG, "VoiceInput: JB, lost " + jbState.lost + ", discard " + jbState.discard + ", empty " + jbState.empty);
        //Log.i(TAG, "VoiceInput: JB, burst " + jbState.burst + ", prefetch " + jbState.prefetch);

        return iPutJBResult;
    }

    //设置消除回音的标记
    public void EnableEcho(boolean isEcho) {
        //需要消除回音
        if (true == isEcho) {
            //isEchoCancel = true;
            EchoCancelStatus = AECStatus.AEC_STATUS_RESUME;//回音消除的状态为恢复
        }
        //不需要消除回音
        else {
            //isEchoCancel = false;
            EchoCancelStatus = AECStatus.AEC_STATUS_PAUSE;//回音消除的状态为暂停
            //listFarSpeech.clear();
        }
    }

	/*
    //设置增益倍数
	public void SetVolume(double volumeMultiple){
		//音量放大倍数
		iAgcLevel = volumeMultiple;
	}*/

    //设置免提模式
    public void SetAudioSpeaker(boolean isSpeaker) {
        if (isSpeaker)
            iStreamType = AudioManager.STREAM_MUSIC;//声音输出类型为喇叭
        else
            iStreamType = AudioManager.STREAM_VOICE_CALL;//声音输出类型为听筒

        //释放播放资源
        ReleaseAudioTrack();

        //复位接收到回音的标记
        isEchoArrived = false;
        listFarSpeech.clear();

        //重新开启AudioTrack
        audioTrack = new AudioTrack(iStreamType, iSampleFrequency,
                AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT,
                playBufSize, AudioTrack.MODE_STREAM);

        if (isSpeaker)
            audioTrack.setStereoVolume(0.6f, 0.6f);//设置当前音量大小, 0.6f//声音输出类型为喇叭
        else
            audioTrack.setStereoVolume(0.9f, 0.9f);//设置当前音量大小, 0.6f;//声音输出类型为听筒

        //设置通知
        audioTrack.setNotificationMarkerPosition(1);//播放缓冲区的第1个音频帧之后，发起事件通知
        audioTrack.setPlaybackPositionUpdateListener(new OnPlaybackPositionUpdateListener() {
            @Override
            public void onMarkerReached(AudioTrack audioTrack) {
                //通知第一帧播放完成
                Log.i(TAG, "Echo Arrived!");
                //设置接收到回音的标记
                isEchoArrived = true;
            }

            @Override
            public void onPeriodicNotification(AudioTrack audioTrack) {
                // TODO Auto-generated method stub

            }
        });

        //开始播放
        audioTrack.play();
    }

	/* 录音处理 */

    //录音线程
    private class AudioRecordThread extends Thread {
        public void run() {
            Looper.prepare();
            try {
                // 创建音频文件
                if (bWriteFile) {
                    //外部存储
                    String strPath = Environment.getExternalStorageDirectory().getPath();
                    //内部存储
                    //String strPath = callTestApplication.mainActivity.getFilesDir();
                    Log.i(TAG, "Write file to " + strPath);
                    fileMic = new File(strPath + "/sosea_mic.pcm");// /sdcard
                    fileDecode = new File(strPath + "/sosea_decode.pcm");
                    fileAEC = new File(strPath + "/sosea_aec.pcm");
                    // 文件不存在就创建文件
                    if (!fileMic.exists()) {
                        fileMic.createNewFile();
                    }
                    //if (!fileEncode.exists()) {
                    //	fileEncode.createNewFile();
                    //}
                    if (!fileDecode.exists()) {
                        fileDecode.createNewFile();
                    }
                    if (!fileAEC.exists()) {
                        fileAEC.createNewFile();
                    }

                    // 定义文件输入输出流
                    outMIC = new FileOutputStream(fileMic);
                    //outEncode = new FileOutputStream(fileEncode);
                    outDecode = new FileOutputStream(fileDecode);
                    outAEC = new FileOutputStream(fileAEC);
                }// end of if (bWriteFile)

                //获取手机厂商、型号和系统版本号
                //sDeviceManufacturer = android.os.Build.MANUFACTURER.toUpperCase();
                //sDeviceModel = android.os.Build.MODEL.toUpperCase();
                /*
                Log.i(TAG, "手机厂商: " + sDeviceManufacturer + ",\n"
		                + "手机型号: " + sDeviceModel + ",\nSDK版本:"
		                + android.os.Build.VERSION.SDK + ",\n系统版本:"
		                + android.os.Build.VERSION.RELEASE);
		        */

		        /*
                //根据手机厂商、型号选择最佳的远端队列空白帧数量
		        if (sDeviceManufacturer.contains("SAMSUNG"))// && (sDeviceModel.contains("GT_N7108"))
		        	iFarListNullFrameNum = 10;//5, Delay Estimator 27-33
		        else if (sDeviceManufacturer.contains("HTC"))
		        	iFarListNullFrameNum = 9;
		        else if (sDeviceManufacturer.contains("HUAWEI"))
		        	iFarListNullFrameNum = 14;//19, Delay Estimator 33-45
		        else if (sDeviceManufacturer.contains("VIVO"))
		        	iFarListNullFrameNum = 23;//Delay Estimator 50
		        else if (sDeviceManufacturer.contains("OPPO"))
		        	iFarListNullFrameNum = 17;
		        else if (sDeviceManufacturer.contains("MEIZU") && sDeviceModel.contains("MX5"))
		        	iFarListNullFrameNum = 5;//Delay Estimator 15

				//为了AEC对齐，先往远端队列放入几个空白帧， 每帧10ms
				//htc: 9, 延时30ms; HuaWei: 9, 延时170ms; Vivo: 9, 延时230ms, 14,170ms
				for (int iNull = 0; iNull < iFarListNullFrameNum; iNull++)//15, 18, 17
					listFarSpeech.add(audioRawData16KNull);
				*/

                // 开始录制声音
                audioRecord.startRecording();
                //设置喇叭，开始播放
                audioTrack.play(); //由录音时统一控制
                lTimeRecordStart = System.currentTimeMillis();//开始录音的时间点

                // 循环录音
                while (isRecording) {
//                    Log.e(TAG, "Audio record ... isRecording: " + isRecording);
                    // 从MIC读取2帧语音数据到缓冲区，因为20ms才做一次回声消除
                    iRecordResult = audioRecord.read(bufferRead, 0, iSamplesBytes);
                    //iRecordResult = audioRecord.read(buffer8K, 0, iSamplesBytes);

                    if (iRecordResult < 1)
                        Log.e(TAG, "Audio record error, result " + iRecordResult);

                    // 如果音频帧序号为0，则表示还没有发送音频帧，先发两个空白帧到对端或者服务器
                    if (0 == sFrameSeq) {
                        VoiceOutput(sFrameSeq, sEncodeResult, bufferDecodeInTemp);//bufferEncode
                        VoiceOutput(sFrameSeq, sEncodeResult, bufferDecodeInTemp);
                        sFrameSeq = 1;
                        continue;
                    }

                    // 如果还没有接收到音频帧，则不发送音频帧，为了主被叫侧同步
                    //北京旅信不需要这么做，一端是电话，一端是IP
                    //if (false == isReceiveAudio){
                    //	continue;
                    //}

                    // Time Test
                    lTimeTestStart = System.currentTimeMillis();

                    // 采样率转换，44.1K->8K
                    iReSampleResult = cntSQE.ReSample44kto8k(bufferRead, iSamplesPoint,
                            buffer8K, 0);

                    // Time Test
                    lTimeTestStop = System.currentTimeMillis();
                    lTimeReSample = lTimeTestStop - lTimeTestStart;

                    // 如果已经接收到回音，而且有播放音频帧，则做回音消除
                    if (//isEchoCancel &&
                            isEchoArrived &&  //直接用256ms模式更靠谱
                                    (listFarSpeech.size() >= 2)
                            ) {
                        //if (false == isStartAEC){
                        //	isStartAEC = true;
                        //	Log.i(TAG, "Start Echo Cancel, No." + sFrameSeq);
                        //}

                        //先降低麦克风音量、再做回音处理，为了处理麦克风或者扬声器增益过高的情况(以后别这么做，会造成声音失真)
                        //VolumeAdjustion2(buffer8K, 0.7071);//0.5

						/* 等AEC处理时远端数据增益控制之后，才去放音，这个流程需要修改 ？？？*/
                        // 获取之前播放的音频帧
                        // 取出一个音频帧
                        audioDataEcho = listFarSpeech.remove(0);
                        if (null != audioDataEcho)
                            System.arraycopy(audioDataEcho.bData, 0, farSpeech, 0, iOneG729Length);
                        // 取出另一个音频帧
                        audioDataEcho = listFarSpeech.remove(0);
                        if (null != audioDataEcho)
                            System.arraycopy(audioDataEcho.bData, 0, farSpeech, iOneG729Length, iOneG729Length);

                        // 写文件
                        if (bWriteFile) {
                            outMIC.write(buffer8K, 0, iTwoG729Length);
                            //outMIC.write(bufferRead, 0, iRecordResult);
                            outMIC.flush();
                            outDecode.write(farSpeech, 0, iTwoG729Length);
                            outDecode.flush();
                        }

                        // 做回音消除
                        if (AECStatus.AEC_STATUS_PAUSE != EchoCancelStatus)
                            sEncodeResult = cntSQE.cntSqeProc(buffer8K, farSpeech, bufferSpeechTwo, bufferVad);
                        else
                            System.arraycopy(buffer8K, 0, bufferSpeechTwo, 0, iTwoG729Length);
                        ;
                        //Log.i(TAG, "Sqe Proc Result " + sEncodeResult);
                        //Log.i(TAG, "Vad " + bufferVad[0]);
                        //做完回音处理后恢复麦克风音量，为了处理麦克风或者扬声器增益过高的情况
                        //VolumeAdjustion2(bufferSpeechTwo, 1.4142);//2

                        //增益控制
                        //cntSQE.cntAgcProc(bufferSpeechTwo);

                        // Time Test
                        lTimeSQE = System.currentTimeMillis()
                                - lTimeTestStop;
                        lTimeTestStop = System.currentTimeMillis();

                        // 写文件
                        if (bWriteFile) {
                            outAEC.write(bufferSpeechTwo, 0, iTwoG729Length);
                            outAEC.flush();
                        }
                    }// end of if(callTestApplication.isEchoArrived)
                    // 如果没有做回音消除，则直接采用麦克风的录音数据
                    else
                        System.arraycopy(buffer8K, 0, bufferSpeechTwo, 0,
                                iTwoG729Length);

                    // 编码2帧
                    for (iEncodeFor = 0; iEncodeFor < 2; iEncodeFor++) {
                        System.arraycopy(bufferSpeechTwo, iEncodeFor
                                        * iOneG729Length, bufferSpeechOne, 0,
                                iOneG729Length);

                        //如果不需要编码，则直接打包音频帧
                        if (AudioEncodeType.AUDIO_ENCODE_TYPE_RAW == iEncodeType) {
                            //发送音频数据包到对端或者服务器
                            VoiceOutput(sFrameSeq, (short) iOneG729Length, bufferSpeechOne);

                            // 音频帧序号增加1
                            sFrameSeq++;
                            // Java中short是有符号的，最大只能表示到32767，否则会出现负数
                            // 为了方便，限定音频帧序号上限为18000
                            if (sFrameSeq >= ConstantDefine.MAX_FRAME_SEQUENCE)
                                sFrameSeq = 1;
                        }
                    }// end of for

                    // Time Test
                    lTimeEncode = System.currentTimeMillis()
                            - lTimeTestStop;
                    lTimeTestStop = System.currentTimeMillis();

                    // 播放2帧接收到的音频数据
                    PlayMediaData();

                    // Time Test
                    lTimeDecodePlay = System.currentTimeMillis()
                            - lTimeTestStop;
                    lTimeTestStop = System.currentTimeMillis();

                    Log.i(TAG, "Total " + (lTimeTestStop -
                            lTimeTestStart) +
                            ", ReSample " + lTimeReSample +
                            ", SQE " + lTimeSQE +
                            ", Encode " + lTimeEncode +
                            ", Decode Play " + lTimeDecodePlay);
                }// end of while

            } catch (IOException e) {
                e.printStackTrace();
                //callTestApplication.bSessionFail = true;
                Log.e(TAG, "Audio record error, e.info: " + e.toString());
            } catch (Throwable t) {
                t.printStackTrace();
                //callTestApplication.bSessionFail = true;
                Log.e(TAG, "Audio record error, t.info: " + t.toString());
            }

            Log.e(TAG, "Audio record Release, isRecording " + isRecording);
            //释放资源
            Release();

            Log.i(TAG, "AudioEncodeThread: Thread exited!");
            Looper.loop();
        }// end of run
    }

    ;// end of Thread

    //释放资源函数
    private void Release() {
        try {
            // 停止播放声音
            if ((null != audioTrack)
                    && (AudioTrack.PLAYSTATE_PLAYING == audioTrack.getPlayState())) {
                audioTrack.stop();
                audioTrack.release();
            }

            // 停止录制声音
            if (null != audioRecord) {
                audioRecord.stop();
                audioRecord.release();
            }
        } catch (Throwable t) {
            t.printStackTrace();
            //callTestApplication.bSessionFail = true;
        }

        // 关闭文件流
        if (bWriteFile) {
            try {
                if (outMIC != null) {
                    outMIC.close();
                }
                //if (outEncode != null) {
                //	outEncode.close();
                //}
                if (outDecode != null) {
                    outDecode.close();
                }
                if (outAEC != null) {
                    outAEC.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // 结束重采样，释放动态申请的内存
        cntSQE.cntSqeExit();
        // 清除以前记录的播放音频帧数据
        listFarSpeech.clear();

        // 释放SQE和编码器
        cntSQE = null;
        //codecG729 = null;
        //codecG711 = null;
        //codecADPCM = null;
        // 释放音频处理空间
        bufferRead = null;
        buffer8K = null;// 2帧
        farSpeech = null;// 2帧
        bufferSpeechTwo = null;// 2帧
    }

    //释放播放资源函数
    private void ReleaseAudioTrack() {
        try {
            // 停止播放声音
            if ((null != audioTrack)
                    && (AudioTrack.PLAYSTATE_PLAYING == audioTrack.getPlayState())) {
                audioTrack.stop();
                audioTrack.release();
                audioTrack = null;
            }
        } catch (Throwable t) {
            t.printStackTrace();
            //callTestApplication.bSessionFail = true;
        }
    }

	/* 放音处理 */

    //处理收到的媒体数据
    private void PlayMediaData() {
        iPlaySize = 0;

        //Time Test
        //lTimeTestStart = System.currentTimeMillis();

        //从Jitter Buffer获取一个音频帧
        if (false == GetAudioFrameFromJB(1))
            return;
        //从Jitter Buffer获取另一个音频帧
        if (false == GetAudioFrameFromJB(2))
            return;

        //Time Test
        //lTimeTestStop = System.currentTimeMillis();
        //lTimeJB = lTimeTestStop - lTimeTestStart;

    	/*
        //播放声音, 写入数据即播放
		if (iPlaySize > 0){
			if (AudioTrack.PLAYSTATE_PLAYING == audioTrack.getPlayState()){
				audioTrack.write(PlayData48K, 0, iPlaySize);//该方法是阻塞的，也就是它要等这些数据播放完才返回

			    //设置开始播放音频数据的标记
		        if (false == isPlayAudio) {
		        	isPlayAudio = true;
		        	Log.i(TAG, "PlayMediaData: Start palying audio frame!");
		        }

		        //Log.i(TAG, "PlayMediaData: play " + iPlaySize);
			}
		}*/

        // Time Test
        //lTimePlay = System.currentTimeMillis() - lTimeTestStop;
        //Log.i(TAG, "JB " + lTimeJB +
        //            ", Play " + lTimePlay);
    }// end of class PlayMediaData

    //从Jitter Buffer获取一个音频帧
    private boolean GetAudioFrameFromJB(int iFrameNumber) {
        //long lTimeTestStart, lTimeTestStop, lTimeJBGet, lTimeDecode;

        //如果Jitter Buffer无效，则返回
        if (null == jb)
            return false;

        //Time Test
        //lTimeTestStart = System.currentTimeMillis();

        //从jitter buffer取出一个音频数据包
        iFrameType = jb.GetFromJitterBuffer(audioFrameFromJB);
        Log.i(TAG, "GetAudioFrameFromJB--->>> iFrameType:" + iFrameType);
        //Time Test
        //lTimeTestStop = System.currentTimeMillis();
        //lTimeJBGet = lTimeTestStop - lTimeTestStart;

        //如果取音频包失败，则返回
        if (iFrameType < 0) {
            Log.e(TAG, "GetAudioFrameFromJB: Failed to get audio frame from JB");
            return false;
        }

        //如果取音频包成功，则分别处理
        switch (iFrameType) {
            case JBFrameType.MISSING_FRAME:
                //未取到数据，该数据包已丢失
                Log.e(TAG, "GetAudioFrameFromJB: Lose a frame");
                //解码、播放音频
                lostframe = 1;//丢包标志，1 为丢包
                DecodeAudio(bufferDecodeInTemp, 0, iFrameNumber);
                break;
            case JBFrameType.NORMAL_FRAME:
                //已取到正常数据包
                //Log.i(TAG, "GetAudioFrameFromJB: Get a normal frame, No." + audioFrameFromJB.sFrameSeq);
                //解码、播放音频
                lostframe = 0;//丢包标志，0 为没有丢包
                DecodeAudio(audioFrameFromJB.data, audioFrameFromJB.sFrameSize, iFrameNumber);
                break;
            case JBFrameType.ZERO_PREFETCH_FRAME:
                //取到空帧，jitter buffer正在缓存数据
                Log.i(TAG, "GetAudioFrameFromJB: JB is prefetching frame");
                //如果已经开始播放音频数据，才开始处理空帧
                if (true == isPlayAudio) {
                    //给用户播放舒适噪音
                    lostframe = 1;//丢包标志，0 为没有丢包
                    DecodeAudio(bufferDecodeInTemp, 0, iFrameNumber);
                }
                break;
            case JBFrameType.ZERO_EMPTY_FRAME:
                //取到空帧，jitter buffer内无数据
                Log.i(TAG, "GetAudioFrameFromJB: Get a empty frame");
                //如果已经开始播放音频数据，才开始处理空帧
                if (true == isPlayAudio) {
                    //给用户播放舒适噪音
                    lostframe = 1;//丢包标志，0 为没有丢包
                    DecodeAudio(bufferDecodeInTemp, 0, iFrameNumber);
                }
                break;
            default:
                break;
        }

        // Time Test
        //lTimeDecode = System.currentTimeMillis() - lTimeTestStop;
        //lTimeTestStop = System.currentTimeMillis();
        //Log.i(TAG, "Total " + (lTimeTestStop - lTimeTestStart) +
        //            ", JBGet " + lTimeJBGet +
        //            ", Decode Play " + lTimeDecode);

        return true;
    }

    //解码一个音频帧
    private void DecodeAudio(byte[] data, int length, int iFrameNumber) {
        //long lTimeTestStart, lTimeTestStop, lTimeDecode, lTimeResample, lTimePlay;

        //Time Test
        //lTimeTestStart = System.currentTimeMillis();

        //如果没有压缩，直接使用数据
        if (AudioEncodeType.AUDIO_ENCODE_TYPE_RAW == iEncodeType) {
            System.arraycopy(data, 0, bufferDecodeOutTemp, 0, iLen8K);
        }

        //Time Test
        //lTimeTestStop = System.currentTimeMillis();
        //lTimeDecode = lTimeTestStop - lTimeTestStart;

        //放大音量
        //现在已经在算法库里面做了AGC了
        //VolumeAdjustion(bufferDecodeOutTemp, fVolumeMultiple);
        //AGC处理，1挡，-3dB - +12dB
        //cntSQE.cntSqeAdjFe(bufferDecodeOutTemp, ConstantDefine.SAMPLE_NUM_G729);

        // Time Test
        //lTimeResample = System.currentTimeMillis() - lTimeTestStop;
        //lTimeTestStop = System.currentTimeMillis();

        //把待播放的音频帧数据放入队列
        audioRawDataPlay = audioDataPlayArray[(int) (lPlayAudioDataSize % ConstantDefine.MAX_PLAY_SPEECH_NUM)];
        System.arraycopy(bufferDecodeOutTemp, 0, audioRawDataPlay.bData, 0, iLen8K);
        if (listPlaySpeech.size() >= ConstantDefine.MAX_PLAY_SPEECH_NUM) {
            listPlaySpeech.remove(0);
            Log.e(TAG, "Play Speech List reaches max length");
        }
        listPlaySpeech.add(audioRawDataPlay);
        //Log.i(TAG, "Add a play audio");

		/*
        //记录最后放音的一帧数据，用于回音消除
		audioRawDataFar = audioDataFarArray[(int)(lPlayAudioDataSize % ConstantDefine.MAX_FAR_SPEECH_NUM)];
		System.arraycopy(bufferDecodeOutTemp, 0, audioRawDataFar.bData, 0, iLen8K);
		if (listFarSpeech.size() >= ConstantDefine.MAX_FAR_SPEECH_NUM){
		    listFarSpeech.remove(0);
		    Log.e(TAG, "Far Speech List reaches max length");
		}
		listFarSpeech.add(audioRawDataFar);
		*/

        lPlayAudioDataSize++;

		/*
        //记录这帧数据，等待播放
		iPlaySize += iSampleBytes;
		if (1 == iFrameNumber)
		    System.arraycopy(SpeechData48K, 0, PlayData48K, 0, iSampleBytes);
			//System.arraycopy(bufferDecodeOutTemp, 0, PlayData48K, 0, iSampleBytes);
		else
			System.arraycopy(SpeechData48K, 0, PlayData48K, iSampleBytes, iSampleBytes);
			//System.arraycopy(bufferDecodeOutTemp, 0, PlayData48K, iSampleBytes, iSampleBytes);
		*/

        // Time Test
        //lTimePlay = System.currentTimeMillis() - lTimeTestStop;
        //lTimeTestStop = System.currentTimeMillis();
        //Log.i(TAG, "Total " + (lTimeTestStop - lTimeTestStart) +
        //            ", Decode " + lTimeDecode +
        //            ", Resample " + lTimeResample +
        //            ", Play " + lTimePlay);
    }

    /* 音量调节函数
      有两个参数：
      a.PCM数据，必须是一帧10ms的8KHz采样率的语音数据，160字节。
      b.音量放大倍数，一般置为1，1.5表示放大1.5倍。
       * */
    int iVolumeFor;
    int iVolumeTemp;
    short sVolumeTemp;
    int iVolumeValue;
    double multipleTemp;

    public void VolumeAdjustion(byte[] data, double multiple) {
        //测试音量
        /*
        iVolumeValue = 0;
		for (iVolumeFor = 0; iVolumeFor < 80; iVolumeFor++){
			iVolumeValue += Math.abs(data[iVolumeFor]);
		}
		Log.i(TAG, "Volume " + iVolumeValue);
		if (iVolumeValue > 2000)
			multipleTemp = 2000 * multiple / iVolumeValue;
			*/
        //放大或者缩小音量
        for (iVolumeFor = 0; iVolumeFor < 80; iVolumeFor++) {
            iVolumeTemp = (int) (multiple * ((short) (data[2 * iVolumeFor] & 0xFF) | (short) ((data[2 * iVolumeFor + 1] & 0xFF) << 8)));
            //iVolumeTemp = (int)(multipleTemp * ((short)(data[2*iVolumeFor] & 0xFF) | (short)((data[2*iVolumeFor + 1] & 0xFF) << 8)));
            if (iVolumeTemp > 32767)
                iVolumeTemp = 32767;
            else if (iVolumeTemp < -32768)
                iVolumeTemp = -32768;
            sVolumeTemp = (short) iVolumeTemp;
            data[2 * iVolumeFor] = (byte) (sVolumeTemp & 0xFF);
            data[2 * iVolumeFor + 1] = (byte) ((sVolumeTemp >> 8) & 0xFF);
        }
    }

    //在麦克风录音后，降低音量，处理两帧
    int iVolumeFor2;
    int iVolumeTemp2;
    short sVolumeTemp2;

    public void VolumeAdjustion2(byte[] data, double multiple) {
        for (iVolumeFor2 = 0; iVolumeFor2 < 160; iVolumeFor2++) {
            iVolumeTemp2 = (int) (multiple * ((short) (data[2 * iVolumeFor2] & 0xFF) | (short) ((data[2 * iVolumeFor2 + 1] & 0xFF) << 8)));
            if (iVolumeTemp2 > 32767)
                iVolumeTemp2 = 32767;
            else if (iVolumeTemp2 < -32768)
                iVolumeTemp2 = -32768;
            sVolumeTemp2 = (short) iVolumeTemp2;
            data[2 * iVolumeFor2] = (byte) (sVolumeTemp2 & 0xFF);
            data[2 * iVolumeFor2 + 1] = (byte) ((sVolumeTemp2 >> 8) & 0xFF);
        }
    }

    //放音线程
    private class AudioPlayThread extends Thread {
        public void run() {
            //Looper.prepare();
            try {
                AudioRawData8K audioDataPlay = null;

                // 循环放音
                while (isRecording) {
//                    Log.i(TAG, "AudioPlayThread: Start isRecording 1111");
                    // 如果有待播放音频帧，则播放
                    if (false == listPlaySpeech.isEmpty()) {
                        // 获取待播放的音频帧
                        // 取出一个音频帧
                        audioDataPlay = listPlaySpeech.remove(0);
                        Log.i(TAG, "AudioPlayThread: Start isRecording 22222");
                        //播放声音, 写入数据即播放
                        if ((null != audioDataPlay)
                                && (null != audioTrack)
                                && (AudioTrack.PLAYSTATE_PLAYING == audioTrack.getPlayState())) {

                            //记录最后放音的一帧数据，用于回音消除
                            audioRawDataFar = audioDataFarArray[(int) (lFarAudioDataSize % ConstantDefine.MAX_FAR_SPEECH_NUM)];
                            System.arraycopy(audioDataPlay.bData, 0, audioRawDataFar.bData, 0, iLen8K);
                            if (listFarSpeech.size() >= ConstantDefine.MAX_FAR_SPEECH_NUM) {
                                listFarSpeech.remove(0);
                                Log.e(TAG, "Far Speech List reaches max length");
                            }
                            listFarSpeech.add(audioRawDataFar);
                            lFarAudioDataSize++;

                            //采样率转换，8K->44.1K
                            iReSampleResult = cntSQE.ReSample8kto44k(audioDataPlay.bData,
                                    ConstantDefine.SAMPLE_NUM_G729,
                                    SpeechData44K,
                                    0);
                            Log.i(TAG, "AudioPlayThread: Start isRecording 3333 write");
                            audioTrack.write(SpeechData44K, 0, iSampleBytes);//该方法是阻塞的，也就是它要等这些数据播放完才返回
                            //audioTrack.write(audioDataPlay.bData, 0, 160);

                            //设置开始播放音频数据的标记
                            if (false == isPlayAudio) {
                                isPlayAudio = true;
                                Log.i(TAG, "AudioPlayThread: Start palying audio frame!");
                            }

                            //Log.i(TAG, "AudioPlayThread: play " + iSampleBytes);
                        }
                    }
                    // 如果没有待播放音频帧，则休息一会
                    else {
                        //延时10ms
                        //Thread.sleep(10);
                        Thread.sleep(0);
                        //Log.i(TAG, "AudioPlayThread: no audio frame");
                    }
                }// end of while
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Log.i(TAG, "AudioPlayThread: Thread exited!");
            //Looper.loop();
        }// end of run
    }

    ;// end of Thread

    //音频帧的原始数据，8K采样率
    class AudioRawData8K {
        //public long lPlayTime;          //播放时间戳
        public byte[] bData;           //数据缓冲

        public AudioRawData8K() {
            bData = new byte[80 * 2]; // 10ms 为一帧，每个采样点存2个字节
            //lPlayTime = System.currentTimeMillis();
        }
    }

    //音频帧的原始数据，44.1K采样率
    class AudioRawData44K {
        public byte[] bData;           //数据缓冲

        public AudioRawData44K() {
            bData = new byte[441 * 2]; // 10ms 为一帧，每个采样点存2个字节
        }
    }

    //rtp packet type
    interface RtpPacektType {
        int RTP_PACKET_NONE = 0;
        int RTP_PACKET_AUDIO = 1;
        int RTP_PACKET_VIDEO = 2;
        int RTP_PACKET_DRAW = 3;
        int RTP_PACKET_UNKNOWN = 4;
    }

    interface JBFrameType {
        int MISSING_FRAME = 0; //未取到数据，该数据包已丢失
        int NORMAL_FRAME = 1; //已取到正常数据包
        int ZERO_PREFETCH_FRAME = 2; //取到空帧，jitter buffer正在缓存数据
        int ZERO_EMPTY_FRAME = 3; //取到空帧，jitter buffer内无数据
    }

}//end of class SpeechEnhancement