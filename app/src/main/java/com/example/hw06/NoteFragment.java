package com.example.hw06;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;

public class NoteFragment extends Fragment {
    private static final String DATE_PICKER_DIALOG_TAG = "DATE_PICKER_DIALOG_TAG";

    private NoteEntity noteEntity;

    private EditText titleEt;
    private EditText textEt;
    private TextView deadlineTv;
    private Date deadline;

    private class Key { //Ключи для хранения данных
        public static final String CURRENT_NOTE = "CURRENT_NOTE";
        public static final String CURRENT_NOTE_TITLE = "CURRENT_NOTE_TITLE";
        public static final String CURRENT_NOTE_DEADLINE = "CURRENT_NOTE_DEADLINE";
        public static final String CURRENT_NOTE_TEXT = "CURRENT_NOTE_TEXT";
    }

    public static void putData(NoteEntity noteEntity) {
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

    public static NoteEntity getCurrentNote() {
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
        deadlineTv = view.findViewById(R.id.deadline_text_view);

        deadlineTv.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(deadline);
            new DatePickerDialog(
                    getContext(),
                    (view1, year, monthOfYear, dayOfMonth) -> {
                        // Получение даты из DatePicker
                        Calendar cal = Calendar.getInstance();
                        cal.set(Calendar.YEAR, year);
                        cal.set(Calendar.MONTH, monthOfYear);
                        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        refreshDeadline(cal.getTime());
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            ).show();
        });

        view.findViewById(R.id.save_button).setOnClickListener(v -> {
            Controller controller = (Controller) getActivity();
            noteEntity.setText(textEt.getText().toString());
            noteEntity.setTitle(titleEt.getText().toString());
            noteEntity.setDeadline(deadline);
            controller.saveNote(noteEntity);
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
        refreshDeadline((lvDeadline == null
                ? noteEntity.getDeadline()
                : lvDeadline));
    }

    public void refreshDeadline(Date deadline) {
        this.deadline = deadline;
        deadlineTv.setText(String.format("%s: %s",
                getString(R.string.note_item_deadline_text),
                new SimpleDateFormat(getString(R.string.default_date_format)).format(deadline)
        ));
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
        if (deadline != null) {
            DataHolder.getInstance().putData(Key.CURRENT_NOTE_DEADLINE, deadline);
        }
    }

    public interface Controller {
        void saveNote(NoteEntity noteEntity);
    }
}
