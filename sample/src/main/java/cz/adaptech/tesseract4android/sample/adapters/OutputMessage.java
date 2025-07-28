package cz.adaptech.tesseract4android.sample.adapters;

import java.text.SimpleDateFormat;
import java.util.Date;

public class OutputMessage {

    private int messageType;
    private String itemAndPrice;
    private String inputTime;
    private Date date;

    public OutputMessage(int type, String messageContent){
        messageType = type;
        itemAndPrice = messageContent;
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy   HH:mm:ss");
        date = new Date(System.currentTimeMillis());
        inputTime = formatter.format(date);

    }

    public OutputMessage(int type, String messageContent, String inputTime){
        this.messageType = type;
        this.itemAndPrice = messageContent;
        this.inputTime = inputTime;
    }

    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int type) {
        this.messageType = type;
    }

    public String getItemAndPrice() {
        return itemAndPrice;
    }

    public void setItemAndPrice(String itemAndPrice) {
        this.itemAndPrice = itemAndPrice;
    }

    public String getInputTime() {
        return inputTime;
    }

    public void setInputTime(String inputTime) {
        this.inputTime = inputTime;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
