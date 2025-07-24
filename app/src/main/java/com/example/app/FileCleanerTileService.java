package com.example.app;

import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.content.SharedPreferences;
import android.util.Log;
import java.io.File;

public class FileCleanerTileService extends TileService {
    private static final String TAG = "FileCleanerTile";
    private static final String PREFS_NAME = "FileCleanerPrefs";
    private static final String FILE_PATH_KEY = "file_path";

    @Override
    public void onClick() {
        super.onClick();

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String filePath = prefs.getString(FILE_PATH_KEY, null);

        if (filePath != null) {
            File file = new File(filePath);
            if (file.exists()) {
                if (file.delete()) {
                    Log.i(TAG, "文件删除成功: " + filePath);
                    updateTileState(Tile.STATE_ACTIVE, "已删除");
                } else {
                    Log.e(TAG, "文件删除失败: " + filePath);
                    updateTileState(Tile.STATE_UNAVAILABLE, "删除失败");
                }
            } else {
                Log.w(TAG, "文件不存在: " + filePath);
                updateTileState(Tile.STATE_INACTIVE, "文件不存在");
            }
        } else {
            Log.e(TAG, "未配置文件路径");
            updateTileState(Tile.STATE_UNAVAILABLE, "未配置路径");
        }
    }

    private void updateTileState(int state, String label) {
        Tile tile = getQsTile();
        if (tile != null) {
            tile.setState(state);
            tile.setLabel(label);
            tile.updateTile();

            // 3秒后恢复默认状态
            new android.os.Handler().postDelayed(() -> {
                tile.setState(Tile.STATE_INACTIVE);
                tile.setLabel("删除文件");
                tile.updateTile();
            }, 3000);
        }
    }
}