package com.nallanudi.nallanudi;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import java.util.List;

public class NotificationReceiver extends BroadcastReceiver {

    public static final String CHANNEL_ID = "nallanudi_channel";

    @Override
    public void onReceive(Context context, Intent intent) {
        // Get a random word from DB
        AppDatabase db = AppDatabase.getInstance(context);
        List<Word> words = db.wordDao().getAllWords();
        if (words.isEmpty()) return;

        int index = (int)(Math.random() * words.size());
        Word word = words.get(index);

        // Create notification channel
        NotificationManager manager =
                (NotificationManager) context.getSystemService(
                        Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Daily Word",
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Daily word of the day");
            manager.createNotificationChannel(channel);
        }

        // Tap notification → open app
        Intent openApp = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, openApp,
                PendingIntent.FLAG_UPDATE_CURRENT |
                        PendingIntent.FLAG_IMMUTABLE);

        // Build notification
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(android.R.drawable.ic_dialog_info)
                        .setContentTitle("📖 Word of the Day — "
                                + word.englishWord)
                        .setContentText(word.kannadaWord
                                + " : " + word.kannadaExplanation)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(word.kannadaWord + "\n"
                                        + word.kannadaExplanation))
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true);

        manager.notify(1001, builder.build());
    }
}