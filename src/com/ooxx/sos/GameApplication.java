package com.ooxx.sos;

import android.app.Application;
import android.util.Log;

import com.wogamecenter.api.WoGameCenter;
import com.wogamecenter.api.WoGameCenterDelegate;
import com.wogamecenter.api.WoGameCenterSettings;


public class GameApplication extends Application {

	static final String gameName = "SOS";
	static final String gameID = "90234616120130123171740979500";
	static final String gameKey = "kp2R3xi4R0fVrnNNlWAEmA";
	static final String gameSecret = "InYg6iY8nva0LE8bCCBaZdpIoJ9k42yZ";
    
    @Override
    public void onCreate() {
        super.onCreate();
        WoGameCenterSettings settings = new WoGameCenterSettings(gameName,
                gameKey, gameSecret, gameID);

        WoGameCenter.initialize(this, settings, new WoGameCenterDelegate() {});
    }
}
