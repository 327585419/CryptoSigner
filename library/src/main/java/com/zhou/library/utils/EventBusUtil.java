package com.zhou.library.utils;

import com.zhou.library.bean.Event;

import org.greenrobot.eventbus.EventBus;

/**
 * author : Zhouzhou
 * e-mail : 553419781@qq.com
 * date   : 2019/6/6 16:00
 */
public class EventBusUtil {

    public static void post(String name, String param) {
        Event event = new Event();
        event.name = name;
        event.param = param;
        EventBus.getDefault().post(event);
    }

    public static void post(String name) {
        Event event = new Event();
        event.name = name;
        event.param = "";
        EventBus.getDefault().post(event);
    }


    public static void post(String name, Object object) {
        Event event = new Event();
        event.name = name;
        event.param = "";
        event.object = object;
        EventBus.getDefault().post(event);
    }

    public static void post(String name, String param, Object object) {
        Event event = new Event();
        event.name = name;
        event.param = param;
        event.object = object;
        EventBus.getDefault().post(event);
    }
}
