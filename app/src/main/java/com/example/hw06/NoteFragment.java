package com.example.hw06;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.Date;
import java.util.Calendar;

public class NoteFragment extends Fragment {
    private NoteEntity noteEntity;

    private EditText titleEt;
    private EditText textEt;
    private DatePicker deadlineDp;

    private class Key { //Ключи для хранения данных
        public static final String CURRENT_NOTE = "CURRENT_NOTE";
        public static final String CURRENT_NOTE_TITLE = "CURRENT_NOTE_TITLE";
        public static final String CURRENT_NOTE_DEADLINE = "CURRENT_NOTE_DEADLINE";
        public static final String CURRENT_NOTE_TEXT = "CURRENT_NOTE_TEXT";
    }

    public static void putData(NoteEntity noteEntity){
        DataHolder.getInstance().putData(NoteFragment.Key.CURRENT_NOTE_TEXT, noteEntity.getText());
        DataHolder.getInstance().putData(NoteFragment.Key.CURRENT_NOTE_TITLE, noteEntity.getTitle());
        DataHolder.getInstance().putData(NoteFragment.Key.CURRENT_NOTE_DEADLINE, noteEntity.getDeadline());
    }

    public static void deleteData() {
        DataHolder.getInstance().deleteData(NoteFragment.Key.CURRENT_NOTE);
        DataHolder.getInstance().deleteData(NoteFragment.Key.CURRENT_NOTE_TEXT);
        DataHolder.getInstance().deleteData(NoteFragment.Key.CURRENT_NOTE_TITLE);
        DataHolder.getInstance().deleteData(NoteFragment.Key.CURRENT_NOTE_DEADLINE);
    }

    public static NoteEntity getCurrentNote(){
        return (NoteEntity) DataHolder.getInstance().getData(NoteFragment.Key.CURRENT_NOTE);
    }

    public static NoteFragment getInstance(NoteEntity noteEntity) {
        NoteFragment noteFragment = new NoteFragment();
        //таким образом ссылка будет хранится на конкретный экземпляр
        //все изменения будут производится непосредственно над экемпляром из списка в MainActivity
        noteFragment.noteEntity = noteEntity;
        DataHolder.getInstance().putData(Key.CURRENT_NOTE, noteEntity);
        return noteFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_note, null);

        titleEt = view.findViewById(R.id.title_edit_text);
        textEt = view.findViewById(R.id.text_edit_text);
        deadlineDp = view.findViewById(R.id.deadline_date_picker);

        view.findViewById(R.id.save_button).setOnClickListener(v -> {
            Controller controller = (Controller) getActivity();
            noteEntity.setText(textEt.getText().toString());
            noteEntity.setTitle(titleEt.getText().toString());
            noteEntity.setDeadline(getDeadlineFromDatePicker());
            controller.saveResult(noteEntity);
        });
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        //После поворота экрана надо вернуть в поля текущие изменяемые значения, если они сохранены
        String lvTitle = (String) DataHolder.getInstance().getData(Key.CURRENT_NOTE_TITLE);
        titleEt.setText(lvTitle == null
                ? noteEntity.getTitle()
                : lvTitle);
        String lvText = (String) DataHolder.getInstance().getData(Key.CURRENT_NOTE_TEXT);
        textEt.setText(lvText == null
                ? noteEntity.getText()
                : lvText);
        Date lvDeadline = (Date) DataHolder.getInstance().getData(Key.CURRENT_NOTE_DEADLINE);
        initDeadlineDatePicker(lvDeadline == null
                ? noteEntity.getDeadline()
                : lvDeadline);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (!(context instanceof Controller)) {
            throw new RuntimeException("Activity must implement NoteFragment.Controller");
        }
        if (noteEntity == null) {
            noteEntity = (NoteEntity) DataHolder.getInstance().getData(Key.CURRENT_NOTE);
        }
    }

    // Сохранение данных (полей ввода, но не самой заметки)
    @Override
    public void onSaveInstanceState(@NonNull Bundle instanceState) {
        super.onSaveInstanceState(instanceState);
        //При повороте экрана сохраняем текущие изменяемые значения
        if (textEt != null) {
            DataHolder.getInstance().putData(Key.CURRENT_NOTE_TEXT, textEt.getText().toString());
        }
        if (titleEt != null) {
            DataHolder.getInstance().putData(Key.CURRENT_NOTE_TITLE, titleEt.getText().toString());
        }
        if (deadlineDp != null) {
            DataHolder.getInstance().putData(Key.CURRENT_NOTE_DEADLINE, getDeadlineFromDatePicker());
        }
    }

    public interface Controller {
        void saveResult(NoteEntity noteEntity);
    }

    // Получение даты из DatePicker
    private Date getDeadlineFromDatePicker() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, this.deadlineDp.getYear());
        cal.set(Calendar.MONTH, this.deadlineDp.getMonth());
        cal.set(Calendar.DAY_OF_MONTH, this.deadlineDp.getDayOfMonth());
        return cal.getTime();
    }

    // Установка даты в DatePicker
    private void initDeadlineDatePicker(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        this.deadlineDp.init(calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH),
                null);
    }

}
