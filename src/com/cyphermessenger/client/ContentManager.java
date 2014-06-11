package com.cyphermessenger.client;


import android.util.Log;
import com.cyphermessenger.crypto.ECKey;
import org.apache.http.impl.entity.StrictContentLengthStrategy;
import org.spongycastle.crypto.InvalidCipherTextException;

import java.io.IOException;
import java.util.*;

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

    public CypherSession getSession() {
        return session;
    }

    public CypherUser getUser() { return session != null ? session.getUser() : null; }

    private void handleException(Exception e) {
        Log.e("ContentManager", "handleException", e);
        e.printStackTrace();
        if(e instanceof IOException) {
            contentListener.onServerError();
        } else if(e instanceof APIErrorException) {
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
                    CypherSession _session = SyncRequest.userLogin(user);
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

    public void login() {
        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    CypherSession _session = SyncRequest.userLogin(session.getUser());
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

    public CypherMessage sendMessage(final CypherUser contact, final String text) {
        final CypherMessage msg = CypherMessage.create(contact, text);
        dbManager.insertMessage(msg);
        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    SyncRequest.sendMessage(session, contact, msg);
                    msg.isSent = true;
                    dbManager.setMessageSent(msg);
                    contentListener.onMessageSent(msg);
                } catch (Exception e) {
                    handleException(e);
                }
            }
        });
        addThread(th);
        return msg;
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

    public void deleteContactRequest(final String username) {
        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    SyncRequest.blockContact(session, username);
                    dbManager.deleteContact(new CypherContact(username, null));
                    contentListener.onContactDeleted(username);
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

    private boolean recoverMessage(CypherMessage message, CypherUser contact) throws IOException, APIErrorException {
        try {
            PullResults res;
            res = SyncRequest.pullKeys(session, session.getUser(), null, null);
            handlePullResults(res, session.getUser());
            res = SyncRequest.pullKeys(session, contact, null, null);
            handlePullResults(res, contact);
            session = dbManager.getSession();
            message.decrypt(session.getUser().getKey(), dbManager.getContactByID(contact.getUserID()).getKey());
            return true;
        } catch (InvalidCipherTextException e) {
            return false;
        }
    }

    private void handlePullResults(PullResults res, CypherUser contact) {
        long notifiedUntil = res.getNotifiedUntil();
        CypherUser user = contact != null ? contact : session.getUser();
        if(res.getKeys() != null) {
            for(ECKey k : res.getKeys()) {
                dbManager.insertKey(user, k);
            }
            contentListener.onPullKeys(res.getKeys(), notifiedUntil);
        } if(res.getContacts() != null) {
            for(CypherContact c : res.getContacts()) {
                dbManager.insertContact(c);
                if(c.getKey() != null) {
                    dbManager.insertKey(c, c.getKey());
                }
            }
            contentListener.onPullContacts(res.getContacts(), notifiedUntil);
        } if(res.getMessages() != null) {
            Iterator<CypherMessage> i = res.getMessages().iterator();
            while(i.hasNext()) {
                CypherMessage m = i.next();
                if(dbManager.messageExists(m)) {
                    i.remove();
                    continue;
                }
                if(m.isEncrypted()) {
                    try {
                        m.decrypt(session.getUser().getKey(), getContactByID(m.getContactID()).getKey());
                    } catch (InvalidCipherTextException e) {
                        try {
                            if(!recoverMessage(m, contact)) {
                                /**
                                 * TODO
                                 * inform the server that this message was rejected
                                 */
                                i.remove();
                                continue;
                            }
                        } catch(Exception ex) {
                            handleException(ex);
                            break;
                        }
                    }
                }
                dbManager.insertMessage(m);
            }
            contentListener.onPullMessages(res.getMessages(), notifiedUntil);
        }
    }

    public void pullMessages(final CypherUser contact, final boolean since, final long time) {
        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    PullResults res = SyncRequest.pullMessages(session, contact, since, time);
                    handlePullResults(res, contact);
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
                    handlePullResults(res, null);
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
                    handlePullResults(res, user);
                } catch(Exception e) {
                    handleException(e);
                }
            }
        });
        addThread(th);
    }

    public void pullAllSync() {
        long since = dbManager.getLastUpdateTime();
        Log.d("LAST UPDATE TIME", since + "");
        dbManager.setLastUpdateTime(System.currentTimeMillis());
                try {
                    PullResults res = SyncRequest.pullAll(session, null, SyncRequest.SINCE, since);
                    handlePullResults(res, null);
                } catch(Exception e) {
                    handleException(e);
                }
    }

    public void pullAll() {
        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {
                pullAllSync();
            }
        });
        addThread(th);
    }

    public void getMessages(final CypherUser user, final int offset, final int limit) {
        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {
                List<CypherMessage> messages = dbManager.getMessages(user, offset, limit);
                contentListener.onGetMessages(messages);
                if(messages.size() < limit) {
                    long untilTime;
                    if(messages.size() > 0) {
                        CypherMessage last = messages.get(messages.size() - 1);
                        untilTime = last.getTimestamp();
                    } else {
                        untilTime = System.currentTimeMillis() + 1000 * 120;
                    }
                    try {
                        PullResults res = SyncRequest.pullMessages(session, user, SyncRequest.UNTIL, untilTime);
                        messages.addAll(res.getMessages());
                        contentListener.onGetMessages(messages);
                    } catch (Exception e) {
                        handleException(e);
                    }
                }

            }
        });
        addThread(th);
    }


    public List<CypherContact> getContactList() {
        return dbManager.getContacts();
    }
}
