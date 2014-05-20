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

    public byte[] getCaptchaImage() {
        return captchaImage;
    }

    public boolean verify(String value) {
        return Arrays.equals(captchaHash, Utils.sha256(value));
    }
}
