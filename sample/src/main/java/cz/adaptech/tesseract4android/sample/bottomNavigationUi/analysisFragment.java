package cz.adaptech.tesseract4android.sample.bottomNavigationUi;

import static cz.adaptech.tesseract4android.sample.preparationPages.login.mySharedPreference;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.anychart.APIlib;
import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.charts.Pie;
import com.anychart.core.cartesian.series.Column;
import com.anychart.core.cartesian.series.Line;
import com.anychart.data.Mapping;
import com.anychart.data.Set;
import com.anychart.enums.Anchor;
import com.anychart.enums.HoverMode;
import com.anychart.enums.MarkerType;
import com.anychart.enums.Position;
import com.anychart.enums.TooltipPositionMode;
import com.anychart.graphics.vector.Stroke;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.adaptech.tesseract4android.sample.R;
//import cz.adaptech.tesseract4android.sample.pie_Activity;
import cz.adaptech.tesseract4android.sample.preparationPages.login;
import cz.adaptech.tesseract4android.sample.ui.main.bill;
import cz.adaptech.tesseract4android.sample.preparationPages.login;
import android.content.SharedPreferences;

public class analysisFragment extends Fragment {
    private View root;
    private List<bill> bills = new ArrayList<>();
    private SwipeRefreshLayout swipeRefreshLayout;

    private int login_customerid;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (root == null){
            root = inflater.inflate(R.layout.activity_pie, container, false);
        }
        FragmentActivity c = this.getActivity();
        swipeRefreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.swipe_refresh_layout);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
        refresh();
        mySharedPreference = getActivity().getSharedPreferences("userId", Context.MODE_PRIVATE);
        login_customerid = login.mySharedPreference.getInt("userId", 1);
        Log.i("login_userid", ""+login_customerid);
        return root;
    }
    public void drawLinechart() {
        AnyChartView lineChartView = getActivity().findViewById(R.id.line_chart_view);
        APIlib.getInstance().setActiveAnyChartView(lineChartView);
        Cartesian cartesian = AnyChart.line();
        cartesian.animation(true);
        cartesian.padding(10d, 20d, 5d, 20d);
        cartesian.crosshair().enabled(true);
        cartesian.crosshair()
                .yLabel(true)
                .yStroke((Stroke) null, null, null, (String) null, (String) null);
        cartesian.tooltip().positionMode(TooltipPositionMode.POINT);
        cartesian.title("Expense for everyday for lastest three months");
        cartesian.yAxis(0).title("Expense for each day (€)");
        cartesian.xAxis(0).labels().padding(5d, 5d, 5d, 5d);
        List<DataEntry> seriesData = new ArrayList<>();
        updateSeriesData(bills,seriesData);
        Set set = Set.instantiate();
        set.data(seriesData);
        Mapping series1Mapping = set.mapAs("{ x: 'x', value: 'value' }");
        Mapping series2Mapping = set.mapAs("{ x: 'x', value: 'value2' }");
        Mapping series3Mapping = set.mapAs("{ x: 'x', value: 'value3' }");
        Line series1 = cartesian.line(series1Mapping);
        series1.name("Apr");
        series1.hovered().markers().enabled(true);
        series1.hovered().markers()
                .type(MarkerType.CIRCLE)
                .size(4d);
        series1.tooltip()
                .position("right")
                .anchor(Anchor.LEFT_CENTER)
                .offsetX(5d)
                .offsetY(5d);
        Line series2 = cartesian.line(series2Mapping);
        series2.name("May");
        series2.hovered().markers().enabled(true);
        series2.hovered().markers()
                .type(MarkerType.CIRCLE)
                .size(4d);
        series2.tooltip()
                .position("right")
                .anchor(Anchor.LEFT_CENTER)
                .offsetX(5d)
                .offsetY(5d);
        Line series3 = cartesian.line(series3Mapping);
        series3.name("Jun");
        series3.hovered().markers().enabled(true);
        series3.hovered().markers()
                .type(MarkerType.CIRCLE)
                .size(4d);
        series3.tooltip()
                .position("right")
                .anchor(Anchor.LEFT_CENTER)
                .offsetX(5d)
                .offsetY(5d);
        cartesian.legend().enabled(true);
        cartesian.legend().fontSize(13d);
        cartesian.legend().padding(0d, 0d, 10d, 0d);
        lineChartView.setChart(cartesian);
    }

    public void drawColumn_chart(){
        AnyChartView anyChartView = getActivity().findViewById(R.id.bar_chart_view);
//        anyChartView.setProgressBar(findViewById(R.id.progress_bar));
        APIlib.getInstance().setActiveAnyChartView(anyChartView);
        Cartesian cartesian = AnyChart.column();
        List<DataEntry> data = new ArrayList<>();
        String JanLabel = "01";
        String FebLabel = "02";
        String MarLabel = "03";
        String AprLabel = "04";
        String MayLabel = "05";
        String JunLabel = "06";
        int Janprice = 0;
        int Febprice = 0;
        int Marprice = 0;
        int Aprprice = 0;
        int Mayprice = 0;
        int Junprice = 0;
        for (bill itembill:bills){
            String label = itembill.getMonth();
            int price = itembill.getPrice();
            if (label.equals(JanLabel)){
                Janprice += price;
            } else if (label.equals(FebLabel) ){
                Febprice += price;
            }else if (label.equals(MarLabel)) {
                Marprice += price;
            }
            else if (label.equals(AprLabel)) {
                Aprprice += price;
            }
            else if (label.equals(MayLabel)) {
                Mayprice += price;
            }
            else if (label.equals(JunLabel)) {
                Junprice += price;
            }
        }
        data.add(new ValueDataEntry(JanLabel, Janprice));
        data.add(new ValueDataEntry(FebLabel, Febprice));
        data.add(new ValueDataEntry(MarLabel, Marprice));
        data.add(new ValueDataEntry(AprLabel, Aprprice));
        data.add(new ValueDataEntry(MayLabel, Mayprice));
        data.add(new ValueDataEntry(JunLabel, Junprice));

        Column column = cartesian.column(data);

        column.tooltip()
                .titleFormat("{%X}")
                .position(Position.CENTER_BOTTOM)
                .anchor(Anchor.CENTER_BOTTOM)
                .offsetX(0d)
                .offsetY(5d)
                .format("${%Value}{groupsSeparator: }");

        cartesian.animation(true);
        cartesian.title("Consumption situation in the past six months");

        cartesian.yScale().minimum(0d);

        cartesian.yAxis(0).labels().format("${%Value}{groupsSeparator: }");

        cartesian.tooltip().positionMode(TooltipPositionMode.POINT);
        cartesian.interactivity().hoverMode(HoverMode.BY_X);

        cartesian.xAxis(0).title("Month");
        cartesian.yAxis(0).title("Price");

        anyChartView.setChart(cartesian);
    }
    public void drawBar_chart() {
        AnyChartView anyChartView = (AnyChartView) getActivity().findViewById(R.id.any_chart_view);
        APIlib.getInstance().setActiveAnyChartView(anyChartView);
        Pie pie = AnyChart.pie();
        List<DataEntry> data = new ArrayList<>();
        String edulabel = "education";
        String lifelabel = "furniture";
        String foodlabel = "food";
        int eduprice = 0;
        int lifeprice = 0;
        int foodprice = 0;
        for (bill itembill:bills){
            String label = itembill.getLabel();
            int price = itembill.getPrice();
            if (label.equals(edulabel)){
                eduprice += price;
            } else if (label.equals(lifelabel) ){
                lifeprice += price;
            }else if (label.equals(foodlabel)) {
                foodprice += price;
            }
        }
        data.add(new ValueDataEntry("Edu", eduprice));
        data.add(new ValueDataEntry("Furn", lifeprice));
        data.add(new ValueDataEntry("Food", foodprice));
        pie.title("Consumption in  different area in half year");
        pie.data(data);
        pie.animation(true);
        anyChartView.setChart(pie);
    }
    public void refresh() {
        bills.clear();
        RequestQueue requestQueue = Volley.newRequestQueue(this.getActivity());
        String requestURL = "https://studev.groept.be/api/a23PT110/retreveInf";

        StringRequest submitRequest = new StringRequest(Request.Method.GET, requestURL,

                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray responseArray = new JSONArray(response);
                            waitFor(200);
                            String responseString = "";
                            int length = responseArray.length();

                            for (int i = 0; i < responseArray.length(); i++) {
                                JSONObject curObject = responseArray.getJSONObject(i);
                                if(login_customerid == curObject.getInt("customerid")){
                                int customerid = curObject.getInt("customerid");
                                String day = curObject.getString("day");
                                String month = curObject.getString("month");
                                String year = curObject.getString("year");
                                String itemname = curObject.getString("itemname");
                                String amount = curObject.getString("amount");
                                String price = curObject.getString("price");
                                String label = curObject.getString("label");
                                bill itembill = new bill(customerid, day, month, year, itemname, Integer.valueOf(amount), Integer.valueOf(price),label);
                                bills.add(itembill);}
                            }
                        } catch (JSONException e) {
                            Log.e("Database", e.getMessage(), e);
                        }finally {

                            swipeRefreshLayout.setRefreshing(false);
                            drawColumn_chart();
                            drawBar_chart();
                            drawLinechart();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley Error", error.toString());
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }
        );

        requestQueue.add(submitRequest);
    }
    public void waitFor(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    public void updateSeriesData(List<bill> bills, List<DataEntry> seriesData) {
        // Initialize arrays to store total amounts for each day in April, May, and June
        int[] aprilTotal = new int[31];
        int[] mayTotal = new int[31];
        int[] juneTotal = new int[31];

        // Iterate over the bills list to aggregate data
        for (bill b : bills) {
            // Extract day and amount from each bill record
            int day = Integer.parseInt(b.getDay());
            int amount = b.getPrice();

            // Update the corresponding total amount array based on the month
            switch (b.getMonth()) {
                case "04":
                    aprilTotal[day - 1] += amount; // Subtract 1 since arrays are zero-indexed
                    break;
                case "05":
                    mayTotal[day - 1] += amount;
                    break;
                case "06":
                    juneTotal[day - 1] += amount;
                    break;
                default:
                    // Ignore records for other months
                    break;
            }
        }

        // Update the seriesData list with the aggregated data
        seriesData.clear(); // Clear existing data
        for (int i = 0; i < 31; i++) {
            // Add CustomDataEntry objects for each day in April, May, and June
            seriesData.add(new CustomDataEntry(String.format("%02d", i + 1), aprilTotal[i], mayTotal[i], juneTotal[i]));
        }
    }


    private class CustomDataEntry extends ValueDataEntry {
        CustomDataEntry(String x, Number value, Number value2, Number value3) {
            super(x, value);
            setValue("value2", value2);
            setValue("value3", value3);
        }
    }

}