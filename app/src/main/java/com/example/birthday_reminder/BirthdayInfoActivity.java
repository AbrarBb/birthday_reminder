package com.example.birthday_reminder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class BirthdayInfoActivity extends AppCompatActivity {

    private Button btnCancel, btnSave;
    private EditText etName, etPhone, etDOB;
    private String personID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_birthday_info);

        btnCancel = findViewById(R.id.btnCancel);
        btnSave = findViewById(R.id.btnSave);

        etName = findViewById(R.id.etName);
        etPhone = findViewById(R.id.etPhone);
        etDOB = findViewById(R.id.etDOB);

        Intent i = this.getIntent();
        if (i != null && i.hasExtra("PERSON_ID")) {
            personID = i.getStringExtra("PERSON_ID");
            loadExistingData();
        }

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("btnSave...");
                //(1) Get data from fields and then check for validity
                String name = etName.getText().toString().trim();
                String phone = etPhone.getText().toString().trim();
                String dob = etDOB.getText().toString().trim();

                if (name.isEmpty() || phone.isEmpty() || dob.isEmpty()) {
                    Toast.makeText(BirthdayInfoActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Convert string-dob to "Date" or "Calendar" type object.
                long dobMills = 0;
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                    sdf.setLenient(false);
                    Date date = sdf.parse(dob);
                    if (date != null) {
                        dobMills = date.getTime();
                    }
                } catch (Exception e) {
                    //(2) If any data is invalid, then show a failure message
                    Toast.makeText(BirthdayInfoActivity.this, "Invalid Date! Use dd-MM-yyyy", Toast.LENGTH_SHORT).show();
                    return;
                }

                //(3) Otherwise, save data, show success message & then Exit
                BirthdayDB bdb = new BirthdayDB(BirthdayInfoActivity.this);
                if (personID == null || personID.isEmpty()) {
                    personID = System.currentTimeMillis() + phone;
                    bdb.insertDOBInfo(personID, name, phone, dobMills);
                    Toast.makeText(BirthdayInfoActivity.this, "Birthday Saved Successfully", Toast.LENGTH_SHORT).show();
                } else {
                    // if personID is previously set,
                    // then it means that we already have this ID in database
                    // So, we need to update the record
                    bdb.updateDOBInfo(personID, name, phone, dobMills);
                    Toast.makeText(BirthdayInfoActivity.this, "Birthday Updated Successfully", Toast.LENGTH_SHORT).show();
                }
                finish();
            }
        });
    }

    private void loadExistingData() {
        BirthdayDB bdb = new BirthdayDB(this);
        Cursor cursor = bdb.selectDOBs("SELECT * FROM dobinfo WHERE ID='" + personID + "'");
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                etName.setText(cursor.getString(1));
                etPhone.setText(cursor.getString(2));
                long dobMills = cursor.getLong(3);
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                etDOB.setText(sdf.format(new Date(dobMills)));
            }
            cursor.close();
        }
    }
}
