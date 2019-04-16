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

public class ModeTile extends TileService {
    private static final String SERVICE_STATUS_FLAG = "serviceStatus";
    private static final String PREFERENCES_KEY = "me.enzaik.fraps.spectrum";
    private boolean click = false;
    private boolean marker = false;

    @Override
    public void onStartListening() {
        updateTile();
    }

    @Override
    public void onClick() {
        setMode();
    }

    private void setMode() {
        MultiProcessSharedPreferencesProvider.MultiProcessSharedPreferences mode =
                MultiProcessSharedPreferencesProvider.getSharedPreferences(ModeTile.this, "profile");
        SharedPreferences.Editor editor = mode.edit();
        boolean isActive = getServiceStatus();

        // Update tile and set mode
        if (marker){
            Utils.setMode(0);

            editor.putString("mode", "secure");
            editor.apply();

        }
        if ((isActive || !isActive) && click && !marker) {
            Utils.setMode(2);

            editor.putString("mode", "agressive");
            editor.apply();
        } else if ((isActive || !isActive) && !click && !marker) {
            Utils.setMode(1);
            editor.putString("mode", "advanced");
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
        String mode = MultiProcessSharedPreferencesProvider
                .getSharedPreferences(getApplicationContext(), "profile")
                .getString("mode", "");
        Tile tile = this.getQsTile();
        Icon newIcon;
        String newLabel;
        int newState = Tile.STATE_ACTIVE;
        //ArrayList<String> disabledProfilesList = new ArrayList<>();
       // disabledProfilesList.addAll(Arrays.asList(Utils.disabledProfiles().split(",")));

        // Update tile
        if (mode.contains("2") || mode.contains("agressive") ) {
            newLabel = "Agressive";
            newIcon = Icon.createWithResource(getApplicationContext(), R.drawable.ic_outline_warning_24px);
            click = false;
            marker = true;
        } else if (mode.contains("1") || mode.contains("advanced") ){
            newLabel = "Advanced";
            newIcon = Icon.createWithResource(getApplicationContext(), R.drawable.ic_outline_star_rate_18px);
            click = true;
        } else if (mode.contains("0") || mode.contains("secure")) {
            newLabel = "Secure";
            newIcon = Icon.createWithResource(getApplicationContext(), R.drawable.ic_outline_verified_user_24px);
            click = false;
            marker = false;
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
//-----end class
}
