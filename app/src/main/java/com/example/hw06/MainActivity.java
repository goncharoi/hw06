package com.example.hw06;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class MainActivity extends AppCompatActivity implements NoteFragment.Controller, NoteListFragment.Controller {

    private static final String NOTE_LIST_TAB = "NOTE_LIST_TAB";
    private List<NoteEntity> noteEntityList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // определяем, выбрана ли заметка для редактирования и в каком положении экран
        NoteEntity noteEntity = NoteFragment.getCurrentNote();
        boolean isLandscape = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;

        db = FirebaseFirestore.getInstance();
        // всегда работаем с одним и тем же экземпляром списка, чтобы не запутаться,
        // для этого при инициализации записываем его в синглтон и храним там до конца работы
        noteEntityList = NoteListFragment.getData();
        if (noteEntityList == null) {
            noteEntityList = new ArrayList<>();
            db.collection(NOTE_LIST_TAB)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                noteEntityList.add(document.toObject(NoteEntity.class));
                            }
                            //поскольку получение данных асинхронное - обновим фрагмент
                            refreshNoteListFragment();
                        }
                    });
            NoteListFragment.putData(noteEntityList);
        }
        // В главный контейнер надо поместить либо список заметок, если ориентация албомная,
        // либо редактируемую заметку, если она выбрана и ориентация экрана портретная
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, isLandscape
                        ? NoteListFragment.getInstance(noteEntityList)
                        : noteEntity != null
                        ? NoteFragment.getInstance(noteEntity)
                        : NoteListFragment.getInstance(noteEntityList))
                .commit();
        // В детальный контейнер помещается выбранная заметка, если она есть и ориентация альбомная,
        // иначе - ничего
        if (isLandscape && noteEntity != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.detail_container, NoteFragment.getInstance(noteEntity))
                    .addToBackStack(null)
                    .commit();
        }

        setSupportActionBar(findViewById(R.id.toolbar));

        ((BottomNavigationView) findViewById(R.id.navigation)).setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    @Override
    public void saveNote(NoteEntity noteEntity) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, NoteListFragment.getInstance(noteEntityList))
                .commit();
        //если фрагмент с заметкой присутствует в правом контейнере - очистим его
        removeNoteFragment();
        // текущая заметка больше таковой не является и никаких текущих данных её тоже нет
        NoteFragment.deleteData();

        db.collection(NOTE_LIST_TAB)
                .document(noteEntity.getId().toString())
                .set(noteEntity)
                .addOnFailureListener(e -> e.printStackTrace());
    }

    public void removeNoteFragment() {
        Fragment loNoteFragment = getSupportFragmentManager().findFragmentById(R.id.detail_container);
        if (loNoteFragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .remove(loNoteFragment)
                    .commit();
        }
    }

    @Override
    public void openNoteScreen(NoteEntity noteEntity) {
        //Поскольку меняем заметку во фрагменте, меняем и текущие значения текстов
        NoteFragment.putData(noteEntity);
        boolean isLandscape = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
        getSupportFragmentManager()
                .beginTransaction()
                .replace(isLandscape ? R.id.detail_container : R.id.container, NoteFragment.getInstance(noteEntity))
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void deleteNote(NoteEntity noteEntity) {
        // Если в хранилище данных зранится ссылка на ту же заметку, которую мы собрались удалять,
        // очистим его
        if (NoteFragment.getCurrentNote() == noteEntity) {
            // предварительно удалим заметку из детального просмотра, если она там есть
            removeNoteFragment();
            NoteFragment.deleteData();
        }

        // собственно удаление заметки из списка
        noteEntityList.remove(noteEntity);
        db.collection(NOTE_LIST_TAB)
                .document(noteEntity.getId().toString())
                .delete();

        // Если внутри контейнера находится список заметок - его надо обновить
        refreshNoteListFragment();
    }

    private void refreshNoteListFragment() {
        if (getSupportFragmentManager().findFragmentById(R.id.container).getClass() == NoteListFragment.class) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, NoteListFragment.getInstance(noteEntityList))
                    .commitNow();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem ioItem) {
        // Обработка выбора пункта меню приложения (активити)
        switch (ioItem.getItemId()) {
            case R.id.menu_calculator:
                Uri address = Uri.parse("calculator://intent");
                Intent loCalculatorIntent = new Intent(Intent.ACTION_VIEW, address);
                startActivity(loCalculatorIntent);
            case R.id.menu_about:
                Toast.makeText(getApplicationContext(), R.string.menu_about_toast, Toast.LENGTH_LONG).show();
                return true;
            case R.id.menu_help:
                Toast.makeText(getApplicationContext(), R.string.menu_help_toast, Toast.LENGTH_LONG).show();
                return true;
            case R.id.menu_options:
                Toast.makeText(MainActivity.this, R.string.menu_options_toast, Toast.LENGTH_LONG).show();
                return true;
        }
        return super.onOptionsItemSelected(ioItem);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Здесь определяем меню приложения (активити)
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    private final BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
        switch (item.getItemId()) {
            case R.id.menu_up:
                shiftNote(1);
                return true;
            case R.id.menu_down:
                shiftNote(-1);
                return true;
            case R.id.menu_add:
                // Создаем и автоматически именуем новую заметку
                int lvNewId = noteEntityList.size() + 1;
                NoteEntity noteEntity = new NoteEntity(lvNewId, getString(R.string.new_note_title) + lvNewId, "", new Date());
                noteEntityList.add(noteEntity);
                openNoteScreen(noteEntity);
                // Если внутри контейнера находится список заметок - его надо обновить
                refreshNoteListFragment();
                return true;
            case R.id.menu_to_list:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container, NoteListFragment.getInstance(noteEntityList))
                        .commit();
                return true;
        }
        return false;
    };

    private void shiftNote(int offset) {
        if(noteEntityList.isEmpty()) return;
        NoteEntity noteEntity = noteEntityList.get(
                (noteEntityList.size() + noteEntityList.indexOf(NoteFragment.getCurrentNote()) + offset)
                        % noteEntityList.size()
        );
        openNoteScreen(noteEntity);
    }


}