package cz.adaptech.tesseract4android.sample.preparationPages;

import static androidx.biometric.BiometricPrompt.ERROR_LOCKOUT;
import static androidx.biometric.BiometricPrompt.ERROR_LOCKOUT_PERMANENT;
import static androidx.biometric.BiometricPrompt.ERROR_NEGATIVE_BUTTON;
import static androidx.biometric.BiometricPrompt.ERROR_NO_BIOMETRICS;
import static androidx.biometric.BiometricPrompt.ERROR_NO_DEVICE_CREDENTIAL;
import static androidx.biometric.BiometricPrompt.ERROR_NO_SPACE;
import static androidx.biometric.BiometricPrompt.ERROR_TIMEOUT;
import static androidx.biometric.BiometricPrompt.ERROR_USER_CANCELED;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.Executor;

import cz.adaptech.tesseract4android.sample.R;
import cz.adaptech.tesseract4android.sample.bottomNavigationUi.bottomNavigationPage;

public class login extends AppCompatActivity implements View.OnClickListener{
    private EditText usernameEtLi;
    private EditText passwordEtLi;
    private TextView mBtnLogin;
    private View progress;
    private View mInputLayout;
    private float mWidth, mHeight;
    private LinearLayout mName, mPsw;
    private RequestQueue requestQueue;
    private ArrayList<String> usernameArray;
    private ArrayList<String> passwordArray;
    private ArrayList<String> roleArray;
    private ArrayList<Integer> customeridArray;
    private  int currentLoginId;
    private boolean loggedIn;
    public static SharedPreferences mySharedPreference;
    public static final int ACTIVITY_STARTED_FROM_INITIAL = 0;
    public static final int ACTIVITY_STARTED_FROM_LOGOUT = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.loginPage), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // change loginState if this activity is started from logout
        int loginState = getIntent().getIntExtra("loginState",ACTIVITY_STARTED_FROM_INITIAL);
        if (loginState == ACTIVITY_STARTED_FROM_LOGOUT) {
            saveLoginState(false);
        }

        // check if logged in
        mySharedPreference=getSharedPreferences("login_state", Context.MODE_PRIVATE);
        if (mySharedPreference.getBoolean("loginState", false)){
            BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                    .setTitle("Authentication")
                    .setSubtitle("Automatic Login")
                    .setDescription("Fingerprint needed to continue")
                    .setNegativeButtonText("Use Password").build();
            getPrompt().authenticate(promptInfo);

//            Intent intent = new Intent(login.this, bottomNavigationPage.class);
//            startActivity(intent);
//            finish();
        }

        // initialize parameters
        initView();
        loggedIn = false;

        //When click on the hint for register
        findViewById(R.id.hint_for_register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(login.this, register.class);
                startActivity(intent);
            }
        });

    }

    public void initView(){
        usernameEtLi = findViewById(R.id.user_name_text_login);
        passwordEtLi = findViewById(R.id.password_text_login);

        mBtnLogin = (TextView) findViewById(R.id.main_btn_login);
        progress = findViewById(R.id.layout_progress);
        mInputLayout = findViewById(R.id.input_layout);
        mName = (LinearLayout) findViewById(R.id.input_layout_name);
        mPsw = (LinearLayout) findViewById(R.id.input_layout_psw);

        mBtnLogin.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {

        // calculate the height and width of item
        mWidth = mBtnLogin.getMeasuredWidth();
        mHeight = mBtnLogin.getMeasuredHeight();
        // invisible EditText
        mName.setVisibility(View.INVISIBLE);
        mPsw.setVisibility(View.INVISIBLE);

        inputAnimator(mInputLayout, mWidth, mHeight);

        loginProcedure();
    }

    public void loginProcedure(){
        usernameArray = new ArrayList<>();
        passwordArray = new ArrayList<>();
        roleArray = new ArrayList<>();
        customeridArray = new ArrayList<>();
        requestQueue = Volley.newRequestQueue( this );
        String requestURL = "https://studev.groept.be/api/a23PT110/userInf";
        JsonArrayRequest submitRequest = new JsonArrayRequest(requestURL,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray jsonArray) {
                        String responseString = "";
                        int length = jsonArray.length();
                        try {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject curObject = jsonArray.getJSONObject(i);
                                usernameArray.add(curObject.getString( "username" ));
                                passwordArray.add(curObject.getString( "password" ));
                                roleArray.add(curObject.getString("role"));
                                customeridArray.add(curObject.getInt("customerid"));
                                responseString += curObject.getString( "username" ) + " " + curObject.getString( "password" ) +" "+curObject.getString("role")+"\n";
                            }
                        } catch (JSONException e) {
                            Toast.makeText(login.this, "no jsonarray response", Toast.LENGTH_SHORT).show();
                        }

                        Log.i("responseString",responseString);

                        boolean foundUserName = false;
//                        boolean foundPassword = false;
                        for (int i=0; i<usernameArray.size(); i++){
                            String u=usernameArray.get(i);
                            String p=passwordArray.get(i);
                            if (usernameEtLi.getText().toString().equals(u)){
                                foundUserName=true;
                            }
                            if ((usernameEtLi.getText().toString().equals(u))&&(passwordEtLi.getText().toString().equals(p))){
                                loggedIn = true;
                                currentLoginId = customeridArray.get(i);
                                mySharedPreference = getSharedPreferences("userId", Context.MODE_PRIVATE);
                                mySharedPreference.edit().putInt("userId", currentLoginId).commit();
                            }
                        }

                        if (loggedIn){
                            saveLoginState(true);
                            Intent intent = new Intent(login.this, bottomNavigationPage.class);
                            intent.putExtra("username", usernameEtLi.getText().toString());
                            startActivity(intent);
                            finish();
                        } else if(!foundUserName){
                            Toast.makeText(login.this, "No user information. Please register first!", Toast.LENGTH_SHORT).show();
                        } else if (foundUserName) {
                            Toast.makeText(login.this, "Wrong password", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(login.this, "No user information.\n"+"Please register first!", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Log.e("Database",volleyError.getLocalizedMessage());
                        Toast.makeText(login.this, "request error", Toast.LENGTH_SHORT).show();
                    }
                });

        requestQueue.add(submitRequest);
    }

    private void saveLoginState(boolean state) {
        mySharedPreference = getSharedPreferences("login_state", Context.MODE_PRIVATE);
        mySharedPreference.edit().putBoolean("loginState", state).commit();
    }

    private BiometricPrompt getPrompt(){
        Executor executor = ContextCompat.getMainExecutor(this);
        BiometricPrompt.AuthenticationCallback callback = new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                switch (errorCode) {
                    case ERROR_USER_CANCELED:
                        Toast.makeText(login.this, "Fingerprint authentication cancelled", Toast.LENGTH_SHORT).show();
                        break;
                    case ERROR_LOCKOUT:
                        Toast.makeText(login.this, "Failed for 5 times!\nPlease wait for 30 seconds then try again", Toast.LENGTH_SHORT).show();
                        break;
                    case ERROR_LOCKOUT_PERMANENT:
                        Toast.makeText(login.this, "Failed too much times!\nPlease use password", Toast.LENGTH_SHORT).show();
                        saveLoginState(false);
                        break;
                    case ERROR_NEGATIVE_BUTTON:
                        Toast.makeText(login.this, "Choose password to login", Toast.LENGTH_SHORT).show();
                        break;
                    case ERROR_NO_DEVICE_CREDENTIAL:
                        Toast.makeText(login.this, "No device credential available", Toast.LENGTH_SHORT).show();
                        Intent intent1 = new Intent(Settings.ACTION_SECURITY_SETTINGS);
                        startActivity(intent1);
                        break;
                    case ERROR_NO_BIOMETRICS:
                        Toast.makeText(login.this, "No fingerprint available\nPlease set biometrics first", Toast.LENGTH_SHORT).show();
                        Intent intent2 = new Intent(Settings.ACTION_SECURITY_SETTINGS);
                        startActivity(intent2);
                        break;
                    case ERROR_NO_SPACE:
                        Toast.makeText(login.this, "Lack of storage", Toast.LENGTH_SHORT).show();
                        break;
                    case ERROR_TIMEOUT:
                        Toast.makeText(login.this, "Authentication Time Out!\n Please use password", Toast.LENGTH_SHORT).show();
                        saveLoginState(false);
                        break;
                    default:
                        Toast.makeText(login.this, "Error "+errorCode+""+errString, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Intent intent = new Intent(login.this, bottomNavigationPage.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(login.this, "Authentication Failed!\nPlease log in with password!!", Toast.LENGTH_SHORT).show();
            }
        };

        BiometricPrompt biometricPrompt = new BiometricPrompt(this, executor, callback);
        return biometricPrompt;
    }


    // realization of animation
    private void inputAnimator(final View view, float w, float h) {

        AnimatorSet set = new AnimatorSet();

        ValueAnimator animator = ValueAnimator.ofFloat(0, w);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (Float) animation.getAnimatedValue();
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
                params.leftMargin = (int) value;
                params.rightMargin = (int) value;
                view.setLayoutParams(params);
            }
        });

        ObjectAnimator animator2 = ObjectAnimator.ofFloat(mInputLayout, "scaleX", 1f, 0.1f);
        set.setDuration(400);
        set.setInterpolator(new AccelerateDecelerateInterpolator());
        set.playTogether(animator, animator2);
        set.start();
        set.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {}

            @Override
            public void onAnimationRepeat(Animator animation) {}

            @Override
            public void onAnimationEnd(Animator animation) {

                //When the animation is over, show the loaded animation first, and then invisible the EditText
                progress.setVisibility(View.VISIBLE);
                progressAnimator(progress);
                mInputLayout.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {}
        });
    }


    //display of progressing animation
    private void progressAnimator(final View view) {
        PropertyValuesHolder animator = PropertyValuesHolder.ofFloat("scaleX",0.4f, 1f);
        PropertyValuesHolder animator2 = PropertyValuesHolder.ofFloat("scaleY",0.4f, 1f);
        ObjectAnimator animator3 = ObjectAnimator.ofPropertyValuesHolder(view, animator, animator2);
        animator3.setDuration(1000);
        animator3.setInterpolator(new JellyInterpolator());
        animator3.start();
        animator3.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(@NonNull Animator animator) {
            }

            @Override
            public void onAnimationEnd(@NonNull Animator animator) {
                if (!loggedIn){
                    progress.setVisibility(View.GONE);
                    recovery();
                }
            }

            @Override
            public void onAnimationCancel(@NonNull Animator animator) {
            }

            @Override
            public void onAnimationRepeat(@NonNull Animator animator) {

            }
        });
    }

    private void recovery() {
//        progress.setVisibility(View.GONE);
        mInputLayout.setVisibility(View.VISIBLE);
        mName.setVisibility(View.VISIBLE);
        mPsw.setVisibility(View.VISIBLE);

        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mInputLayout.getLayoutParams();
        params.leftMargin = 0;
        params.rightMargin = 0;
        mInputLayout.setLayoutParams(params);


        ObjectAnimator animator2 = ObjectAnimator.ofFloat(mInputLayout, "scaleX", 0.1f,1f );
        animator2.setDuration(500);
        animator2.setInterpolator(new AccelerateDecelerateInterpolator());
        animator2.start();
    }

    public int getCurrentLoginId(){
        return currentLoginId;
    }
}