package com.example.hw06;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class ViewHolder extends RecyclerView.ViewHolder {
    private final TextView titleTw;
    private final TextView dateTw;
    private NoteEntity noteEntity;

    public ViewHolder(@NonNull View itemView, @Nullable Adapter.OnItemClickListener clickListener) {
        super(itemView);
        titleTw = itemView.findViewById(R.id.note_card_title);
        dateTw = itemView.findViewById(R.id.note_card_date);
        itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                PopupMenu popupMenu = new PopupMenu(itemView.getContext(), v);
                ((Activity) itemView.getContext()).getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(item -> {
                    int id = item.getItemId();
                    switch (id) {
                        case R.id.menu_change:
                            clickListener.openNoteScreen(noteEntity);
                            return true;
                        case R.id.menu_delete:
                            clickListener.deleteNote(noteEntity);
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
        titleTw.setText(noteEntity.getText());
        dateTw.setText(noteEntity.getCreatedOn().toString());
    }
}
