package com.cyphermessenger.android;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.*;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.cyphermessenger.client.*;
import com.cyphermessenger.crypto.ECKey;
import com.cyphermessenger.sqlite.DBManagerAndroidImpl;

import java.util.*;

/**
 * Created by paolo on 24/05/14.
 */
public class ContentUpdateManager extends BroadcastReceiver implements ContentListener {

    private final static long LONG_INTERVAL = 1000 * 60;
    private final static long SHORT_INTERVAL = 1000 * 10;
    private final static long MAX_THREAD_TIME = 1000 * 10;

    public static final String BROADCAST_NOTIFICATIONS = "com.cyphermessenger.android.BROADCAST_NOTIFICATIONS";

    private final AlarmManager alarmManager;
    private final PendingIntent pendingIntent;
    private final PackageManager packageManager;
    private final ComponentName componentName;
    private final IntentFilter intentFilter;



    private NotificationManager notificationManager;
    private NotificationCompat.Builder notificationBuilder;
    private NotificationListener notificationListener;
    private long activeContact = -1;
    private ContentManager contentManager;
    private Context applicationContext;

    public ContentUpdateManager() {
        this.alarmManager = null;
        this.notificationManager = null;
        this.pendingIntent = null;
        this.packageManager = null;
        this.componentName = null;
        this.intentFilter = null;
    }

    public ContentUpdateManager(Context ctx) {
        this.alarmManager = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent();
        intent.setAction(BROADCAST_NOTIFICATIONS);
        //intent.setClass(ctx, ContentUpdateManager.class);
        this.pendingIntent = PendingIntent.getBroadcast(ctx, 1, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        this.packageManager = ctx.getPackageManager();
        this.componentName = new ComponentName(ctx, ContentUpdateManager.class);
        this.intentFilter = new IntentFilter(BROADCAST_NOTIFICATIONS);
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

    private void toggleDefaultReceiver(boolean enabled) {
        if(enabled) {
            packageManager.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
        } else {
            packageManager.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        }
    }

    public void startDefaultReceiver() {
        toggleDefaultReceiver(true);
        setLongInterval();
    }

    public void register(Context ctx) {
        toggleDefaultReceiver(false);
        ctx.registerReceiver(this, intentFilter);
        setShortInterval();
    }

    public void unregister(Context ctx) {
        ctx.unregisterReceiver(this);
        toggleDefaultReceiver(true);
        setLongInterval();
    }

    private void setLongInterval() {
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), LONG_INTERVAL, pendingIntent);
    }

    private void setShortInterval() {
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), SHORT_INTERVAL, pendingIntent);
    }

    public void clearAlarm() {
        toggleDefaultReceiver(false);
        alarmManager.cancel(pendingIntent);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("onReceive", "onReceive called");
        applicationContext = context;
        contentManager = new ContentManager(DBManagerAndroidImpl.getInstance(context), this);
        notificationBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(android.R.drawable.stat_notify_chat)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS);
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        contentManager.pullAll();
    }


    @Override
    public final void onPullMessages(List<CypherMessage> messages, long notifiedUntil) {
        Log.d("MSGS", Arrays.toString(messages.toArray()));
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
                Intent contentIntent = new Intent(applicationContext, MessagesActivity.class);
                contentIntent.putExtra("CONTACT", contact.getUserID());
                notificationManager.notify(contact.getUsername().hashCode(),
                        notificationBuilder
                                .setContentIntent(PendingIntent.getActivity(applicationContext, 1, contentIntent, PendingIntent.FLAG_UPDATE_CURRENT))
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
    public final void onPullContacts(List<CypherContact> contacts, long notifiedUntil) {
        Log.d("CTCTS", Arrays.toString(contacts.toArray()));
        Iterator<CypherContact> i = contacts.iterator();
        while(i.hasNext()) {
            CypherContact contact = i.next();
            if(contact.isFirst()) {
                i.remove();
            } else if(contact.getContactTimestamp() > notifiedUntil) {
                String text = null;
                switch(contact.getStatus()) {
                    case CypherContact.ACCEPTED:
                        text = contact.getUsername() + applicationContext.getString(R.string.notification_contact_accepted);
                        break;
                    case CypherContact.DENIED:
                        text = contact.getUsername() + applicationContext.getString(R.string.notification_contact_denied);
                        break;
                    case CypherContact.WAITING:
                        text = contact.getUsername() + applicationContext.getString(R.string.notification_contact_request);
                }
                if(text != null) {
                    Intent contactNotification = new Intent(applicationContext, ContactsActivity.class);
                    notificationManager.notify(contact.getUsername().hashCode(),
                            notificationBuilder
                                    .setContentIntent(PendingIntent.getActivity(applicationContext, 1, contactNotification, PendingIntent.FLAG_UPDATE_CURRENT))
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
    public final void onPullKeys(List<ECKey> keys, long notifiedUntil) {
        Log.d("KEYS", Arrays.toString(keys.toArray()));
        if(notificationListener != null) {
            notificationListener.onNewKeys();
        }
    }

    @Override
    public final void onGetMessages(List<CypherMessage> messages) {}
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
    public void onContactDeleted(String name) {}
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
