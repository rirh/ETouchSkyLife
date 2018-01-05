
/*
* 文 件 名：PlayVedio.java
* 版本信息：一点天下有限公司 Copyright 2012-10-11,  All rights reserved
* 描    述:
* 创 建 人: guosong
* 创建时间：2012-10-11
*
*/

package com.etouchsky.util;

import android.content.Context;
import android.media.MediaPlayer;


/**
 * {一句话功能简述}
 * {功能详细描述}
 *
 * @author guosong
 * @version [版本号, 2012-10-11 下午04:28:27]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 * @hide
 */
public class PlayVedio {
	public static MediaPlayer play;

	public static void playVedio(Context context, int url) {

		try
		{
			/*int url = url_cn;
			if (!HdUtils.getLocaleLanguage().equals("zh-CN") && !HdUtils.getLocaleLanguage().equals("zh-TW")) {
				url = url_en;
			}
			if (url == 0) {
				return;
			}*/

			if (play != null) {
				try {
					play.release();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			play = MediaPlayer.create(context, url);
			play.start();
			play.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
				@Override
				public void onCompletion(MediaPlayer arg0) {
					try {
						play.release();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		}
		catch (IllegalStateException e)
		{
			e.printStackTrace();
		}

	}
}
