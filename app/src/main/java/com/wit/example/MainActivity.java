package com.wit.example;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;

import static com.wit.example.sign_up_dp_helper.TABLE_NAME_S;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.wit.witsdk.modular.sensor.device.exceptions.OpenDeviceException;
import com.wit.witsdk.modular.sensor.example.ble5.Bwt901ble;
import com.wit.witsdk.modular.sensor.example.ble5.interfaces.IBwt901bleRecordObserver;
import com.wit.witsdk.modular.sensor.modular.connector.modular.bluetooth.BluetoothBLE;
import com.wit.witsdk.modular.sensor.modular.connector.modular.bluetooth.BluetoothSPP;
import com.wit.witsdk.modular.sensor.modular.connector.modular.bluetooth.WitBluetoothManager;
import com.wit.witsdk.modular.sensor.modular.connector.modular.bluetooth.exceptions.BluetoothBLEException;
import com.wit.witsdk.modular.sensor.modular.connector.modular.bluetooth.interfaces.IBluetoothFoundObserver;
import com.wit.witsdk.modular.sensor.modular.processor.constant.WitSensorKey;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements IBluetoothFoundObserver, IBwt901bleRecordObserver {
    private static final String TAG = "MainActivity";
    Context context;
    my_database_helper myDb = new my_database_helper(MainActivity.this);
    public static String exTime;
    public static String sysTime;
    public static String sysTimeNext;
    boolean isPressed_start = false;
    int column_counter = 0;

    private Spinner spNames;
    private List<String> namesList;
    private long id_num = 1000000001;
    TextView dspTxt;
    String selectedUsername;
    String seans_date = getCurrentDate();

    private final List<Bwt901ble> bwt901bleList = new ArrayList<>();
    //    db_variables db_variables = new db_variables();
    private boolean destroyed = false;
    Bwt901ble bwt901ble;
    private static final int STORAGE_PERMISSION_REQUEST_CODE = 1;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        namesList = new ArrayList<>();
        TextView db_txt = findViewById(R.id.db_txt);
//        Button btn_db_view = findViewById(R.id.btn_db_view);
        Button btn_start = findViewById(R.id.btn_start);
//        Button btn_stop = findViewById(R.id.btn_stop);
        Button btn_delete_db = findViewById(R.id.btn_delete_db);
        Button btn_log_out = findViewById(R.id.btn_log_out);
        Button btn_export = findViewById(R.id.export_excel);

        EditText column_size = findViewById(R.id.column_size);
        EditText et_action_1 = findViewById(R.id.action_1);
        EditText et_action_2 = findViewById(R.id.action_2);
        EditText et_action_3 = findViewById(R.id.action_3);
        dspTxt = findViewById(R.id.username_display_main);
        spNames = findViewById(R.id.sp_names);
        String[] userDetails = getUserData(getIntent().getStringExtra("USERNAME"));
        String username = userDetails[1];
        dspTxt.setText(username);
        List<String> usernameList = getAllUsernames();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, usernameList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spNames.setAdapter(adapter);

        // Initialize the Bluetooth manager, here will apply for Bluetooth permissions
        WitBluetoothManager.initInstance(this);

        // start search button
        Button startSearchButton = findViewById(R.id.startSearchButton);
        startSearchButton.setOnClickListener((v) -> {
            startDiscovery();
        });

        // stop search button
        Button stopSearchButton = findViewById(R.id.stopSearchButton);
        stopSearchButton.setOnClickListener((v) -> {
            stopDiscovery();
        });

        // Acceleration calibration button
        Button appliedCalibrationButton = findViewById(R.id.appliedCalibrationButton);
        appliedCalibrationButton.setOnClickListener((v) -> {
            handleAppliedCalibration();
        });

        // Start Magnetic Field Calibration button
        Button startFieldCalibrationButton = findViewById(R.id.startFieldCalibrationButton);
        startFieldCalibrationButton.setOnClickListener((v) -> {
            handleStartFieldCalibration();
        });

        // End Magnetic Field Calibration button
        Button endFieldCalibrationButton = findViewById(R.id.endFieldCalibrationButton);
        endFieldCalibrationButton.setOnClickListener((v) -> {
            handleEndFieldCalibration();
        });

        // Read 03 register button
        Button readReg03Button = findViewById(R.id.readReg03Button);
        readReg03Button.setOnClickListener((v) -> {
            handleReadReg03();
        });

        // Auto refresh data thread
        Thread thread = new Thread(this::refreshDataTh);
        destroyed = false;
        thread.start();

        spNames.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                    String selectedName = spNames.getSelectedItem().toString();
//                    selectedUsername = getIntent().getStringExtra("USERNAME");
                    selectedUsername = spNames.getSelectedItem().toString();
                    getUserData(selectedUsername);
                    String[] selected_smskdg = getUserData(selectedUsername);
                    dspTxt.setText(selected_smskdg[1]);
                    id_num ++;
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    selectedUsername = getIntent().getStringExtra("USERNAME");
                }
        });

        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String action_1 = et_action_1.getText().toString();
                    String action_2 = et_action_2.getText().toString();
                    String action_3 = et_action_3.getText().toString();


                    int size_of_column;
                    size_of_column = Integer.parseInt(column_size.getText().toString());
                    db_start_stop_time tmDB = new db_start_stop_time(MainActivity.this);
                    tmDB.delete_actions();
                    sysTime = sysTime();
                    sysTimeNext = sysTimeNext();
                    tmDB.start("Start", "0", sysTime(), sysTimeNext());

                    while (column_counter <= size_of_column) {
                        try {
                            Thread.sleep(100);
                            db_txt.setText("Ma'lumotlar qo'shilmoqda...");
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        try {
                            StringBuilder acc_x = new StringBuilder();
                            Bwt901ble bwt901ble = bwt901bleList.get(0);
                            String deviceData_1 = getAcc_X(bwt901ble);
                            acc_x.append(deviceData_1);

                            StringBuilder acc_y = new StringBuilder();
                            bwt901ble = bwt901bleList.get(0);
                            String deviceData_2 = getAcc_Y(bwt901ble);
                            acc_y.append(deviceData_2);

                            StringBuilder acc_z = new StringBuilder();
                            bwt901ble = bwt901bleList.get(0);
                            String deviceData_3 = getAcc_Z(bwt901ble);
                            acc_z.append(deviceData_3);

                            StringBuilder angle_x = new StringBuilder();
                            Bwt901ble bwt901ble_ang_x = bwt901bleList.get(0);
                            String deviceData_4 = getAngle_X(bwt901ble_ang_x);
                            angle_x.append(deviceData_4);

                            StringBuilder angle_y = new StringBuilder();
                            Bwt901ble bwt901ble_ang_y = bwt901bleList.get(0);
                            String deviceData_5 = getAngle_Y(bwt901ble_ang_y);
                            angle_y.append(deviceData_5);


                            StringBuilder angle_z = new StringBuilder();
                            Bwt901ble bwt901ble_ang_z = bwt901bleList.get(0);
                            String deviceData_6 = getAngle_Z(bwt901ble_ang_z);
                            angle_z.append(deviceData_6);

                            StringBuilder gyro_x = new StringBuilder();
                            Bwt901ble bwt901ble_gyro_x = bwt901bleList.get(0);
                            String deviceData_7 = getGyroX(bwt901ble_gyro_x);
                            gyro_x.append(deviceData_7);

                            StringBuilder gyro_y = new StringBuilder();
                            Bwt901ble bwt901ble_gyro_y = bwt901bleList.get(0);
                            String deviceData_8 = getGyroY(bwt901ble_gyro_y);
                            gyro_y.append(deviceData_8);


                            StringBuilder gyro_z = new StringBuilder();
                            Bwt901ble bwt901ble_gyro_z = bwt901bleList.get(0);
                            String deviceData_9 = getGyroZ(bwt901ble_gyro_z);
                            gyro_z.append(deviceData_9);

                            StringBuilder mag_x = new StringBuilder();
                            Bwt901ble bwt901ble_mag_x = bwt901bleList.get(0);
                            String deviceData_10 = getMagX(bwt901ble_mag_x);
                            mag_x.append(deviceData_10);

                            StringBuilder mag_y = new StringBuilder();
                            Bwt901ble bwt901ble_mag_y = bwt901bleList.get(0);
                            String deviceData_11 = getMagY(bwt901ble_mag_y);
                            mag_y.append(deviceData_11);


                            StringBuilder mag_z = new StringBuilder();
                            Bwt901ble bwt901ble_mag_z = bwt901bleList.get(0);
                            String deviceData_12 = getMagZ(bwt901ble_mag_z);
                            mag_z.append(deviceData_12);


                            my_database_helper myDB = new my_database_helper(MainActivity.this);
                            runOnUiThread(() -> {
                                try {
                                    String[] userDetails = getUserData(selectedUsername);
                                    myDB.addActions(Long.toString(id_num), userDetails[0], userDetails[1], userDetails[2], userDetails[3], userDetails[4], userDetails[5], seans_date, action_1, action_2, action_3, acc_x.toString(), acc_y.toString(), acc_z.toString(), mag_x.toString(),mag_y.toString(),mag_z.toString(),angle_x.toString(),angle_y.toString(),angle_z.toString(),gyro_x.toString(),gyro_y.toString(),gyro_z.toString());
                                }catch (Exception e){
                                    System.err.println(e);
                                }
                                column_counter++;
                            });

                            try {

                                device_data_db dvDB = new device_data_db(MainActivity.this);
                                dvDB.insertData();
                            }catch (Exception e){
                                System.err.println("GD");
                            }
//                            column_size.setText("");

                        } catch (Exception e) {
                            db_txt.setText("Qurilma Qo'shilmagan...");
//                            fakeData();
                            Toast.makeText(MainActivity.this, "Qurilma qo'shilmagan !", Toast.LENGTH_SHORT).show();
                            break;
                        }

                        if (column_counter > size_of_column) {
                            db_txt.setText("Ma'lumotlar qo'shib bo'lindi");
                            Toast.makeText(MainActivity.this, size_of_column + " Data Added", Toast.LENGTH_SHORT).show();
//                            size_of_column = Integer.parseInt(column_size.getText().toString());
                            column_size.setText("");
                            id_num ++;
                            break;
                        }
                    }
                    column_counter = 0;
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "Bo'sh joylarni To'ldiring", Toast.LENGTH_SHORT).show();
                }

                exTime = exTime();
                sysTime = sysTime();
                sysTimeNext = sysTimeNext();
                db_start_stop_time tmDB = new db_start_stop_time(MainActivity.this);
                tmDB.start("Stop", exTime(), sysTime(), sysTimeNext());
            }
        });

        btn_log_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, sign_up.class);
                startActivity(intent);
                id_num ++;
            }
        });

        btn_delete_db.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myDb.delete_actions();

                Toast.makeText(MainActivity.this, "Data Base Tozalandi!", Toast.LENGTH_SHORT).show();
                db_txt.setText("Ma'lumotlar Hozircha Yo'q.");
            }
        });
        btn_export.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkStoragePermission();
                exportToExcel();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    public void startDiscovery() {
        // Turn off all device
        for (int i = 0; i < bwt901bleList.size(); i++) {
            Bwt901ble bwt901ble = bwt901bleList.get(i);
            bwt901ble.removeRecordObserver(this);
            bwt901ble.close();
        }

        // Erase all devices
        bwt901bleList.clear();

        // Start searching for bluetooth
        try {
            // get bluetooth manager
            WitBluetoothManager bluetoothManager = WitBluetoothManager.getInstance();
            // Monitor communication signals
            bluetoothManager.registerObserver(this);
            // start search
            bluetoothManager.startDiscovery();
        } catch (BluetoothBLEException e) {
            e.printStackTrace();
        }
    }
    public void stopDiscovery() {
        // stop searching for bluetooth
        try {
            // acquire Bluetooth manager
            WitBluetoothManager bluetoothManager = WitBluetoothManager.getInstance();
            // Cancel monitor communication signals
            bluetoothManager.removeObserver(this);
            // stop searching
            bluetoothManager.stopDiscovery();
        } catch (BluetoothBLEException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onFoundBle(BluetoothBLE bluetoothBLE) {
        // Create a Bluetooth 5.0 sensor connection object
        Bwt901ble bwt901ble = new Bwt901ble(bluetoothBLE);
        // add to device list
        bwt901bleList.add(bwt901ble);

        // Registration data record
        bwt901ble.registerRecordObserver(this);

        // Turn on the device
        try {
            bwt901ble.open();
        } catch (OpenDeviceException e) {
            // Failed to open device
            e.printStackTrace();
        }
    }
    @Override
    public void onFoundSPP(BluetoothSPP bluetoothSPP) {
        // Without doing any processing, this sample program only demonstrates how to connect a Bluetooth 5.0 device
    }
    @Override
    public void onRecord(Bwt901ble bwt901ble) {
        String deviceData = getDeviceData(bwt901ble);
        Log.d(TAG, "device data [ " + bwt901ble.getDeviceName() + "] = " + deviceData);
    }
    private void refreshDataTh() {

        while (!destroyed) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            StringBuilder text = new StringBuilder();
            for (int i = 0; i < bwt901bleList.size(); i++) {
                // Make all devices accelerometer calibrated
                Bwt901ble bwt901ble = bwt901bleList.get(i);
                String deviceData = getDeviceData(bwt901ble);
                text.append(deviceData);
            }

            TextView deviceDataTextView = findViewById(R.id.deviceDataTextView);
            runOnUiThread(() -> {
                deviceDataTextView.setText(text);
            });

            if (isPressed_start == true) {
                try {
                } catch (Exception e) {
                    System.err.println("Base ERR");
                }
            }
        }
//            btn_stop.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    isPressed_start = false;
//                }
//            });
//                deviceDataTextView.setText(text.append(getString(R.string.accX));
//                deviceDataTextView.setText(text.append(getString(R.string.accY));
//                deviceDataTextView.setText(text.append(getString(R.string.accZ));
//                System.out.println(text);

    }
    private String getDeviceData(Bwt901ble bwt901ble) {

        StringBuilder builder = new StringBuilder();

        builder.append(bwt901ble.getDeviceName()).append("\n");
        builder.append(getString(R.string.accX)).append(":").append(bwt901ble.getDeviceData(WitSensorKey.AccX)).append("g \t");
        builder.append(getString(R.string.accY)).append(":").append(bwt901ble.getDeviceData(WitSensorKey.AccY)).append("g \t");
        builder.append(getString(R.string.accZ)).append(":").append(bwt901ble.getDeviceData(WitSensorKey.AccZ)).append("g \n");
        builder.append(getString(R.string.asX)).append(":").append(bwt901ble.getDeviceData(WitSensorKey.AsX)).append("°/s \t");
        builder.append(getString(R.string.asY)).append(":").append(bwt901ble.getDeviceData(WitSensorKey.AsY)).append("°/s \t");
        builder.append(getString(R.string.asZ)).append(":").append(bwt901ble.getDeviceData(WitSensorKey.AsZ)).append("°/s \n");
        builder.append(getString(R.string.angleX)).append(":").append(bwt901ble.getDeviceData(WitSensorKey.AngleX)).append("° \t");
        builder.append(getString(R.string.angleY)).append(":").append(bwt901ble.getDeviceData(WitSensorKey.AngleY)).append("° \t");
        builder.append(getString(R.string.angleZ)).append(":").append(bwt901ble.getDeviceData(WitSensorKey.AngleZ)).append("° \n");
        builder.append(getString(R.string.hX)).append(":").append(bwt901ble.getDeviceData(WitSensorKey.HX)).append("\t");
        builder.append(getString(R.string.hY)).append(":").append(bwt901ble.getDeviceData(WitSensorKey.HY)).append("\t");
        builder.append(getString(R.string.hZ)).append(":").append(bwt901ble.getDeviceData(WitSensorKey.HZ)).append("\n");
        builder.append(getString(R.string.t)).append(":").append(bwt901ble.getDeviceData(WitSensorKey.T)).append("\n");
        builder.append(getString(R.string.electricQuantityPercentage)).append(":").append(bwt901ble.getDeviceData(WitSensorKey.ElectricQuantityPercentage)).append("\n");
        builder.append(getString(R.string.versionNumber)).append(":").append(bwt901ble.getDeviceData(WitSensorKey.VersionNumber)).append("\n");
        return builder.toString();
    }
    private String getAcc_X(Bwt901ble bwt901ble) {
        StringBuilder accBuilder = new StringBuilder();
        accBuilder.append(bwt901ble.getDeviceData(WitSensorKey.AccX)).append("g \t");
        return accBuilder.toString();
    }
    private String getAcc_Y(Bwt901ble bwt901ble) {
        StringBuilder accBuilder = new StringBuilder();
        accBuilder.append(bwt901ble.getDeviceData(WitSensorKey.AccY)).append("g \t");
        return accBuilder.toString();
    }
    private String getAcc_Z(Bwt901ble bwt901ble) {
        StringBuilder accBuilder = new StringBuilder();
        accBuilder.append(bwt901ble.getDeviceData(WitSensorKey.AccZ)).append("g \t");
        return accBuilder.toString();
    }

    private String getAngle_X(Bwt901ble bwt901ble) {
        StringBuilder accBuilder = new StringBuilder();
        accBuilder.append(bwt901ble.getDeviceData(WitSensorKey.AngleX)).append("g \t");
        return accBuilder.toString();
    }

    private String getAngle_Y(Bwt901ble bwt901ble) {
        StringBuilder accBuilder = new StringBuilder();
        accBuilder.append(bwt901ble.getDeviceData(WitSensorKey.AngleY)).append("g \t");
        return accBuilder.toString();
    }

    private String getAngle_Z(Bwt901ble bwt901ble) {
        StringBuilder accBuilder = new StringBuilder();
        accBuilder.append(bwt901ble.getDeviceData(WitSensorKey.AngleZ)).append("g \t");
        return accBuilder.toString();
    }


    private String getMagX(Bwt901ble bwt901ble) {
        StringBuilder accBuilder = new StringBuilder();
        accBuilder.append(bwt901ble.getDeviceData(WitSensorKey.HX)).append("g \t");
        return accBuilder.toString();
    }

    private String getMagY(Bwt901ble bwt901ble) {
        StringBuilder accBuilder = new StringBuilder();
        accBuilder.append(bwt901ble.getDeviceData(WitSensorKey.HY)).append("g \t");
        return accBuilder.toString();
    }

    private String getMagZ(Bwt901ble bwt901ble) {
        StringBuilder accBuilder = new StringBuilder();
        accBuilder.append(bwt901ble.getDeviceData(WitSensorKey.HZ)).append("g \t");
        return accBuilder.toString();
    }
    private void handleAppliedCalibration() {
        for (int i = 0; i < bwt901bleList.size(); i++) {
            Bwt901ble bwt901ble = bwt901bleList.get(i);
            // unlock register
            bwt901ble.unlockReg();
            // send command
            bwt901ble.appliedCalibration();
        }
        makeText(this, "OK", LENGTH_LONG).show();
    }

    private String getGyroX(Bwt901ble bwt901ble) {
        StringBuilder accBuilder = new StringBuilder();
        accBuilder.append(bwt901ble.getDeviceData(WitSensorKey.AsX)).append("g \t");
        return accBuilder.toString();
    }

    private String getGyroY(Bwt901ble bwt901ble) {
        StringBuilder accBuilder = new StringBuilder();
        accBuilder.append(bwt901ble.getDeviceData(WitSensorKey.AsY)).append("g \t");
        return accBuilder.toString();
    }

    private String getGyroZ(Bwt901ble bwt901ble) {
        StringBuilder accBuilder = new StringBuilder();
        accBuilder.append(bwt901ble.getDeviceData(WitSensorKey.AsZ)).append("g \t");
        return accBuilder.toString();
    }
    private void handleStartFieldCalibration() {
        for (int i = 0; i < bwt901bleList.size(); i++) {
            Bwt901ble bwt901ble = bwt901bleList.get(i);
            // unlock register
            bwt901ble.unlockReg();
            // send command
            bwt901ble.startFieldCalibration();
        }
        makeText(this, "OK", LENGTH_LONG).show();
    }
    private void handleEndFieldCalibration() {
        for (int i = 0; i < bwt901bleList.size(); i++) {
            Bwt901ble bwt901ble = bwt901bleList.get(i);
            // unlock register
            bwt901ble.unlockReg();
            // send command
            bwt901ble.endFieldCalibration();
        }
        makeText(this, "OK", LENGTH_LONG).show();
    }
    private void handleReadReg03() {
        for (int i = 0; i < bwt901bleList.size(); i++) {
            Bwt901ble bwt901ble = bwt901bleList.get(i);
            // Must be used sendProtocolData method, and the device will read the register value when you using this method
            int waitTime = 200;
            // The command to send the command, and wait 200ms
            bwt901ble.sendProtocolData(new byte[]{(byte) 0xff, (byte) 0xAA, (byte) 0x27, (byte) 0x03, (byte) 0x00}, waitTime);
            //get the value of register 03
            String reg03Value = bwt901ble.getDeviceData("03");
            // If it is read up, reg03Value is the value of the register. If it is not read up, you can enlarge waitTime, or read it several times.v
            makeText(this, bwt901ble.getDeviceName() + " reg03Value: " + reg03Value, LENGTH_LONG).show();
        }
    }
    private void exportToExcel() {
        // Ruxsat berilgan, Excel faylni xotiraga eksport qilishni boshlash
        my_database_helper db_1 = new my_database_helper(MainActivity.this);
        SQLiteDatabase s_db_1 = db_1.getReadableDatabase();
        Cursor cursor_1 = s_db_1.rawQuery("SELECT * FROM raw_data", null);

        Workbook workbook = new HSSFWorkbook();
        Sheet sheet_1 = workbook.createSheet("RawData");

        int rowindex_1 = 0;

        Row headerRow_1 = sheet_1.createRow(rowindex_1++);
        for (int columnIndex_1 = 0; columnIndex_1 < cursor_1.getColumnCount(); columnIndex_1++) {
            Cell cell = headerRow_1.createCell(columnIndex_1);
            cell.setCellValue(cursor_1.getColumnName(columnIndex_1));
        }

        while (cursor_1.moveToNext()) {
            Row row_1 = sheet_1.createRow(rowindex_1++);
            for (int columnIndex_1 = 0; columnIndex_1 < cursor_1.getColumnCount(); columnIndex_1++) {
                Cell cell = row_1.createCell(columnIndex_1);
                cell.setCellValue(cursor_1.getString(columnIndex_1));
            }
        }

        device_data_db db_2 = new device_data_db(MainActivity.this);
        SQLiteDatabase s_db_2 = db_2.getReadableDatabase();
        Cursor cursor_2 = s_db_2.rawQuery("SELECT * FROM mytable", null);

        Sheet sheet_2 = workbook.createSheet("MetadataDevice");

        int rowindex_2 = 0;

        Row headerRow_2 = sheet_2.createRow(rowindex_2++);
        for (int columnIndex_2 = 0; columnIndex_2 < cursor_2.getColumnCount(); columnIndex_2++) {
            Cell cell = headerRow_2.createCell(columnIndex_2);
            cell.setCellValue(cursor_2.getColumnName(columnIndex_2));
        }

        while (cursor_2.moveToNext()) {
            Row row_2 = sheet_2.createRow(rowindex_2++);
            for (int columnIndex_2 = 0; columnIndex_2 < cursor_2.getColumnCount(); columnIndex_2++) {
                Cell cell = row_2.createCell(columnIndex_2);
                cell.setCellValue(cursor_2.getString(columnIndex_2));
            }
        }

        db_start_stop_time db_3 = new db_start_stop_time(MainActivity.this);
        SQLiteDatabase s_db_3 = db_3.getReadableDatabase();
        Cursor cursor_3 = s_db_3.rawQuery("SELECT * FROM metadatatime", null);

        Sheet sheet_3 = workbook.createSheet("MetadataTime");

        int rowindex_3 = 0;

        Row headerRow_3 = sheet_3.createRow(rowindex_3++);
        for (int columnIndex_3 = 0; columnIndex_3 < cursor_3.getColumnCount(); columnIndex_3++) {
            Cell cell = headerRow_3.createCell(columnIndex_3);
            cell.setCellValue(cursor_3.getColumnName(columnIndex_3));
        }

        while (cursor_3.moveToNext()) {
            Row row_3 = sheet_3.createRow(rowindex_3++);
            for (int columnIndex_3 = 0; columnIndex_3 < cursor_3.getColumnCount(); columnIndex_3++) {
                Cell cell = row_3.createCell(columnIndex_3);
                cell.setCellValue(cursor_3.getString(columnIndex_3));
            }
        }

        File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "ExportedFiles");
        if (!directory.exists()) {
            directory.mkdirs();
        }

        File file = new File(directory, "comp_2.xls");
        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            workbook.write(outputStream);
            Toast.makeText(MainActivity.this, "Ma'lumotlar Eksport qilindi !", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.this, "Xatolik !", Toast.LENGTH_SHORT).show();

        }
        // Resurslarni tozalash
        cursor_1.close();
        s_db_1.close();
        cursor_2.close();
        s_db_2.close();
        cursor_3.close();
        s_db_3.close();
    }
    private String sysTimeNext() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);

        return String.format(Locale.getDefault(), "%02d/%02d/%02d %02d:%02d:%02d", year, month, day, hour, minute, second);
    }
    public String sysTime() {
        return Long.toString(System.currentTimeMillis());
    }
    public String exTime() {
        return Long.toString(System.nanoTime());
    }
    private String getCurrentTime() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);

        return String.format(Locale.getDefault(), "%02d:%02d:%02d", hour, minute, second);
    }
    private String getCurrentSecond() {
        Calendar calendar = Calendar.getInstance();
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.MILLISECOND);
        return String.format(Locale.getDefault(), "%02d:%02d", minute, second);
    }
    private String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return String.format(Locale.getDefault(), "%02d/%02d/%02d", year, month, day);
    }
    @SuppressLint("Range")
    public String[] getUserData(String username) {
        sign_up_dp_helper sgDB = new sign_up_dp_helper(MainActivity.this);
        String[] userData = new String[6];
        SQLiteDatabase db = sgDB.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME_S, new String[]{"_id","NAME", "SURNAME", "MIDDLE", "GENDER", "BIRTHDAY"}, "USERNAME=?", new String[]{username}, null, null, null);

        if (cursor.moveToFirst()) {
            userData[0] = cursor.getString(cursor.getColumnIndex("_id"));
            userData[1] = cursor.getString(cursor.getColumnIndex("SURNAME"));
            userData[2] = cursor.getString(cursor.getColumnIndex("NAME"));
            userData[3] = cursor.getString(cursor.getColumnIndex("MIDDLE"));
            userData[4] = cursor.getString(cursor.getColumnIndex("GENDER"));
            userData[5] = cursor.getString(cursor.getColumnIndex("BIRTHDAY"));
            dspTxt.setText(userData[1]);
        }

        cursor.close();
        db.close();

        return userData;
    }
    private void checkStoragePermission() {
        String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, permissions, STORAGE_PERMISSION_REQUEST_CODE);
        } else {
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == STORAGE_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Ruxsat berildi, Excel faylni xotiraga eksport qilishni boshlash
//                exportToExcel();
            } else {
                // Ruxsat berilmadi, foydalanuvchiga xotiradan foydalanish uchun ruxsat so'radi
                // ...
            }
        }
    }
    public List<String> getAllUsernames() {
        List<String> usernameList = new ArrayList<>();
        sign_up_dp_helper sgDB = new sign_up_dp_helper(MainActivity.this);
        SQLiteDatabase db = sgDB.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME_S, new String[]{"USERNAME"}, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") String username = cursor.getString(cursor.getColumnIndex("USERNAME"));
                usernameList.add(username);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return usernameList;
    }
}