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

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import ir.mahdi.mzip.zip.ZipArchive;

public class Zip {
    public static void packFile(Context c, String name) {
        String[] files = {name + INCOMES, name + EXPENSES, name + CATEGORY + INCOMES,
                name + CATEGORY + EXPENSES, name + MAIN_SETTINGS
        };

        String zipFileName = c.getFilesDir() + "/" + name + ".zip";

        try {
            ZipParameters parameters = new ZipParameters();
            parameters.setCompressionMethod(Zip4jConstants.COMP_STORE);
            parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_FASTEST);

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

    public static boolean extractZip(Context c, String name) {
        return extractZip(c, name, false, null);
    }

    public static boolean extractZip(Context c, String name, boolean isSync, String additionalName) {
        if (!name.endsWith("sce")) {
            return false;
        }

        String baseName = getBaseName(name);

        if (baseName.length() < 2 || baseName.length() > 17) {
            return false;
        }

        if (name.contains("@") || name.contains(" ") || name.contains(":")) {
            return false;
        }

        File oldFile = new File(c.getFilesDir(), name);
        String newName = name.replace(".sce", ".zip");
        renameFile(oldFile, newName);

        if (!isProtected(c.getFilesDir() + "/" + newName)){
            return false;
        }

        if (!containsFile(c, newName, baseName + "CategoryExpenses.xml")) {
            deleteFolder(new File(c.getFilesDir().getParent() + "/temp"));
            return false;
        }

        Databases db = new Databases(c, ALL_DATABASES);
        if (db.get(baseName) != null && !isSync) {
            return false;
        }

        moveFiles(c, baseName, additionalName == null ? "" : additionalName);

        if (!isSync) db.set(getBaseName(name), TRUE);

        new File(c.getFilesDir() + "/" + newName).delete();
        return true;
    }

    private static void moveFiles(Context c, String name, String prefix) {
        String[] files = {name + INCOMES, name + EXPENSES, name + CATEGORY + INCOMES,
                name + CATEGORY + EXPENSES, name + MAIN_SETTINGS
        };

        for (String file : files) {
            File targetFile = new File(c.getFilesDir().getParent() + "/temp/", file + ".xml");

            try {
                copyFile(targetFile, new File(c.getFilesDir().getParent() + "/shared_prefs/", prefix + file + ".xml"));
                targetFile.delete();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void copyFile(File sourceFile, File destFile) throws IOException {
        if (!destFile.exists()) {
            destFile.createNewFile();
        }

        try (FileInputStream fis = new FileInputStream(sourceFile);
             FileOutputStream fos = new FileOutputStream(destFile)) {

            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                fos.write(buffer, 0, length);
            }
        }
    }

    private static boolean isProtected(String zipFilePath) {
        try {
            ZipFile zipFile = new ZipFile(zipFilePath);
            return zipFile.isEncrypted();
        } catch (ZipException ignore) {
            return false;
        }
    }

    private static String getBaseName(String fileName) {
        if (fileName == null || fileName.lastIndexOf(".") == -1) {
            return fileName;
        }
        return fileName.substring(0, fileName.lastIndexOf("."));
    }

    private static boolean containsFile(Context c, String newName, String fileName) {
        ZipArchive.unzip(c.getFilesDir() + "/" + newName, c.getFilesDir().getParent() + "/temp/", PASS_FROM_ZIP(c));
        return new File(c.getFilesDir().getParent() + "/temp/" + fileName).exists();
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
