package com.cyphermessenger.client;


import android.util.Log;
import com.cyphermessenger.crypto.ECKey;
import org.apache.http.impl.entity.StrictContentLengthStrategy;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class ContentManager {

    private static ContentManager contentManager;
    private final DBManager dbManager;
    private ContentListener contentListener;
    private final HashSet<Thread> activeThreads = new HashSet<>(5);
    private CypherSession session;

    public ContentManager(DBManager dbManager) {
        this.dbManager = dbManager;
        this.session = dbManager.getSession();
        this.contentListener = new BasicContentListener();
    }

    public ContentManager(DBManager dbManager, ContentListener contentListener) {
        this.dbManager = dbManager;
        this.session = dbManager.getSession();
        this.contentListener = contentListener;
    }

    public void setContentListener(ContentListener contentListener) {
        this.contentListener = contentListener;
    }

    public void interruptRequests() {
        synchronized (activeThreads) {
            Iterator<Thread> i = activeThreads.iterator();
            while (i.hasNext()) {
                Thread th = i.next();
                if (th.isAlive()) {
                    th.interrupt();
                }
                i.remove();
            }
        }
    }

    public void waitForAllRequests(long millis) throws InterruptedException {
        synchronized (activeThreads) {
            Iterator<Thread> i = activeThreads.iterator();
            while (i.hasNext()) {
                Thread th = i.next();
                if (th.isAlive()) {
                    th.join(millis);
                }
                i.remove();
            }
        }
    }

    public void waitForAllRequests() {
        try {
            waitForAllRequests(1000 * 30);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void addThread(Thread thread) {
        thread.start();
        synchronized (activeThreads) {
            Iterator<Thread> i = activeThreads.iterator();
            while (i.hasNext()) {
                if (!i.next().isAlive()) {
                    i.remove();
                }
            }
            activeThreads.add(thread);
        }
    }
    
    public boolean isLogged() {
    	return session != null;
    }

    public CypherSession getSession() {
        return session;
    }

    private void handleException(Exception e) {
        Log.e("ContentManager", "handleException", e);
        if(e.getClass() == IOException.class) {
            contentListener.onServerError();
        } else if(e.getClass() == APIErrorException.class) {
            switch(((APIErrorException) e).getStatusCode()) {
                case StatusCode.SESSION_INVALID:
                case StatusCode.SESSION_EXPIRED:
                    contentListener.onSessionInvalid(); break;
                case StatusCode.CAPTCHA_INVALID:
                    contentListener.onCaptchaInvalid(); break;
                case StatusCode.LOGIN_INVALID:
                    contentListener.onLoginInvalid(); break;
                case StatusCode.USERNAME_TAKEN:
                    contentListener.onUsernameTaken(); break;
                case StatusCode.USERNAME_NOT_FOUND:
                    contentListener.onUsernameNotFound(); break;
                case StatusCode.CONTACT_NOT_FOUND:
                    contentListener.onContactNotFound(); break;
                case StatusCode.CONTACT_WAITING:
                    contentListener.onContactWaiting(); break;
                case StatusCode.CONTACT_BLOCKED:
                    contentListener.onContactBlocked(); break;
                case StatusCode.CONTACT_DENIED:
                    contentListener.onContactDenied(); break;
                default:
                    contentListener.onServerError();
            }
        }
    }

    public void requestCaptcha() {
    	Thread th = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Captcha captcha = SyncRequest.requestCaptcha();
                    contentListener.onCaptcha(captcha);
                } catch (Exception e) {
                    handleException(e);
                }
            }
        });
        addThread(th);
    }

    public void register(final String username, final String password, final String captchaValue, final Captcha captcha) {
        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    CypherUser user = SyncRequest.registerUser(username, password, captchaValue, captcha);
                    CypherSession _session = SyncRequest.userLogin(username, password);
                    dbManager.insertKey(_session.getUser(), _session.getUser().getKey());
                    dbManager.setSession(_session);
                    session = _session;
                    contentListener.onLogged(_session.getUser());
                } catch (Exception e) {
                    handleException(e);
                }
            }
        });
        addThread(th);
    }

    public void login(final String username, final String password) {
        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    CypherSession _session = SyncRequest.userLogin(username, password);
                    dbManager.setSession(_session);
                    dbManager.insertKey(_session.getUser(), _session.getUser().getKey());
                    session = _session;
                    contentListener.onLogged(_session.getUser());
                } catch (Exception e) {
                    handleException(e);
                }
            }
        });
        addThread(th);
    }

    public void logout() {
        if(session == null) {
            return;
        }
        final CypherSession sessionBackup = session;
        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    SyncRequest.userLogout(sessionBackup);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (APIErrorException e) {
                    e.printStackTrace();
                }
            }
        });
        addThread(th);
        dbManager.logout();
        session = null;
    }


    public List<CypherMessage> getMessageList(CypherUser contact) {
        return dbManager.getMessages(contact, 0, 100);
    }

    public void sendMessage(final CypherUser contact, final String text) {
        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    CypherMessage msg = SyncRequest.sendMessage(session, contact, text);
                    dbManager.insertMessage(msg);
                    contentListener.onMessageSent(msg);
                } catch (Exception e) {
                    handleException(e);
                }
            }
        });
        addThread(th);
    }

    public void findUser(final String query) {
        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    List<String> users = SyncRequest.findUser(session, query);
                    contentListener.onFindUser(users);
                } catch (Exception e) {
                    handleException(e);
                }
            }
        });
        addThread(th);
    }

    public void addContact(final String username) {
        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    CypherContact contact = SyncRequest.addContact(session, username);
                    dbManager.insertContact(contact);
                    if(contact.getKey() != null) {
                        dbManager.insertKey(contact, contact.getKey());
                    }
                    contentListener.onContactChange(contact);
                } catch (Exception e) {
                    handleException(e);
                }
            }
        });
        addThread(th);
    }

    public void blockContact(final String username) {
        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    CypherContact contact = SyncRequest.blockContact(session, username);
                    dbManager.insertContact(contact);
                    contentListener.onContactChange(contact);
                } catch (Exception e) {
                    handleException(e);
                }
            }
        });
        addThread(th);
    }

    public CypherContact getContactByID(long id) {
        return dbManager.getContactByID(id);
    }

    private void handlePullResults(PullResults res) {
        long notifiedUntil = res.getNotifiedUntil();
        if(res.getKeys() != null) {
            contentListener.onPullKeys(res.getKeys(), notifiedUntil);
            for(ECKey k : res.getKeys()) {
                dbManager.insertKey(session.getUser(), k);
            }
        } else if(res.getContacts() != null) {
            contentListener.onPullContacts(res.getContacts(), notifiedUntil);
            for(CypherContact c : res.getContacts()) {
                dbManager.insertContact(c);
                if(c.getKey() != null) {
                    dbManager.insertKey(c, c.getKey());
                }
            }
        } else if(res.getMessages() != null) {
            contentListener.onPullMessages(res.getMessages(), notifiedUntil);
            for(CypherMessage m : res.getMessages()) {
                dbManager.insertMessage(m);
            }
        }
    }

    public void pullMessages(final CypherUser contact, final boolean since, final long time) {
        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    PullResults res = SyncRequest.pullMessages(session, contact, since, time);
                    handlePullResults(res);
                } catch(Exception e) {
                    handleException(e);
                }
            }
        });
        addThread(th);
    }

    public void pullContacts(final long since) {
        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    PullResults res = SyncRequest.pullContacts(session, SyncRequest.SINCE, since);
                    handlePullResults(res);
                } catch(Exception e) {
                    handleException(e);
                }
            }
        });
        addThread(th);
    }

    public void pullContacts() {
        pullContacts(dbManager.getLastUpdateTime());
    }

    public void pullKeys(final CypherUser user, final boolean since, final long time) {
        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    PullResults res = SyncRequest.pullKeys(session, user, since, time);
                    handlePullResults(res);
                } catch(Exception e) {
                    handleException(e);
                }
            }
        });
        addThread(th);
    }

    public void pullAll() {
        final long since = dbManager.getLastUpdateTime();
        dbManager.setLastUpdateTime(System.currentTimeMillis());
        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    PullResults res = SyncRequest.pullAll(session, null, true, since);
                    handlePullResults(res);
                } catch(Exception e) {
                    handleException(e);
                }
            }
        });
        addThread(th);
    }

    public void getMessages(final CypherUser user, final int offset, final int limit) {
        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {
                List<CypherMessage> messages = dbManager.getMessages(user, offset, limit);
                if(messages.size() < limit) {

                }
            }
        });
        addThread(th);
    }


    public List<CypherContact> getContactList() {
        return dbManager.getContacts();
    }
}
