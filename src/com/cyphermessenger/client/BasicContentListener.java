package com.cyphermessenger.client;

import com.cyphermessenger.crypto.ECKey;

import java.util.List;

/**
 * Created by paolo on 22/05/14.
 */
public class BasicContentListener implements ContentListener {

    @Override
    public void onLogged(CypherUser user) {

    }

    @Override
    public void onMessageSent(CypherMessage message) {

    }

    @Override
    public void onPullMessages(List<CypherMessage> messages, long notifiedUntil) {

    }

    @Override
    public void onPullContacts(List<CypherContact> contacts, long notifiedUntil) {

    }

    @Override
    public void onPullKeys(List<ECKey> keys, long notifiedUntil) {

    }

    @Override
    public void onGetMessages(List<CypherMessage> messages) {

    }

    @Override
    public void onCaptcha(Captcha captcha) {

    }

    @Override
    public void onFindUser(List<String> list) {

    }

    @Override
    public void onContactChange(CypherContact contact) {

    }

    @Override
    public void onContactDeleted(String name) {

    }

    @Override
    public void onServerError() {

    }

    @Override
    public void onSessionInvalid() {

    }

    @Override
    public void onCaptchaInvalid() {

    }

    @Override
    public void onUsernameTaken() {

    }

    @Override
    public void onUsernameNotFound() {

    }

    @Override
    public void onLoginInvalid() {

    }

    @Override
    public void onContactNotFound() {

    }

    @Override
    public void onContactWaiting() {

    }

    @Override
    public void onContactBlocked() {

    }

    @Override
    public void onContactDenied() {

    }
}
