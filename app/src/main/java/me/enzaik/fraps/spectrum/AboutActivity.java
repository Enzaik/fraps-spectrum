package me.enzaik.fraps.spectrum;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;


import java.util.List;

import eu.chainfire.libsuperuser.Shell;

import static me.enzaik.fraps.spectrum.Utils.balanceAuthorProp;
import static me.enzaik.fraps.spectrum.Utils.balanceNameProp;

import static me.enzaik.fraps.spectrum.Utils.performanceAuthorProp;
import static me.enzaik.fraps.spectrum.Utils.performanceNameProp;

import static me.enzaik.fraps.spectrum.Utils.batteryAuthorProp;
import static me.enzaik.fraps.spectrum.Utils.batteryNameProp;

import static me.enzaik.fraps.spectrum.Utils.gamingAuthorProp;
import static me.enzaik.fraps.spectrum.Utils.gamingNameProp;

import static me.enzaik.fraps.spectrum.Utils.kernelProp;
import static me.enzaik.fraps.spectrum.Utils.listToString;
import static me.enzaik.fraps.spectrum.Utils.vendorProp;
import static me.enzaik.fraps.spectrum.Utils.modelProp;


public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);




        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        final Drawable upArrow =  ContextCompat.getDrawable(this, R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(ContextCompat.getColor(this, R.color.headerAccentColor), PorterDuff.Mode.SRC_ATOP);
        this.getSupportActionBar().setHomeAsUpIndicator(upArrow);

        toolbar.setNavigationOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                finish();
            }
        });

        getDesc();

    }

    private void getDesc() {
      List<String> kernelName;
      List<String> phoneModel;
      List<String> phoneVendor;

      List<String> balanceName;
      List<String> balanceAuthor;

      List<String> performanceName;
      List<String> performanceAuthor;

      List<String> batteryName;
      List<String> batteryAuthor;

      List<String> gamingName;
      List<String> gamingAuthor;

      final TextView kernelTxt = (TextView) findViewById(R.id.kernel_name);
      final TextView phoneModelTxt = (TextView) findViewById(R.id.phone_model);
      final TextView balanceInfoTxt = (TextView) findViewById(R.id.balance_info);
      final TextView performanceInfoTxt = (TextView) findViewById(R.id.performance_info);
      final TextView batteryInfoTxt = (TextView) findViewById(R.id.battery_info);
      final TextView gamingInfoTxt = (TextView) findViewById(R.id.gaming_info);



        kernelName = Shell.SU.run("getprop " + kernelProp);
        phoneModel = Shell.SU.run(String.format("getprop %s", modelProp));
        phoneVendor = Shell.SU.run(String.format("getprop %s", vendorProp));
        balanceName = Shell.SU.run(String.format("getprop %s", balanceNameProp));
        balanceAuthor = Shell.SU.run(String.format("getprop %s", balanceAuthorProp));

        performanceName = Shell.SU.run(String.format("getprop %s", performanceNameProp));
        performanceAuthor = Shell.SU.run(String.format("getprop %s", performanceAuthorProp));

        batteryName = Shell.SU.run(String.format("getprop %s", batteryNameProp));
        batteryAuthor = Shell.SU.run(String.format("getprop %s", batteryAuthorProp));

        gamingName = Shell.SU.run(String.format("getprop %s", gamingNameProp));
        gamingAuthor = Shell.SU.run(String.format("getprop %s", gamingAuthorProp));

        if(kernelName != null){
            kernelTxt.setText(listToString(kernelName));
        }
        if(phoneModel != null){
            phoneModelTxt.setText(listToString(phoneVendor) + " " + listToString(phoneModel));
        }

        if(balanceName != null&& listToString(balanceName) != ""){
            balanceInfoTxt.setText(listToString(balanceName) + " (by " + listToString(balanceAuthor) + ")");

        }

        if(performanceName != null && listToString(performanceName) != ""){
            performanceInfoTxt.setText(listToString(performanceName) + " (by " + listToString(performanceAuthor) + ")");

        }

        if(batteryName != null && listToString(batteryName) != ""){
            batteryInfoTxt.setText(listToString(batteryName) + " (by " + listToString(batteryAuthor) + ")");

        }

        if(gamingName != null && listToString(gamingName) != ""){
            gamingInfoTxt.setText(listToString(gamingName) + " (by " + listToString(gamingAuthor) + ")");

        }

    }

}
