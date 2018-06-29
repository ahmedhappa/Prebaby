package com.usama.salamtek.Services;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.usama.salamtek.LoginActivity;
import com.usama.salamtek.QuestionsActivity;
import com.usama.salamtek.R;

public class MyJobServiceNotification extends JobService {
    AsyncTask asyncTask;


    @SuppressLint("StaticFieldLeak")
    @Override
    public boolean onStartJob(JobParameters job) {
        asyncTask = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                int requstId = 300;
                String notificationChannelId = "100";
                int notificationId = 300;
                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel notificationChannel = new NotificationChannel(
                            notificationChannelId,
                            "main Channel",
                            NotificationManager.IMPORTANCE_HIGH
                    );
                    if (notificationManager != null) {
                        notificationManager.createNotificationChannel(notificationChannel);
                    }
                }
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), requstId, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), notificationChannelId)
                        .setSmallIcon(R.drawable.ic_comments)
                        .setContentTitle("Weekly Questions")
                        .setContentText("Press the Notification To answer Weekly Question")
                        .setContentIntent(pendingIntent)
                        .setOngoing(true);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                    builder.setPriority(NotificationCompat.PRIORITY_HIGH);
                }

                if (notificationManager != null) {
                    notificationManager.notify(notificationId, builder.build());
                }
                Log.e("Yeah", "It is working");
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {

                jobFinished(job, false);
            }
        };
        asyncTask.execute();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        if (asyncTask != null) {
            asyncTask.cancel(true);
        }
        Log.e("Sorry", "The job ends..!");
        return true;
    }
}
