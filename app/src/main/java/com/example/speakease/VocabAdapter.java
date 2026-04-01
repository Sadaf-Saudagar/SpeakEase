package com.example.speakease;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class VocabAdapter extends RecyclerView.Adapter<VocabAdapter.VocabViewHolder> {

    private List<VocabActivity.VocabModel> originalList;
    private List<VocabActivity.VocabModel> filteredList;
    private OnSpeakClickListener listener;

    public interface OnSpeakClickListener { void onSpeak(String word); }

    public VocabAdapter(List<VocabActivity.VocabModel> list, OnSpeakClickListener listener) {
        this.originalList = list;
        this.filteredList = new ArrayList<>(list);
        this.listener = listener;
    }

    public void filter(String query) {
        filteredList.clear();
        if (query.isEmpty()) {
            filteredList.addAll(originalList);
        } else {
            for (VocabActivity.VocabModel item : originalList) {
                if (item.word.toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VocabViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_vocab_card, parent, false);
        return new VocabViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VocabViewHolder holder, int position) {
        VocabActivity.VocabModel model = filteredList.get(position);
        holder.word.setText(model.word);
        holder.type.setText("(" + model.type + ")");
        holder.meaning.setText(model.meaning);
        holder.synonym.setText("Synonym: " + model.synonym);
        holder.example.setText("\"" + model.example + "\"");
        holder.btnSpeak.setOnClickListener(v -> listener.onSpeak(model.word));
    }

    @Override
    public int getItemCount() { return filteredList.size(); }

    static class VocabViewHolder extends RecyclerView.ViewHolder {
        TextView word, type, meaning, synonym, example;
        ImageButton btnSpeak;
        public VocabViewHolder(View v) {
            super(v);
            word = v.findViewById(R.id.tvVocabWord);
            type = v.findViewById(R.id.tvVocabType);
            meaning = v.findViewById(R.id.tvVocabMeaning);
            synonym = v.findViewById(R.id.tvVocabSynonym);
            example = v.findViewById(R.id.tvVocabExample);
            btnSpeak = v.findViewById(R.id.btnSpeakVocab);
        }
    }
}