/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cyphermessenger.client;

import android.util.Log;
import com.cyphermessenger.crypto.Decrypt;
import com.cyphermessenger.crypto.ECKey;
import com.cyphermessenger.crypto.Encrypt;
import com.cyphermessenger.utils.Utils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.spongycastle.crypto.InvalidCipherTextException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public final class SyncRequest {

    final static String DOMAIN = "https://cyphermessenger.herokuapp.com/beta1/";
    final static ObjectMapper MAPPER = new ObjectMapper();

    public static final boolean SINCE = true;
    public static final boolean UNTIL = false;
    private static final AbstractHttpClient HTTP_CLIENT = new DefaultHttpClient();

    public static Captcha requestCaptcha() throws IOException, APIErrorException {
        String finalurl = DOMAIN + "captcha";
        HttpPost post = new HttpPost(finalurl);
        HttpResponse response = HTTP_CLIENT.execute(post);
        if (response.getStatusLine().getStatusCode() != 200) {
            throw new IOException("Server error");
        }
        InputStream in = response.getEntity().getContent();
        JsonNode node = MAPPER.readTree(in);
        int statusCode = node.get("status").asInt();
        String captchaTokenString = node.get("captchaToken").asText();
        String captchaHashString = node.get("captchaHash").asText();
        String captchaImageString = node.get("captchaBytes").asText();

        byte[] captchaHash = Utils.BASE64_URL.decode(captchaHashString);
        byte[] captchaImage = Utils.BASE64_URL.decode(captchaImageString);
        if (statusCode == StatusCode.OK) {
            return new Captcha(captchaTokenString, captchaHash, captchaImage);
        } else {
            throw new APIErrorException(statusCode);
        }
    }

    public static CypherUser registerUser(String username, String password, String captchaValue, Captcha captcha) throws IOException, APIErrorException {
        if (!captcha.verify(captchaValue)) {
            throw new APIErrorException(StatusCode.CAPTCHA_INVALID);
        }

        String finalurl = DOMAIN + "register";
        byte[] serverPassword = Utils.cryptPassword(password.getBytes(), username);
        byte[] localPassword = Utils.sha256(password);

        ECKey key = new ECKey();
        byte[] publicKey = key.getPublicKey();
        byte[] privateKey = key.getPrivateKey();
        try {
            privateKey = Encrypt.process(localPassword, privateKey);
        } catch (InvalidCipherTextException ex) {
            throw new RuntimeException(ex);
        }
        String serverPasswordEncoded = Utils.BASE64_URL.encode(serverPassword);
        HttpPost post = new HttpPost(finalurl);
        ArrayList<NameValuePair> pair = new ArrayList<>();
        pair.add(new BasicNameValuePair("captchaToken", captcha.captchaToken));
        pair.add(new BasicNameValuePair("captchaValue", captchaValue));
        pair.add(new BasicNameValuePair("username", username));
        pair.add(new BasicNameValuePair("password", serverPasswordEncoded));
        pair.add(new BasicNameValuePair("publicKey", Utils.BASE64_URL.encode(publicKey)));
        pair.add(new BasicNameValuePair("privateKey", Utils.BASE64_URL.encode(privateKey)));
        post.setEntity(new UrlEncodedFormEntity(pair));
        HttpResponse response = HTTP_CLIENT.execute(post);
        if (response.getStatusLine().getStatusCode() != 200) {
            throw new IOException("Server error");
        }
        InputStream in = response.getEntity().getContent();
        JsonNode node = MAPPER.readTree(in);
        int statusCode = node.get("status").asInt();
        if (statusCode == StatusCode.OK) {
            long userID = node.get("userID").asLong();
            long keyTime = node.get("timestamp").asLong();
            CypherUser user = new CypherUser(username, localPassword, serverPassword, userID, key, keyTime);
            return user;
        } else {
            throw new APIErrorException(statusCode);
        }
    }

    public static CypherSession userLogin(String username, byte[] serverPassword, byte[] localPassword) throws IOException, APIErrorException {
        String finalurl = DOMAIN + "login";
        String passwordHashEncoded = Utils.BASE64_URL.encode(serverPassword);
        HttpPost post = new HttpPost(finalurl);

        ArrayList<NameValuePair> pair = new ArrayList<>();
        pair.add(new BasicNameValuePair("username", username));
        pair.add(new BasicNameValuePair("password", passwordHashEncoded));
        post.setEntity(new UrlEncodedFormEntity(pair));
        HttpResponse response = HTTP_CLIENT.execute(post);
        if (response.getStatusLine().getStatusCode() != 200) {
            throw new IOException("Server error");
        }

        InputStream in = response.getEntity().getContent();
        JsonNode node = MAPPER.readTree(in);
        int statusCode = node.get("status").asInt();
        if (statusCode == StatusCode.OK) {
            long userID = node.get("userID").asLong();
            ECKey key;
            try {
                key = Utils.decodeKey(node.get("publicKey").asText(), node.get("privateKey").asText(), localPassword);
            } catch (InvalidCipherTextException ex) {
                throw new RuntimeException(ex);
            }
            long keyTimestamp = node.get("keyTimestamp").asLong();
            CypherUser newUser = new CypherUser(username, localPassword, serverPassword, userID, key, keyTimestamp);
            String sessionID = node.get("sessionID").asText();
            CypherSession session = new CypherSession(newUser, sessionID);
            return session;
        } else {
            throw new APIErrorException(statusCode);
        }
    }

    public static CypherSession userLogin(String username, String password) throws IOException, APIErrorException {
        byte[] serverPassword = Utils.cryptPassword(password.getBytes(), username);
        byte[] localPassword = Utils.sha256(password);
        return userLogin(username, serverPassword,localPassword);
    }

    public static CypherSession userLogin(CypherUser user) throws IOException, APIErrorException {
        return userLogin(user.getUsername(), user.getServerPassword(), user.getLocalPassword());
    }

    public static void userLogout(CypherSession session) throws IOException, APIErrorException {
        String finalurl = DOMAIN + "logout";
        HttpPost post = new HttpPost(finalurl);

        ArrayList<NameValuePair> pair = new ArrayList<>();
        pair.add(new BasicNameValuePair("userID", session.getUser().getUserID() + ""));
        pair.add(new BasicNameValuePair("sessionID", session.getSessionID()));
        post.setEntity(new UrlEncodedFormEntity(pair));
        HttpResponse response = HTTP_CLIENT.execute(post);
        if (response.getStatusLine().getStatusCode() != 200) {
            throw new IOException("Server error");
        }

        InputStream in = response.getEntity().getContent();
        JsonNode node = MAPPER.readTree(in);
        int statusCode = node.get("status").asInt();
        if (statusCode != StatusCode.OK) {
            throw new APIErrorException(statusCode);
        }
    }

    public static List<String> findUser(CypherSession session, String username, int limit) throws IOException, APIErrorException {
        String finalurl = DOMAIN + "find";
        HttpPost post = new HttpPost(finalurl);
        CypherUser user = session.getUser();
        ArrayList<NameValuePair> pair = new ArrayList<>();
        pair.add(new BasicNameValuePair("userID", user.getUserID() + ""));
        pair.add(new BasicNameValuePair("sessionID", session.getSessionID()));
        pair.add(new BasicNameValuePair("username", username));
        pair.add(new BasicNameValuePair("limit", limit + ""));
        post.setEntity(new UrlEncodedFormEntity(pair));
        HttpResponse response = HTTP_CLIENT.execute(post);
        if (response.getStatusLine().getStatusCode() != 200) {
            throw new IOException("Server error");
        }

        InputStream in = response.getEntity().getContent();
        JsonNode node = MAPPER.readTree(in);
        int statusCode = node.get("status").asInt();
        if (statusCode != StatusCode.OK) {
            throw new APIErrorException(statusCode);
        } else {
            ArrayList<String> users = Utils.MAPPER.treeToValue(node.get("users"), ArrayList.class);
            return users;
        }
    }

    public static List<String> findUser(CypherSession session, String username) throws IOException, APIErrorException {
        return findUser(session, username, 10);
    }

    public static CypherMessage sendMessage(CypherSession session, CypherUser contactUser, String message) throws IOException, APIErrorException {
        String finalUrl = DOMAIN + "message";
        HttpPost post = new HttpPost(finalUrl);
        CypherUser user = session.getUser();
        long timestamp = System.currentTimeMillis();
        byte[] messageID = Utils.randomBytes(4);
        byte[] timestampBytes = Utils.longToBytes(timestamp);
        int messageIDLong = (int) Utils.bytesToLong(messageID);
        Encrypt encryptionCtx = new Encrypt(user.getKey().getSharedSecret(contactUser.getKey()));
        encryptionCtx.updateAuthenticatedData(messageID);
        encryptionCtx.updateAuthenticatedData(timestampBytes);
        byte[] payload;
        try {
            payload = encryptionCtx.process(message.getBytes());
        } catch (InvalidCipherTextException e) {
            throw new RuntimeException(e);
        }

        ArrayList<NameValuePair> pair = new ArrayList<>();
        pair.add(new BasicNameValuePair("userID", session.getUser().getUserID() + ""));
        pair.add(new BasicNameValuePair("sessionID", session.getSessionID()));
        pair.add(new BasicNameValuePair("payload", Utils.BASE64_URL.encode(payload)));
        pair.add(new BasicNameValuePair("contactID", contactUser.getUserID() + ""));
        pair.add(new BasicNameValuePair("messageID", messageIDLong + ""));
        pair.add(new BasicNameValuePair("messageTimestamp", timestamp + ""));
        pair.add(new BasicNameValuePair("userKeyTimestamp", session.getUser().getKeyTime() + ""));
        pair.add(new BasicNameValuePair("contactKeyTimestamp", contactUser.getKeyTime() + ""));
        post.setEntity(new UrlEncodedFormEntity(pair));
        HttpResponse response = HTTP_CLIENT.execute(post);
        if (response.getStatusLine().getStatusCode() != 200) {
            throw new IOException("Server error");
        }

        InputStream in = response.getEntity().getContent();
        JsonNode node = MAPPER.readTree(in);
        int statusCode = node.get("status").asInt();
        if (statusCode != StatusCode.OK) {
            throw new APIErrorException(statusCode);
        }
        return new CypherMessage(messageIDLong, message, timestamp, true, contactUser.getUserID());
    }

    private static CypherContact manageContact(CypherSession session, String contactName, boolean add) throws IOException, APIErrorException {
        String finalurl = DOMAIN + "contact";
        HttpPost post = new HttpPost(finalurl);

        String action = "block";
        if (add) {
            action = "add";
        }

        CypherUser user = session.getUser();
        ArrayList<NameValuePair> pair = new ArrayList<>();
        pair.add(new BasicNameValuePair("userID", user.getUserID() + ""));
        pair.add(new BasicNameValuePair("sessionID", session.getSessionID()));
        pair.add(new BasicNameValuePair("action", action));
        pair.add(new BasicNameValuePair("contactName", contactName));
        post.setEntity(new UrlEncodedFormEntity(pair));
        HttpResponse response = HTTP_CLIENT.execute(post);
        if (response.getStatusLine().getStatusCode() != 200) {
            throw new IOException("Server error");
        }

        InputStream in = response.getEntity().getContent();
        JsonNode node = MAPPER.readTree(in);
        int statusCode = node.get("status").asInt();
        if(statusCode == StatusCode.OK) {
            long userID = node.get("contactID").asLong();
            long keyTimestamp = node.get("keyTimestamp").asLong();
            long contactTimestamp = node.get("contactTimestamp").asLong();
            ECKey key = Utils.decodeKey(node.get("publicKey").asText());
            return new CypherContact(contactName, userID, key, keyTimestamp, CypherContact.ACCEPTED, contactTimestamp);
        } else {
            String status;
            switch (statusCode) {
                case StatusCode.CONTACT_WAITING:
                    status = CypherContact.WAITING; break;
                case StatusCode.CONTACT_BLOCKED:
                    status = CypherContact.BLOCKED; break;
                case StatusCode.CONTACT_DENIED:
                    status = CypherContact.DENIED; break;
                default:
                    throw new APIErrorException(statusCode);
            }
            return new CypherContact(contactName, status);
        }
    }

    public static CypherContact addContact(CypherSession session, String username) throws IOException, APIErrorException {
        return manageContact(session, username, true);
    }

    public static CypherContact blockContact(CypherSession session, String username) throws IOException, APIErrorException {
        return manageContact(session, username, false);
    }

    private static JsonNode pullUpdate(CypherSession session, CypherUser contact, String action, boolean since, long time) throws IOException, APIErrorException {
        String finalurl = DOMAIN + "pull";
        HttpPost post = new HttpPost(finalurl);
        String timeRelativeTo = "since";
        if (!since) {
            timeRelativeTo = "until";
        }
        CypherUser user = session.getUser();
        ArrayList<NameValuePair> pair = new ArrayList<>();
        pair.add(new BasicNameValuePair("userID", user.getUserID() + ""));
        pair.add(new BasicNameValuePair("sessionID", session.getSessionID()));
        pair.add(new BasicNameValuePair("action", action));
        if (contact != null) {
            pair.add(new BasicNameValuePair("contactID", contact.getUserID() + ""));
        }
        pair.add(new BasicNameValuePair(timeRelativeTo, time + ""));
        post.setEntity(new UrlEncodedFormEntity(pair));
        HttpResponse response = HTTP_CLIENT.execute(post);
        if (response.getStatusLine().getStatusCode() != 200) {
            throw new IOException("Server error");
        }

        InputStream in = response.getEntity().getContent();
        JsonNode node = MAPPER.readTree(in);
        int statusCode = node.get("status").asInt();
        if (statusCode == StatusCode.OK) {
            return node;
        } else {
            throw new APIErrorException(statusCode);
        }
    }

    public static PullResults pullMessages(CypherSession session, CypherUser contact, boolean since, long time) throws IOException, APIErrorException {
        JsonNode node = pullUpdate(session, contact, "messages", since, time);
        int statusCode = node.get("status").asInt();
        if (statusCode == StatusCode.OK) {
            ArrayList<CypherMessage> array = handleMessageNode(node, session.getUser().getKey(), contact.getKey());
            return new PullResults(array, null, null, node.get("notifiedUntil").asLong());
        } else {
            throw new APIErrorException(statusCode);
        }
    }

    public static PullResults pullContacts(CypherSession session, boolean since, long time) throws IOException, APIErrorException {
        JsonNode node = pullUpdate(session, null, "contacts", since, time);
        int statusCode = node.get("status").asInt();
        if (statusCode == StatusCode.OK) {
            ArrayList<CypherContact> array = handleContactNode(node);
            return new PullResults(null, array, null, node.get("notifiedUntil").asLong());
        } else {
            throw new APIErrorException(statusCode);
        }
    }

    public static PullResults pullKeys(CypherSession session, CypherUser contact, boolean since, long time) throws IOException, APIErrorException {
        JsonNode node = pullUpdate(session, contact, "keys", since, time);
        int statusCode = node.get("status").asInt();
        if (statusCode == StatusCode.OK) {
            return new PullResults(null, null, handleKeyNode(node, session.getUser().getLocalPassword()), node.get("notifiedUntil").asLong());
        } else {
            throw new APIErrorException(statusCode);
        }
    }

    public static PullResults pullAll(CypherSession session, CypherUser contact, boolean since, long time) throws IOException, APIErrorException {
        JsonNode node = pullUpdate(session, contact, "all", since, time);
        int statusCode = node.get("status").asInt();
        if (statusCode == StatusCode.OK) {
            ArrayList<ECKey> keysArray = handleKeyNode(node, session.getUser().getLocalPassword());
            ArrayList<CypherContact> contactsArray = handleContactNode(node);
            ArrayList<CypherMessage> messagesArray = handleMessageNode(node, session.getUser().getKey(), contact.getKey());
            long notifiedUntil = node.get("notifiedUntil").asLong();
            return new PullResults(messagesArray, contactsArray, keysArray, notifiedUntil);
        } else {
            throw new APIErrorException(statusCode);
        }
    }

    private static ArrayList<CypherMessage> handleMessageNode(JsonNode node, ECKey key1, ECKey key2) {
        ArrayList<CypherMessage> array = new ArrayList<>();
        JsonNode arrayNode = node.get("messages");
        if (arrayNode.isArray()) {
            for (JsonNode selectedNode : arrayNode) {
                boolean isSender = selectedNode.get("isSender").asBoolean();
                long receivedContactID = selectedNode.get("contactID").asLong();
                long timestamp = selectedNode.get("timestamp").asLong();
                int messageID = selectedNode.get("messageID").asInt();
                byte[] payload = Utils.BASE64_URL.decode(selectedNode.get("payload").asText());
                byte[] sharedSecret = key1.getSharedSecret(key2);

                // try to decrypt message
                try {
                    String plainText = new String(Decrypt.process(sharedSecret, payload, Utils.longToBytes(messageID), Utils.longToBytes(timestamp)));
                    CypherMessage message = new CypherMessage(messageID, plainText, timestamp, isSender, receivedContactID);
                    array.add(message);
                } catch (InvalidCipherTextException ex) {
                    Log.e("PULL", "Message decryption failed", ex);
                    /** TODO
                     * handle message decryption error
                     */
                }
            }
        }
        return array;
    }

    private static ArrayList<ECKey> handleKeyNode(JsonNode node, byte[] localPassword) {
        ArrayList<ECKey> array = new ArrayList<>();
        JsonNode arrayNode = node.get("keys");
        if (arrayNode.isArray()) {
            for (JsonNode selectedNode : arrayNode) {
                try {
                    ECKey newECKey = Utils.decodeKey(selectedNode.get("publicKey").asText(), selectedNode.get("privateKey").asText(), localPassword);
                    newECKey.setTime(selectedNode.get("timestamp").asLong());
                    array.add(newECKey);
                } catch (InvalidCipherTextException ex) {
                    Log.e("PULL", "Key decryption failed", ex);
                    /** TODO
                     * handle key decryption error
                     */
                }
            }
        }
        return array;
    }

    private static ArrayList<CypherContact> handleContactNode(JsonNode node) {
        ArrayList<CypherContact> array = new ArrayList<>();
        JsonNode arrayNode = node.get("contacts");
        if (arrayNode.isArray()) {
            for (JsonNode selectedNode : arrayNode) {
                long receivedContactID = selectedNode.get("contactID").asLong();
                long timestamp = selectedNode.get("contactTimestamp").asLong();
                long keyTime = selectedNode.get("keyTimestamp").asLong();
                String username = selectedNode.get("username").asText();
                String contactStatus = selectedNode.get("contactStatus").asText();
                ECKey publicKey = Utils.decodeKey(selectedNode.get("publicKey").asText());

                CypherContact newContact = new CypherContact(username, receivedContactID, publicKey, keyTime, contactStatus, timestamp);
                array.add(newContact);
            }
        }
        return array;
    }


    public static void main(String argString[]) throws Exception {
        Captcha c = requestCaptcha();
        File f = new File("/Users/paolo/Desktop/chaptcha.png");
        FileOutputStream out = new FileOutputStream(f);
        out.write(c.getCaptchaImage());
        out.close();
        String captchaVal = new Scanner(System.in).nextLine();
        CypherUser user = registerUser("paolo", "password", captchaVal, c);
        CypherSession session = userLogin("paolo", "password");

        System.out.println(session.getUser().getUsername() + " " + session.getSessionID());
    }
}
