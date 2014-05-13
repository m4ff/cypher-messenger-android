/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.cyphermessenger.client;

import java.util.Arrays;

import com.cyphermessenger.utils.Utils;

public class Captcha {
    byte[] captchaImage;
    String captchaToken;
    byte[] captchaHash;

    public byte[] getCaptchaImage() {
        return captchaImage;
    }

    public boolean verify(String value) {
        return Arrays.equals(captchaHash, Utils.sha256(value));
    }
}
