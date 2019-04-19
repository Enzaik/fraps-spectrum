package me.enzaik.fraps.spectrum;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Objects;

import eu.chainfire.libsuperuser.Shell;

import static  me.enzaik.fraps.spectrum.Utils.KPM;
import static  me.enzaik.fraps.spectrum.Utils.checkSupport;
import static  me.enzaik.fraps.spectrum.Utils.cpuScalingGovernorPath;
import static  me.enzaik.fraps.spectrum.Utils.finalGov;
import static  me.enzaik.fraps.spectrum.Utils.getCustomDesc;
import static  me.enzaik.fraps.spectrum.Utils.kernelProp;
import static  me.enzaik.fraps.spectrum.Utils.kpmFinal;
import static  me.enzaik.fraps.spectrum.Utils.kpmPath;
import static  me.enzaik.fraps.spectrum.Utils.kpmPropPath;
import static  me.enzaik.fraps.spectrum.Utils.listToString;
import static  me.enzaik.fraps.spectrum.Utils.notTunedGov;
import static  me.enzaik.fraps.spectrum.Utils.profileProp;
import static  me.enzaik.fraps.spectrum.Utils.modeProp;
import static  me.enzaik.fraps.spectrum.Utils.setProfile;
import static  me.enzaik.fraps.spectrum.Utils.setMode;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private CardView oldCard;
    private TextView oldText;
    private List<String> suResult = null;
    private List<String> suModeResult = null;
    private int notaneasteregg = 0;
    private static final int PERMISSIONS_REQUEST = 0;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        //----------------Modes Listener----------------------------------
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {


            switch (item.getItemId()) {
                case R.id.mode0:
                    Toast.makeText(getApplicationContext(),
                            "A",
                            Toast.LENGTH_SHORT).show();
                    setMode(0);
                    clickMode("secure");
                    return true;
                case R.id.mode1:
                    Toast.makeText(getApplicationContext(),
                            "B",
                            Toast.LENGTH_SHORT).show();
                    setMode(1);
                    clickMode("advanced");
                    return true;
                case R.id.mode2:
                    Toast.makeText(getApplicationContext(),
                            "C",
                            Toast.LENGTH_SHORT).show();
                    setMode(2);
                    clickMode("agressive");
                    return true;

            }

            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        final CardView card0 = (CardView) findViewById(R.id.card0);
        final CardView card1 = (CardView) findViewById(R.id.card1);
        final CardView card2 = (CardView) findViewById(R.id.card2);
        final CardView card3 = (CardView) findViewById(R.id.card3);
        final TextView desc0 = (TextView) findViewById(R.id.desc0);
        final TextView desc1 = (TextView) findViewById(R.id.desc1);
        final TextView desc2 = (TextView) findViewById(R.id.desc2);
        final TextView desc3 = (TextView) findViewById(R.id.desc3);
        final TextView kernelView = (TextView) findViewById(R.id.kernel_name);

        final int balColor = ContextCompat.getColor(this, R.color.colorBalance);
        final int perColor = ContextCompat.getColor(this, R.color.colorPerformance);
        final int batColor = ContextCompat.getColor(this, R.color.colorBattery);
        final int gamColor = ContextCompat.getColor(this, R.color.colorGaming);

        final int textWhiteColor = ContextCompat.getColor(this, R.color.colorTextWhite);

        // Check for Spectrum Support
        if (!checkSupport(this)) {
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.no_spectrum_support_dialog_title))
                    .setMessage(getString(R.string.no_spectrum_support_dialog_message))
                    .setCancelable(false)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            finish();
                        }
                    })
                    .show();
            return;
        }

        // Ensure root access
        if (!Utils.checkSU()) {
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.no_root_detected_dialog_title))
                    .setMessage(getString(R.string.no_root_detected_dialog_message))
                    .setCancelable(false)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            finish();
                        }
                    })
                    .show();
            return;
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
        }

        String disabledProfiles = Utils.disabledProfiles();
        String[] profilesToDisable = disabledProfiles.split(",");
        for (String profile : profilesToDisable){
            switch (profile) {
                case "balance":
                    card0.setVisibility(View.GONE);
                    break;
                case "performance":
                    card1.setVisibility(View.GONE);
                    break;
                case "battery":
                    card2.setVisibility(View.GONE);
                    break;
                case "gaming":
                    card3.setVisibility(View.GONE);
                    break;
                default:
                    break;
            }
        }


        //--
        // Get profile descriptions
        getDesc();

        // Highlight current profile
        initSelected();

        // Set system property on click
        card0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardClick(card0, desc0,0, balColor, textWhiteColor);
                if (notaneasteregg == 1) {
                    notaneasteregg++;
                } else {
                    notaneasteregg = 0;
                }
            }
        });

        card1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardClick(card1, desc1,1, perColor, textWhiteColor);
                if (notaneasteregg == 3) {
                    Intent intent = new Intent(MainActivity.this, ProfileLoaderActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    notaneasteregg = 0;
                }
            }
        });

        card2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardClick(card2, desc2,2, batColor, textWhiteColor);
                if (notaneasteregg == 2) {
                    notaneasteregg++;
                } else {
                    notaneasteregg = 0;
                }
            }
        });

        card3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardClick(card3, desc3, 3, gamColor, textWhiteColor);
                notaneasteregg = 1;
            }
        });

        //Modes listeners







        //------------------------------------------------

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        final BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }


   //------------

    // Method that detects the selected profile on launch
    private void initSelected() {
        SharedPreferences profile = this.getSharedPreferences("profile", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = profile.edit();
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);


        if(KPM) {
            suResult = Shell.SU.run(String.format("cat %s", kpmPath));
        } else {
            suResult = Shell.SU.run(String.format("getprop %s", profileProp));
            suModeResult = Shell.SU.run(String.format("getprop %s", modeProp));

        }

        if (suResult != null) {
            String result = listToString(suResult);


            if(result.contains("-1")) {
                // Default KPM value, just in case
            } else if (result.contains("0")) {
                CardView card0 = (CardView) findViewById(R.id.card0);
                TextView txt0 = (TextView) findViewById(R.id.desc0);
                int balColor = ContextCompat.getColor(this, R.color.colorBalance);
                card0.setCardBackgroundColor(balColor);
                txt0.setTextColor(Color.parseColor("#FFFFFF"));
                oldCard = card0;
                oldText = txt0;
                editor.putString("profile", "balanced");
                editor.apply();
            } else if (result.contains("1")) {
                CardView card1 = (CardView) findViewById(R.id.card1);
                TextView txt1 = (TextView) findViewById(R.id.desc1);
                int perColor = ContextCompat.getColor(this, R.color.colorPerformance);
                card1.setCardBackgroundColor(perColor);
                txt1.setTextColor(Color.parseColor("#FFFFFF"));
                oldCard = card1;
                oldText = txt1;
                editor.putString("profile", "performance");
                editor.apply();
            } else if (result.contains("2")) {
                CardView card2 = (CardView) findViewById(R.id.card2);
                TextView txt2 = (TextView) findViewById(R.id.desc2);
                int batColor = ContextCompat.getColor(this, R.color.colorBattery);
                card2.setCardBackgroundColor(batColor);
                txt2.setTextColor(Color.parseColor("#FFFFFF"));
                oldCard = card2;
                oldText = txt2;
                editor.putString("profile", "battery");
                editor.apply();
            } else if (result.contains("3")) {
                CardView card3 = (CardView) findViewById(R.id.card3);
                TextView txt3 = (TextView) findViewById(R.id.desc1);
                int gamColor = ContextCompat.getColor(this, R.color.colorGaming);
                card3.setCardBackgroundColor(gamColor);
                txt3.setTextColor(Color.parseColor("#FFFFFF"));
                oldCard = card3;
                oldText = txt3;
                editor.putString("profile", "gaming");
                editor.apply();
            } else {
                editor.putString("profile", "custom");
                editor.apply();
            }
        }
        //---------init Mode------------
        if (suModeResult != null){
            String modeResult = listToString(suModeResult);
            switch (modeResult){
                case "-1":
                    Toast.makeText(getApplicationContext(),
                            "-1",
                            Toast.LENGTH_SHORT).show();
                    editor.putString("mode", "secure");
                    editor.apply();
                    return;
                case "0":
                    Toast.makeText(getApplicationContext(),
                            "A",
                            Toast.LENGTH_SHORT).show();
                    editor.putString("mode", "secure");
                    editor.apply();
                    navigation.setSelectedItemId(R.id.mode0);

                    return;

                case "1":
                    Toast.makeText(getApplicationContext(),
                            "B",
                            Toast.LENGTH_SHORT).show();
                    editor.putString("mode", "advanced");
                    editor.apply();
                    navigation.setSelectedItemId(R.id.mode1);

                    return;
                case "2":
                    Toast.makeText(getApplicationContext(),
                            "C",
                            Toast.LENGTH_SHORT).show();
                    editor.putString("mode", "agressive");
                    editor.apply();
                    navigation.setSelectedItemId(R.id.mode2);

                    return;
                default:
                    Toast.makeText(getApplicationContext(),
                            "default",
                            Toast.LENGTH_SHORT).show();
                    return;

            }

        }
    }

    // Method that reads and sets profile descriptions
    private void getDesc() {
        TextView desc0 = (TextView) findViewById(R.id.desc0);
        TextView desc1 = (TextView) findViewById(R.id.desc1);
        TextView desc2 = (TextView) findViewById(R.id.desc2);
        TextView desc3 = (TextView) findViewById(R.id.desc3);

        String balDesc;
        String kernel;



        if(KPM){
            suResult = Shell.SU.run(String.format("cat %s", kpmPropPath));
        } else {
            suResult = Shell.SU.run(String.format("getprop %s", kernelProp));
            suModeResult = Shell.SU.run(String.format("getprop %s", modeProp));


        }
        kernel = listToString(suResult);
        kernel = listToString(suResult);

        if (kernel.isEmpty())
            return;
        balDesc = desc0.getText().toString();
        balDesc = balDesc.replaceAll("\\bElectron\\b", kernel);
        desc0.setText(balDesc);

        if (Utils.supportsCustomDesc()){
            if(!Objects.equals(getCustomDesc("balance"), "fail")) desc0.setText(getCustomDesc("balance"));
            if(!Objects.equals(getCustomDesc("performance"), "fail")) desc1.setText(getCustomDesc("performance"));
            if(!Objects.equals(getCustomDesc("battery"), "fail")) desc2.setText(getCustomDesc("battery"));
            if(!Objects.equals(getCustomDesc("gaming"), "fail")) desc3.setText(getCustomDesc("gaming"));
        }
    }



    // Method that completes card onClick tasks
    private void cardClick(CardView card, TextView text, int prof, int color, int textColor) {
        if (oldCard != card) {
            ColorStateList ogColor = card.getCardBackgroundColor();
            ColorStateList txtColor = text.getTextColors();
            card.setCardBackgroundColor(color);
            text.setTextColor(textColor);
            if (oldCard != null)
                oldCard.setCardBackgroundColor(ogColor);
                oldText.setTextColor(txtColor);
            setProfile(prof);
            if (KPM) {
                Shell.SU.run(String.format("echo %s > %s", notTunedGov, cpuScalingGovernorPath));
                finalGov = listToString(Shell.SU.run(String.format("cat %s", kpmFinal)));
                Shell.SU.run(String.format("echo %s > %s", finalGov, cpuScalingGovernorPath));
            }
            oldCard = card;
            oldText = text;
            SharedPreferences profile = this.getSharedPreferences("profile", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = profile.edit();
            editor.putString("profile", String.valueOf(prof));
            editor.apply();
        }
    }

    private void clickMode(String mode){
        SharedPreferences profile = this.getSharedPreferences("profile", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = profile.edit();
        editor.putString("mode", mode);
        editor.apply();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST: {
                if (grantResults.length > 0) {
                    for(int i = 0; i < grantResults.length; i++) {
                        String permission = permissions[i];
                        if(Manifest.permission.READ_EXTERNAL_STORAGE.equals(permission)) {
                            if(grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                                this.recreate();
                            } else {
                                Toast.makeText(this, R.string.custom_descriptions_fail_text, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
                break;
            }
            default:
                break;
        }
    }



   //------------


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        TextView kernelView = (TextView) findViewById(R.id.kernel_name);
        suResult = Shell.SU.run(String.format("getprop %s", kernelProp));
        String kernel_name = listToString(suResult);
        kernelView.setText(kernel_name);
        return true;
    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.mode1) {
            Toast.makeText(getApplicationContext(),
                    "This is a message displayed in a Toast",
                    Toast.LENGTH_SHORT).show();
            // Handle the camera action
        }  else if (id == R.id.nav_share) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}

