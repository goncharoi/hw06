package com.example.hw06;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class NoteFragment extends Fragment {
    private NoteEntity noteEntity;

    private EditText titleEt;
    private EditText textEt;
    private TextView createdOnTw;

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
        createdOnTw = view.findViewById(R.id.created_on_text_view);

        view.findViewById(R.id.save_button).setOnClickListener(v -> {
            Controller controller = (Controller) getActivity();
            noteEntity.setText(textEt.getText().toString());
            noteEntity.setTitle(titleEt.getText().toString());
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

        createdOnTw.setText(noteEntity.getCreatedOn().toString());
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
    }

    public interface Controller {
        void saveResult(NoteEntity noteEntity);
    }
}
