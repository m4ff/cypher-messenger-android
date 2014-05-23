package com.cyphermessenger.client;

import java.util.List;

/**
 * Created by paolo on 22/05/14.
 */
class BasicContentListener implements ContentListener {

    BasicContentListener() {}

    @Override
    public void onLogged(CypherUser user) {

    }

    @Override
    public void onMessageSent(CypherMessage message) {

    }

    @Override
    public void onCaptcha(Captcha captcha) {

    }

    @Override
    public void onError(int statusCode) {
        switch (statusCode) {
            case StatusCode.SESSION_EXPIRED:
            case StatusCode.SESSION_INVALID:
                // retry
        }
    }

    @Override
    public void onFindUser(List<String> list) {

    }

    @Override
    public void onContactChange(CypherContact contact) {

    }
}
