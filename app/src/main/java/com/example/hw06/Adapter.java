package com.example.hw06;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class Adapter extends ListAdapter<NoteEntity, ViewHolder> {
    protected Adapter(@NonNull DiffUtil.ItemCallback<NoteEntity> diffCallback) {
        super(diffCallback);
    }
    private OnItemClickListener onItemClickListener;

    interface OnItemClickListener {
        void openNoteScreen(NoteEntity noteEntity);
        void deleteNote(NoteEntity noteEntity);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.card, parent, false),
                onItemClickListener
                );
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public static class NoteDiff extends DiffUtil.ItemCallback<NoteEntity> {
        @Override
        public boolean areItemsTheSame(@NonNull NoteEntity oldItem, @NonNull NoteEntity newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull NoteEntity oldItem, @NonNull NoteEntity newItem) {
            return oldItem.getText().equals(newItem.getText())
                    && oldItem.getTitle().equals(newItem.getTitle())
                    && oldItem.getCreatedOn().equals(newItem.getCreatedOn());
        }
    }
}
