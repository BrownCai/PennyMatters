package cz.adaptech.tesseract4android.sample.bottomNavigationUi;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.text.SimpleDateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.adaptech.tesseract4android.sample.MainActivity;
import cz.adaptech.tesseract4android.sample.R;
import cz.adaptech.tesseract4android.sample.adapters.OutputMessage;
import cz.adaptech.tesseract4android.sample.adapters.RecordAdapter;
import cz.adaptech.tesseract4android.sample.preparationPages.login;
import cz.adaptech.tesseract4android.sample.ui.main.MainFragment;

public class recordFragment extends Fragment {
    private View root;
    private MainFragment mainFragment;
    private static RecyclerView myRecyclerView;
    private static Stack<OutputMessage> messages;
    private static RecordAdapter adapter;
    private String itemname;
    private String shop;
    private String price;
    private String day;
    private String month;
    private String year;
    private String amount;
    private Date date;
    private int customerid = 1;
    private String deleted_itemname;
    private String deleted_price;
//    public static Vibrator vibrator;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


//        messages = new ArrayList<>();
//        myRecyclerView = (RecyclerView) root.findViewById(R.id.recyclerview_record_page);
//        //initialize test data
//        initData();
//
//        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
//        myRecyclerView.setLayoutManager(layoutManager);
//        RecordAdapter adapter = new RecordAdapter(getActivity(),messages);
//        myRecyclerView.setAdapter(adapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (root == null){
            root = inflater.inflate(R.layout.fragment_record, container, false);
        }

        messages = new Stack<>();
        myRecyclerView = (RecyclerView) root.findViewById(R.id.recyclerview_record_page);
        //initialize test data
        initMessage();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
//        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
//        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        myRecyclerView.setLayoutManager(layoutManager);
        adapter = new RecordAdapter(getActivity(),messages);
        myRecyclerView.setAdapter(adapter);

        bottomNavigationPage.vibrator = (Vibrator) getActivity().getSystemService(Service.VIBRATOR_SERVICE);

        ImageButton addButton = root.findViewById(R.id.send_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText priceEt = root.findViewById(R.id.price_et);
                EditText itemEt = root.findViewById(R.id.item_et);
                EditText shopEt = root.findViewById(R.id.shop_et);
                EditText amountEt = root.findViewById(R.id.amount_et);

                if((!TextUtils.isEmpty(priceEt.getText().toString().trim()))
                        &&(!TextUtils.isEmpty(itemEt.getText().toString().trim()))
                        &&(!TextUtils.isEmpty(shopEt.getText().toString().trim()))){
                     itemname = itemEt.getText().toString();
                     price = priceEt.getText().toString();
                     shop = shopEt.getText().toString();
                    amount = amountEt.getText().toString();
                    String text;
                    if (!TextUtils.isEmpty(amountEt.getText().toString().trim())){
                        text = "From "+shop+", bought "+itemname+" ⨯"+amount+" cost "+price+" €";
                    } else {
                        text = "From "+shop+", bought "+itemname+" cost "+price+" €";
                    }
                    date = messageGeneration(RecordAdapter.MESSAGE_SENT, text);
                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy   HH:mm:ss");
                    String inputTime = formatter.format(date);

                    addHistory(text, inputTime, RecordAdapter.MESSAGE_SENT);


                    //Call vibration
//                    bottomNavigationPage.vibrator.cancel();
                    bottomNavigationPage.vibrator.vibrate(new long[]{100, 200}, -1);

                    amountEt.setText("");
                    priceEt.setText("");
                    itemEt.setText("");
                    shopEt.setText("");
                    SimpleDateFormat formatter1 = new SimpleDateFormat("dd");
                    SimpleDateFormat formatter2 = new SimpleDateFormat("MM");
                    SimpleDateFormat formatter3 = new SimpleDateFormat("yyyy");
                    day = formatter1.format(date);
                    month = formatter2.format(date);
                    year =  formatter3.format(date);

                    upload();
                    waitFor(50);

                } else if (TextUtils.isEmpty(itemEt.getText().toString().trim())){
                    Toast.makeText(getActivity(), "Please tell me the item", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Please tell me the price", Toast.LENGTH_SHORT).show();
                }
            }
        });

        addButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast.makeText(getActivity(), "Choose scan of receipts or take photos", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.putExtra("id", 1);
                startActivity(intent);
                return true;
            }
        });

        adapter.setOnItemClickListener(new RecordAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(int position) {
                messages.get(position).setMessageType(2);
                adapter.notifyItemChanged(position);
//               Toast.makeText(getActivity(), "Clicked on "+position+" item", Toast.LENGTH_SHORT).show();

                String realtime_string = messages.get(position).getItemAndPrice();
                String[] extractedData = extractItemAndPrice(realtime_string);
                deleted_itemname = extractedData[0].trim();
                deleted_price = removeTrailingZeros(extractedData[1]);
                delete();
                waitFor(50);

                login.mySharedPreference = getActivity().getSharedPreferences("chat_history_size", Context.MODE_PRIVATE);
                int historySize = login.mySharedPreference.getInt("chat_history_size",0);
                int historyIndex = historySize-(messages.size()-(position+1));
                login.mySharedPreference = getActivity().getSharedPreferences("chat_history_type", Context.MODE_PRIVATE);
                login.mySharedPreference.edit().putInt("chat_history_type"+historyIndex, RecordAdapter.MESSAGE_DELETED).commit();

//                bottomNavigationPage.vibrator.cancel();
                bottomNavigationPage.vibrator.vibrate(new long[]{100, 500}, -1);

                //Call notification
                Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                Ringtone ring = RingtoneManager.getRingtone(getContext(), notification);
                ring.play();

            }
        });

        return root;
    }

    private void initMessage(){
        //Read history
        login.mySharedPreference = getActivity().getSharedPreferences("chat_history_size", Context.MODE_PRIVATE);
        int size = login.mySharedPreference.getInt("chat_history_size",0);
        for (int i=1; i<=size; i++){
            login.mySharedPreference = getActivity().getSharedPreferences("chat_history_content", Context.MODE_PRIVATE);
            String text = login.mySharedPreference.getString("chat_history_content"+i,"History not found");
            Log.i("text_in_sp",text);
            login.mySharedPreference = getActivity().getSharedPreferences("chat_history_time", Context.MODE_PRIVATE);
            String time = login.mySharedPreference.getString("chat_history_time"+i," ");
            Log.i("time_in_sp",time);
            login.mySharedPreference = getActivity().getSharedPreferences("chat_history_type", Context.MODE_PRIVATE);
            int type = login.mySharedPreference.getInt("chat_history_type"+i, RecordAdapter.MESSAGE_SENT);
            Log.i("type_in_sp",""+type);
            messageGeneration(type,text,time);
        }

        // Here gives greetings
        OutputMessage message1 = new OutputMessage(RecordAdapter.MESSAGE_DIALOG,"Hallo! Ik ben PennyMatters.");
        messages.push(message1);
        OutputMessage message2 = new OutputMessage(RecordAdapter.MESSAGE_DIALOG,"Just send recent expenditure.");
        messages.push(message2);
        OutputMessage message3 = new OutputMessage(RecordAdapter.MESSAGE_DIALOG,"Or long press the send button to use\nreceipt");
        messages.push(message3);
        OutputMessage message4 = new OutputMessage(RecordAdapter.MESSAGE_DIALOG, "The default amount is 1");
        messages.push(message4);

//        OutputMessage message5 = new OutputMessage(RecordAdapter.MESSAGE_RECEIVED, "test received message put into history");
//        messages.push(message5);
    }

    public static Date messageGeneration(int messageType, String messageContent){
        OutputMessage messageSent = new OutputMessage(messageType, messageContent);
        messages.push(messageSent);
        adapter.notifyItemInserted(messages.size());
        myRecyclerView.scrollToPosition(messages.size()-1);
        return messageSent.getDate();
    }

    public void messageGeneration(int messageType, String messageContent, String messageTime){
        OutputMessage messageSent = new OutputMessage(messageType, messageContent, messageTime);
        messages.push(messageSent);
    }

    private void upload() {
        login.mySharedPreference = getActivity().getSharedPreferences("userId", Context.MODE_PRIVATE);
        customerid = login.mySharedPreference.getInt("userId", 1);

        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        String createTableUrl = "https://studev.groept.be/api/a23PT110/billinf/" + customerid  + "/" +shop + "/" + day + "/" + month + "/" + year + "/" + itemname + "/" + amount + "/" + price;
        StringRequest createTableRequest = new StringRequest(
                Request.Method.POST,
                createTableUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }
        ) { //NOTE THIS PART: here we are passing the POST parameters to the webservice
            @Override
            protected Map<String, String> getParams() {
                /* Map<String, String> with key value pairs as data load */
                return getPostParameters();
            }
        };
        requestQueue.add(createTableRequest);

    }
    public Map<String, String> getPostParameters () {
        Map<String, String> params = new HashMap<>();// Replace with your actual URL
        params.put("customerid", String.valueOf(customerid));
        params.put("shop", shop);
        params.put("day", day);
        params.put("month", month);
        params.put("abc", year);
        params.put("amount", amount);
        params.put("itemname", itemname);
        params.put("price", price);
        return params;
    }

    public void waitFor(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static String[] extractItemAndPrice(String text) {
        // Define regular expressions to extract itemname and price
        String itemNameRegex = "bought\\s(.*?)\\s⨯";
        String priceRegex = "cost\\s(.*?)\\s€";

        // Create pattern objects
        Pattern itemNamePattern = Pattern.compile(itemNameRegex);
        Pattern pricePattern = Pattern.compile(priceRegex);

        // Create matcher objects
        Matcher itemNameMatcher = itemNamePattern.matcher(text);
        Matcher priceMatcher = pricePattern.matcher(text);

        String itemName = null;
        String price = null;

        // Find item name
        if (itemNameMatcher.find()) {
            itemName = itemNameMatcher.group(1); // Group 1 captures the content inside the parentheses
        }

        // Find price
        if (priceMatcher.find()) {
            price = priceMatcher.group(1); // Group 1 captures the content inside the parentheses
        }

        return new String[]{itemName, price};
    }

    public void delete() {

        RequestQueue requestQueue1 = Volley.newRequestQueue(requireContext());
        String createTableUrl = "https://studev.groept.be/api/a23PT110/delete_item/" + deleted_itemname  + "/" +deleted_price;
        StringRequest createTableRequest = new StringRequest(
//                Request.Method.POST,
                createTableUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println("");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("");
                    }
                }
        ) { //NOTE THIS PART: here we are passing the POST parameters to the webservice
            @Override
            protected Map<String, String> getParams() {
                /* Map<String, String> with key value pairs as data load */
                return getdeletedPostParameters();
            }
        };
        requestQueue1.add(createTableRequest);

    }
    public Map<String, String> getdeletedPostParameters () {
        Map<String, String> params = new HashMap<>();// Replace with your actual URL
        params.put("deleted_itemname", deleted_itemname);
        params.put("deleted_price", deleted_price);
        return params;
    }

    public static String removeTrailingZeros(String text) {
        // Convert the string to a floating-point number
        double number = Double.parseDouble(text);

        // Convert the floating-point number to an integer if it has no decimal places
        int intValue = (int) number;

        // If the integer value equals the original number, return it as a string
        if (intValue == number) {
            return String.valueOf(intValue);
        }

        // Otherwise, return the original string
        return text;
    }

    public void addHistory(String content, String time, int type){
        login.mySharedPreference = getActivity().getSharedPreferences("chat_history_size", Context.MODE_PRIVATE);
        int size = login.mySharedPreference.getInt("chat_history_size",0);
        size++;
        login.mySharedPreference.edit().putInt("chat_history_size",size).commit();

        login.mySharedPreference = getActivity().getSharedPreferences("chat_history_content", Context.MODE_PRIVATE);
        login.mySharedPreference.edit().putString("chat_history_content"+size, content).commit();

        login.mySharedPreference = getActivity().getSharedPreferences("chat_history_time", Context.MODE_PRIVATE);
        login.mySharedPreference.edit().putString("chat_history_time"+size, time).commit();

        login.mySharedPreference = getActivity().getSharedPreferences("chat_history_type", Context.MODE_PRIVATE);
        login.mySharedPreference.edit().putInt("chat_history_type"+size, type).commit();
    }
}