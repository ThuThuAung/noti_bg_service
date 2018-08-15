package com.thuthu.notiusingbackgroundservice.bg_service;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.text.Html;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.RemoteViews;

import com.thuthu.notiusingbackgroundservice.NotiDetailActivity;
import com.thuthu.notiusingbackgroundservice.R;
import com.thuthu.notiusingbackgroundservice.model.User;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Thu Thu on 23/11/16.
 */
public class Androidservice extends Service {

    final static int RQS_STOP_SERVICE = 1;

    NotifyServiceReceiver notifyServiceReceiver;

    WindowManager mWindowManager;
    View mView;
    ConnectivityManager connectivity;
    int delayTime = 1000 * 60 * 3; //3 minutes
    int periodTime = 1000 * 60 * 15; //15 minutes
    private static final String TAG = Androidservice.class.getSimpleName();


   // private CheckNotiTask mTask;

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        notifyServiceReceiver = new NotifyServiceReceiver();
        super.onCreate();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        MyTimerTask myTask = new MyTimerTask();
        Timer myTimer = new Timer();

        myTimer.schedule(myTask, delayTime, periodTime);

        return START_STICKY;
    }
    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        this.unregisterReceiver(notifyServiceReceiver);
        super.onDestroy();
    }


    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    static public class NotifyServiceReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent arg1) {
            // TODO Auto-generated method stub
            int rqs = arg1.getIntExtra("RQS", 0);
            if (rqs == RQS_STOP_SERVICE) {
               // stopSelf();

            }
            Intent startServiceIntent = new Intent(context, Androidservice.class);
            context.startService(startServiceIntent);

        }
    }

    class MyTimerTask extends TimerTask {
        public void run() {

            // observable
            Observable<User> testObservable = getData();

            // observer
            Observer<User> testObserver = getDataObserver();

            // observer subscribing to observable
            testObservable
                    .observeOn(Schedulers.io())
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .subscribe(testObserver);

          /*  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                new CheckNotiTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                new CheckNotiTask().execute();
            }*/

        }
    }

    private Observable<User>
    getData() {
        User user = new User();
        user.setName("Thu Thu Aung");
        user.setPosition("Android Developer");
        user.setExp("5 years");
        return Observable.just(user);
    }


    private Observer<User> getDataObserver() {
        return new Observer<User>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.d(TAG, "onSubscribe");
            }

            @Override
            public void onNext(User user) {
                try {

                    onCreateCustomNoti(getApplicationContext(), user);

                } catch (NullPointerException ne) {
                    ne.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError: " + e.getMessage());
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "All items are emitted!");
            }
        };
    }

    /*private class CheckNotiTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            return null;
        }

        @Override
        protected void onPostExecute(String str) {
            super.onPostExecute(str);
            try {

                onCreateCustomNoti(getApplicationContext(), "Testing Notification");

            } catch (NullPointerException ne) {
                ne.printStackTrace();
            }
        }



    }*/

    private void onCreateCustomNoti(Context context, User user) {
        Random generator = new Random();

        //Create Intent to launch this Activity on notification click
        Intent intent = new Intent(context, NotiDetailActivity.class);
        intent.putExtra("Title", user.getName());
        intent.putExtra("Body", user.getPosition());

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);


        // Send data to NotificationView Class
        PendingIntent pIntent = PendingIntent.getActivity(context, generator.nextInt(), intent,
                PendingIntent.FLAG_ONE_SHOT);

        // Inflate the notification layout as RemoteViews
        RemoteViews contentView = new RemoteViews(context.getPackageName(), R.layout.custom_noti);


        WindowManager window = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = window.getDefaultDisplay();
        int width = display.getWidth();
        int height = display.getHeight();

        int size = context.getResources().getDimensionPixelSize(R.dimen.myFontSizeCat);


        contentView.setImageViewBitmap(R.id.tvNotificationTitle, drawText(context, user.getName(), 1100));

        contentView.setImageViewBitmap(R.id.iv_cat, drawTextAuthorandCategory(context, user.getPosition(), width/3, android.R.color.darker_gray, size));

        contentView.setImageViewBitmap(R.id.iv_author, drawTextAuthorandCategory(context, user.getExp(), width/3, R.color.colorPrimary, size));

        Bitmap largeIconBitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);
        contentView.setImageViewBitmap(R.id.album_art, largeIconBitmap);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                // Set Icon
                .setLargeIcon(largeIconBitmap)
                // Sets the ticker text
                .setTicker(getResources().getString(R.string.app_name))
                // Sets the small icon for the ticker
                .setSmallIcon(R.mipmap.ic_launcher)
                // Dismiss Notification
                .setAutoCancel(true)
                .setVisibility(View.VISIBLE)
                // Set PendingIntent into Notification
                .setContentIntent(pIntent)
                // Set RemoteViews into Notification
                .setContent(contentView);

        // Use the NotificationManager to show the notification
        NotificationManager notificationmanager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationmanager.notify(generator.nextInt(), builder.build());
    }




    public static Bitmap drawText(Context context, String text, int textWidth) {
        // Get text dimensions
        String fontName = "Zawgyi-One";

        int size = context.getResources().getDimensionPixelSize(R.dimen.myFontSize);
        Typeface font = Typeface.createFromAsset(context.getAssets(), String.format("%s.ttf", fontName));
        TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG
                | Paint.LINEAR_TEXT_FLAG);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setColor(Color.BLACK);
        textPaint.setTypeface(font);
        textPaint.setTextAlign(Align.LEFT);
        textPaint.setTextSize(size);
        StaticLayout mTextLayout = new StaticLayout(text, textPaint,
                textWidth, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);

        // Create bitmap and canvas to draw to
        Bitmap b = Bitmap.createBitmap(textWidth, mTextLayout.getHeight(), Config.ARGB_8888);
        Canvas c = new Canvas(b);

        // Draw background
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG
                | Paint.LINEAR_TEXT_FLAG);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);

        c.drawPaint(paint);
        // Draw text
        c.save();

        c.translate(0, 0);
        mTextLayout.draw(c);
        c.restore();

        return b;
    }


    public static Bitmap drawTextAuthorandCategory(Context context, String text, int textWidth, int color, int size) {
        // Get text dimensions
        String fontName = "Zawgyi-One";


        Typeface font = Typeface.createFromAsset(context.getAssets(), String.format("%s.ttf", fontName));
        TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG
                | Paint.LINEAR_TEXT_FLAG);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setColor(context.getResources().getColor(color));
        textPaint.setTextAlign(Align.LEFT);
        textPaint.setTypeface(font);
        textPaint.setTextSize(size);

        StaticLayout mTextLayout = new StaticLayout(text, textPaint,
                textWidth, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);

        // Create bitmap and canvas to draw to
        Bitmap b = Bitmap.createBitmap(textWidth, mTextLayout.getHeight(), Config.ARGB_8888);
        Canvas c = new Canvas(b);

        // Draw background
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG
                | Paint.LINEAR_TEXT_FLAG);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);

        c.drawPaint(paint);
        // Draw text
        c.save();

        c.translate(0, 0);
        mTextLayout.draw(c);
        c.restore();

        return b;
    }

    public String stripHtml(String html) {
        return Html.fromHtml(html).toString();
    }


    private void hideDialog(){
        if(mView != null && mWindowManager != null){
            mWindowManager.removeView(mView);
            mView = null;
        }
    }



}