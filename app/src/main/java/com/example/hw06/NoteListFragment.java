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

    private NoteAdapter noteAdapter;

    private static final String KEY_NOTE_LIST = "NOTE_LIST"; //Ключи для хранения данных

    public static void putData(List<NoteEntity> noteEntityList){
        DataHolder.getInstance().putData(NoteListFragment.KEY_NOTE_LIST, noteEntityList);
    }

    public static List<NoteEntity> getData(){
        return  (List<NoteEntity>) DataHolder.getInstance().getData(NoteListFragment.KEY_NOTE_LIST);
    }

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
            noteEntityList = getData();
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        noteAdapter = new NoteAdapter(new NoteAdapter.NoteDiff());
        recyclerView.setAdapter(noteAdapter);
        noteAdapter.setOnItemClickListener(
                new NoteAdapter.OnItemClickListener() {
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
        noteAdapter.submitList(noteEntityList);
    }

    public void setData(List<NoteEntity> noteEntityList){
        if(noteAdapter != null){
            noteAdapter.submitList(noteEntityList);
        }
    }

    private Controller getController() {
        return (Controller) getActivity();
    }

    interface Controller {
        void openNoteScreen(NoteEntity noteEntity);

        void deleteNote(NoteEntity noteEntity);
    }
}
