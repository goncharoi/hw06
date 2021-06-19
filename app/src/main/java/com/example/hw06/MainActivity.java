package com.example.hw06;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Configuration;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NoteFragment.Controller, NoteListFragment.Controller {

    List<NoteEntity> noteEntityList;

    public MainActivity() {
       noteEntityList = (List<NoteEntity>) DataHolder.getInstance().getData(Key.NOTE_LIST);
        if (noteEntityList == null) {
            noteEntityList = new ArrayList<>();
            noteEntityList.add(new NoteEntity(1, "Список вещей в поездку", "Палатка, спальник, пенка, рюкзак"));
            noteEntityList.add(new NoteEntity(2, "Купить на ужин", "Хдеб, пиво, кобасу, сыр"));
            noteEntityList.add(new NoteEntity(3, "ДЗ", "Разобраться с фрагментами, доделать интенты, ДЗ7"));
            DataHolder.getInstance().putData(Key.NOTE_LIST, noteEntityList);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NoteEntity noteEntity = (NoteEntity) DataHolder.getInstance().getData(Key.CURRENT_NOTE);
        boolean isLandscape = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, isLandscape
                        ? NoteListFragment.getInstance(noteEntityList)
                        : noteEntity != null
                            ? NoteFragment.getInstance(noteEntity)
                            : NoteListFragment.getInstance(noteEntityList))
                .commit();
        if (isLandscape && noteEntity != null){
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.detail_container, NoteFragment.getInstance(noteEntity))
                    .commit();
        }
    }

    @Override
    public void saveResult(NoteEntity noteEntity) {
//        ListIterator<NoteEntity> listIterator = noteEntityList.listIterator();
//        while (listIterator.hasNext()) {
//            NoteEntity noteEntityNext = listIterator.next();
//            if (noteEntityNext.getId() == noteEntity.getId()) {
//                listIterator.set(noteEntity);

                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container, NoteListFragment.getInstance(noteEntityList))
                        .commit();
                DataHolder.getInstance().deleteData(Key.CURRENT_NOTE);
                DataHolder.getInstance().deleteData(Key.CURRENT_NOTE_TEXT);
                DataHolder.getInstance().deleteData(Key.CURRENT_NOTE_TITLE);
//                return;
//            }
//        }
    }

    @Override
    public void openNoteScreen(NoteEntity noteEntity) {
        boolean isLandscape = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
        getSupportFragmentManager()
                .beginTransaction()
                .add(isLandscape ? R.id.detail_container : R.id.container, NoteFragment.getInstance(noteEntity))
                .addToBackStack(null)
                .commit();
    }

}