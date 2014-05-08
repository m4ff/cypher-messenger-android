/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.cyphermessenger.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 *
 * @author halfblood
 */
public class Captcha {
    private byte[] captchaImage;
    private String captchaToken;
    private byte[] captchaHash;

    public byte[] getCaptchaImage() {
        return captchaImage;
    }

    public void setCaptchaImage(byte[] captchaImage) {
        this.captchaImage = captchaImage;
    }

    public String getCaptchaToken() {
        return captchaToken;
    }

    public void setCaptchaToken(String captchaToken) {
        this.captchaToken = captchaToken;
    }

    public byte[] getCaptchaHash() {
        return captchaHash;
    }

    public void setCaptchaHash(byte[] captchaHash) {
        this.captchaHash = captchaHash;
    }
}
