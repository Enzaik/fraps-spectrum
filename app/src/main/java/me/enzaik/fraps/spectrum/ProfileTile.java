package me.enzaik.fraps.spectrum;

import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

import com.ruesga.preferences.MultiProcessSharedPreferencesProvider;

import java.util.ArrayList;
import java.util.Arrays;

@TargetApi(Build.VERSION_CODES.N)
public class ProfileTile extends TileService {

    private static final String SERVICE_STATUS_FLAG = "serviceStatus";
    private static final String PREFERENCES_KEY = "org.frap129.spectrum";
    private boolean click = false;

    @Override
    public void onStartListening() {
        updateTile();
    }

    @Override
    public void onClick() {
        setProfile();
    }

    private void setProfile() {
        MultiProcessSharedPreferencesProvider.MultiProcessSharedPreferences profile =
                MultiProcessSharedPreferencesProvider.getSharedPreferences(ProfileTile.this, "profile");
        SharedPreferences.Editor editor = profile.edit();
        boolean isActive = getServiceStatus();

        // Update tile and set profile
        if (isActive && click) {
            Utils.setProfile(3);
            //---------

            //---------
            editor.putString("profile", "gaming");
            editor.apply();
        } else if (!isActive && click) {
            Utils.setProfile(2);
            editor.putString("profile", "battery");
            editor.apply();
        } else if (isActive && !click){
            Utils.setProfile(1);
            editor.putString("profile", "performance");
            editor.apply();
        } else {
            Utils.setProfile(0);
            editor.putString("profile", "balanced");
            editor.apply();
        }

        updateTile();
    }

    private boolean getServiceStatus() {
        SharedPreferences prefs = getApplicationContext().getSharedPreferences(PREFERENCES_KEY, MODE_PRIVATE);
        boolean isActive = prefs.getBoolean(SERVICE_STATUS_FLAG, false);
        isActive = !isActive;

        prefs.edit().putBoolean(SERVICE_STATUS_FLAG, isActive).apply();

        return isActive;
    }

    private void updateTile() {
        String profile = MultiProcessSharedPreferencesProvider
                .getSharedPreferences(getApplicationContext(), "profile")
                .getString("profile", "");
        Tile tile = this.getQsTile();
        Icon newIcon;
        String newLabel;
        int newState = Tile.STATE_ACTIVE;
        ArrayList<String> disabledProfilesList = new ArrayList<>();
  //      disabledProfilesList.addAll(Arrays.asList(Utils.disabledProfiles().split(",")));

        // Update tile
        if (profile.contains("3") || profile.contains("gaming") ) {
            newLabel = "Gaming";
            newIcon = Icon.createWithResource(getApplicationContext(), R.drawable.game);
            click = false;
        } else if (profile.contains("2") || profile.contains("battery") ) {
            newLabel = "Battery";
            newIcon = Icon.createWithResource(getApplicationContext(), R.drawable.battery);
            click = true;
        } else if (profile.contains("1") || profile.contains("performance") ){
            newLabel = "Performance";
            newIcon = Icon.createWithResource(getApplicationContext(), R.drawable.rocket);
            click = true;
        } else if (profile.contains("0") || profile.contains("balanced") ) {
            newLabel = "Balance";
            newIcon = Icon.createWithResource(getApplicationContext(), R.drawable.atom);
            click = false;
        } else {
            newLabel = "Custom";
            newIcon = Icon.createWithResource(getApplicationContext(), R.drawable.ic_mono);
            click = false;
        }

        // Change the UI of the tile.
        tile.setLabel(newLabel);
        tile.setIcon(newIcon);
        tile.setState(newState);
        tile.updateTile();
    }
}