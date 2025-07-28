package cz.adaptech.tesseract4android.sample.bottomNavigationUi;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;

import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.adaptech.tesseract4android.sample.R;
import cz.adaptech.tesseract4android.sample.preparationPages.LogoutDialog;
import cz.adaptech.tesseract4android.sample.preparationPages.login;

public class bottomNavigationPage extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private analysisFragment analysisFragment;
    private recordFragment recordFragment;
    private suggestionFragment suggestionFragment;
//    private SharedPreferences mySharedPreference;

    public static final int ANALYSIS_FRAGMENT_POSITION = 0;
    public static final int RECORD_FRAGMENT_POSITION = 1;
    public static final int SUGGESTION_FRAGMENT_POSITION = 2;
    private LogoutDialog dialog = null;
    private LogoutDialog clearHistoryDialog = null;
    public static Vibrator vibrator;
    public static String currentUsername;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_navigation_page);

        //initialize bottom
        bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setItemIconTintList(null);
//        initView();

        // set default page
        selectedFragment(ANALYSIS_FRAGMENT_POSITION);

        // action of click
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if(item.getItemId()==R.id.analysis){
                    selectedFragment(ANALYSIS_FRAGMENT_POSITION);
                }
                else if (item.getItemId()==R.id.record){
                    selectedFragment(RECORD_FRAGMENT_POSITION);
                }
                else {
                    selectedFragment(SUGGESTION_FRAGMENT_POSITION);
                }

                return true;
            }
        });

        currentUsername = getIntent().getStringExtra("username");
        int flushOrNot = getIntent().getIntExtra("flush", 0);
        if (flushOrNot==0){
            if (currentUsername != null){
                Toast.makeText(this, "Welcome to PennyMatters, "+currentUsername+"!", Toast.LENGTH_SHORT).show();
                login.mySharedPreference = getSharedPreferences("user_name", Context.MODE_PRIVATE);
                login.mySharedPreference.edit().putString("user_name", currentUsername).commit();
            } else {
                Toast.makeText(this, "Welcome back!", Toast.LENGTH_SHORT).show();
            }
        }

        login.mySharedPreference = getSharedPreferences("user_name", Context.MODE_PRIVATE);
        currentUsername = login.mySharedPreference.getString("user_name", "");

        NavigationView myDrawer = findViewById(R.id.drawer);
        View headerLayout = myDrawer.getHeaderView(0);
        TextView usernameTv = headerLayout.findViewById(R.id.user_name_drawer);
        usernameTv.setText(currentUsername);

        LinearLayout clearButton = headerLayout.findViewById(R.id.clear_history_drawer);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearHistoryDialog();
            }
        });

        FloatingActionButton logOutBt = findViewById(R.id.log_out);
        logOutBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logoutDialog();

            }
        });
    }

    private void logoutDialog() {
        dialog = LogoutDialog.Builder(this)
                .setTitle("Confirm Logout")
                .setMessage("Are you sure to logout? Your data will be securely saved on cloud.")
                .setOnConfirmClickListener("Confirm", view -> {
                    clearHistory();
                    Toast.makeText(bottomNavigationPage.this, "Logout safely", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(bottomNavigationPage.this, login.class);
                    intent.putExtra("loginState", login.ACTIVITY_STARTED_FROM_LOGOUT);
                    startActivity(intent);
                    finish();
                })
                .setOnCancelClickListener("Cancel", view -> {
                    Toast.makeText(this, "Logout cancelled", Toast.LENGTH_SHORT).show();
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                })
                .build()
                .shown();
    }

    public void selectedFragment(int position){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        hideFragment(fragmentTransaction);
        if (position==ANALYSIS_FRAGMENT_POSITION){
            if (analysisFragment==null){
                analysisFragment = new analysisFragment();
                fragmentTransaction.add(R.id.content,analysisFragment);
            }
            else {
                fragmentTransaction.show(analysisFragment);
            }
        }
        else if (position==RECORD_FRAGMENT_POSITION) {
            if (recordFragment==null){
                recordFragment = new recordFragment();
                fragmentTransaction.add(R.id.content,recordFragment);
            }
            else {
                fragmentTransaction.show(recordFragment);
            }
        }
        else {
            if (suggestionFragment==null){
                suggestionFragment = new suggestionFragment();
                fragmentTransaction.add(R.id.content,suggestionFragment);
            }
            else {
                fragmentTransaction.show(suggestionFragment);
            }
        }

        fragmentTransaction.commit();
    }

    public void hideFragment(FragmentTransaction fragmentTransaction){
        if (analysisFragment!=null){
            fragmentTransaction.hide(analysisFragment);
        }
        if (recordFragment!=null){
            fragmentTransaction.hide(recordFragment);
        }
        if (suggestionFragment!=null){
            fragmentTransaction.hide(suggestionFragment);
        }

    }

    public void clearHistory(){
        login.mySharedPreference = getSharedPreferences("chat_history_size", Context.MODE_PRIVATE);
        int size = login.mySharedPreference.getInt("chat_history_size",0);
        login.mySharedPreference.edit().putInt("chat_history_size",0).commit();
        for (int i=1; i<=size; i++){
            login.mySharedPreference = getSharedPreferences("chat_history_content", Context.MODE_PRIVATE);
            login.mySharedPreference.edit().remove("chat_history_content"+i).commit();
            login.mySharedPreference = getSharedPreferences("chat_history_time", Context.MODE_PRIVATE);
            login.mySharedPreference.edit().remove("chat_history_time"+i).commit();
            login.mySharedPreference = getSharedPreferences("chat_history_type", Context.MODE_PRIVATE);
            login.mySharedPreference.edit().remove("chat_history_type"+i).commit();
        }
    }

    public void clearHistoryDialog(){
        clearHistoryDialog = LogoutDialog.Builder(this)
                .setTitle("Clear Local History")
                .setMessage("Are you sure to clear local chat history?\nIt's unrecoverable")
                .setOnConfirmClickListener("I'm sure", view -> {
                    clearHistory();
                    Toast.makeText(bottomNavigationPage.this, "History cleared.", Toast.LENGTH_SHORT).show();
                    if (clearHistoryDialog != null) {
                        clearHistoryDialog.dismiss();
                    }
                    finish();
                    Intent intent = new Intent(this, bottomNavigationPage.class);
                    intent.putExtra("flush", 1);
                    startActivity(intent);
                })
                .setOnCancelClickListener("Not sure", view -> {
                    Toast.makeText(bottomNavigationPage.this, "Clear cancelled", Toast.LENGTH_SHORT).show();
                    if (clearHistoryDialog != null) {
                        clearHistoryDialog.dismiss();
                    }
                })
                .build()
                .shown();
    }

    private void requestForUserId(String name){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String requestURL = "https://studev.groept.be/api/a23PT110/getUserId/"+name;
        int id = 0;
        JsonArrayRequest submitRequest = new JsonArrayRequest(requestURL,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray jsonArray) {
                        try {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject curObject = jsonArray.getJSONObject(i);
                                int id = curObject.getInt("customerid");
                                login.mySharedPreference = getSharedPreferences("userId", Context.MODE_PRIVATE);
                                login.mySharedPreference.edit().putInt("userId",id).commit();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(bottomNavigationPage.this, "No Id respond", Toast.LENGTH_SHORT).show();
                        }
                    }
                },

                new Response.ErrorListener(){

                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(bottomNavigationPage.this, "volleyError response", Toast.LENGTH_SHORT).show();
                    }
                });

        requestQueue.add(submitRequest);
    }

}