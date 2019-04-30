package me.enzaik.fraps.spectrum;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;


import com.ruesga.preferences.MultiProcessSharedPreferencesProvider;

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
     /* List<String> kernelName;
      List<String> phoneModel;
      List<String> phoneVendor;

      List<String> balanceName;
      List<String> balanceAuthor;

      List<String> performanceName;
      List<String> performanceAuthor;

      List<String> batteryName;
      List<String> batteryAuthor;

      List<String> gamingName;
      List<String> gamingAuthor;*/

      final TextView kernelTxt = (TextView) findViewById(R.id.kernel_name);
      final TextView phoneModelTxt = (TextView) findViewById(R.id.phone_model);
      final TextView balanceInfoTxt = (TextView) findViewById(R.id.balance_info);
      final TextView performanceInfoTxt = (TextView) findViewById(R.id.performance_info);
      final TextView batteryInfoTxt = (TextView) findViewById(R.id.battery_info);
      final TextView gamingInfoTxt = (TextView) findViewById(R.id.gaming_info);



        String kernelName = MultiProcessSharedPreferencesProvider
                .getSharedPreferences(getApplicationContext(), "profile")
                .getString("kernelName", "");

        String phoneModel = MultiProcessSharedPreferencesProvider
                .getSharedPreferences(getApplicationContext(), "profile")
                .getString("phoneModel", "");

        String phoneVendor = MultiProcessSharedPreferencesProvider
                .getSharedPreferences(getApplicationContext(), "profile")
                .getString("phoneVendor", "");

        String balanceName = MultiProcessSharedPreferencesProvider
                .getSharedPreferences(getApplicationContext(), "profile")
                .getString("balanceName", "");
        String balanceAuthor = MultiProcessSharedPreferencesProvider
                .getSharedPreferences(getApplicationContext(), "profile")
                .getString("balanceAuthor", "");

        String performanceName = MultiProcessSharedPreferencesProvider
                .getSharedPreferences(getApplicationContext(), "profile")
                .getString("performanceName", "");
        String performanceAuthor= MultiProcessSharedPreferencesProvider
                .getSharedPreferences(getApplicationContext(), "profile")
                .getString("performanceAuthor", "");

        String batteryName = MultiProcessSharedPreferencesProvider
                .getSharedPreferences(getApplicationContext(), "profile")
                .getString("batteryName", "");
        String batteryAuthor = MultiProcessSharedPreferencesProvider
                .getSharedPreferences(getApplicationContext(), "profile")
                .getString("batteryAuthor", "");

        String gamingName = MultiProcessSharedPreferencesProvider
                .getSharedPreferences(getApplicationContext(), "profile")
                .getString("gamingName", "");
        String gamingAuthor = MultiProcessSharedPreferencesProvider
                .getSharedPreferences(getApplicationContext(), "profile")
                .getString("gamingAuthor", "");



        kernelTxt.setText(kernelName);
        phoneModelTxt.setText((phoneVendor) + " " + (phoneModel));
        balanceInfoTxt.setText((balanceName) + " " + (balanceAuthor) );
        performanceInfoTxt.setText((performanceName) +  " " + (performanceAuthor) );
        batteryInfoTxt.setText((batteryName) + " " + (batteryAuthor));
        gamingInfoTxt.setText((gamingName) + " " + (gamingAuthor) );


    }

}
