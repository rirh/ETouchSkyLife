package net.mnano;

import java.nio.ByteBuffer;

public class JitterBuffer {
	static {
		System.loadLibrary("MNPCJitterBuffer");
	}
	//字节序转换
	EndianTransfer endianTransfer = null;
	byte [] bFramePut = null;
	ByteBuffer bufferFramePut = null;
	byte [] bFrameGet = null;
	ByteBuffer bufferFrameGet = null;

	//获取状态
	byte [] bState = new byte[ConstantDefine.JB_STATE_SIZE];
	ByteBuffer bufferState = ByteBuffer.wrap(bState);
	int iStateRet = 0;

	public JitterBuffer(int packetSize){
		endianTransfer = new EndianTransfer();
		bFramePut = new byte[packetSize];
		bufferFramePut = ByteBuffer.wrap(bFramePut);
		bFrameGet = new byte[packetSize];
		bufferFrameGet = ByteBuffer.wrap(bFrameGet);
	}

	/* Jitter Buffer接口 */

	/*-----------------------------------------------------------------------------------------
	 函数   JitterBufferCreate
	 功能   创建jitter buffer， 一共有五个通道
	 参数
	    iFrameSize   	该通道数据包长度（字节）最大值
		iFrameTime   	该通道数据包帧长（ms），如g.729帧长为10ms，nFrameTime=10

	 返回值
	 	 0      成功
	     <0     失败
	*/
	public native int JitterBufferCreate(int iFrameSize, int iFrameTime);

	/*-----------------------------------------------------------------------------------------
	 函数   JitterBufferDestroy
	 功能   销毁jitter buffer
	 参数

	 返回值
	 	0      成功
	    <0     失败
	*/
	public native int JitterBufferDestroy();

	/*-----------------------------------------------------------------------------------------
	 函数   JitterBufferPut (不要直接使用，请使用PutIntoJitterBuffer)
	 功能   把一包编码后的音频数据加入jitter buffer
	 参数
	        bPacket        编码后的音频数据包
	        iFrameSize     该音频数据包的实际字节长度
	        iFrameSeq      接收到的数据包的序号（1,2,3......）

	 返回值
	        0      成功
	        <0     失败
	*/
	public native int JitterBufferPut(byte[] bPacket, int iFrameSize, int iFrameSeq);

	/*-----------------------------------------------------------------------------------------
	 函数   PutIntoJitterBuffer
	 功能   把一包编码后的音频数据加入jitter buffer
	 参数
	        sFrameSeq      接收到的数据包的序号（1,2,3......）
	        sFrameSize     该音频数据包的实际字节长度
	        data           编码后的音频数据包

	 返回值
	        0      成功
	        <0     失败
	*/
	public int PutIntoJitterBuffer(short sFrameSeq, short sFrameSize, byte[] data)
	{
		int iRet = 0;
		bufferFramePut.clear();

		//获取音频帧的内容
		//音频帧序列号
		bufferFramePut.putShort(sFrameSeq);
		//音频帧大小
		bufferFramePut.putShort(sFrameSize);
		//音频帧数据
		bufferFramePut.put(data, 0, sFrameSize);

		//把一包编码后的音频数据加入jitter buffer
		iRet = JitterBufferPut(bFramePut, (4 + sFrameSize), sFrameSeq);

		//返回结果
		return iRet;
	}

	/*-----------------------------------------------------------------------------------------
	 函数   JitterBufferGet (不要直接使用，请使用GetFromJitterBuffer)
	 功能   从jitter buffer取出一个音频数据包
	 参数
	        bPacket       指向用于存储编码数据包的音频缓冲的指针

	 返回值
	 		<0  失败
			>=0 成功
				0	表示未取到数据，该数据包已丢失。
				1	表示已取到正常数据包。
				2	表示取到空帧，jitter buffer正在缓存数据。
				3	表示取到空帧，jitter buffer内无数据。
	*/
	public native int JitterBufferGet(byte[] bPacket);

	/*-----------------------------------------------------------------------------------------
	 函数   GetFromJitterBuffer
	 功能   从jitter buffer取出一个音频数据包
	 参数
	        frame         指向用于存储编码数据包的音频缓冲的指针

	 返回值
	 		<0  失败
			>=0 成功
				0	表示未取到数据，该数据包已丢失。
				1	表示已取到正常数据包。
				2	表示取到空帧，jitter buffer正在缓存数据。
				3	表示取到空帧，jitter buffer内无数据。
	*/
	public int GetFromJitterBuffer(AudioFrame frame)
	{
		int iRet = 0;
		bufferFrameGet.clear();

		//从jitter buffer取出一个音频数据包
		iRet = JitterBufferGet(bFrameGet);

		//获取音频帧的内容
		if (1 == iRet){
			//音频帧序列号
			frame.sFrameSeq = bufferFrameGet.getShort();
			//音频帧大小
			frame.sFrameSize = bufferFrameGet.getShort();
			//音频帧数据
			bufferFrameGet.get(frame.data, 0, frame.sFrameSize);
		}else{
			//音频帧序列号
			frame.sFrameSeq = 0;
			//音频帧大小
			frame.sFrameSize = 0;
		}

		//返回结果
		return iRet;
	}
}

//Jitter Buffer状态类
class JBState
{
	/* Setting */
	int	frame_size;	    /**< Individual frame size, in bytes.   */
int	min_prefetch;	    /**< Minimum allowed prefetch, in frms. */
int	max_prefetch;	    /**< Maximum allowed prefetch, in frms. */

    /* Status */
int	burst;		    /**< Current burst level, in frames	    */
int	prefetch;	    /**< Current prefetch value, in frames  */
int	size;		    /**< Current buffer size, in frames.    */

    /* Statistic */
int	avg_delay;	    /**< Average delay, in ms.		    */
int	min_delay;	    /**< Minimum delay, in ms.		    */
int	max_delay;	    /**< Maximum delay, in ms.		    */
int	dev_delay;	    /**< Standard deviation of delay, in ms.*/
int	avg_burst;	    /**< Average burst, in frames.	    */
int	lost;		    /**< Number of lost frames.		    */
int	discard;	    /**< Number of discarded frames.	    */
int	empty;		    /**< Number of empty on GET events.	    */
}

interface ConstantDefine {
	//RTP数量与长度
	int MIN_RTP_PACKAGE_SIZE          = 8;
	int RTP_HEADER_SIZE               = 4;
	int RTP_SIZE_LOC_IN_HEADER        = 2;
	int AUDIO_FRAME_HEADER_SIZE       = 4;

	int FRAME_TIME  = 10;   // 帧长为10ms

	//G729
	int FRAME_SIZE_G729  = 10;   // g.729帧大小为10
	int PACKET_SIZE_G729 = 14;   // g.729包大小为14
	int SAMPLE_NUM_G729  = 80;   // g.729一帧样本数为80
	int JB_STATE_SIZE    = 56;   // Jitter Buffer状态大小为56
	int RTP_PACKET_FLAG  = 0x53;

	//ADPCM
	int FRAME_SIZE_ADPCM  = 40;   // adpcm帧大小为40
	int PACKET_SIZE_ADPCM = 44;   // adpcm包大小为44

	//G711
	int FRAME_SIZE_G711  = 80;   // g.711帧大小为80
	int PACKET_SIZE_G711 = 84;   // g.711包大小为84

	//不编码，使用裸数据
	int FRAME_SIZE_RAW_8K   = 160;   // RAW帧大小为160
	int PACKET_SIZE_RAW_8K  = 164;   // RAW包大小为164

	int SAMPLE_NUM_16K      = 160;   // 16K一帧样本数为160
	int FRAME_SIZE_RAW_16K  = 320;   // RAW帧大小为320
	int PACKET_SIZE_RAW_16K = 324;   // RAW包大小为324

	//最大值
	int MAX_FRAME_SEQUENCE           = 18000;
	int MAX_FAR_SPEECH_NUM           = 50;//25
	int MAX_PLAY_SPEECH_NUM          = 50;//25
}//end of interface ConstantDefine

//音频编码类型
interface AudioEncodeType{
	int AUDIO_ENCODE_TYPE_G729   = 0;
	int AUDIO_ENCODE_TYPE_ADPCM  = 1;
	int AUDIO_ENCODE_TYPE_G711   = 2;
	int AUDIO_ENCODE_TYPE_RAW    = 3;
}

//回声消除状态
interface AECStatus{
	int AEC_STATUS_START   = 0;
	int AEC_STATUS_PAUSE   = 1;
	int AEC_STATUS_RESUME  = 2;
}

//音频帧类
class AudioFrame
{
	public short sFrameSeq;  //音频帧序列号
	//public long lTimestamp;  //音频帧时间戳
	public short sFrameSize; //音频帧大小
	public byte [] data;     //音频帧数据， ConstantDefine.FRAME_SIZE_G729

	public AudioFrame(int iEncodeType){
		if (AudioEncodeType.AUDIO_ENCODE_TYPE_G729 == iEncodeType)
			data= new byte[ ConstantDefine.FRAME_SIZE_G729];
		else if (AudioEncodeType.AUDIO_ENCODE_TYPE_G711 == iEncodeType)
			data= new byte[ ConstantDefine.FRAME_SIZE_G711];
		else if (AudioEncodeType.AUDIO_ENCODE_TYPE_ADPCM == iEncodeType)
			data= new byte[ ConstantDefine.FRAME_SIZE_ADPCM];
		else
			data= new byte[ ConstantDefine.FRAME_SIZE_RAW_8K];
		//for (int i =0; i <  ConstantDefine.FRAME_SIZE_G729; i++)
		//	data[i] = 0;
	}
}

class EndianTransfer {

	/* 字节序转换函数  */
	public short HTONS(short x)
	{
		return (short)((((x)>>8)&0xff) | (((x)&0xff)<<8));
	}

	public short NTOHS(short x)
	{
		return HTONS(x);
	}

	public int HTONL(int x)
	{
		return ((((x)>>24)&0xff) | (((x)&0xff)<<24) | (((x)&0xff0000)>>8) | (((x)&0xff00)<<8));
	}

	public int NTOHL(int x)
	{
		return HTONL(x);
	}

}
