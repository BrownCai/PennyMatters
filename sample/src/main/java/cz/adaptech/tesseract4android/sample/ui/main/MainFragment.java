package cz.adaptech.tesseract4android.sample.ui.main;


import static cz.adaptech.tesseract4android.sample.bottomNavigationUi.recordFragment.messageGeneration;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.adaptech.tesseract4android.sample.Assets;
import cz.adaptech.tesseract4android.sample.Config;
import cz.adaptech.tesseract4android.sample.MainActivity;
import cz.adaptech.tesseract4android.sample.R;
import cz.adaptech.tesseract4android.sample.adapters.RecordAdapter;
import cz.adaptech.tesseract4android.sample.bottomNavigationUi.bottomNavigationPage;
import cz.adaptech.tesseract4android.sample.databinding.FragmentMainBinding;
import cz.adaptech.tesseract4android.sample.preparationPages.login;

public class MainFragment extends Fragment {

    private FragmentMainBinding binding;
    private MainViewModel viewModel;

    private static final int PICK_IMAGE = 100;
    private static final int REQUEST_EXTERNAL_STORAGE_PERMISSION = 123;

    private File newImageFile;

    private int customerid;
    private String shop;
    private String day;

    private String month;
    private String year;
    private String itemname;
    private String amount;
    private String price;

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        Assets.extractAssets(requireContext());

        if (!viewModel.isInitialized()) {
            String dataPath = Assets.getTessDataPath(requireContext());
            viewModel.initTesseract(dataPath, Config.TESS_LANG, Config.TESS_ENGINE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentMainBinding.inflate(inflater, container, false);
        binding.getRoot().findViewById(R.id.back_to_record).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });

        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.image.setImageBitmap(Assets.getImageBitmap(requireContext()));

        binding.start.setOnClickListener(v -> {
            Log.d("TAG", String.valueOf(requireContext()));
            File imageFile = Assets.getImageFile(requireContext());
            viewModel.recognizeImage(newImageFile);
//            viewModel.recognizeImage(imageFile);
        });

        binding.stop.setOnClickListener(v -> {
            viewModel.stop();
        });

        binding.text.setMovementMethod(new ScrollingMovementMethod());

        binding.importButton.setOnClickListener(v -> {
            openGallery();
        });

        viewModel.getProcessing().observe(getViewLifecycleOwner(), processing -> {
            binding.start.setEnabled(!processing);
            binding.stop.setEnabled(processing);
        });
        viewModel.getProgress().observe(getViewLifecycleOwner(), progress -> {
            binding.status.setText(progress);
        });
        viewModel.getResult().observe(getViewLifecycleOwner(), result -> {
            displayItemNamesAndPrices(result);
//            upload();
            Log.d("result", result);
//            binding.text.setText(result);
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == AppCompatActivity.RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            try {
                Bitmap selectedImageBitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), selectedImageUri);
                if (selectedImageBitmap != null) {
                    binding.image.setImageBitmap(null);
                    binding.image.setImageBitmap(selectedImageBitmap);
//                    path = getRealPathFromUri(selectedImageUri);
//                    Log.d("path", path);
                    newImageFile = FileUtil.uriToFile(requireContext(), selectedImageUri);
                    // Check and request permission to access external storage
                    checkPermissionAndAccessFile();
                } else {
                    Log.e("MainFragment", "Failed to load selected image bitmap.");
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("MainFragment", "Error loading selected image: " + e.getMessage());
            }
        } else {
            Log.e("MainFragment", "Failed to pick image from gallery.");
        }
    }

    private void displayItemNamesAndPrices(String result) {
        // Split the input text by lines
        String[] lines = result.split("\\n");
        // Variables to store extracted information
        shop = "";
        String customer = "";
        String invoiceDate = "";
        double totalPrice = 0.0;
        // Extract shop name


        // Extract invoice number
        for (String line : lines) {
            if (line.contains("INVOICE #")) {
                int startIndex = line.indexOf("INVOICE #") + "INVOICE #".length();
                shop = line.substring(startIndex).trim();
                // Now shop contains "SPAR"
                break;
            }
        }

        // Extract customer name
        if (lines.length > 1) {
            String[] customerWords = lines[1].trim().split("\\s+");
            if (customerWords.length > 1 && customerWords[0].matches("[A-Za-z]+")) {
                customer = customerWords[0] + " " + customerWords[1];
                customerid = 1;
            }
        }

        // Extract invoice date
        for (String line : lines) {
            if (line.contains("INVOICE DATE")) {
                String[] parts = line.split("INVOICE DATE");
                if (parts.length > 1) {
                    invoiceDate = parts[1].trim();
                    String[] dateParts = invoiceDate.split("/");
                    day = dateParts[0];
                    month = dateParts[1];
                    year = dateParts[2];
                }
                break;
            }
        }

        // Build the formatted receipt string
        StringBuilder receiptBuilder = new StringBuilder();
        receiptBuilder.append("Shop: ").append(shop).append("\n");
//        receiptBuilder.append("Invoice Number: ").append(invoiceNumber).append("\n");
        receiptBuilder.append("Customer: ").append(customer).append("\n");
        receiptBuilder.append("Date: ").append(invoiceDate).append("\n\n");
        receiptBuilder.append("ITEM\tAMOUNT\tPRICE\n");

        // Extract item details and total price

        boolean foundDescription = false;
        boolean foundFirstItem = false;
        for (String line : lines) {
            if (foundDescription && line.contains("@")) {
                String[] parts = line.trim().split("\\s+");
                String[] itemNameAndAmount = parts[0].split("\\@");
                itemname = String.valueOf(itemNameAndAmount[0]);
                amount = itemNameAndAmount[1];
                price = parts[1];
                receiptBuilder.append(itemname).append("\t").append(amount).append("\t").append(price).append("\n");
                upload();
                String text = "From " + shop + ", bought " + itemname + " \n⨯" + amount + " cost " + price + " €";
                Date date1 = messageGeneration(RecordAdapter.MESSAGE_RECEIVED, text);
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy   HH:mm:ss");
                String inputTime = formatter.format(date1);

                //Call notification
                Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                Ringtone ring = RingtoneManager.getRingtone(getContext(), notification);
                ring.play();
                //Call vibration
//                bottomNavigationPage.vibrator.cancel();
                bottomNavigationPage.vibrator.vibrate(new long[]{100, 400, 200, 400}, -1);

                // put into chatting history
                addHistory(text, inputTime, RecordAdapter.MESSAGE_DIALOG);

                waitFor(50);
            } else if (line.contains("DESCRIPTION AMOUNT")) {
                foundDescription = true;
            } else if (line.startsWith("TOTAL")) {
                String totalLine = line.replace("TOTAL", "").trim();
                try {
                    totalPrice = Double.parseDouble(totalLine.replaceAll("[^\\d.]", ""));
//                    price = String.valueOf(totalPrice) ;

//                    upload();
                } catch (NumberFormatException ignored) {
                }
                break;
            }
        }


        // Append the total at the end of the receipt
        receiptBuilder.append("\nTOTAL: ").append(totalPrice).append("€");

        // Check if binding.text is not null before setting the text
        if (binding != null && binding.text != null) {
            binding.text.setText(receiptBuilder.toString());
        } else {
            Log.e("MainFragment", "Binding or TextView is null");
        }
    }


    private String getRealPathFromUri(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = requireActivity().getContentResolver().query(uri, projection, null, null, null);
        if (cursor == null) {
            return uri.getPath();
        } else {
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String realPath = cursor.getString(columnIndex);
            cursor.close();
            return realPath;
        }
    }

    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }

    private void checkPermissionAndAccessFile() {
        // Check for permission to access external storage
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request it
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_EXTERNAL_STORAGE_PERMISSION);
        } else {
            // Permission already granted, proceed with file access
            accessFile();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_EXTERNAL_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with file access
                accessFile();
            } else {
                // Permission denied, show a message or handle it accordingly
                Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void accessFile() {
        // File path in external storage
        String filePath = "/storage/emulated/0/ap-110/test.jpg";
    }


    private void upload() {
        login.mySharedPreference = getActivity().getSharedPreferences("userId", Context.MODE_PRIVATE);
        customerid = login.mySharedPreference.getInt("userId", 1);

        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        String createTableUrl = "https://studev.groept.be/api/a23PT110/billinf/" + customerid + "/" + shop + "/" + day + "/" + month + "/" + year + "/" + itemname + "/" + amount + "/" + price;
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

    public Map<String, String> getPostParameters() {
        Map<String, String> params = new HashMap<>();// Replace with your actual URL
        params.put("customerid", String.valueOf(customerid));
        params.put("shop", shop);
        params.put("day", day);
        params.put("month", month);
        params.put("year", year);
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

    public void addHistory(String content, String time, int type) {
        login.mySharedPreference = getActivity().getSharedPreferences("chat_history_size", Context.MODE_PRIVATE);
        int size = login.mySharedPreference.getInt("chat_history_size", 0);
        size++;
        login.mySharedPreference.edit().putInt("chat_history_size", size).commit();

        login.mySharedPreference = getActivity().getSharedPreferences("chat_history_content", Context.MODE_PRIVATE);
        login.mySharedPreference.edit().putString("chat_history_content" + size, content).commit();

        login.mySharedPreference = getActivity().getSharedPreferences("chat_history_time", Context.MODE_PRIVATE);
        login.mySharedPreference.edit().putString("chat_history_time" + size, time).commit();

        login.mySharedPreference = getActivity().getSharedPreferences("chat_history_type", Context.MODE_PRIVATE);
        login.mySharedPreference.edit().putInt("chat_history_type" + size, type).commit();
    }
}
