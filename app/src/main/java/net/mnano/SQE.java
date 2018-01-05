package net.mnano;

public class SQE {
	static {
		//System.loadLibrary("MNPCSQE");
		//System.loadLibrary("MNPCSQEFIX");
		//System.loadLibrary("MNPCSQE48K");
		System.loadLibrary("MNPCSQE48KAECM");
	}

	/* 初始化函数 */
	public native short cntSqeInit(int sample_rate, int aec_param, int ns_param, int agc_param, int auto_align);

	/* 语音增强处理函数     */
	public native short cntSqeProc(byte[] in_near, byte[] in_far, byte[] out, byte[] vad);

	/* AGC处理函数     */
	//public native short cntAgcProc(byte[] in_near);

	/* 结束函数     */
	public native short cntSqeExit();

	/* 采样率转换函数 */
	public native short ReSample8kto48k(byte[] in, int length, byte[] out, int channel);
	public native short ReSample48kto8k(byte[] in, int length, byte[] out, int channel);

	public native short ReSample16kto48k(byte[] in, int length, byte[] out, int channel);
	public native short ReSample48kto16k(byte[] in, int length, byte[] out, int channel);

	public native short ReSample16kto8k(byte[] in, int length, byte[] out, int channel);
	public native short ReSample8kto16k(byte[] in, int length, byte[] out, int channel);

	public native short ReSample44kto8k(byte[] in, int length, byte[] out, int channel);
	public native short ReSample8kto44k(byte[] in, int length, byte[] out, int channel);

	public native short ReSample44kto16k(byte[] in, int length, byte[] out, int channel);
	public native short ReSample16kto44k(byte[] in, int length, byte[] out, int channel);


}

