package cz.adaptech.tesseract4android.sample;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

import cz.adaptech.tesseract4android.sample.adapters.SuggestionResult;

public class ItemDetail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_item_detail);

        TextView nameTv = findViewById(R.id.name_detail_tv);
        TextView priceTv = findViewById(R.id.price_detail_tv);
        TextView shopTv = findViewById(R.id.shop_detail_tv);
        TextView descriptionTv = findViewById(R.id.description_detail_tv);
        TextView dateTv = findViewById(R.id.date_detail_tv);

        ArrayList<String> item = getIntent().getStringArrayListExtra("result");
        nameTv.setText(item.get(0));
        priceTv.setText(item.get(1));
        shopTv.setText(item.get(2));
        String label = item.get(3);
        if (label.equals("null")){
            descriptionTv.setText("");
        } else {
            descriptionTv.setText(label);
        }
        dateTv.setText(item.get(4));

        findViewById(R.id.back_to_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
}