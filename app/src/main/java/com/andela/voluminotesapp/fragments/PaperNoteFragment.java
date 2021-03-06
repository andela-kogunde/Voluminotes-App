package com.andela.voluminotesapp.fragments;


import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.andela.notelib.note.Folders;
import com.andela.notelib.note.NoteManager;
import com.andela.notelib.note.PaperNote;
import com.andela.voluminotesapp.R;
import com.andela.voluminotesapp.activities.MyApplication;
import com.andela.voluminotesapp.callbacks.AutoSaveCallback;
import com.andela.voluminotesapp.config.Constants;
import com.andela.voluminotesapp.utilities.MsgBox;
import com.andela.voluminotesapp.utilities.ShareNote;


public class PaperNoteFragment extends AutoSaveFragment {
    public static final String STATE = "state";

    private EditText noteTitle;
    private EditText noteArea;
    private RelativeLayout noteColor;

    private PaperNote paperNote;
    private Bundle bundle;
    private InputMethodManager inputMethodManager;
    private boolean toggle = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_note, container, false);

        manageToolbar();

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);

        initializeComponents(view);

        getPaperNote(savedInstanceState);

        setPaperNote();

        setAutoSaveListener(new AutoSaveCallback() {
            @Override
            public void onSave() {
                save();
            }

        });
        activateAutoSave();
    }

    private void manageToolbar() {
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void getPaperNote(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            bundle = getArguments();
            if (bundle != null) {
                this.paperNote = bundle.getParcelable(getContext().getString(Folders.GENERAL_NOTE.getFolder()));
            } else {
                initPaperNote();
            }
        } else {
            this.paperNote = savedInstanceState.getParcelable(STATE);
            this.bundle = new Bundle();
        }
    }

    private void initializeComponents(View view) {
        noteTitle = (EditText) view.findViewById(R.id.noteTitle);
        noteArea = (EditText) view.findViewById(R.id.noteArea);
        noteColor = (RelativeLayout) view.findViewById(R.id.noteColor);
        inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        noteArea.setOnClickListener(onClickListener);
        noteTitle.setOnClickListener(onClickListener);
    }

    private void initPaperNote() {
        this.paperNote = new PaperNote();
        this.paperNote.setFontFamily(getContext().getString(R.string.roboto_font));
        this.paperNote.setFontSize(Constants.SMALL.getSizes());
        this.paperNote.setBackground(R.drawable.note_white);
    }

    private void setPaperNote() {
        setFontFamily(this.paperNote.getFontFamily());
        setFontSize(this.paperNote.getFontSize());
        setNoteBackground(this.paperNote.getBackground());
        noteTitle.setText(this.paperNote.getTitle());
        noteArea.setText(this.paperNote.getNote());
    }

    private void setFontFamily(String fontFamily) {
        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), fontFamily);
        noteTitle.setTypeface(typeface);
        noteArea.setTypeface(typeface);
        this.paperNote.setFontFamily(fontFamily);
    }

    private void setFontSize(int fontSize) {
        noteTitle.setTextSize((float) fontSize);
        noteArea.setTextSize((float) fontSize);
        this.paperNote.setFontSize(fontSize);
    }

    private void setNoteBackground(int noteBackground) {
        noteColor.setBackground(ContextCompat.getDrawable(getContext(), noteBackground));
        this.paperNote.setBackground(noteBackground);
    }

    private void shareNote(PaperNote note) {
        if (note != null) {
            if (!note.getTitle().isEmpty() || !note.getNote().isEmpty()) {
                new ShareNote().sharePaperNote(getActivity(), note);
            }
        }
    }

    private boolean changeFontFamily(int id) {
        switch (id) {
            case R.id.roboto:
                setFontFamily(getContext().getString(R.string.roboto_font));
                return true;
            case R.id.heart:
                setFontFamily(getContext().getString(R.string.heart_font));
                return true;
            case R.id.harabara:
                setFontFamily(getContext().getString(R.string.harabara_font));
                return true;
            case R.id.calibri:
                setFontFamily(getContext().getString(R.string.calibri_font));
                return true;
            default:
                return false;
        }
    }

    private boolean changeFontSize(int id) {
        switch (id) {
            case R.id.small:
                setFontSize(Constants.SMALL.getSizes());
                return true;
            case R.id.medium:
                setFontSize(Constants.MEDIUM.getSizes());
                return true;
            case R.id.large:
                setFontSize(Constants.LARGE.getSizes());
                return true;
            case R.id.extraLarge:
                setFontSize(Constants.EXTRA_LARGE.getSizes());
                return true;
            default:
                return false;
        }
    }

    private boolean changeNoteBackground(int id) {
        switch (id) {
            case R.id.white:
                setNoteBackground(R.drawable.note_white);
                return true;
            case R.id.blue:
                setNoteBackground(R.drawable.note_blue);
                return true;
            case R.id.pink:
                setNoteBackground(R.drawable.note_pink);
                return true;
            case R.id.yellow:
                setNoteBackground(R.drawable.note_yellow);
                return true;
            case R.id.green:
                setNoteBackground(R.drawable.note_green);
                return true;
            default:
                return false;
        }
    }

    private boolean clickShareNote(int id) {
        switch (id) {
            case R.id.share:
                shareNote(this.paperNote);
                return true;
            default:
                return false;
        }
    }

    private boolean saveBeforeClose(int id) {
        switch (id) {
            case android.R.id.home:
                saveNote();
                MsgBox.show(getContext(), getActivity().getString(R.string.note_saved));
                return true;
            default:
                return false;
        }
    }

    private void prepareNote() {
        this.paperNote.setFolderId(Folders.STATIC_PAPER_NOTE);
        this.paperNote.setTitle(noteTitle.getText().toString());
        this.paperNote.setNote(noteArea.getText().toString());
    }

    private void saveNote() {
        save();
        if (this.paperNote != null) {
            if (this.paperNote.getTitle().equals("") && this.paperNote.getNote().equals("")) {
                cancelNote();
            }
        }
    }

    private void save() {
        if (this.paperNote != null) {
            prepareNote();
            if (!this.paperNote.getTitle().equals("") || !this.paperNote.getNote().equals("")) {
                if (bundle == null) {
                    this.paperNote = (PaperNote) noteManager().saveNote(this.paperNote);
                    bundle = new Bundle();
                } else {
                    noteManager().updateNote(this.paperNote);
                    bundle = new Bundle();
                }
            }
        }
    }

    private void cancelNote() {
        if (bundle != null) {
            noteManager().deleteNote(this.paperNote);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.paper_note_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (changeFontFamily(id)) {
            return true;
        } else if (changeFontSize(id)) {
            return true;
        } else if (changeNoteBackground(id)) {
            return true;
        } else if (clickShareNote(id)) {
            return true;
        } else if (saveBeforeClose(id)) {
            return false;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (!isVisibleToUser) {
            save();
        }

        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        deactivateAutoSave();
        save();
        outState.putParcelable(STATE, this.paperNote);
        super.onSaveInstanceState(outState);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!toggle) {
                inputMethodManager.toggleSoftInput(0, 1);
            } else {
                inputMethodManager.toggleSoftInput(1, 0);
            }
            toggle = !toggle;
        }
    };

    private NoteManager noteManager() {
        return MyApplication.getNoteManager(getContext());
    }
}
