package cz.adaptech.tesseract4android.sample.preparationPages;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cz.adaptech.tesseract4android.sample.R;

public class register extends AppCompatActivity {
    private EditText usernameEtRg;
    private EditText passwordEtRg;
    private EditText confirmPasswordEt;
    private Spinner roleSelector;
    private String roleSelected;
    private ArrayList<String> existingUser;
    private boolean existOrNot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.registerPage), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        usernameEtRg = findViewById(R.id.user_name_text_register);
        passwordEtRg = findViewById(R.id.password_text_register);
        confirmPasswordEt = findViewById(R.id.confirm_password_register);
        roleSelector = findViewById(R.id.role_selector);

        //back to login page
        findViewById(R.id.back_to_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        roleSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if (position==1){
                    roleSelected = "seller";
//                    Toast.makeText(register.this, "seller", Toast.LENGTH_SHORT).show();
                } else {
                    roleSelected = "customer";
//                    Toast.makeText(register.this, "customer", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        findViewById(R.id.bt_register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = usernameEtRg.getText().toString();
                String pw = passwordEtRg.getText().toString();
                String confirmPW = confirmPasswordEt.getText().toString();

//                checkExist();

                if (id.isEmpty()||pw.isEmpty()||confirmPW.isEmpty()){
                    Toast toastEmpty = Toast.makeText(register.this, "Please fill in all the blanks!", Toast.LENGTH_SHORT);
                    toastEmpty.setGravity(Gravity.CENTER_VERTICAL,0,0);
                    toastEmpty.show();
                }
                else if (!pw.equals(confirmPW)){
                    Toast.makeText(register.this, "Confirmation Error!\n"+"Please reconfirm password!", Toast.LENGTH_SHORT).show();
                } else if (existOrNot) {
                    Toast.makeText(register.this, "Username already exists!\n"+"Please login or change username.", Toast.LENGTH_SHORT).show();
                } else{
                    registerProcedure(id,pw,roleSelected);
                }
            }
        });
    }

    public void registerProcedure(String username, String password, String role){
        Map<String, String> params= new HashMap<>();
        params.put("username",username);
        params.put("password",password);
        params.put("role",role);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        // add interaction with database here
        String requestURL = "https://studev.groept.be/api/a23PT110/register/username/password/role";
        StringRequest submitRequest = new StringRequest(Request.Method.POST, requestURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Toast.makeText(register.this, "Register succeed!\n Please login", Toast.LENGTH_SHORT).show();
                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(register.this, "Register failed!", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams(){
                return params;
            }
        };

        requestQueue.add(submitRequest);
    }



    public void checkExist(){
        existOrNot = false;
        RequestQueue requestQueue = Volley.newRequestQueue( this );
        String requestURL = "https://studev.groept.be/api/a23PT110/existingUser";
        existingUser = new ArrayList<>();
        JsonArrayRequest submitRequest = new JsonArrayRequest(requestURL,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray jsonArray) {
                        try {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject curObject = jsonArray.getJSONObject(i);
                                existingUser.add(curObject.getString( "username" ));
                            }
                        } catch (JSONException e) {
                            Toast.makeText(register.this, "no jsonarray response", Toast.LENGTH_SHORT).show();
                        }

                        boolean foundUserName = false;
//                        boolean foundPassword = false;
                        for (int i=0; i<existingUser.size(); i++){
                            String u=existingUser.get(i);
                            String id=usernameEtRg.getText().toString();
                            if (u.equals(id)){
                                existOrNot=true;
                            }
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Log.e("Database",volleyError.getLocalizedMessage());
                        Toast.makeText(register.this, "request error", Toast.LENGTH_SHORT).show();
                    }
                });

        requestQueue.add(submitRequest);
    }
}