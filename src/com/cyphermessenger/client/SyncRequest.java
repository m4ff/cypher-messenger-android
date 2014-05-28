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
import org.spongycastle.crypto.InvalidCipherTextException;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;


public final class SyncRequest {

    final static String DOMAIN = "https://cyphermessenger.herokuapp.com/beta1/";
    final static ObjectMapper MAPPER = new ObjectMapper();

    public static final boolean SINCE = true;
    public static final boolean UNTIL = false;

    public static Captcha requestCaptcha() throws IOException, APIErrorException {
        HttpURLConnection conn = doRequest("captcha");
        if (conn.getResponseCode() != 200) {
            throw new IOException("Server error");
        }
        InputStream in = conn.getInputStream();
        JsonNode node = MAPPER.readTree(in);
        conn.disconnect();
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

        byte[] serverPassword = Utils.cryptPassword(password.getBytes(), username);
        byte[] localPassword = Utils.sha256(password);
        String serverPasswordEncoded = Utils.BASE64_URL.encode(serverPassword);
        ECKey key = new ECKey();
        byte[] publicKey = key.getPublicKey();
        byte[] privateKey = key.getPrivateKey();
        try {
            privateKey = Encrypt.process(localPassword, privateKey);
        } catch (InvalidCipherTextException ex) {
            throw new RuntimeException(ex);
        }
        String[] keys = new String[]{"captchaToken", "captchaValue", "username", "password", "publicKey", "privateKey"};
        String[] vals = new String[]{
                captcha.captchaToken,
                captchaValue,
                username,
                serverPasswordEncoded,
                Utils.BASE64_URL.encode(publicKey),
                Utils.BASE64_URL.encode(privateKey)
        };
        HttpURLConnection conn = doRequest("register", null, keys, vals);

        if (conn.getResponseCode() != 200) {
            throw new IOException("Server error");
        }
        InputStream in = conn.getInputStream();
        JsonNode node = MAPPER.readTree(in);
        conn.disconnect();
        int statusCode = node.get("status").asInt();
        if (statusCode == StatusCode.OK) {
            long userID = node.get("userID").asLong();
            long keyTime = node.get("timestamp").asLong();
            return new CypherUser(username, localPassword, serverPassword, userID, key, keyTime);
        } else {
            throw new APIErrorException(statusCode);
        }
    }

    public static CypherSession userLogin(String username, byte[] serverPassword, byte[] localPassword) throws IOException, APIErrorException {
        String passwordHashEncoded = Utils.BASE64_URL.encode(serverPassword);

        HttpURLConnection conn = doRequest("login", null, new String[]{"username", "password"}, new String[]{username, passwordHashEncoded});

        if (conn.getResponseCode() != 200) {
            throw new IOException("Server error");
        }

        InputStream in = conn.getInputStream();
        JsonNode node = MAPPER.readTree(in);
        conn.disconnect();
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
            return new CypherSession(newUser, sessionID);
        } else {
            throw new APIErrorException(statusCode);
        }
    }

    public static CypherSession userLogin(String username, String password) throws IOException, APIErrorException {
        byte[] serverPassword = Utils.cryptPassword(password.getBytes(), username);
        byte[] localPassword = Utils.sha256(password);
        return userLogin(username, serverPassword, localPassword);
    }

    public static CypherSession userLogin(CypherUser user) throws IOException, APIErrorException {
        return userLogin(user.getUsername(), user.getServerPassword(), user.getLocalPassword());
    }

    public static void userLogout(CypherSession session) throws IOException, APIErrorException {
        HttpURLConnection conn = doRequest("logout", session, null);

        if (conn.getResponseCode() != 200) {
            throw new IOException("Server error");
        }

        InputStream in = conn.getInputStream();
        JsonNode node = MAPPER.readTree(in);
        conn.disconnect();
        int statusCode = node.get("status").asInt();
        if (statusCode != StatusCode.OK) {
            throw new APIErrorException(statusCode);
        }
    }

    public static List<String> findUser(CypherSession session, String username, int limit) throws IOException, APIErrorException {
        String[] keys = new String[]{"username", "limit"};
        String[] vals = new String[]{username, limit + ""};

        HttpURLConnection conn = doRequest("find", session, keys, vals);

        if (conn.getResponseCode() != 200) {
            throw new IOException("Server error");
        }

        JsonNode node = MAPPER.readTree(conn.getInputStream());
        conn.disconnect();
        int statusCode = node.get("status").asInt();
        if (statusCode != StatusCode.OK) {
            throw new APIErrorException(statusCode);
        } else {
            return Utils.MAPPER.treeToValue(node.get("users"), ArrayList.class);
        }
    }

    public static List<String> findUser(CypherSession session, String username) throws IOException, APIErrorException {
        return findUser(session, username, 10);
    }

    public static CypherMessage sendMessage(CypherSession session, CypherUser contactUser, String message) throws IOException, APIErrorException {
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
        HashMap<String, String> pairs = new HashMap<>(6);
        pairs.put("payload", Utils.BASE64_URL.encode(payload));
        pairs.put("contactID", contactUser.getUserID() + "");
        pairs.put("messageID", messageIDLong + "");
        pairs.put("messageTimestamp", timestamp + "");
        pairs.put("userKeyTimestamp", session.getUser().getKeyTime() + "");
        pairs.put("contactKeyTimestamp", contactUser.getKeyTime() + "");

        HttpURLConnection conn = doRequest("message", session, pairs);
        if (conn.getResponseCode() != 200) {
            throw new IOException("Server error");
        }

        InputStream in = conn.getInputStream();
        JsonNode node = MAPPER.readTree(in);
        conn.disconnect();
        int statusCode = node.get("status").asInt();
        if (statusCode != StatusCode.OK) {
            throw new APIErrorException(statusCode);
        }
        return new CypherMessage(messageIDLong, message, timestamp, true, contactUser.getUserID());
    }

    private static CypherContact manageContact(CypherSession session, String contactName, boolean add) throws IOException, APIErrorException {
        String action = "block";
        if (add) {
            action = "add";
        }
        String[] keys = new String[]{"action", "contactName"};
        String[] vals = new String[]{action, contactName};

        HttpURLConnection conn = doRequest("contact", session, keys, vals);
        if (conn.getResponseCode() != 200) {
            throw new IOException("Server error");
        }

        InputStream in = conn.getInputStream();
        JsonNode node = MAPPER.readTree(in);
        conn.disconnect();
        int statusCode = node.get("status").asInt();
        if (statusCode == StatusCode.OK) {
            long userID = node.get("contactID").asLong();
            long keyTimestamp = node.get("keyTimestamp").asLong();
            long contactTimestamp = node.get("contactTimestamp").asLong();
            ECKey key = Utils.decodeKey(node.get("publicKey").asText());
            return new CypherContact(contactName, userID, key, keyTimestamp, CypherContact.ACCEPTED, contactTimestamp);
        } else {
            String status;
            switch (statusCode) {
                case StatusCode.CONTACT_WAITING:
                    status = CypherContact.WAITING;
                    break;
                case StatusCode.CONTACT_BLOCKED:
                    status = CypherContact.BLOCKED;
                    break;
                case StatusCode.CONTACT_DENIED:
                    status = CypherContact.DENIED;
                    break;
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

    private static JsonNode pullUpdate(CypherSession session, CypherUser contact, String action, Boolean since, Long time) throws IOException, APIErrorException {

        String timeRelativeTo = "since";
        if (since != null && !since) {
            timeRelativeTo = "until";
        }
        HashMap<String, String> pairs = new HashMap<>(3);
        pairs.put("action", action);
        if (contact != null) {
            pairs.put("contactID", contact.getUserID() + "");
        }
        if (since != null) {
            pairs.put(timeRelativeTo, time + "");
        }
        HttpURLConnection conn = doRequest("pull", session, pairs);
        if (conn.getResponseCode() != 200) {
            throw new IOException("Server error");
        }

        InputStream in = conn.getInputStream();
        JsonNode node = MAPPER.readTree(in);
        conn.disconnect();
        int statusCode = node.get("status").asInt();
        if (statusCode == StatusCode.OK) {
            return node;
        } else {
            throw new APIErrorException(statusCode);
        }
    }

    public static PullResults pullMessages(CypherSession session, CypherUser contact, Boolean since, Long time) throws IOException, APIErrorException {
        JsonNode node = pullUpdate(session, contact, "messages", since, time);
        int statusCode = node.get("status").asInt();
        if (statusCode == StatusCode.OK) {
            ArrayList<CypherMessage> array = handleMessageNode(node, session.getUser().getKey(), contact.getKey());
            return new PullResults(array, null, null, node.get("notifiedUntil").asLong());
        } else {
            throw new APIErrorException(statusCode);
        }
    }

    public static PullResults pullContacts(CypherSession session, Boolean since, Long time) throws IOException, APIErrorException {
        JsonNode node = pullUpdate(session, null, "contacts", since, time);
        int statusCode = node.get("status").asInt();
        if (statusCode == StatusCode.OK) {
            ArrayList<CypherContact> array = handleContactNode(node);
            return new PullResults(null, array, null, node.get("notifiedUntil").asLong());
        } else {
            throw new APIErrorException(statusCode);
        }
    }

    public static PullResults pullKeys(CypherSession session, CypherUser contact, Boolean since, Long time) throws IOException, APIErrorException {
        JsonNode node = pullUpdate(session, contact, "keys", since, time);
        int statusCode = node.get("status").asInt();
        if (statusCode == StatusCode.OK) {
            return new PullResults(null, null, handleKeyNode(node, session.getUser().getLocalPassword()), node.get("notifiedUntil").asLong());
        } else {
            throw new APIErrorException(statusCode);
        }
    }

    public static PullResults pullAll(CypherSession session, CypherUser contact, Boolean since, Long time) throws IOException, APIErrorException {
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

    public static HttpURLConnection doRequest(String endpoint, CypherSession session, String[] keys, String[] values) throws IOException {
        HttpURLConnection connection = (java.net.HttpURLConnection) new URL(DOMAIN + endpoint).openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Accept-Charset", "UTF-8");
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
        connection.connect();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
        if (session != null) {
            writer.write("userID=");
            writer.write(session.getUser().getUserID() + "&sessionID=");
            writer.write(session.getSessionID());
        }
        if (keys != null && keys.length > 0) {
            if (session != null) {
                writer.write('&');
            }
            // write first row
            writer.write(keys[0]);
            writer.write('=');
            writer.write(URLEncoder.encode(values[0], "UTF-8"));
            for (int i = 1; i < keys.length; i++) {
                writer.write('&');
                writer.write(keys[i]);
                writer.write('=');
                writer.write(URLEncoder.encode(values[i], "UTF-8"));
            }
        }
        writer.close();
        return connection;
    }

    public static HttpURLConnection doRequest(String endpoint, CypherSession session, Map<String, String> m) throws IOException {
        String[] keys = null;
        String[] vals = null;
        if (m != null) {
            keys = (String[]) m.keySet().toArray();
            vals = (String[]) m.values().toArray();
        }
        return doRequest(endpoint, session, keys, vals);
    }

    public static HttpURLConnection doRequest(String endpoint) throws IOException {
        return doRequest(endpoint, null, null, null);
    }

}
