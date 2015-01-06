package com.telnet.karibou;

public class Constants {
    public static final String BASE_URL = "http://karibou2.telecom-lille.fr";
    public static final String HOME_URL = BASE_URL + "/";
    public static final String PANTIE_URL = BASE_URL + "/header/pantie";
    public static final String LOGIN_URL = BASE_URL + "/login";
    public static final String PRESENCE_URL = BASE_URL + "/login/presence";
    public static final String KARIBOU_PUSH = BASE_URL + "/push.php";
    public static final String MC_URL = BASE_URL + "/mc2/state/60,msg";
    public static final String MC_POST = BASE_URL + "/mc2/post/";
    public static final String FLASHMAIL_URL = BASE_URL + "/flashmail/unreadlistJSON/";
    public static final String FLASHMAIL_READ_URL = BASE_URL + "/flashmail/setasread/";
    public static final String USER_LIST_URL = BASE_URL + "/onlineusers/listJSON/";
    protected static final int USER_LIST_REFRESH = 30;
    protected static final int PRESENCE_REFRESH = 240;
    protected static final int FLASHMAIL_REFRESH = 60;
    protected static final int NOTIFICATION_ID = 1;

    protected static final int RESULT_SETTINGS = 1;
}
