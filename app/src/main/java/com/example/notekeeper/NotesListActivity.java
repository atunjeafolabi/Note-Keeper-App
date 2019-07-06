package com.example.notekeeper;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

public class NotesListActivity extends AppCompatActivity {

    private ArrayAdapter<NoteInfo> adapterNotes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(NotesListActivity.this, NoteActivity.class));

//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            }
        });

        initializeDisplayContent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapterNotes.notifyDataSetChanged();
    }

    private void initializeDisplayContent() {

        final ListView listNotes = findViewById(R.id.list_notes);

        List<NoteInfo> notes = DataManager.getInstance().getNotes();

        adapterNotes = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, notes);
        listNotes.setAdapter(adapterNotes);

        listNotes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(NotesListActivity.this, NoteActivity.class);
//                NoteInfo note = (NoteInfo) listNotes.getItemAtPosition(position);
//                intent.putExtra(NoteActivity.NOTE_POSITION, note);
                intent.putExtra(NoteActivity.NOTE_POSITION, position);
                startActivity(intent);
            }
        });
    }

}
