package com.example.speakease;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class GrammarAdapter extends RecyclerView.Adapter<GrammarAdapter.GrammarViewHolder> {

    // Correctly referencing the static inner class from the Activity
    private List<GrammarBasicsActivity.GrammarCard> grammarCards;

    public GrammarAdapter(List<GrammarBasicsActivity.GrammarCard> grammarCards) {
        this.grammarCards = grammarCards;
    }

    @NonNull
    @Override
    public GrammarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_grammar_card, parent, false);
        return new GrammarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GrammarViewHolder holder, int position) {
        GrammarBasicsActivity.GrammarCard card = grammarCards.get(position);
        holder.tvTitle.setText(card.title);
        holder.tvDesc.setText(card.description);
        holder.tvFormula.setText(card.formula);
        holder.tvExample.setText(card.example);
    }

    @Override
    public int getItemCount() {
        return grammarCards != null ? grammarCards.size() : 0;
    }

    static class GrammarViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDesc, tvFormula, tvExample;

        public GrammarViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvCardTitle);
            tvDesc = itemView.findViewById(R.id.tvCardDesc);
            tvFormula = itemView.findViewById(R.id.tvCardFormula);
            tvExample = itemView.findViewById(R.id.tvCardExample);
        }
    }
}