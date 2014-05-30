/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.cyphermessenger.client;

import com.cyphermessenger.utils.Utils;

import java.util.Arrays;

public class Captcha {
    byte[] captchaImage;
    String captchaToken;
    byte[] captchaHash;

    public Captcha(String captchaToken, byte[] captchaHash, byte[] captchaImage) {
        this.captchaImage = captchaImage;
        this.captchaToken = captchaToken;
        this.captchaHash = captchaHash;
    }

    public Captcha(String[] s) {
        this(s[0], Utils.BASE64_URL.decode(s[1]), Utils.BASE64_URL.decode(s[2]));
    }

    public byte[] getCaptchaImage() {
        return captchaImage;
    }

    public boolean verify(String value) {
        return Arrays.equals(captchaHash, Utils.sha256(value.toLowerCase()));
    }

    public String[] toStringArray() {
        return new String[] {captchaToken, Utils.BASE64_URL.encode(captchaHash), Utils.BASE64_URL.encode(captchaImage)};
    }
}
