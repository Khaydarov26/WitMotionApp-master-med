package com.wit.example;
import static com.wit.example.my_database_helper.TABLE_NAME;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class login extends AppCompatActivity {

    TextView txt_have_not;



    EditText password,username;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Button btn_log_in = findViewById(R.id.btn_login);
        txt_have_not = findViewById(R.id.already_have_not);
        password = findViewById(R.id.log_password);
        username = findViewById(R.id.log_username);

        txt_have_not.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent_sign_up = new Intent(login.this, sign_up.class);
                startActivity(intent_sign_up);
            }
        });

        btn_log_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sign_up_dp_helper myDB_s = new sign_up_dp_helper(login.this);

                String getUsername = username.getText().toString();
                String getPassword = "";

                if (verifyLogin(getUsername, getPassword)) {
                    // Login muvaffaqiyatli bo'lganidan so'ng, asosiy activity ga qaytish uchun kod
                    Intent intent_ma = new Intent(login.this, MainActivity.class);
                    intent_ma.putExtra("USERNAME", getUsername);
                    startActivity(intent_ma);

                }else {
                    if(getUsername.length() < 5){username.setError("foydalanuvchi nomi uzunligi 5 ta harfdan kam bo'lmasligi kerak");}else
//                    if(getPassword.length() < 8){username.setError("parolni to'g'ri kiriting, uzunlik 8 dan kichik bo'lmasligi kerak");}else
                    if(!myDB_s.isUsernameExist(getUsername)){
                        username.setError("bunday username mavjud emas");
                    }else{
//                        password.setError("Parol Noto'g'ri");
                        password.requestFocus();
                    }
//                    Toast.makeText(login.this, "Login Yoki Parol noto'g'ri", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private boolean verifyLogin(String getUsername, String getPassword) {
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = openOrCreateDatabase("logins.db", MODE_PRIVATE, null);

            String query = "SELECT * FROM login_info WHERE USERNAME='" + getUsername + "'";
            cursor = db.rawQuery(query, null);
            return cursor.getCount() > 0;
        } catch (Exception e) {
            // Xatolik yuz berdi, false qaytarish
            return false;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
    }
}