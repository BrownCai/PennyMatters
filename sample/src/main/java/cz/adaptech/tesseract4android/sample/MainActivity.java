package cz.adaptech.tesseract4android.sample;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;

import org.json.JSONArray;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.adaptech.tesseract4android.sample.ui.main.DBOpenHelper;
import cz.adaptech.tesseract4android.sample.ui.main.MainFragment;

public class MainActivity extends AppCompatActivity {
    private static final String QUEUE_URL = "https://studev.groept.be/api/ptdemo/queue";
    private TextView txtInfo;
    private static final String POST_URL = "https://studev.groept.be/api/ptdemo/order/";

    private int id;
    private double price;
    private String shop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Find the button in the layout
        Button openFragmentButton = findViewById(R.id.import_button);

        // Set OnClickListener to the button
        openFragmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call the method to open the MainFragment
                openMainFragment();
            }
        });


        /***********************************************database************************************************/
        Button database_insert  = findViewById(R.id.database_insert); //add
        database_insert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
                String createTableUrl = "https://studev.groept.be/api/a23PT110/iteminf/"+String.valueOf(id)+"/"+String.valueOf(price)+"/"+String.valueOf(shop);
                getPostParameters();
                StringRequest createTableRequest = new StringRequest(
                        Request.Method.POST,
                        createTableUrl,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                Toast.makeText(
                                        MainActivity.this,
                                        "Connect successfully",
                                        Toast.LENGTH_SHORT).show();
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                                Toast.makeText(
                                        MainActivity.this,
                                        "Error creating table: " + error.getMessage(),
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                ){ //NOTE THIS PART: here we are passing the POST parameters to the webservice
                    @Override
                    protected Map<String, String> getParams() {
                        /* Map<String, String> with key value pairs as data load */
                        return getPostParameters();
                    }
                };
                requestQueue.add(createTableRequest);
            }
        });
        database_insert.setVisibility(View.GONE);

        int id = getIntent().getIntExtra("id", 0);
        if (id==1){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main, MainFragment.newInstance())
                    .addToBackStack(null) // Add this line if you want the transaction to be reversible
                    .commit();
            openFragmentButton.setVisibility(View.GONE);
        }
    }

    // Method to open the MainFragment
    private void openMainFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main, MainFragment.newInstance())
                .addToBackStack(null) // Add this line if you want the transaction to be reversible
                .commit();
        Button openFragmentButton = findViewById(R.id.import_button);
        openFragmentButton.setVisibility(View.GONE);
    }

    /************************************************************/
    public  Map<String, String> getPostParameters() {
        Map<String, String> params = new HashMap<>();
        id = 1;
        price = 10.12;
        shop = "jimmychoo";
        params.put("id", String.valueOf(id));
        params.put("price", String.valueOf(price));
        params.put("shop", shop);
        return params;

    }

}
