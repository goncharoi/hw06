package com.example.hw06;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class NoteFragment extends Fragment {
    private NoteEntity noteEntity;
    private static NoteFragment noteFragment;

    private EditText titleEt;
    private EditText textEt;
    private TextView createdOnTw;
    private Button saveBtn;

    public static NoteFragment getInstance(NoteEntity noteEntity) {
        if (noteFragment == null) {
            noteFragment = new NoteFragment();
        }
        noteFragment.noteEntity = noteEntity; //таким образом ссылка будет хранится на конкретный экземпляр
        DataHolder.getInstance().putData(Key.CURRENT_NOTE, noteEntity);
        return noteFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_note, null);

        titleEt = view.findViewById(R.id.title_edit_text);
        textEt = view.findViewById(R.id.text_edit_text);
        createdOnTw = view.findViewById(R.id.created_on_text_view);

        saveBtn = view.findViewById(R.id.save_button);

        saveBtn.setOnClickListener(v -> {
            Controller controller = (Controller) getActivity();
            noteEntity.setText(textEt.getText().toString());
            noteEntity.setTitle(titleEt.getText().toString());
            controller.saveResult(noteEntity);
        });
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        String lvTitle = (String) DataHolder.getInstance().getData(Key.CURRENT_NOTE_TITLE);
        titleEt.setText(lvTitle == null
                ? noteEntity.getTitle()
//                : savedInstanceState.getString(Key.CURRENT_NOTE_TITLE));
                : lvTitle);
        String lvText = (String) DataHolder.getInstance().getData(Key.CURRENT_NOTE_TEXT);
        textEt.setText(lvText == null
                ? noteEntity.getText()
//                : savedInstanceState.getString(Key.CURRENT_NOTE_TEXT));
                : lvText);

//        titleEt.setText(noteEntity.getTitle());
//        textEt.setText(noteEntity.getText());
        createdOnTw.setText(noteEntity.getCreatedOn().toString());
    }

    @Override
    public void onAttach(Context context) {
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
    public void onSaveInstanceState(Bundle instanceState) {
        super.onSaveInstanceState(instanceState);
//        instanceState.putString(Key.CURRENT_NOTE_TITLE, titleEt.getText().toString());
//        instanceState.putString(Key.CURRENT_NOTE_TEXT, textEt.getText().toString());
        DataHolder.getInstance().putData(Key.CURRENT_NOTE_TEXT, textEt.getText().toString());
        DataHolder.getInstance().putData(Key.CURRENT_NOTE_TITLE, titleEt.getText().toString());
    }

    public interface Controller {
        void saveResult(NoteEntity noteEntity);
    }
}
