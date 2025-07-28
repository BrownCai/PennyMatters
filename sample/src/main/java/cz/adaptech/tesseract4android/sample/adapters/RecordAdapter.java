package cz.adaptech.tesseract4android.sample.adapters;

import android.content.Context;
import android.graphics.Paint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Stack;

import cz.adaptech.tesseract4android.sample.R;

public class RecordAdapter extends RecyclerView.Adapter {
    private Context context;
    public static final int MESSAGE_SENT = 0;
    public static final int MESSAGE_RECEIVED = 1;
    public static final int MESSAGE_DELETED = 2;
    public static final int MESSAGE_DIALOG = 3;
    private Stack<OutputMessage> myMessages;
    private static OnItemClickListener cancelBtListener;

    public RecordAdapter(Context context, Stack<OutputMessage> messages){
        this.context = context;
        this.myMessages = messages;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType==MESSAGE_SENT){
            view = View.inflate(context, R.layout.message_sent, null);
            return new messageSentHolder(view);
        } else if (viewType==MESSAGE_RECEIVED) {
            view = View.inflate(context, R.layout.message_received, null);
            return new messageReceivedHolder(view);
        } else if (viewType==MESSAGE_DELETED) {
            view = View.inflate(context, R.layout.message_deleted, null);
            return new messageDeletedHolder(view);
        } else if (viewType==MESSAGE_DIALOG) {
            view = View.inflate(context, R.layout.message_dialog, null);
            return new messageDialogHolder(view);
        } else {
            return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int messageType = holder.getItemViewType();
        if (messageType==0){
            messageSentHolder mySentHolder = (messageSentHolder) holder;
            mySentHolder.setData(myMessages.get(position));
        } else if (messageType==2) {
            messageDeletedHolder myDeletedHolder = (messageDeletedHolder) holder;
            myDeletedHolder.setData(myMessages.get(position));
        } else if (messageType==3) {
            messageDialogHolder myDeletedHolder = (messageDialogHolder) holder;
            myDeletedHolder.setData(myMessages.get(position));
        } else {
            messageReceivedHolder myReceivedHolder = (messageReceivedHolder) holder;
            myReceivedHolder.setData(myMessages.get(position));
        }
    }

    @Override
    public int getItemCount() {
        if (myMessages != null){
            return myMessages.size();
        }
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        OutputMessage message = myMessages.get(position);
        if (message.getMessageType()==0){
            return MESSAGE_SENT;
        } else if (message.getMessageType()==1) {
            return MESSAGE_RECEIVED;
        } else if (message.getMessageType()==2) {
            return MESSAGE_DELETED;
        } else if (message.getMessageType()==3) {
            return MESSAGE_DIALOG;
        } else {
            return MESSAGE_RECEIVED;
        }
    }

    protected class messageSentHolder extends RecyclerView.ViewHolder{
        private TextView textSent;
        private TextView timeSent;
        public messageSentHolder(@NonNull View itemView) {
            super(itemView);
            textSent = itemView.findViewById(R.id.text_message_sent);
            timeSent = itemView.findViewById(R.id.text_time_sent);
        }

        public void setData(OutputMessage message){
            textSent.setText(message.getItemAndPrice());
            timeSent.setText(message.getInputTime());
        }
    }

    protected class messageReceivedHolder extends RecyclerView.ViewHolder{
        private TextView textReceived;
        private TextView timeReceived;
        public messageReceivedHolder(@NonNull View itemView) {
            super(itemView);
            textReceived = itemView.findViewById(R.id.text_message_received);
            timeReceived = itemView.findViewById(R.id.text_time_received);
            ImageView cancelBt = itemView.findViewById(R.id.cancel_button);
            cancelBt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (cancelBtListener != null){
                        cancelBtListener.OnItemClick(getAdapterPosition());
                    }
                }
            });
        }

        public void setData(OutputMessage message){
            textReceived.setText(message.getItemAndPrice()+"         ");
            timeReceived.setText(message.getInputTime());
        }
    }

    protected class messageDeletedHolder extends RecyclerView.ViewHolder{
        private TextView textDeleted;
        private TextView timeDeleted;
        public messageDeletedHolder(@NonNull View itemView) {
            super(itemView);
            textDeleted = itemView.findViewById(R.id.text_message_deleted);
            timeDeleted = itemView.findViewById(R.id.text_time_deleted);
        }

        public void setData(OutputMessage message){
            textDeleted.setText(message.getItemAndPrice());
            textDeleted.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            timeDeleted.setText(message.getInputTime());
        }
    }

    protected class messageDialogHolder extends RecyclerView.ViewHolder{
        private TextView textSent;
        private TextView timeSent;
        public messageDialogHolder(@NonNull View itemView) {
            super(itemView);
            textSent = itemView.findViewById(R.id.text_message_dialog);
            timeSent = itemView.findViewById(R.id.text_time_dialog);
        }

        public void setData(OutputMessage message){
            textSent.setText(message.getItemAndPrice());
            timeSent.setText(message.getInputTime());
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        cancelBtListener = listener;
    }

    public interface OnItemClickListener {
        void OnItemClick(int position);
    }
}
