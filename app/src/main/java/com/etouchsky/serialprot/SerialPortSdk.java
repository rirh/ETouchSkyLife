package com.etouchsky.serialprot;

/**
 * 通讯工具类，包括开始通讯、停止通讯、写数据功能
 *
 * @author gemvary
 * @version 1.0
 */
public class SerialPortSdk {
	static
	{
		loadLibrary();
	}

	/**
	 * 加载so库
	 *
	 * @hide
	 */
	static void loadLibrary(){
		System.loadLibrary("serialble");
	}


	public native static byte[] Packdata(int cmd,String hexData,int len);
}
