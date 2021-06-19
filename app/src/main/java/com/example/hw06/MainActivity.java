package com.example.hw06;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NoteFragment.Controller, NoteListFragment.Controller {

    List<NoteEntity> noteEntityList;

    public MainActivity() {
        // всегда работаем с одним и тем же экземпляром списка, чтобы не запутаться,
        // для этого при инициализации записываем его в синглтон и храним там до конца работы
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

        // определяем, выбрана ли заметка для редактирования и какой в каком положении экран
        NoteEntity noteEntity = (NoteEntity) DataHolder.getInstance().getData(Key.CURRENT_NOTE);
        boolean isLandscape = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;

        // В главный контейнер надо пометсить либо список заметок, если ориентация албомная,
        // либо редактируемую заметку, если она выбрана и ориентация экрана портретная
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, isLandscape
                        ? NoteListFragment.getInstance(noteEntityList)
                        : noteEntity != null
                        ? NoteFragment.getInstance(noteEntity)
                        : NoteListFragment.getInstance(noteEntityList))
                .commitNow();
        // В детальный контейнер помещается выбранная заметка, если она есть и ориентация альбомная,
        // иначе - ничего
        if (isLandscape && noteEntity != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.detail_container, NoteFragment.getInstance(noteEntity))
                    .addToBackStack(null)
                    .commitNow();
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public void saveResult(NoteEntity noteEntity) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, NoteListFragment.getInstance(noteEntityList))
                .commit();

        //если фрагмент с заметкой присутствует в правом контейнере - очистим его
        removeNoteFragment();

        // текущая заметка больше таковой не является и никаких текущих данных её тоже нет
        DataHolder.getInstance().deleteData(Key.CURRENT_NOTE);
        DataHolder.getInstance().deleteData(Key.CURRENT_NOTE_TEXT);
        DataHolder.getInstance().deleteData(Key.CURRENT_NOTE_TITLE);
    }

    public void  removeNoteFragment(){
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
        DataHolder.getInstance().putData(Key.CURRENT_NOTE_TEXT, noteEntity.getText());
        DataHolder.getInstance().putData(Key.CURRENT_NOTE_TITLE, noteEntity.getTitle());
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
        if (DataHolder.getInstance().getData(Key.CURRENT_NOTE) == noteEntity) {
            // предварительно удалим заметку из детального просмотра, если она там есть
            removeNoteFragment();

            DataHolder.getInstance().deleteData(Key.CURRENT_NOTE);
            DataHolder.getInstance().deleteData(Key.CURRENT_NOTE_TEXT);
            DataHolder.getInstance().deleteData(Key.CURRENT_NOTE_TITLE);
        }

        // собственно дулаение заметки из списка
        noteEntityList.remove(noteEntity);

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
        int lvId = ioItem.getItemId();

        switch (lvId) {
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
            case R.id.menu_add:
                // Создаем и автоматически именуем новую заметку
                int lvNewId = noteEntityList.size() + 1;
                noteEntityList.add(new NoteEntity(lvNewId, getString(R.string.new_note_title) + lvNewId, ""));
                // Если внутри контейнера находится список заметок - его надо обновить
                refreshNoteListFragment();
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

}