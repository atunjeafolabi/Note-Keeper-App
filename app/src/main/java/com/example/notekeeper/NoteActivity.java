package com.example.notekeeper;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.List;

public class NoteActivity extends AppCompatActivity {

    //public static final String NOTE_INFO = "com.example.notekeeper.NOTE_INFO";
    public static final String NOTE_POSITION = "com.example.notekeeper.NOTE_POSITION";
    public static final int POSITION_NOT_SET = -1;

    private NoteInfo note;
    private boolean isNewNote;
    private EditText textNoteTitle;
    private EditText textNoteText;
    private Spinner spinnerCourses;
    private int notePosition;
    private boolean isCancelling;
    private String originalNoteCourseId;
    private String originalNoteTitle;
    private String originalNoteText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Populate spinner dropdown
        spinnerCourses = (Spinner) findViewById(R.id.spinner_courses);
        List<CourseInfo> courses = DataManager.getInstance().getCourses();
        ArrayAdapter<CourseInfo> adapterCourses = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, courses);
        adapterCourses.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCourses.setAdapter(adapterCourses);

        readDisplayStateValues();
        saveOriginalNoteValues();

        textNoteTitle = (EditText) findViewById(R.id.text_note_title);
        textNoteText = (EditText)  findViewById(R.id.text_note_text);

        if(!isNewNote) {
            displayNote(spinnerCourses, textNoteTitle, textNoteText);
        }

    }

    private void saveOriginalNoteValues() {
        if(isNewNote)
            return;

        originalNoteCourseId = note.getCourse().getCourseId();
        originalNoteTitle = note.getTitle();
        originalNoteText = note.getText();
    }

    @Override
    protected void onPause() {

        super.onPause();

        if(isCancelling){

            if(isNewNote){
                DataManager.getInstance().removeNote(notePosition);
            }else{
                storePreviousNoteValues();
            }

        } else {
            saveNote();
        }
    }

    private void storePreviousNoteValues() {
        CourseInfo course = DataManager.getInstance().getCourse(originalNoteCourseId);
        note.setCourse(course);
        note.setTitle(originalNoteTitle);
        note.setText(originalNoteText);
    }

    private void saveNote() {
        //This only writes the changes to the current 'note' object. No persistence
        note.setCourse((CourseInfo) spinnerCourses.getSelectedItem());
        note.setTitle(textNoteTitle.getText().toString());
        note.setText(textNoteText.getText().toString());
    }

    private void displayNote(Spinner spinnerCourses, EditText textNoteTitle, EditText textNoteText) {

        List<CourseInfo> courses = DataManager.getInstance().getCourses();
        int courseIndex = courses.indexOf(note.getCourse());
        spinnerCourses.setSelection(courseIndex);

        textNoteTitle.setText(note.getTitle());
        textNoteText.setText(note.getText());
    }

    //This method gets the notes 'extra' passed via the intent
    private void readDisplayStateValues() {
        /* getIntent() will return the intent used to start this particular activity.The intent will be sent from another Activity */
        Intent intent = getIntent();
//        note = intent.getParcelableExtra(NOTE_POSITION);
        int position = intent.getIntExtra(NOTE_POSITION, POSITION_NOT_SET);

//        isNewNote = note == null;
        isNewNote = position == POSITION_NOT_SET;

        if(isNewNote){
            createNewNote();
        }else {
            note = DataManager.getInstance().getNotes().get(position);
        }
    }

    private void createNewNote() {
        DataManager dm = DataManager.getInstance();
        notePosition = dm.createNewNote();
        note = dm.getNotes().get(notePosition);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_note, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_send_email) {
            sendEmail();
            return true;
        } else if(id == R.id.action_cancel) {
            isCancelling = true;
            finish();   //exit and go back to the previous Activity. onPause() will be called before going back
        }

        return super.onOptionsItemSelected(item);
    }

    private void sendEmail() {
        CourseInfo course = (CourseInfo) spinnerCourses.getSelectedItem();
        String subject = textNoteTitle.getText().toString();
        String text = "Checkout what I learned in the Pluralsight course " + course.getTitle() + " " + textNoteText.getText().toString();

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc2822");
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, text);
        startActivity(intent);
    }
}
