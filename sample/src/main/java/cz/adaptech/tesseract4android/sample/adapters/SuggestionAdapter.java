package cz.adaptech.tesseract4android.sample.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import cz.adaptech.tesseract4android.sample.R;

public class SuggestionAdapter extends RecyclerView.Adapter {
    private Context context;
    private Stack<SuggestionResult> suggestions;
    private OnItemClickListener mOnItemClickListener;

    public SuggestionAdapter(Context context, Stack<SuggestionResult> suggestions){
        this.context = context;
        this.suggestions = suggestions;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = View.inflate(context, R.layout.suggestion_result, null);
        return new SuggestionHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        SuggestionHolder suggestionHolder = (SuggestionHolder) holder;
        suggestionHolder.setData(suggestions.get(position), position);
    }

    @Override
    public int getItemCount() {
        if (suggestions != null){
            return suggestions.size();
        }
        return 0;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }


    // set Callback interface
    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    protected class SuggestionHolder extends RecyclerView.ViewHolder{
        private TextView priceText;
        private TextView nameText;
        private TextView shopText;
        private TextView labelText;
        private TextView dateText;
        private int position;

        public SuggestionHolder(@NonNull View itemView) {
            super(itemView);
            priceText = itemView.findViewById(R.id.price_tv);
            nameText = itemView.findViewById(R.id.name_tv);
            shopText = itemView.findViewById(R.id.shop_tv);
            labelText = itemView.findViewById(R.id.label_tv);
            dateText = itemView.findViewById(R.id.date_tv);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnItemClickListener != null){
                        mOnItemClickListener.onItemClick(position);
                    }
                }
            });
        }

        public void setData(SuggestionResult suggestion, int position){
            this.position = position;
            float unitPrice = ((float)suggestion.getPrice()) /((float)suggestion.getAmount());
            String unitPriceText = String.format("%.1f",unitPrice)+"€";
            priceText.setText(unitPriceText);
            nameText.setText(suggestion.getItemname());
            shopText.setText(suggestion.getShop());
            String dateTx = suggestion.getDay()+"/"+suggestion.getMonth()+"/"+suggestion.getYear();
            dateText.setText(dateTx);
            labelText.setText(suggestion.getLabel());
        }
    }
}
