package com.example.birthday_reminder;


import android.app.Activity;
import android.content.Intent;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("onCreate...MainActivity");
        setContentView(R.layout.activity_main);
        btnExit = findViewById(R.id.btnExit);
        btnAddNew = findViewById(R.id.btnAddNew);

        ListView listBirthDays = findViewById(R.id.listBirthDays);
        birthdays = new ArrayList<>();
        adapter = new CustomListAdapter(this, birthdays);
        listBirthDays.setAdapter(adapter);

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("btnExit...MainActivity");
                finish();
            }
        });
        btnAddNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("btnAddNew...MainActivity");
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
        System.out.println("onStart...MainActivity");
        this.loadDOBs();
    }

    private void loadDOBs() {
        // clear any previously stored items from the arraylist
        this.birthdays.clear();
        BirthdayDB bdb = new BirthdayDB(this);

        Cursor res = bdb.selectDOBs("SELECT * FROM dobinfo");
        if (res != null && res.getCount() > 0) {
            while (res.moveToNext()) {
                String id = res.getString(0);
                String name = res.getString(1);
                String phone = res.getString(2);
                long dobMills = res.getLong(3);
                String dobStr = String.valueOf(dobMills);
                Birthday dob = new Birthday(id, name, dobStr, phone, "");
                this.birthdays.add(dob);
            }
            res.close();
        } else {
            Toast.makeText(this, "Database is empty !!", Toast.LENGTH_LONG).show();
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onPause() {
        super.onPause();
        System.out.println("onPause...MainActivity");
    }

    @Override
    public void onResume() {
        super.onResume();
        System.out.println("onResume...MainActivity");
    }

    @Override
    public void onStop() {
        super.onStop();
        System.out.println("onStop...MainActivity");
    }

    @Override
    public void onRestart() {
        super.onRestart();
        System.out.println("onRestart...MainActivity");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("onDestroy...MainActivity");
    }
}
