package com.example.pemrogramanmobilebegerak;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Button addButton, deleteButton;
    private DatabaseHelper dbHelper;
    private DataAdapter dataAdapter;
    private List<String> dataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        addButton = findViewById(R.id.AddButton);
        deleteButton = findViewById(R.id.deleteButton);

        dbHelper = new DatabaseHelper(this);
        dataList = new ArrayList<>();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        dataAdapter = new DataAdapter(dataList);
        recyclerView.setAdapter(dataAdapter);

        loadData();

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddDialog();
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteConfirmationDialog();
            }
        });
    }

    private void loadData() {
        dataList.clear();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM data_table", null);
        if (cursor.moveToFirst()) {
            do {
                String data = cursor.getString(cursor.getColumnIndex("data"));
                dataList.add(data);
            } while (cursor.moveToNext());
        }
        cursor.close();
        dataAdapter.notifyDataSetChanged();
    }

    private void showAddDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add New Data");


        final EditText input = new EditText(this);
        builder.setView(input);


        builder.setPositiveButton("Add", (dialog, which) -> {
            String newData = input.getText().toString();
            if (!newData.isEmpty()) {
                addData(newData);
            } else {
                Toast.makeText(MainActivity.this, "Please enter some data", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void addData(String newData) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("data", newData);
        db.insert("data_table", null, values);
        loadData();
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Confirmation");
        builder.setMessage("Are you sure you want to delete the last entry?");


        builder.setPositiveButton("Yes", (dialog, which) -> deleteData());
        builder.setNegativeButton("No", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void deleteData() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (!dataList.isEmpty()) {
            String lastItem = dataList.get(dataList.size() - 1);
            db.delete("data_table", "data = ?", new String[]{lastItem});
            loadData();
        } else {
            Toast.makeText(this, "No data to delete", Toast.LENGTH_SHORT).show();
        }
    }

    class DatabaseHelper extends SQLiteOpenHelper {
        private static final String DATABASE_NAME = "data_db";
        private static final int DATABASE_VERSION = 1;

        public DatabaseHelper(MainActivity context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE IF NOT EXISTS data_table (id INTEGER PRIMARY KEY AUTOINCREMENT, data TEXT)");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS data_table");
            onCreate(db);
        }
    }
}
