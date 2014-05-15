/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cyphermessenger.client;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import android.provider.ContactsContract;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.bouncycastle.crypto.InvalidCipherTextException;

import com.cyphermessenger.crypto.Decrypt;
import com.cyphermessenger.crypto.ECKey;
import com.cyphermessenger.crypto.Encrypt;
import com.cyphermessenger.utils.Utils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;


public final class SyncRequest {

    final static String DOMAIN = "https://cyphermessenger.herokuapp.com/beta1/";
    final static ObjectMapper MAPPER = new ObjectMapper();

    public final boolean SINCE = true;
    public final boolean UNTIL = false;

    public static Captcha requestCaptcha() throws IOException, APIErrorException {
        String finalurl = DOMAIN + "captcha";
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(finalurl);
        CloseableHttpResponse response = client.execute(post);
       if (response.getStatusLine().getStatusCode() == 200){
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
            Captcha captcha = new Captcha();
            captcha.captchaImage = captchaImage;
            captcha.captchaToken = captchaTokenString;
            captcha.captchaHash = captchaHash;
            return captcha;
        } else {
            throw new APIErrorException(statusCode);
        }
    }
    /*Returns user id*/

    public static CypherUser registerUser(String username, String password, String captchaValue, Captcha captcha) throws IOException, APIErrorException {
        String finalurl = DOMAIN + "register";
        CloseableHttpClient client = HttpClients.createDefault();
        byte[] passwordHash = Utils.cryptPassword(password.getBytes(), username);

        ECKey key = new ECKey();
        byte[] publicKey = key.getPublicKey();
        byte[] privateKey = key.getPrivateKey();
        try {
            privateKey = new Encrypt(password).process(privateKey);
        } catch (InvalidCipherTextException ex) {
            Logger.getLogger(SyncRequest.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        }
        String passwordHashEncoded = Utils.BASE64_URL.encode(passwordHash);
        HttpPost post = new HttpPost(finalurl);
        ArrayList<NameValuePair> pair = new ArrayList<>();
        pair.add(new BasicNameValuePair("captchaToken", captcha.captchaToken));
        pair.add(new BasicNameValuePair("captchaValue", captchaValue));
        pair.add(new BasicNameValuePair("username", username));
        pair.add(new BasicNameValuePair("password", passwordHashEncoded));
        pair.add(new BasicNameValuePair("publicKey", Utils.BASE64_URL.encode(publicKey)));
        pair.add(new BasicNameValuePair("privateKey", Utils.BASE64_URL.encode(privateKey)));
        post.setEntity(new UrlEncodedFormEntity(pair));
        CloseableHttpResponse response = client.execute(post);
        if (response.getStatusLine().getStatusCode() == 200){
            throw new IOException("Server error");
        }
        InputStream in = response.getEntity().getContent();
        JsonNode node = MAPPER.readTree(in);
        int statusCode = node.get("status").asInt();
        if (statusCode == StatusCode.OK) {
            long userID = node.get("userID").asLong();
            Date keyTime = new Date(node.get("timestamp").asLong());
            CypherUser user = new CypherUser(username, passwordHashEncoded, userID, key, keyTime);
            return user;
        } else {
            throw new APIErrorException(statusCode);
        }
    }

    public static CypherSession userLogin(String username, String password) throws IOException, APIErrorException, InvalidCipherTextException {
        String finalurl = DOMAIN + "login";
        String passwordHashEncoded = Utils.BASE64_URL.encode(Utils.cryptPassword(password.getBytes(), username));
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(finalurl);
        
        ArrayList<NameValuePair> pair = new ArrayList<>();
        pair.add(new BasicNameValuePair("username", username));
        pair.add(new BasicNameValuePair("password", passwordHashEncoded));
        post.setEntity(new UrlEncodedFormEntity(pair));
        CloseableHttpResponse response = client.execute(post);
        if (response.getStatusLine().getStatusCode() == 200){
            throw new IOException("Server error");
        }

        InputStream in = response.getEntity().getContent();
        JsonNode node = MAPPER.readTree(in);
        int statusCode = node.get("status").asInt();
        if (statusCode == StatusCode.OK) {
            long userID = node.get("userID").asLong();
            ECKey key = Utils.decodeKey(node.get("publicKey").asText(), node.get("privateKey").asText(), password);
            Date keyTimestamp = new Date(node.get("keyTimestamp").asLong());
            CypherUser newUser = new CypherUser(username, passwordHashEncoded, userID, key, keyTimestamp);
            String sessionID = node.get("sessionID").asText();
            CypherSession session = new CypherSession(newUser, sessionID);
            return session;
        } else {
            throw new APIErrorException(statusCode);
        }
    }
    
    /*
    SOSTITUITO DA LOGIN e PULL 
    public static CypherSession requestUserKeyPair(CypherSession session, String password) throws IOException, APIErrorException, InvalidCipherTextException {
        String finalurl = DOMAIN + "userkey";
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(finalurl);

        ArrayList<NameValuePair> pair = new ArrayList<>();
        pair.add(new BasicNameValuePair("sessionID", session.getSessionID()));
        pair.add(new BasicNameValuePair("userID", session.getUser().getUserID() + ""));
        post.setEntity(new UrlEncodedFormEntity(pair));
        CloseableHttpResponse response = client.execute(post);
        InputStream in = response.getEntity().getContent();
        JsonNode node = MAPPER.readTree(in);
        int statusCode = node.get("status").asInt();
        if (statusCode == StatusCode.OK) {
            byte[] publicKey = Utils.BASE64_URL.decode(node.get("publicKey").asText());
            byte[] privateKey = Utils.BASE64_URL.decode(node.get("privateKey").asText());
            privateKey = Decrypt.process(password, privateKey);
            ECKey key = new ECKey(publicKey, privateKey);
            CypherUser user = session.getUser();
            CypherUser newUser = new CypherUser(user.getUsername(), user.getPassword(), user.getUserID(), key);
            return new CypherSession(newUser, session.getSessionID());
        } else {
            throw new APIErrorException(statusCode);
        }
    }
    */

    public static void userLogout(int userID, String sessionID) throws IOException, APIErrorException {
        String finalurl = DOMAIN + "logout";
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(finalurl);

        ArrayList<NameValuePair> pair = new ArrayList<>();
        pair.add(new BasicNameValuePair("userID", userID + ""));
        pair.add(new BasicNameValuePair("sessionID", sessionID + ""));
        post.setEntity(new UrlEncodedFormEntity(pair));
        CloseableHttpResponse response = client.execute(post);
        if (response.getStatusLine().getStatusCode() == 200){
            throw new IOException("Server error");
        }

        InputStream in = response.getEntity().getContent();
        JsonNode node = MAPPER.readTree(in);
        int statusCode = node.get("status").asInt();
        if (statusCode != StatusCode.OK) {
            throw new APIErrorException(statusCode);
        }
    }

    public static ArrayList<String> findUser(CypherSession session, String username, int limit) throws IOException, APIErrorException {
        String finalurl = DOMAIN + "find";
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(finalurl);
        CypherUser user = session.getUser();
        ArrayList<NameValuePair> pair = new ArrayList<>();
        pair.add(new BasicNameValuePair("userID", user.getUserID()+ ""));
        pair.add(new BasicNameValuePair("sessionID", session.getSessionID()));
        pair.add(new BasicNameValuePair("username", username));
        pair.add(new BasicNameValuePair("limit", limit + ""));
        post.setEntity(new UrlEncodedFormEntity(pair));
        CloseableHttpResponse response = client.execute(post);
        if (response.getStatusLine().getStatusCode() == 200){
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
    
    public static ArrayList<String> findUser(CypherSession session, String username) throws IOException, APIErrorException {
        return findUser(session, username, 10);
    }

    public static void sendMessageToUser(CypherSession session, String message, CypherUser contactUser) throws IOException, APIErrorException, IllegalStateException, InvalidCipherTextException {
        String finalUrl = DOMAIN + "message";
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(finalUrl);
        CypherUser user = session.getUser();
        long timestamp = new Date().getTime();
        byte[] messageId = new byte[4];
        Utils.RANDOM.nextBytes(messageId);

        byte[] timestampBytes = BigInteger.valueOf(timestamp).toByteArray();
        Encrypt encryptionCtx = new Encrypt(user.getKey().getSharedSecret(contactUser.getKey()));
        encryptionCtx.updateAuthenticatedData(messageId);
        encryptionCtx.updateAuthenticatedData(timestampBytes);
        byte[] payload = encryptionCtx.process(message.getBytes());

        ArrayList<NameValuePair> pair = new ArrayList<>();
        pair.add(new BasicNameValuePair("userID", session.getUser().getUserID() + ""));
        pair.add(new BasicNameValuePair("sessionID", session.getSessionID()));
        pair.add(new BasicNameValuePair("payload", Utils.BASE64_URL.encode(payload)));
        pair.add(new BasicNameValuePair("contactID", contactUser.getUserID() + ""));
        pair.add(new BasicNameValuePair("messageID", new BigInteger(messageId).intValue() + ""));
        pair.add(new BasicNameValuePair("timestamp", timestamp + ""));
        post.setEntity(new UrlEncodedFormEntity(pair));
        CloseableHttpResponse response = client.execute(post);
        if (response.getStatusLine().getStatusCode() == 200){
            throw new IOException("Server error");
        }

        InputStream in = response.getEntity().getContent();
        JsonNode node = MAPPER.readTree(in);
        int statusCode = node.get("status").asInt();
        if (statusCode != StatusCode.OK) {
            throw new APIErrorException(statusCode);
        }
    }
    
    private static JsonNode manageContact(CypherSession session, String contactName, boolean add) throws IOException, APIErrorException {
        String finalurl = DOMAIN + "contact";
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(finalurl);
        
        String action = "block";
        if (add){
          action = "add";
        }        
                
        CypherUser user = session.getUser();
        ArrayList<NameValuePair> pair = new ArrayList<>();
        pair.add(new BasicNameValuePair("userID", user.getUserID()+ ""));
        pair.add(new BasicNameValuePair("sessionID", session.getSessionID()));
        pair.add(new BasicNameValuePair("action", action));
        pair.add(new BasicNameValuePair("contactName", contactName));
        post.setEntity(new UrlEncodedFormEntity(pair));
        CloseableHttpResponse response = client.execute(post);
        if (response.getStatusLine().getStatusCode() == 200){
            throw new IOException("Server error");
        }

        InputStream in = response.getEntity().getContent();
        JsonNode node = MAPPER.readTree(in);
        int statusCode = node.get("status").asInt();
        switch(statusCode) {
            case StatusCode.OK:
            case StatusCode.CONTACT_WAITING:
            case StatusCode.CONTACT_BLOCKED:
            case StatusCode.CONTACT_DENIED:
                return node;
            default:
                throw new APIErrorException(statusCode);
        }
    }
   
    public static void inviteContact(CypherSession session, String username) throws IOException, APIErrorException{
        JsonNode node = manageContact(session, username, true);
        int statusCode = node.get("status").asInt();
        switch(statusCode) {
            case StatusCode.OK:
            case StatusCode.CONTACT_WAITING:
                return;
            default:
                throw new APIErrorException(statusCode);
        }
    }
    
    public static CypherUser acceptContact(CypherSession session, String username) throws IOException, APIErrorException{
        JsonNode node = manageContact(session, username, true);
        int statusCode = node.get("status").asInt();
        switch(statusCode) {
            case StatusCode.OK:
                long userID = node.get("contactID").asLong();
                Date keyTimestamp = new Date(node.get("keyTimestamp").asLong());
                ECKey key = Utils.decodeKey(node.get("publicKey").asText());
                CypherUser newUser = new CypherUser(username, null, userID, key, keyTimestamp);
                return newUser;
            default:
                throw new APIErrorException(statusCode);
        }
    }
    
    public static void blockContact(CypherSession session, String username) throws IOException, APIErrorException{
        JsonNode node = manageContact(session, username, false);
        int statusCode = node.get("status").asInt();
        switch(statusCode) {
            case StatusCode.OK:
            case StatusCode.CONTACT_BLOCKED:
                return;
            default:
                throw new APIErrorException(statusCode);
        }
    }

    public static JsonNode pullUpdate(CypherSession session, long contactID, String action, boolean since, Date time) throws IOException, APIErrorException{
        String finalurl = DOMAIN + "pull";
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(finalurl);
        String timeRelativeTo = "since";
        if (!since){
            timeRelativeTo = "until";
        }
        CypherUser user = session.getUser();
        ArrayList<NameValuePair> pair = new ArrayList<>();
        pair.add(new BasicNameValuePair("userID", user.getUserID()+ ""));
        pair.add(new BasicNameValuePair("sessionID", session.getSessionID()));
        pair.add(new BasicNameValuePair("action", action));
        pair.add(new BasicNameValuePair("contactID", contactID + ""));
        pair.add(new BasicNameValuePair(timeRelativeTo, time + ""));
        post.setEntity(new UrlEncodedFormEntity(pair));
        CloseableHttpResponse response = client.execute(post);
        if (response.getStatusLine().getStatusCode() == 200){
            throw new IOException("Server error");
        }

        InputStream in = response.getEntity().getContent();
        JsonNode node = MAPPER.readTree(in);
        int statusCode = node.get("status").asInt();
        if(statusCode == StatusCode.OK) {
            return node;
        } else {
            throw new APIErrorException(statusCode);
        }
    }

    public static ArrayList<CypherMessage> pullMessages(CypherSession session, long contactID, boolean since, Date time) throws IOException, APIErrorException {
        JsonNode node = pullUpdate(session, contactID,"messages",since, time);
        int statusCode = node.get("status").asInt();
        ArrayList<CypherMessage> array = new ArrayList<CypherMessage>();
        if (statusCode == StatusCode.OK) {
            JsonNode arrayNode = node.get("messages");
            if (arrayNode.isArray()) {
                for (JsonNode selectedNode : arrayNode) {
                    boolean isSender = selectedNode.get("isSender").asBoolean();
                    long receivedContactID = selectedNode.get("contactID").asLong();
                    int messageID = selectedNode.get("messageID").asInt();
                    byte[] payload = selectedNode.get("payload").asText().getBytes();
                    Date timestamp = new Date(selectedNode.get("timestamp").asLong());
                    CypherMessage message = new CypherMessage(messageID, payload, timestamp, isSender, receivedContactID);
                    array.add(message);
                }
            }
            return array;
        } else {
            throw new APIErrorException(statusCode);
        }
    }

    public static ArrayList<CypherContact> pullContacts(CypherSession session, long contactID, boolean since, Date time) throws IOException, APIErrorException{
        JsonNode node = pullUpdate(session, contactID,"contacts", since, time);
        int statusCode = node.get("status").asInt();
        ArrayList<CypherContact> array = new ArrayList<CypherContact>();
        if(statusCode == StatusCode.OK) {
            JsonNode arrayNode = node.get("contacts");
            if (arrayNode.isArray()){
                for (JsonNode selectedNode : arrayNode){
                    String username = selectedNode.get("username").asText();
                    long receivedContactID = selectedNode.get("contactID").asLong();
                    String contactStatus = selectedNode.get("contactStatus").asText();
                    ECKey publicKey = Utils.decodeKey(selectedNode.get("publicKey").asText());
                    Date timestamp = new Date(selectedNode.get("timestamp").asLong() );
                    Date keyTime = new Date(selectedNode.get("keyTimestamp").asLong());
                    CypherContact newContact = new CypherContact(username,contactID, publicKey, keyTime, contactStatus, timestamp);
                    array.add(newContact);
                }
            }
            return array;
        } else {
            throw  new APIErrorException(statusCode);
        }
    }

    public static ArrayList<ECKey> pullKeys(CypherSession session, long contactID, String password, boolean since, Date time) throws IOException, APIErrorException, InvalidCipherTextException {
        JsonNode node = pullUpdate(session, contactID,"keys", since, time);
        int statusCode = node.get("status").asInt();
        ArrayList<ECKey> array = new ArrayList<ECKey>();
        if(statusCode == StatusCode.OK) {
            JsonNode arrayNode = node.get("keys");
            if (arrayNode.isArray()){
                for (JsonNode selectedNode : arrayNode){
                    ECKey newECKey = Utils.decodeKey(selectedNode.get("publicKey").asText(), selectedNode.get("privateKey").asText(), password);
                    newECKey.setKeyTime(new Date(selectedNode.get("keyTimestamp").asLong()));
                    array.add(newECKey);
                }
            }
            return array;
        } else {
            throw  new APIErrorException(statusCode);
        }
    }

    public static PullResults pullAll(CypherSession session, long contactID, String password, boolean since, Date time) throws IOException, APIErrorException, InvalidCipherTextException {
        ArrayList<ECKey> keys = pullKeys(session, contactID, password, since, time);
        ArrayList<CypherContact> contacts = pullContacts(session,contactID, since, time);
        ArrayList<CypherMessage> messages = pullMessages(session, contactID, since, time);
        return new PullResults(messages,contacts, keys, time);
    }


    public static void main(String argString[]) throws Exception {
        String text = "asdasdasd";
        byte[] cypher = new Encrypt("ciao").process(text.getBytes());
        String plain = new String(Decrypt.process("cialo", cypher));
        System.out.println(plain);
        SyncRequest r = new SyncRequest();
    }
}
