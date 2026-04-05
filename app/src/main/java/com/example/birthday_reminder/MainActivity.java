package com.example.birthday_reminder;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends Activity {
    private Button btnExit, btnAddNew;
    private ArrayList<Birthday> birthdays;
    private CustomListAdapter adapter;
    private String userId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        SharedPreferences pref = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        userId = pref.getString("LOGGED_IN_USER", "");
        
        if (userId.isEmpty()) {
            Intent intent = new Intent(this, SignupActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        btnExit = findViewById(R.id.btnExit);
        btnAddNew = findViewById(R.id.btnAddNew);

        ListView listBirthDays = findViewById(R.id.listBirthDays);
        birthdays = new ArrayList<>();
        adapter = new CustomListAdapter(this, birthdays);
        listBirthDays.setAdapter(adapter);

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Logout logic
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("LOGGED_IN_USER", "");
                editor.apply();
                Intent intent = new Intent(MainActivity.this, SignupActivity.class);
                startActivity(intent);
                finish();
            }
        });
        
        btnAddNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, BirthdayInfoActivity.class);
                startActivity(i);
            }
        });

        listBirthDays.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Birthday b = birthdays.get(i);
                Intent intent = new Intent(MainActivity.this, BirthdayInfoActivity.class);
                intent.putExtra("PERSON_ID", b.id);
                startActivity(intent);
            }
        });

        listBirthDays.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Birthday b = birthdays.get(i);
                BirthdayDB bdb = new BirthdayDB(MainActivity.this);
                bdb.deleteDOBInfo(b.id);
                loadDOBs();
                return true;
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        this.loadDOBs();
    }

    private void loadDOBs() {
        this.birthdays.clear();
        BirthdayDB bdb = new BirthdayDB(this);

        // Filter by user_id
        Cursor res = bdb.selectDOBs("SELECT * FROM dobinfo WHERE user_id='" + userId + "'");
        if (res != null && res.getCount() > 0) {
            while (res.moveToNext()) {
                String id = res.getString(0);
                String name = res.getString(1);
                String phone = res.getString(2);
                long dobMills = res.getLong(3);
                String image = res.getString(4);
                
                Birthday dob = new Birthday(id, name, String.valueOf(dobMills), phone, image);
                this.birthdays.add(dob);
            }
            res.close();
        }
        adapter.notifyDataSetChanged();
    }
}
