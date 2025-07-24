package com.example.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.io.File;

public class MainActivity extends AppCompatActivity {
    private static final int STORAGE_PERMISSION_CODE = 100;
    private EditText etFilePath;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = getSharedPreferences("FileCleanerPrefs", MODE_PRIVATE);
        etFilePath = findViewById(R.id.etFilePath);
        Button btnSave = findViewById(R.id.btnSave);
        Button btnTest = findViewById(R.id.btnTest);

        // 加载保存的路径
        etFilePath.setText(prefs.getString("file_path", ""));

        // 检查权限
        if (!checkStoragePermission()) {
            requestStoragePermission();
        }

        btnSave.setOnClickListener(v -> saveFilePath());
        btnTest.setOnClickListener(v -> testFileDeletion());
    }

    private void saveFilePath() {
        String path = etFilePath.getText().toString().trim();
        if (!path.isEmpty()) {
            prefs.edit().putString("file_path", path).apply();
            Toast.makeText(this, "路径已保存", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "请输入文件路径", Toast.LENGTH_SHORT).show();
        }
    }

    private void testFileDeletion() {
        String path = prefs.getString("file_path", "");
        if (path.isEmpty()) {
            Toast.makeText(this, "请先配置文件路径", Toast.LENGTH_SHORT).show();
            return;
        }

        File file = new File(path);
        if (file.exists()) {
            Toast.makeText(this, "文件存在: " + path, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "文件不存在", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkStoragePermission() {
        return ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                },
                STORAGE_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "存储权限已授予", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "需要存储权限才能操作文件", Toast.LENGTH_SHORT).show();
            }
        }
    }
}