package com.davyuu.leagueitemsmobafire;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.SQLException;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.MenuItemImpl;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static android.R.layout.simple_list_item_1;

public class ItemListActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private EditText searchEditText;
    private ImageButton searchButton;
    private SearchView searchView;
    private ListView itemListView;
    private ItemListAdapter itemListAdapterAdapter;

    private List<String> itemAllNamesList;
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

        /*searchEditText = (EditText) findViewById(R.id.search_edit_text);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                itemListAdapterAdapter.getFilter().filter(s.toString());
            }
        });
        searchButton = (ImageButton) findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displaySpeechRecognizer();
            }
        });*/

        /*searchView = (SearchView) findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                itemListAdapterAdapter.getFilter().filter(newText);
                return false;
            }
        });*/

        itemListView = (ListView) findViewById(R.id.item_list);
        itemAllNamesList = dbHelper.getAllNames();
        itemNameList = itemAllNamesList;
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

    @Override
    protected void onNewIntent(Intent intent){
        if(Intent.ACTION_SEARCH.equals(intent.getAction())){
            String query = intent.getStringExtra(SearchManager.QUERY);
            searchView.setQuery(String.valueOf(query), false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        searchView = (SearchView) menu.findItem(R.id.search_view).getActionView();

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText != null){
                    doSearch(newText);
                }
                return false;
            }
        });
        return true;
    }

    public void doSearch(String searchText){
        itemListAdapterAdapter.getFilter().filter(searchText);
    }

    private static final int SPEECH_REQUEST_CODE = 0;
    private void displaySpeechRecognizer() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        // Start the activity, the intent will be populated with the speech text
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
