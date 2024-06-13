package com.codersanx.splitcost.utils;

import static com.codersanx.splitcost.utils.Constants.ALL_DATABASES;
import static com.codersanx.splitcost.utils.Constants.CATEGORY;
import static com.codersanx.splitcost.utils.Constants.EXPENSES;
import static com.codersanx.splitcost.utils.Constants.INCOMES;
import static com.codersanx.splitcost.utils.Constants.MAIN_SETTINGS;
import static com.codersanx.splitcost.utils.Constants.PASS_FROM_ZIP;
import static com.codersanx.splitcost.utils.Constants.TRUE;
import static com.codersanx.splitcost.utils.Utils.renameFile;

import android.content.Context;
import android.widget.Toast;

import com.codersanx.splitcost.R;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

import java.io.File;

import ir.mahdi.mzip.zip.ZipArchive;

public class Zip {
    public static void packFile(Context c, String name) {
        String[] files = {name + INCOMES, name + EXPENSES, name + CATEGORY + INCOMES,
                name + CATEGORY + EXPENSES, name + MAIN_SETTINGS
        };

        String zipFileName = c.getFilesDir() + "/" + name + ".zip";

        try {
            ZipParameters parameters = new ZipParameters();
            parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
            parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);

            parameters.setEncryptFiles(true);
            parameters.setEncryptionMethod(Zip4jConstants.ENC_METHOD_AES);
            parameters.setAesKeyStrength(Zip4jConstants.AES_STRENGTH_256);
            parameters.setPassword(PASS_FROM_ZIP(c));

            ZipFile zipFile = new ZipFile(zipFileName);

            for (String targetPath : files) {
                File targetFile = new File(c.getFilesDir().getParent() + "/shared_prefs/", targetPath + ".xml");
                if (!targetFile.exists()) continue;
                if (targetFile.isFile()) {
                    zipFile.addFile(targetFile, parameters);
                } else if (targetFile.isDirectory()) {
                    zipFile.addFolder(targetFile, parameters);
                }
            }
        } catch (Exception ignored) {
        }
    }

    public static void extractZip(Context c, String name) {
        if (!getFileExtension(name).equals("sce")) {
            Toast.makeText(c, c.getResources().getString(R.string.incorrectFile), Toast.LENGTH_SHORT).show();
            return;
        }

        if (getBaseName(name).length() < 2 || getBaseName(name).length() > 17) {
            Toast.makeText(c, c.getResources().getString(R.string.incorrectFile), Toast.LENGTH_SHORT).show();
            return;
        }

        if (name.contains("@") || name.contains(" ") || name.contains(":")) {
            Toast.makeText(c, c.getResources().getString(R.string.incorrectFile), Toast.LENGTH_SHORT).show();
            return;
        }

        File oldFile = new File(c.getFilesDir(), name);
        String newName = name.replace(".sce", ".zip");
        renameFile(oldFile, newName);

        if (!isProtected(c.getFilesDir() + "/" + newName)){
            Toast.makeText(c, c.getResources().getString(R.string.incorrectFile), Toast.LENGTH_SHORT).show();
            return;
        }

        if (!containsFile(c, newName, getBaseName(name) + "CategoryExpenses.xml")) {
            Toast.makeText(c, c.getResources().getString(R.string.incorrectFile), Toast.LENGTH_SHORT).show();
            return;
        }

        Databases db = new Databases(c, ALL_DATABASES);
        if (db.get(getBaseName(name)) != null) {
            Toast.makeText(c, c.getResources().getString(R.string.dbAlreadyExist), Toast.LENGTH_SHORT).show();
            return;
        }

        ZipArchive.unzip(c.getFilesDir() + "/" + newName, c.getFilesDir().getParent() + "/shared_prefs/", PASS_FROM_ZIP(c));
        db.set(getBaseName(name), TRUE);

        new File(c.getFilesDir() + "/" + newName).delete();
    }

    private static boolean isProtected(String zipFilePath) {
        try {
            ZipFile zipFile = new ZipFile(zipFilePath);
            return zipFile.isEncrypted();
        } catch (ZipException ignore) {
            return false;
        }
    }

    private static String getFileExtension(String fileName) {
        if (fileName == null || fileName.lastIndexOf(".") == -1) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    private static String getBaseName(String fileName) {
        if (fileName == null || fileName.lastIndexOf(".") == -1) {
            return fileName;
        }
        return fileName.substring(0, fileName.lastIndexOf("."));
    }

    private static boolean containsFile(Context c, String newName, String fileName) {
        ZipArchive.unzip(c.getFilesDir() + "/" + newName, c.getFilesDir().getParent() + "/temp/", PASS_FROM_ZIP(c));
        if(new File(c.getFilesDir().getParent() + "/temp/" + fileName).exists()) {
            File folder = new File(c.getFilesDir().getParent() + "/temp");
            deleteFolder(folder);
            return true;
        } else {
            File folder = new File(c.getFilesDir().getParent() + "/temp");
            deleteFolder(folder);
            return false;
        }
    }

    private static boolean deleteFolder(File folder) {
        if (folder.isDirectory()) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    boolean success = deleteFolder(file);
                    if (!success) {
                        return false;
                    }
                }
            }
        }
        return folder.delete();
    }
}
