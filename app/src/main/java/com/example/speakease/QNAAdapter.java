package com.example.speakease;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class QNAAdapter extends RecyclerView.Adapter<QNAAdapter.QNAViewHolder> {

    private List<QNAActivity.QNAModel> qnaList;
    private int expandedPosition = -1; // Keeps track of which card is expanded

    public QNAAdapter(List<QNAActivity.QNAModel> qnaList) {
        this.qnaList = qnaList;
    }

    @NonNull
    @Override
    public QNAViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_qna_card, parent, false);
        return new QNAViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QNAViewHolder holder, int position) {
        QNAActivity.QNAModel model = qnaList.get(position);

        holder.tvQuestion.setText(model.question);
        holder.tvFormalAnswer.setText(model.answerFormal);
        holder.tvCasualAnswer.setText(model.answerCasual);
        holder.tvCategoryLabel.setText(model.category);

        // Check if this card should be expanded or collapsed
        final boolean isExpanded = position == expandedPosition;
        holder.expandableLayout.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.itemView.setActivated(isExpanded);

        // Click to toggle expand/collapse
        holder.itemView.setOnClickListener(v -> {
            int currentPosition = holder.getAdapterPosition();
            if (currentPosition == RecyclerView.NO_POSITION) return;

            if (isExpanded) {
                expandedPosition = -1; // Collapse if already open
                notifyItemChanged(currentPosition);
            } else {
                int previousExpanded = expandedPosition;
                expandedPosition = currentPosition; // Expand new one
                notifyItemChanged(previousExpanded); // Collapse old one
                notifyItemChanged(expandedPosition);
            }
        });
    }

    @Override
    public int getItemCount() {
        return qnaList != null ? qnaList.size() : 0;
    }

    static class QNAViewHolder extends RecyclerView.ViewHolder {
        TextView tvQuestion, tvFormalAnswer, tvCasualAnswer, tvCategoryLabel;
        LinearLayout expandableLayout;

        public QNAViewHolder(@NonNull View itemView) {
            super(itemView);
            tvQuestion = itemView.findViewById(R.id.tvQnaQuestion);
            tvFormalAnswer = itemView.findViewById(R.id.tvQnaFormal);
            tvCasualAnswer = itemView.findViewById(R.id.tvQnaCasual);
            tvCategoryLabel = itemView.findViewById(R.id.tvQnaCategoryTag);
            expandableLayout = itemView.findViewById(R.id.layoutExpandableAnswers);
        }
    }
}