package com.zm.secretsign.utils;


import android.content.Context;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.media.SoundPool;
import android.net.Uri;
import android.util.SparseIntArray;



/**
 * author : Zhouzhou
 * e-mail : 553419781@qq.com
 * date   : 2019/7/4 15:59
 */
public class SoundUtil {

    public static int SOUND_SUCCESS = 1;
    public static int SOUND_FAIL = 2;

    private static SparseIntArray soundMap;
    private static SoundPool soundPool;
//    private static AudioManager am;

    public static void initSound(Context context) {
        if (soundPool != null) {
            return;
        }

        soundPool = new SoundPool(10, AudioManager.STREAM_NOTIFICATION, 5);
        if (soundMap == null) {
            soundMap = new SparseIntArray();
        }
        soundMap.put(SOUND_SUCCESS, soundPool.load(context, com.zhou.zxing.R.raw.barcodebeep, 1));
        soundMap.put(SOUND_FAIL, soundPool.load(context, com.zhou.zxing.R.raw.serror, 1));

//        am = (AudioManager) context.getSystemService(AUDIO_SERVICE);// 实例化AudioManager对象
    }

    public static void freeSound() {
        if (soundMap != null) {
            soundMap.clear();
            soundMap = null;
        }
        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
        }

//        if (am != null) {
//            am = null;
//        }
    }

    /**
     * 播放提示音
     * SOUND_SUCCESS SOUND_FAIL
     *
     * @param id 成功1，失败2
     */
    public static void playSound(int id) {
//        float audioMaxVolume = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC); // 返回当前AudioManager对象的最大音量值
//        float audioCurrentVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC);// 返回当前AudioManager对象的音量值
//        float volumeRatio = audioCurrentVolume / audioMaxVolume;
//        try {
//            soundPool.play(soundMap.get(id), volumeRatio, // 左声道音量
//                    volumeRatio, // 右声道音量
//                    1, // 优先级，0为最低
//                    0, // 循环次数，0无不循环，-1无永远循环
//                    1 // 回放速度 ，该值在0.5-2.0之间，1为正常速度
//            );
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        soundPool.play(soundMap.get(id), 1, 1, 0, 0, 1);
    }

    public static void playBee(Context context) {
        if (soundPool == null) {
            initSound(context);
        }

        playSound(SOUND_SUCCESS);
    }

    public static void playFail() {
        playSound(SOUND_FAIL);
    }

    public static void playSystemSound(Context context) {
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone r = RingtoneManager.getRingtone(context, notification);
        r.play();
    }
}

