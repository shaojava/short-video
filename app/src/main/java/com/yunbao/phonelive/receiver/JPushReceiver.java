package com.yunbao.phonelive.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.yunbao.phonelive.jpush.JPushUtil;
import com.yunbao.phonelive.utils.L;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by cxf on 2017/8/3.
 */

public class JPushReceiver extends BroadcastReceiver {

    private String TAG="收到推送-----";

    @Override
    public void onReceive(Context context, Intent intent) {

        Bundle bundle = intent.getExtras();

        switch (intent.getAction()) {
            case "cn.jpush.android.intent.REGISTRATION":
                L.e(JPushUtil.TAG, "用户注册");
                break;
            case "cn.jpush.android.intent.MESSAGE_RECEIVED":
                L.e(JPushUtil.TAG, "用户接收SDK消息");
                break;
            case "cn.jpush.android.intent.NOTIFICATION_RECEIVED":
                L.e(JPushUtil.TAG, "用户收到通知栏信息");
                receivingNotification(bundle);
                break;
            case "cn.jpush.android.intent.NOTIFICATION_OPENED":
                L.e(JPushUtil.TAG, "用户打开通知栏信息");
                openNotification(context,bundle);
                break;
        }
    }


    private void receivingNotification(Bundle bundle){
        String title = bundle.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE);
        L.e(TAG, " title : " + title);
        String message = bundle.getString(JPushInterface.EXTRA_ALERT);
        L.e(TAG, "message : " + message);
        String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
        L.e(TAG, "extras : " + extras);
    }

    private void openNotification( Context context,Bundle bundle){
//        String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
//        String myValue = "";
//        try {//解析成功  直播间消息
//            JSONObject jsonObject=JSON.parseObject(extras);
//            JPushEvent jPushEvent=JSON.parseObject(jsonObject.getString("userinfo"),JPushEvent.class);
//            onOpenNotification(context,jPushEvent);
//        } catch (Exception e) {//解析失败 普通推送
//            L.e(TAG, e.getMessage());
//            onOpenNotification(context);
//            return;
//        }
    }







//
//    /**
//     * 解析成功打开推送处理
//     * @param jPushEvent
//     */
//    private void onOpenNotification(Context context,JPushEvent jPushEvent) {
//        if (!AppConfig.getInstance().isLaunched()) {//app没有启动
//            Intent intent = new Intent(context, LauncherActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            Bundle bundle=new Bundle();
//            bundle.putParcelable("jpushevent",jPushEvent);
//            intent.putExtra("jpusheventBundle",bundle);
//            context.startActivity(intent);
//        }else{
//            ActivityManager mAm = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
//            //获得当前运行的task
//            List<ActivityManager.RunningTaskInfo> taskList = mAm.getRunningTasks(100);
//            for (ActivityManager.RunningTaskInfo rti : taskList) {
//                //找到当前应用的task，并启动task的栈顶activity，达到程序切换到前台
//                if (rti.topActivity.getPackageName().equals(context.getPackageName())) {
//                    L.e("回到前台----->");
//                    mAm.moveTaskToFront(rti.id, 0);
//                    EventBus.getDefault().post(jPushEvent);
//                    return;
//                }
//            }
//        }
//    }
//
//    /**
//     * 解析失败打开推送处理
//     * @param context
//     */
//    private void onOpenNotification(Context context) {
//        if (!AppConfig.getInstance().isLaunched()) {//app没有启动
//            Intent intent = new Intent(context, LauncherActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            context.startActivity(intent);
//        }else{
//            ActivityManager mAm = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
//            //获得当前运行的task
//            List<ActivityManager.RunningTaskInfo> taskList = mAm.getRunningTasks(100);
//            for (ActivityManager.RunningTaskInfo rti : taskList) {
//                //找到当前应用的task，并启动task的栈顶activity，达到程序切换到前台
//                if (rti.topActivity.getPackageName().equals(context.getPackageName())) {
//                    L.e("回到前台----->");
//                    mAm.moveTaskToFront(rti.id, 0);
//                    return;
//                }
//            }
//         /*   //若没有找到运行的task，用户结束了task或被系统释放，则重新启动LauncherActivity
//            Intent intent2 = new Intent(context, LauncherActivity.class);
//            intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            context.startActivity(intent2);*/
//        }
//    }

}
