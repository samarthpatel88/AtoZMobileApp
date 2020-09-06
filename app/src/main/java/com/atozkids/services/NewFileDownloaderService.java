package com.atozkids.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class NewFileDownloaderService extends IntentService {
    public static String APP_FOLDER_NAME = "/.AtoZKids";
    public static String FILE_NAME = "FILE_NAME";
    public static String FILE_URL = "FILE_URL";

    public NewFileDownloaderService() {
        super(NewFileDownloaderService.class.getName());
    }

    public static void downloadPhotoFile(Context context, String fileName, String fileUrl) {
        Intent intent = new Intent(context, NewFileDownloaderService.class);
        intent.putExtra(FILE_NAME, fileName.replace(" ", "").trim() + ".jpg");
        intent.putExtra(FILE_URL, fileUrl);
        context.startService(intent);
    }

    public static void downloadSoundFile(Context context, String fileName, String fileUrl) {
        Intent intent = new Intent(context, NewFileDownloaderService.class);
        intent.putExtra(FILE_NAME, fileName.replace(" ", "").trim() + ".mp3");
        intent.putExtra(FILE_URL, fileUrl);
        context.startService(intent);
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        String fileUrl = intent.getStringExtra(FILE_URL);
        String fileName = intent.getStringExtra(FILE_NAME);

        try {
            URL url = new URL(fileUrl);//Create Download URl
            HttpURLConnection c = (HttpURLConnection) url.openConnection();//Open Url Connection
            c.setRequestMethod("GET");
            c.connect();//connect the URL Connection

            File folder = new File(Environment.getExternalStorageDirectory() + APP_FOLDER_NAME);

            if (!folder.exists()) {
                folder.mkdir();
            }

            File outputFile = new File(folder, fileName);

            //Create New File if not present
            if (!outputFile.exists()) {
                outputFile.createNewFile();
            }

            FileOutputStream fos = new FileOutputStream(outputFile);//Get OutputStream for NewFile Location

            InputStream is = c.getInputStream();//Get InputStream for connection

            byte[] buffer = new byte[1024];//Set buffer type
            int len1 = 0;//init length
            while ((len1 = is.read(buffer)) != -1) {
                fos.write(buffer, 0, len1);//Write new file
            }

            //Close all connection after doing task
            fos.close();
            is.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
