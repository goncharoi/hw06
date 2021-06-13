package com.example.hw06;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;

import java.util.List;
import java.util.ListIterator;

public class NoteListFragment extends Fragment {
    private List<NoteEntity> noteEntityList;
    private LinearLayout linearLayout;

    private void addNoteToList(NoteEntity noteEntity) {
        Button button = new Button(getContext());
        button.setText(noteEntity.toString());
        button.setOnClickListener(v -> ((Controller) getActivity()).openNoteScreen(noteEntity));
        linearLayout.addView(button);
    }

    public static NoteListFragment newInstance(List<NoteEntity> noteEntityList) {
        NoteListFragment noteListFragment = new NoteListFragment();
        noteListFragment.noteEntityList = noteEntityList;
        return noteListFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_note_list, null);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (!(context instanceof Controller)) {
            throw new RuntimeException("Activity must implement NoteListFragment.Controller");
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        linearLayout = view.findViewById(R.id.linear);

        for (NoteEntity noteEntity : noteEntityList) {
            addNoteToList(noteEntity);
        }

        view.findViewById(R.id.go_to_calc).setOnClickListener(v -> {
            Uri address = Uri.parse("calculator://intent");
            Intent loCalculatorIntent = new Intent(Intent.ACTION_VIEW, address);
            startActivity(loCalculatorIntent);
        });
    }

    interface Controller {
        void openNoteScreen(NoteEntity dossier);
    }
}
