package com.davyuu.leagueitemsmobafire;

import android.app.Activity;
import android.content.Intent;
import android.database.SQLException;
import android.speech.RecognizerIntent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import java.util.List;

public class ItemListActivity extends Activity {

    private DatabaseHelper dbHelper;
    private EditText searchEditText;
    private Button searchRemoveBtn;
    private Button searchVoiceBtn;
    private Spinner filterSpinner;
    private ListView itemListView;
    private ItemListAdapter itemListAdapterAdapter;

    private List<String> itemNameList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list_search);

        dbHelper = new DatabaseHelper(this);
        try {
            dbHelper.createDataBase();
        }
        catch(Exception e){
            Log.d("dbHelper", e.getMessage().toString());
            throw new Error("Unable to create database");
        }
        try {
            dbHelper.openDataBase();
        }catch(SQLException sqle){
            throw sqle;
        }

        searchEditText = (EditText) findViewById(R.id.search_edit_text);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() > 0){
                    searchRemoveBtn.setVisibility(View.VISIBLE);
                    searchVoiceBtn.setVisibility(View.GONE);
                }
                else{
                    searchRemoveBtn.setVisibility(View.GONE);
                    searchVoiceBtn.setVisibility(View.VISIBLE);
                }
                doSearch(s.toString());
            }
        });
        searchRemoveBtn = (Button) findViewById(R.id.search_remove_btn);
        searchRemoveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchEditText.setText("");
            }
        });

        searchVoiceBtn = (Button) findViewById(R.id.search_voice_btn);
        searchVoiceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displaySpeechRecognizer();
            }
        });

        filterSpinner = (Spinner) findViewById(R.id.filter_spinner);

        itemListView = (ListView) findViewById(R.id.item_list);
        itemNameList = dbHelper.getAllNames();
        itemListAdapterAdapter = new ItemListAdapter(ItemListActivity.this, itemNameList,
               dbHelper);
        itemListView.setAdapter(itemListAdapterAdapter);
        itemListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent i = new Intent(getApplicationContext(), ItemActivity.class);

                String name = ((TextView) view.findViewById(R.id.item_name)).getText().toString();
                i.putExtra("name", name);
                startActivity(i);
            }
        });
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }*/

    public void doSearch(String searchText){
        itemListAdapterAdapter.getFilter().filter(searchText);
    }

    private static final int SPEECH_REQUEST_CODE = 0;
    private void displaySpeechRecognizer() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        startActivityForResult(intent, SPEECH_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            String spokenText = results.get(0);
            searchEditText.setText(spokenText);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
