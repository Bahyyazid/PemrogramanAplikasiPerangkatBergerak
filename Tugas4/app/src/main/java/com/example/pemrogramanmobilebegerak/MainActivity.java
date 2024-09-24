package com.example.pemrogramanmobilebegerak;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Adapter adapter;
    private List<String> dataList;
    private Button addButton, deleteButton;
    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "RecyclerViewData";
    private static final String DATA_KEY = "DataList";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);


        dataList = loadDataFromPreferences();


        adapter = new Adapter(dataList);
        recyclerView.setAdapter(adapter);


        addButton = findViewById(R.id.AddButton);
        deleteButton = findViewById(R.id.deleteButton);


        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddDataDialog();
            }
        });


        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteConfirmationDialog();
            }
        });
    }


    private void showAddDataDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add New Data");


        final EditText input = new EditText(this);
        builder.setView(input);


        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newItem = input.getText().toString();
                if (!newItem.isEmpty()) {
                    addDataToList(newItem);
                    adapter.notifyDataSetChanged();
                    saveDataToPreferences();
                } else {
                    Toast.makeText(MainActivity.this, "Input cannot be empty", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }


    private void showDeleteConfirmationDialog() {
        if (!dataList.isEmpty()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Delete Confirmation");
            builder.setMessage("Are you sure you want to delete the last item?");


            builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    deleteLastItem();
                    saveDataToPreferences();
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.show();
        } else {
            Toast.makeText(this, "No items to delete", Toast.LENGTH_SHORT).show();
        }
    }


    private void addDataToList(String data) {
        dataList.add(data);
    }


    private void deleteLastItem() {
        if (!dataList.isEmpty()) {
            dataList.remove(dataList.size() - 1);
            adapter.notifyDataSetChanged();
        }
    }

    private List<String> loadDataFromPreferences() {
        Set<String> storedData = sharedPreferences.getStringSet(DATA_KEY, new HashSet<String>());
        return new ArrayList<>(storedData);
    }


    private void saveDataToPreferences() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Set<String> dataSet = new HashSet<>(dataList);
        editor.putStringSet(DATA_KEY, dataSet);
        editor.apply();
    }
}
