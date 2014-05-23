package com.cyphermessenger.client;

import com.cyphermessenger.crypto.ECKey;

import java.util.List;

/**
 * Created by paolo on 22/05/14.
 */
public interface ContentListener {

    public void onLogged(CypherUser user);

    public void onMessageSent(CypherMessage message);

    public void onPullMessages(List<CypherMessage> messages);

    public void onPullContacts(List<CypherContact> contacts);

    public void onPullKeys(List<ECKey> keys);

    public void onCaptcha(Captcha captcha);

    public void onFindUser(List<String> list);

    public void onContactChange(CypherContact contact);

    public void onServerError();

    public void onSessionInvalid();

    public void onCaptchaInvalid();

    public void onUsernameTaken();

    public void onUsernameNotFound();

    public void onLoginInvalid();

    public void onContactNotFound();

    public void onContactWaiting();

    public void onContactBlocked();

    public void onContactDenied();

}
