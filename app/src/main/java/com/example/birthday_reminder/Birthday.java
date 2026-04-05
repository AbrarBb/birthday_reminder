package com.example.birthday_reminder;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Birthday {
    String id = "";
    String name = "";
    String dob = "";
    String phone = "";
    String imageString = "";

    public Birthday(String id, String name, String dob, String phone, String imageString) {
        this.id = id;
        this.name = name;
        this.dob = dob;
        this.phone = phone;
        this.imageString = imageString;
    }

    public String getFormattedDOB() {
        try {
            long millis = Long.parseLong(dob);
            Date date = new Date(millis);
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
            return sdf.format(date);
        } catch (NumberFormatException e) {
            return dob;
        }
    }

    public String getAge() {
        try {
            long millis = Long.parseLong(dob);
            Calendar dobCal = Calendar.getInstance();
            dobCal.setTimeInMillis(millis);

            Calendar today = Calendar.getInstance();

            int age = today.get(Calendar.YEAR) - dobCal.get(Calendar.YEAR);

            if (today.get(Calendar.MONTH) < dobCal.get(Calendar.MONTH) ||
                    (today.get(Calendar.MONTH) == dobCal.get(Calendar.MONTH) &&
                            today.get(Calendar.DAY_OF_MONTH) < dobCal.get(Calendar.DAY_OF_MONTH))) {
                age--;
            }
            return String.valueOf(age);
        } catch (NumberFormatException e) {
            return "0";
        }
    }
}
