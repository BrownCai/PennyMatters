package cz.adaptech.tesseract4android.sample.bottomNavigationUi;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Stack;

import cz.adaptech.tesseract4android.sample.ItemDetail;
import cz.adaptech.tesseract4android.sample.R;
import cz.adaptech.tesseract4android.sample.adapters.SuggestionAdapter;
import cz.adaptech.tesseract4android.sample.adapters.SuggestionResult;
import cz.adaptech.tesseract4android.sample.preparationPages.login;

public class suggestionFragment extends Fragment {
    private View root;
    private static RecyclerView myRecyclerView;
    private static Stack<SuggestionResult> suggestions;
    private static SuggestionAdapter adapter;
    
    public static suggestionFragment newInstance(String param1, String param2) {
        suggestionFragment fragment = new suggestionFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (root == null){
            root = inflater.inflate(R.layout.fragment_suggestion, container, false);
        }

        suggestions = new Stack<>();
        myRecyclerView = (RecyclerView) root.findViewById(R.id.search_result);
        showResult();

        ImageButton searchButton = root.findViewById(R.id.search_bt);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearSuggestion();
                search();
            }
        });


        return root;
    }

    public void showResult(){
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(),2);
        layoutManager.setOrientation(GridLayoutManager.VERTICAL);
//        layoutManager.setReverseLayout(true);
        myRecyclerView.setLayoutManager(layoutManager);

        adapter = new SuggestionAdapter(getActivity(),suggestions);

        myRecyclerView.setAdapter(adapter);

        initListener();
    }

    public void search(){
        EditText searchKeyEt = root.findViewById(R.id.search_keys);
        String searchKey = searchKeyEt.getText().toString();

        String nameRequestURL = "https://studev.groept.be/api/a23PT110/searchByName/"+searchKey;
        String shopRequestURL = "https://studev.groept.be/api/a23PT110/searchByShop/"+searchKey;
        String labelRequestURL = "https://studev.groept.be/api/a23PT110/searchByLabel/"+searchKey;

        RequestQueue nameQueue = Volley.newRequestQueue(getActivity());
        RequestQueue shopQueue = Volley.newRequestQueue(getActivity());
        RequestQueue labelQueue = Volley.newRequestQueue(getActivity());

        JsonArrayRequest nameRequest = new JsonArrayRequest(nameRequestURL,
            new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray jsonArray) {
                    formatJson(jsonArray);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    Toast.makeText(getActivity(), "name request error", Toast.LENGTH_SHORT).show();
                }
            });

        JsonArrayRequest shopRequest = new JsonArrayRequest(shopRequestURL,
            new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray jsonArray) {
                    formatJson(jsonArray);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    Toast.makeText(getActivity(), "shop request error", Toast.LENGTH_SHORT).show();
                }
            });

        JsonArrayRequest labelRequest = new JsonArrayRequest(labelRequestURL,
            new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray jsonArray) {
                    formatJson(jsonArray);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    Toast.makeText(getActivity(), "label request error", Toast.LENGTH_SHORT).show();
                }
            });


        if (TextUtils.isEmpty(searchKeyEt.getText().toString().trim())){
            Toast.makeText(getActivity(), "Please enter keys you want to search", Toast.LENGTH_SHORT).show();
        } else {
            nameQueue.add(nameRequest);
            shopQueue.add(shopRequest);
            labelQueue.add(labelRequest);
        }

    }

    public void formatJson(JSONArray jsonArray){
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject curObject = jsonArray.getJSONObject(i);
                String shop = curObject.getString("shop");
                String day = curObject.getString("day");
                String month = curObject.getString("month");
                String year = curObject.getString("year");
                String name = curObject.getString("itemname");
                int amount = curObject.getInt("amount");
                int price = curObject.getInt("price");
                String label = curObject.getString("label");
                if (label.equals("null")){
                    label = " ";
                }
                SuggestionResult result = new SuggestionResult(shop,day,month,year,name,amount,price,label);
                suggestions.add(result);
                adapter.notifyItemInserted(suggestions.size());
            }
        } catch (JSONException e) {

        }
    }

    public void clearSuggestion(){
        while (!suggestions.isEmpty()) {
            suggestions.pop();
            adapter.notifyItemRemoved(0);
        }
    }

    public void initListener(){
        adapter.setOnItemClickListener(new SuggestionAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                SuggestionResult item = suggestions.get(position);
                Intent intent = new Intent(getActivity(), ItemDetail.class);
                ArrayList<String> resultArrays = new ArrayList<>();
                resultArrays.add(item.getItemname());
                String unitPrice = String.format("%.1f",((float)item.getPrice())/((float)item.getAmount()))+" €";
                resultArrays.add(unitPrice);
                resultArrays.add(item.getShop());
                resultArrays.add(item.getLabel());
                String date = item.getDay()+"-"+item.getMonth()+"-"+item.getYear();
                resultArrays.add(date);
                intent.putExtra("result", resultArrays);
                startActivity(intent);
//                Toast.makeText(getActivity(), "clicked on "+position+" item", Toast.LENGTH_SHORT).show();
            }
        });
    }

}