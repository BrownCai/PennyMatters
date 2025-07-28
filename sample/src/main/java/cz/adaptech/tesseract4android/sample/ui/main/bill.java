package cz.adaptech.tesseract4android.sample.ui.main;

public class bill {

    private int customerid;
    private String day;
    private String month;
    private String year;
    private String itemname;
    private int amount;
    private int price;
    private String label;


    public bill(int customerid, String day, String month, String year, String itemname, int amount, int price, String label) {
        this.customerid = customerid;
        this.day = day;
        this.month = month;
        this.year = year;
        this.itemname = itemname;
        this.amount = amount;
        this.price = price;
        this.label = label;
    }

    public int getCustomerid() {
        return customerid;
    }

    public String getDay() {
        return day;
    }

    public String getMonth() {
        return month;
    }

    public String getYear() {
        return year;
    }

    public String getItemname() {
        return itemname;
    }

    public int getAmount() {
        return amount;
    }

    public int getPrice() {
        return price;
    }

    public String getLabel() {
        return label;
    }
}
