package com.wit.example;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.Layout;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class sign_up extends AppCompatActivity {

    EditText date_of_birth, surname, name, middle_name, username, password, password_2;

    Switch gender;
    TextView txt_have_account;
    Calendar c;
    DatePickerDialog dpd;

    Button btn_sign_up;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_sign_up);
        btn_sign_up = findViewById(R.id.btn_sign_up);
        surname = findViewById(R.id.surname);
        name = findViewById(R.id.name);
        middle_name = findViewById(R.id.middle_name);
        date_of_birth = findViewById(R.id.date_of_birth);
        gender = findViewById(R.id.gender);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        password_2 = findViewById(R.id.password_2);


        final String[] txt_gender = {"FEMALE"};

        txt_have_account = findViewById(R.id.already_have);

        MaterialDatePicker.Builder builder = MaterialDatePicker.Builder.datePicker();
        builder.setTitleText("Tug'ilgan sana");

// DatePicker ni yoqing
        final MaterialDatePicker materialDatePicker = builder.build();

// EditTextni bosing
        date_of_birth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                materialDatePicker.show(getSupportFragmentManager(), "DATE_PICKER");
            }
        });

// Tug'ilgan sana tanlandiqda
        materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
            @Override
            public void onPositiveButtonClick(Object selection) {
                // Tug'ilgan sanani olib EditText ga yozing
                date_of_birth.setText(materialDatePicker.getHeaderText());
            }
        });


        gender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (gender.isChecked()) {
                    txt_gender[0] = "MALE";
                } else
                    txt_gender[0] = "FEMALE";
            }
        });

        txt_have_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_log = new Intent(sign_up.this, login.class);
                startActivity(intent_log);
            }
        });

        btn_sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sign_up_dp_helper myDB_s = new sign_up_dp_helper(sign_up.this);
                String s_surname = surname.getText().toString();
                String s_name = name.getText().toString();
                String s_middle_name = middle_name.getText().toString();
                String s_date_of_birth = date_of_birth.getText().toString();
                String s_username = username.getText().toString();
                String s_password = password.getText().toString();
                String s_password_2 = password_2.getText().toString();
                final String[] error_log = {"1", "null", "1", "1", "1"};

                if (s_middle_name.length() < 4 && s_name.length() < 4 && s_surname.length() < 4) {
                    name.setError("Ism va Familiyani to'g'ri kiriting !");
                    name.requestFocus();
                    error_log[0] = "1";
                } else {
                    error_log[0] = "null";
                }

                if(dataEdited()){
                    error_log[1] = "null";
                }else {
                    error_log[1] = "1";
                    date_of_birth.setError("Sanani Tanglang");
                }

                if (isUsernameValid(s_username)) {
                    if(myDB_s.isUsernameExist(s_username)){
                        username.setError("username allaqachon mavjud!");
                        username.requestFocus();
                    }else {
                        error_log[2] = "null";
                    }
                } else {
                    username.setError("5 ta belgidan kam bo'lmasligi va raqam bilan boshlanishi mumkin emas !");
                    error_log[2] = "1";
                }

                if (!isPasswordValid(s_password)) {
                    error_log[3] = "null";
                } else {
                    password.setError("parol uzunligi kamida 8 bo'lishi, harflar va raqamdan tashkil topgan bo'lishi shart!");
                    password.requestFocus();
                    error_log[3] = "1";
                }

                if (s_password.equals(s_password_2)) {
                    error_log[4] = "null";
                } else {
                    password.setError("parollar bir xil bo'lishi shart!");
                    password.requestFocus();
                    error_log[4] = "1";
                }

                if (error_log[0].equals("null") && error_log[1].equals("null") && error_log[2].equals("null") && error_log[3].equals("null") && error_log[4].equals("null")) {
                    myDB_s.addValues(s_surname, s_name, s_middle_name, s_date_of_birth, txt_gender[0], s_username, s_password);
                        Intent intent_s = new Intent(sign_up.this, login.class);
                        startActivity(intent_s);
                }
            }
        });
    }

    public static boolean isPasswordValid(String password) {
        // Kamida 8 belgidan ko'p bo'lishi kerak
        if (password.length() < 8) {
            return false;
        }

        boolean hasLowercase = false;
        boolean hasUppercase = false;
        boolean hasDigit = false;

        // Har bir belgi uchun tekshirish
        for (int i = 0; i < password.length(); i++) {
            char c = password.charAt(i);
            if (Character.isLowerCase(c)) {
                hasLowercase = true;
            } else if (Character.isUpperCase(c)) {
                hasUppercase = true;
            } else if (Character.isDigit(c)) {
                hasDigit = true;
            }
        }

        // Kichik harf, katta harf va raqam bormi tekshirish
        if (!hasLowercase || !hasUppercase || !hasDigit) {
            return false;
        }

        return true;
    }

    public boolean isUsernameValid(String username) {
        // Check length
        if (username.length() < 5) {
            return false;
        }

        // Check if starts with a digit
        if (Character.isDigit(username.charAt(0))) {
            return false;
        }

        return true;
    }

    private boolean dataEdited() {
        String selectedDate = date_of_birth.getText().toString().trim();
        return !TextUtils.isEmpty(selectedDate);
    }
}