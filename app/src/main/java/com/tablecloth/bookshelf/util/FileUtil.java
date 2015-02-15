package com.tablecloth.bookshelf.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Calendar;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;

public class FileUtil {

	final private static String BACKUP_FILE_SUFFIX = ".txt";
	final private static String BACKUP_FILE_NAME = "com_tablecloth_bookshelf";

	public static String saveBackupFile(Context context, String saveContent, boolean doOverwrite) {
		String fileName = BACKUP_FILE_NAME + BACKUP_FILE_SUFFIX;
		// 時間情報を含んだファイル名に変更する
		if(!doOverwrite) {
			Calendar cal = Calendar.getInstance();
			fileName = BACKUP_FILE_NAME + cal.get(Calendar.YEAR) + (cal.get(Calendar.MONTH) + 1) + cal.get(Calendar.DAY_OF_MONTH) + cal.get(Calendar.HOUR_OF_DAY) + cal.get(Calendar.MINUTE) + cal.get(Calendar.SECOND) + cal.get(Calendar.MILLISECOND) + BACKUP_FILE_SUFFIX;
		}
		
		try {
			FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
			OutputStreamWriter osw = new OutputStreamWriter(fos);
			osw.write(saveContent);
			osw.flush();
			osw.close();
			ToastUtil.show(context, "バックアップファイル作成完了");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return fileName;
	}
	
	/**
	 * 内部ディレクトリのパス取得
	 * @param context
	 * @param fileName
	 * @return
	 */
	public static String getInternalStorageDirectory(Context context, String fileName) {
		return context.getFilesDir().getAbsolutePath() + "/" + fileName;
	}
	
	public static Uri getSendToFileUri(String path) {
		File sendFile = new File(path);
		return Uri.parse(sendFile.getAbsolutePath()); //Uri.fromFile(sendFile);
	}
}
