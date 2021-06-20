package com.example.hw06;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class NoteListFragment extends Fragment {
    private List<NoteEntity> noteEntityList;

    private void addNoteToList(LinearLayout layout, NoteEntity noteEntity) {
        Button button = new Button(getContext());
        button.setText(noteEntity.toString());
        layout.addView(button);
        initPopupMenu(button, noteEntity);
    }

    public static NoteListFragment getInstance(List<NoteEntity> noteEntityList) {
        NoteListFragment noteListFragment = new NoteListFragment();
        //храним ссылку непосредственно на список в MainActivity
        noteListFragment.noteEntityList = noteEntityList;
        return noteListFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_note_list, null);
        return view;
    }

    private void initPopupMenu(View view, NoteEntity noteEntity) {
        view.setOnClickListener(v -> {
            Activity activity = requireActivity();
            PopupMenu popupMenu = new PopupMenu(activity, v);
            activity.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(item -> {
                int id = item.getItemId();
                switch (id) {
                    case R.id.menu_change:
                        ((Controller) getActivity()).openNoteScreen(noteEntity);
                        return true;
                    case R.id.menu_delete:
                        ((Controller) getActivity()).deleteNote(noteEntity);
                        return true;
                }
                return true;
            });
            popupMenu.show();
        });
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (!(context instanceof Controller)) {
            throw new RuntimeException("Activity must implement NoteListFragment.Controller");
        }
        if (noteEntityList == null) {
            noteEntityList = (List<NoteEntity>) DataHolder.getInstance().getData(Key.NOTE_LIST);
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        for (NoteEntity noteEntity : noteEntityList) {
            addNoteToList((LinearLayout) view.findViewById(R.id.linear), noteEntity);
        }
    }

    interface Controller {
        void openNoteScreen(NoteEntity noteEntity);
        void deleteNote(NoteEntity noteEntity);
    }
}
