package com.cyphermessenger.android;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import com.cyphermessenger.client.*;
import com.cyphermessenger.crypto.ECKey;
import com.cyphermessenger.sqlite.DBManagerAndroidImpl;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by paolo on 24/05/14.
 */
public class ContentUpdateManager extends BroadcastReceiver implements ContentListener {

    private final static long LONG_INTERVAL = 1000 * 60 * 1;
    private final static long SHORT_INTERVAL = 1000 * 10;
    private final static long MAX_THREAD_TIME = 1000 * 10;

    private final AlarmManager alarmManager;
    private final PendingIntent pendingIntent;
    private final NotificationManager notificationManager;
    private final NotificationCompat.Builder notificationBuilder;
    private final Context ctx;
    private final ContentManager contentManager;
    private NotificationListener notificationListener;
    private long activeContact;

    public ContentUpdateManager(Context ctx) {
        this.ctx = ctx;
        this.alarmManager = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        this.notificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(ctx, ContentUpdateManager.class);
        this.pendingIntent = PendingIntent.getBroadcast(ctx, 0, intent, 0);
        this.contentManager = new ContentManager(DBManagerAndroidImpl.getInstance(ctx), this);
        this.notificationBuilder = new NotificationCompat.Builder(ctx).setSmallIcon(android.R.drawable.stat_notify_chat);
    }

    public ContentUpdateManager(Context ctx, NotificationListener notificationListener) {
        this(ctx);
        this.notificationListener = notificationListener;
    }

    public void setNotificationListener(NotificationListener notificationListener) {
        this.notificationListener = notificationListener;
    }

    public void setActiveContact(long activeContact) {
        this.activeContact = activeContact;
    }

    public void unsetActiveContact() {
        this.activeContact = -1;
    }

    void startReceiver() {
        setLongInterval();
    }

    public void setLongInterval() {
        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, System.currentTimeMillis(), LONG_INTERVAL, pendingIntent);
    }

    public void setShortInterval() {
        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, System.currentTimeMillis(), SHORT_INTERVAL, pendingIntent);
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        contentManager.pullAll();
        try {
            contentManager.waitForAllRequests(MAX_THREAD_TIME);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onPullMessages(List<CypherMessage> messages, long notifiedUntil) {
        HashMap<Long, List<CypherMessage>> map = new HashMap<>();
        for(CypherMessage m : messages) {
            long id = m.getContactID();
            LinkedList<CypherMessage> list = (LinkedList<CypherMessage>) map.get(id);
            if(list == null) {
                list = new LinkedList<>();
                map.put(id, list);
            }
            list.add(m);
        }
        for(Map.Entry<Long, List<CypherMessage>> e : map.entrySet()) {
            // discard active contact
            if(e.getKey().equals(activeContact)) {
                continue;
            }
            CypherMessage first = e.getValue().get(0);
            if(first != null && first.getTimestamp() > notifiedUntil) {
                CypherContact contact = contentManager.getContactByID(e.getKey());
                notificationManager.notify(contact.getUsername().hashCode(),
                        notificationBuilder
                                .setContentTitle(contact.getUsername())
                                .setContentText(first.getText())
                                .build()
                );
            }
        }
        if(notificationListener != null) {
            notificationListener.onNewMessages(map);
        }
    }

    @Override
    public void onPullContacts(List<CypherContact> contacts, long notifiedUntil) {
        for(CypherContact contact : contacts) {
            if(contact.getContactTimestamp() > notifiedUntil) {
                NotificationCompat.Builder tmp = notificationBuilder;
                String text = null;
                switch(contact.getStatus()) {
                    case CypherContact.ACCEPTED:
                        text = contact.getUsername() + " accepted your contact request";
                        break;
                    case CypherContact.DENIED:
                        text = contact.getUsername() + " blocked you";
                        break;
                    case CypherContact.WAITING:
                        text = contact.getUsername() + " sent you a contact request";
                }
                if(text != null) {
                    notificationManager.notify(contact.getUsername().hashCode(),
                            notificationBuilder
                                    .setContentTitle(contact.getUsername())
                                    .setContentText(text)
                                    .build()
                    );
                }
            }
        }
        if(notificationListener != null) {
            notificationListener.onNewContacts(contacts);
        }
    }

    @Override
    public void onPullKeys(List<ECKey> keys, long notifiedUntil) {
        if(notificationListener != null) {
            notificationListener.onNewKeys();
        }
    }

    @Override
    public void onLogged(CypherUser user) {}
    @Override
    public void onMessageSent(CypherMessage message) {}
    @Override
    public void onCaptcha(Captcha captcha) {}
    @Override
    public void onFindUser(List<String> list) {}
    @Override
    public void onContactChange(CypherContact contact) {}
    @Override
    public void onServerError() {}
    @Override
    public void onSessionInvalid() {}
    @Override
    public void onCaptchaInvalid() {}
    @Override
    public void onUsernameTaken() {}
    @Override
    public void onUsernameNotFound() {}
    @Override
    public void onLoginInvalid() {}
    @Override
    public void onContactNotFound() {}
    @Override
    public void onContactWaiting() {}
    @Override
    public void onContactBlocked() {}
    @Override
    public void onContactDenied() {}
}
