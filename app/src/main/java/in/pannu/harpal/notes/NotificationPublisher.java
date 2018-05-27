package in.pannu.harpal.notes;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;


public class NotificationPublisher extends BroadcastReceiver {

    public static String NOTIFICATION_ID = "notification-id";

    @Override
    public void onReceive(Context context, Intent intentn) {
         String id = intentn.getStringExtra(NOTIFICATION_ID);
         String Title = intentn.getStringExtra("TITLE");
         Intent intent;
         PendingIntent pendingIntent;
         NotificationCompat.Builder builder;
         NotificationManager notificationManager;

         String name = "Notes";
         String channelID = "Notes Channel";
         String description = "Notification Channel";
        notificationManager =(NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = null;
            if (notificationManager != null) {
                mChannel = notificationManager.getNotificationChannel(channelID);
            }
            if (mChannel == null) {
                mChannel = new NotificationChannel(channelID, name, importance);
                mChannel.setDescription(description);
                mChannel.enableVibration(true);
                mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                if (notificationManager != null) {
                    notificationManager.createNotificationChannel(mChannel);
                }
            }
            builder = new NotificationCompat.Builder(context, channelID);

            intent = new Intent(context, NoteView.class);
            intent.putExtra("ID", id);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            Log.d("Hz",id + " ID IN");
           // intent.setFlags(Intent.Flag_C);
            pendingIntent = PendingIntent.getActivity(context, Integer.parseInt(id), intent,  PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentTitle(Title)  // required
                    .setSmallIcon(android.R.drawable.ic_popup_reminder) // required
                    .setContentText("Note Reminder")  // required
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setTicker("Note Reminder")
                    .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
        } else {

            builder = new NotificationCompat.Builder( context);
            intent = new Intent(context, NoteView.class);
            intent.putExtra("ID", id);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            pendingIntent = PendingIntent.getActivity(context,  Integer.parseInt(id), intent,  PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentTitle(Title)                           // required
                    .setSmallIcon(android.R.drawable.ic_popup_reminder) // required
                    .setContentText("Note Reminder")  // required
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setTicker("Note Reminder")
                    .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400})
                    .setPriority(Notification.PRIORITY_HIGH);
        }

        Notification notification = builder.build();
        if (notificationManager != null) {
            notificationManager.notify(Integer.parseInt(id), notification);
        }
    }
}
