package com.example.hw06;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class NoteListFragment extends Fragment {
    private List<NoteEntity> noteEntityList;

    public static NoteListFragment getInstance(List<NoteEntity> noteEntityList) {
        NoteListFragment noteListFragment = new NoteListFragment();
        //храним ссылку непосредственно на список в MainActivity
        noteListFragment.noteEntityList = noteEntityList;
        return noteListFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_note_list, container, false);
        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
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
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        Adapter adapter = new Adapter(new Adapter.NoteDiff());
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(
                new Adapter.OnItemClickListener() {
                    @Override
                    public void openNoteScreen(NoteEntity noteEntity) {
                        getController().openNoteScreen(noteEntity);
                    }

                    @Override
                    public void deleteNote(NoteEntity noteEntity) {
                        getController().deleteNote(noteEntity);
                    }
                }
        );
        adapter.submitList(noteEntityList);
    }

    private Controller getController() {
        return (Controller) getActivity();
    }

    interface Controller {
        void openNoteScreen(NoteEntity noteEntity);

        void deleteNote(NoteEntity noteEntity);
    }
}
