package cz.adaptech.tesseract4android.sample.adapters;

public class SuggestionResult {
    private String shop;
    private int customerid;
    private String day;
    private String month;
    private String year;
    private String itemname;
    private int amount;
    private int price;
    private String label;



    public SuggestionResult(String shop, String day, String month, String year, String name, int amount, int price, String label){
        this.shop = shop;
        this.day = day;
        this.month = month;
        this.year = year;
        this.itemname = name;
        this.amount = amount;
        this.price  = price;
        this.label = label;
    }

    public String getShop() {
        return shop;
    }

    public void setShop(String shop) {
        this.shop = shop;
    }

    public int getCustomerid() {
        return customerid;
    }

    public void setCustomerid(int customerid) {
        this.customerid = customerid;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getItemname() {
        return itemname;
    }

    public void setItemname(String itemname) {
        this.itemname = itemname;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
