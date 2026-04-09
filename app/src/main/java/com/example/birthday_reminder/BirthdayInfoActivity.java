package com.example.birthday_reminder;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.NameValuePair;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.message.BasicNameValuePair;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BirthdayInfoActivity extends AppCompatActivity {

    private Button btnCancel, btnSave, btnDelete;
    private EditText etName, etPhone, etDOB;
    private ImageView ivPhoto;
    private String personID = "";
    private String imageString = "";
    private String userId = "";

    private final ActivityResultLauncher<String> mGetContent = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    try {
                        InputStream inputStream = getContentResolver().openInputStream(uri);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        ivPhoto.setImageBitmap(bitmap);
                        imageString = encodeImage(bitmap);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_birthday_info);
        
        SharedPreferences pref = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        userId = pref.getString("LOGGED_IN_USER", "");

        btnCancel = findViewById(R.id.btnCancel);
        btnSave = findViewById(R.id.btnSave);
        btnDelete = findViewById(R.id.btnDelete);

        etName = findViewById(R.id.etName);
        etPhone = findViewById(R.id.etPhone);
        etDOB = findViewById(R.id.etDOB);
        ivPhoto = findViewById(R.id.ivPhoto);

        Intent i = this.getIntent();
        if (i != null && i.hasExtra("PERSON_ID")) {
            personID = i.getStringExtra("PERSON_ID");
            loadExistingData();
            btnDelete.setVisibility(View.VISIBLE);
        }

        ivPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGetContent.launch("image/*");
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BirthdayDB bdb = new BirthdayDB(BirthdayInfoActivity.this);
                bdb.deleteDOBInfo(personID);
                // Synchronize deletion with remote server
                deleteDOBFromRemoteServer(personID);
                Toast.makeText(BirthdayInfoActivity.this, "Birthday Deleted", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = etName.getText().toString().trim();
                String phone = etPhone.getText().toString().trim();
                String dob = etDOB.getText().toString().trim();

                if (name.isEmpty() || phone.isEmpty() || dob.isEmpty()) {
                    Toast.makeText(BirthdayInfoActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                long dobMills = 0;
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                    sdf.setLenient(false);
                    Date date = sdf.parse(dob);
                    if (date != null) {
                        dobMills = date.getTime();
                    }
                } catch (Exception e) {
                    Toast.makeText(BirthdayInfoActivity.this, "Invalid Date! Use dd-MM-yyyy", Toast.LENGTH_SHORT).show();
                    return;
                }

                BirthdayDB bdb = new BirthdayDB(BirthdayInfoActivity.this);
                if (personID == null || personID.isEmpty()) {
                    personID = System.currentTimeMillis() + phone;
                    bdb.insertDOBInfo(personID, name, phone, dobMills, imageString, userId);
                } else {
                    bdb.updateDOBInfo(personID, name, phone, dobMills, imageString, userId);
                }
                
                // Backup to Remote Server including userId for filtering
                storeDOBinRemoteServer(personID, name, phone, dobMills);
                
                finish();
            }
        });
    }

    private void storeDOBinRemoteServer(String personID, String name, String phone, long dobMills) {
        String dobKey = personID;
        // Append userId to the value string so we can filter when restoring
        String dobValue = personID + "," + name + "," + phone + "," + dobMills + "," + userId;

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                String url = "http://www.muthosoft.com/univ/cse489/key_value.php";
                List<NameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair("action", "backup"));
                params.add(new BasicNameValuePair("sid", "2022360010"));
                params.add(new BasicNameValuePair("semester", "2026-1"));
                params.add(new BasicNameValuePair("key", dobKey));
                params.add(new BasicNameValuePair("value", dobValue));

                return RemoteAccess.getInstance().makeHttpRequest(url, "POST", params);
            }
            @Override
            protected void onPostExecute(String data) {}
        }.execute();
    }

    private void deleteDOBFromRemoteServer(String personID) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                String url = "http://www.muthosoft.com/univ/cse489/key_value.php";
                List<NameValuePair> params = new ArrayList<>();
                // Common action names for deletion in this API are 'remove' or 'delete'
                params.add(new BasicNameValuePair("action", "remove")); 
                params.add(new BasicNameValuePair("sid", "2022360010"));
                params.add(new BasicNameValuePair("semester", "2026-1"));
                params.add(new BasicNameValuePair("key", personID));

                return RemoteAccess.getInstance().makeHttpRequest(url, "POST", params);
            }
            @Override
            protected void onPostExecute(String data) {}
        }.execute();
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
                
                imageString = cursor.getString(4);
                if (imageString != null && !imageString.isEmpty()) {
                    Bitmap bitmap = decodeImage(imageString);
                    ivPhoto.setImageBitmap(bitmap);
                }
            }
            cursor.close();
        }
    }

    private String encodeImage(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream);
        byte[] bytes = outputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    private Bitmap decodeImage(String encodedImage) {
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
}
