package com.telnet.authentication;

import android.content.Context;
import android.os.Bundle;

/**
 * Created by Pierre Quételart on 06/02/2015.
 */
public interface IAuthentication {
    Bundle signIn(Context context, String login, String password);
}
