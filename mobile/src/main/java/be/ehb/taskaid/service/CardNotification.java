package be.ehb.taskaid.service;

import android.app.Notification;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;

import be.ehb.taskaid.R;
import be.ehb.taskaid.model.Card;
import be.ehb.taskaid.model.Task;

/**
 * Created by davy.van.belle on 8/09/2015.
 */
public class CardNotification {
    private Task task = null;

    public CardNotification(Task task) {
        this.task = task;
    }


    public Notification[] buildNotifications(Context context) {

        Bitmap bitmap = Bitmap.createBitmap(320,320, Bitmap.Config.ARGB_8888);
        bitmap.eraseColor(Color.RED);

        NotificationCompat.WearableExtender wearableOptions =
                new NotificationCompat.WearableExtender()
                .setBackground(bitmap);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setContentTitle(task.getName())
                .setVibrate(new long[]{0, 100, 50, 100})
                .setPriority(Notification.PRIORITY_DEFAULT)
                .setSmallIcon(R.mipmap.ic_launcher);

        for(Card card : task.getCards()){
            NotificationCompat.BigPictureStyle style = new NotificationCompat.BigPictureStyle();
            if (card.getPicture() != null) {
                style.bigPicture(card.getPicture());

                Notification picPage = new NotificationCompat.Builder(context)
                        .extend(new NotificationCompat.WearableExtender()
                                .setHintShowBackgroundOnly(true))
                        .setStyle(style)
                        .build();

                Notification textPage = new NotificationCompat.Builder(context)
                        .setContentTitle(card.getText())
                        .setStyle(style)
                        .build();

                wearableOptions.addPage(textPage)
                        .addPage(picPage);
            } else {

                Notification textPage = new NotificationCompat.Builder(context)
                        .extend(new NotificationCompat.WearableExtender().setBackground(bitmap))
                        .setContentTitle(card.getText())
                        .setStyle(style)
                        .build();
                wearableOptions.addPage(textPage);
            }

        }

        builder.extend(wearableOptions);


        /*
        NotificationCompat.BigPictureStyle style = new NotificationCompat.BigPictureStyle();
        style.bigPicture(BitmapFactory.decodeResource(context.getResources(),
                R.drawable.example_big_picture));
        style.setBigContentTitle(context.getString(R.string.big_picture_style_example_title));
        style.setSummaryText(context.getString(
                R.string.big_picture_style_example_summary_text));

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setStyle(style);

        Notification secondPage = new NotificationCompat.Builder(context)
                .extend(new NotificationCompat.WearableExtender()
                        .setHintShowBackgroundOnly(true))
                .build();

        Notification thirdPage = new NotificationCompat.Builder(context)
                .setContentTitle(context.getString(R.string.second_page_content_title))
                .setContentText(context.getString(R.string.second_page_content_text))
                .extend(new NotificationCompat.WearableExtender()
                        .setContentIcon(R.drawable.content_icon_small)
                        .setContentIconGravity(Gravity.START))
                .build();


        NotificationCompat.WearableExtender wearableOptions =
                new NotificationCompat.WearableExtender()
                        .addPage(secondPage);

        applyBasicOptions(context, builder, wearableOptions, options);
        builder.extend(wearableOptions);
        */
        return new Notification[] { builder.build() };
    }

    /*
    private static NotificationCompat.Builder applyBasicOptions(Context context,
                                                                NotificationCompat.Builder builder, NotificationCompat.WearableExtender wearableOptions,
                                                                NotificationPreset.BuildOptions options) {
        builder.setContentTitle(options.titlePreset)
                .setContentText(options.textPreset)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setDeleteIntent(NotificationUtil.getExamplePendingIntent(
                        context, R.string.example_notification_deleted));
        options.actionsPreset.apply(context, builder, wearableOptions);
        options.priorityPreset.apply(builder, wearableOptions);
        if (options.includeLargeIcon) {
            builder.setLargeIcon(BitmapFactory.decodeResource(
                    context.getResources(), R.drawable.example_large_icon));
        }
        if (options.isLocalOnly) {
            builder.setLocalOnly(true);
        }
        if (options.hasContentIntent) {
            builder.setContentIntent(NotificationUtil.getExamplePendingIntent(context,
                    R.string.content_intent_clicked));
        }
        if (options.vibrate) {
            builder.setVibrate(new long[] {0, 100, 50, 100} );
        }
        return builder;
    }
    */

}

/*
    private static NotificationCompat.Builder applyBasicOptions(Context context,
                                                                NotificationCompat.Builder builder, NotificationCompat.WearableExtender wearableOptions,
                                                                NotificationPreset.BuildOptions options) {
        builder.setContentTitle(options.titlePreset)
                .setContentText(options.textPreset)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setDeleteIntent(NotificationUtil.getExamplePendingIntent(
                        context, R.string.example_notification_deleted));
        options.actionsPreset.apply(context, builder, wearableOptions);
        options.priorityPreset.apply(builder, wearableOptions);
        if (options.includeLargeIcon) {
            builder.setLargeIcon(BitmapFactory.decodeResource(
                    context.getResources(), R.drawable.example_large_icon));
        }
        if (options.isLocalOnly) {
            builder.setLocalOnly(true);
        }
        if (options.hasContentIntent) {
            builder.setContentIntent(NotificationUtil.getExamplePendingIntent(context,
                    R.string.content_intent_clicked));
        }
        if (options.vibrate) {
            builder.setVibrate(new long[] {0, 100, 50, 100} );
        }
        return builder;
    }
 */
