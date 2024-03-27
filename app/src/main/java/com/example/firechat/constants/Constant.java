package com.example.firechat.constants;

import java.util.HashMap;

public class Constant {
    public static final String DEFAULT_AVATAR_URL = "https://firebasestorage.googleapis.com/v0/b/firechat-aa433.appspot.com/o/unknownavatar.png?alt=media&token=9a49ff27-e5fa-4813-97d4-47bd15281550";
    public static final String REMOTE_MSG_AUTHORIZATION = "Authorization";
    public static final String REMOTE_MSG_CONTENT_TYPE = "Content-Type";

    public static final String REMOTE_MSG_DATA = "data";

    public static final String REMOTE_MSG_REGISTRATION_IDS = "registration_ids";

    public static final String FCM_URL = "https://fcm.googleapis.com/fcm/";

    public static final String KEY_FCM_TOKEN = "fcm_token";
    public static final String KEY_MESSAGE = "message";

    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_NAME = "name";
    public static final String KEY_AVATAR = "avatar";
    public static final String KEY_EMAIL = "email";
    public static HashMap<String, String> remoteMsgHeaders = null;
    public static HashMap<String, String> getRemoteMsgHeaders(){
        if(remoteMsgHeaders == null) {
            remoteMsgHeaders = new HashMap<>();
            remoteMsgHeaders.put(REMOTE_MSG_AUTHORIZATION,
                    "key=AAAArVU2EM0:APA91bFO912dkn-eLz9VVMkhh3_a3KDV4-cIKssP-uwXlzNhKhU35XyLj83BgLr_Y9v0ysjgd5OjP0dFpT-0TXuBnJHTQics2rCNrab7bZjCpdZsqNlB4ldER11tgJuyAHKpdBZs3QPG");
            remoteMsgHeaders.put(REMOTE_MSG_CONTENT_TYPE,"application/json");
        }
        return remoteMsgHeaders;
    }
}
