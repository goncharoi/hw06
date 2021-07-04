package com.example.hw06;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;

public class NoteAdapter extends ListAdapter<NoteEntity, NoteAdapter.NoteViewHolder> {
    protected NoteAdapter(@NonNull DiffUtil.ItemCallback<NoteEntity> diffCallback) {
        super(diffCallback);
    }

    private OnItemClickListener onItemClickListener;
    private ViewGroup parent;

    interface OnItemClickListener {
        void openNoteScreen(NoteEntity noteEntity);

        void deleteNote(NoteEntity noteEntity);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public NoteAdapter.NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.parent = parent;
        return new NoteViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.card, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
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

    public class NoteViewHolder extends RecyclerView.ViewHolder {
        private final TextView titleTw;
        private final TextView deadlineTv;
        private NoteEntity noteEntity;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTw = itemView.findViewById(R.id.note_card_title);
            deadlineTv = itemView.findViewById(R.id.note_card_deadline);
            itemView.setOnClickListener(v -> {
                if (onItemClickListener != null) {
                    PopupMenu popupMenu = new PopupMenu(itemView.getContext(), v);
                    ((Activity) itemView.getContext()).getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());
                    popupMenu.setOnMenuItemClickListener(item -> {
                        int id = item.getItemId();
                        switch (id) {
                            case R.id.menu_change:
                                onItemClickListener.openNoteScreen(noteEntity);
                                return true;
                            case R.id.menu_delete:
                                onItemClickListener.deleteNote(noteEntity);
                                return true;
                        }
                        return true;
                    });
                    popupMenu.show();
                }
            });
        }

        public void bind(NoteEntity noteEntity) {
            this.noteEntity = noteEntity;
            titleTw.setText(noteEntity.getTitle());
            deadlineTv.setText(String.format("%s: %s",
                    ((Activity) parent.getContext()).getString(R.string.note_item_deadline_text),
                    new SimpleDateFormat(((Activity) parent.getContext()).getString(R.string.default_date_format))
                            .format(noteEntity.getDeadline())
            ));

        }
    }
}
