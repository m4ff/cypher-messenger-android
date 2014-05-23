package com.cyphermessenger.client;


import com.cyphermessenger.crypto.ECKey;

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
    
    public CypherUser getUser() {
    	return dbManager.getUser();
    }
    public CypherSession getSession() {
        return session;
    }
    /*
    private boolean handleSessionRestore(int statusCode) {
        if(statusCode == StatusCode.SESSION_EXPIRED || statusCode == StatusCode.SESSION_INVALID) {
            try {
                CypherSession _session = SyncRequest.userLogin(session.getUser());
                session = _session;
                return true;
            } catch (IOException e) {
                return false;
            } catch (APIErrorException e) {
                e.printStackTrace();
            }
        } else {
            return false;
        }
    }
    */

    private void handleException(Exception e) {
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
                    dbManager.setUser(user);
                    CypherSession _session = SyncRequest.userLogin(username, password);
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
                    session = _session;
                    contentListener.onLogged(_session.getUser());
                } catch (Exception e) {
                    handleException(e);
                }
            }
        });
        addThread(th);
    }

    public void logout(boolean deleteData) {
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
        return dbManager.getMessages(contact);
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

    private void handlePullResults(PullResults res) {
        if(res.getKeys() != null) {
            contentListener.onPullKeys(res.getKeys());
            for(ECKey k : res.getKeys()) {
                dbManager.insertKey(k);
            }
        } else if(res.getContacts() != null) {
            contentListener.onPullContacts(res.getContacts());
            for(CypherContact c : res.getContacts()) {
                dbManager.insertContact(c);
            }
        } else if(res.getMessages() != null) {
            contentListener.onPullMessages(res.getMessages());
            for(CypherMessage m : res.getMessages()) {
                dbManager.insertMessage(m);
            }
        }
        dbManager.setNotifiedUntil(res.getNotifiedUntil());
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

    public void pullContacts(final boolean since, final long time) {
        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    PullResults res = SyncRequest.pullContacts(session, since, time);
                    handlePullResults(res);
                } catch(Exception e) {
                    handleException(e);
                }
            }
        });
        addThread(th);
    }

    public void pullKeys(final CypherUser contact, final boolean since, final long time) {
        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    PullResults res = SyncRequest.pullKeys(session, contact, since, time);
                    handlePullResults(res);
                } catch(Exception e) {
                    handleException(e);
                }
            }
        });
        addThread(th);
    }

    public void pullAll(final CypherUser contact, final boolean since, final long time) {
        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    PullResults res = SyncRequest.pullAll(session, contact, since, time);
                    handlePullResults(res);
                } catch(Exception e) {
                    handleException(e);
                }
            }
        });
        addThread(th);
    }


    public List<CypherContact> getContactList(CypherSession session) {
        return dbManager.getContacts();
    }
}
