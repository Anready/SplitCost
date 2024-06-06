package com.codersanx.splitcost.utils;

import static com.codersanx.splitcost.utils.Constants.CATEGORY;
import static com.codersanx.splitcost.utils.Constants.EXPENSES;
import static com.codersanx.splitcost.utils.Constants.INCOMES;
import static com.codersanx.splitcost.utils.Constants.MAIN_SETTINGS;

import android.content.Context;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

import java.io.File;


public class Zip {
    public static void packFile(Context c, String name) {
        String[] files = {name + INCOMES, name + EXPENSES, name + CATEGORY + INCOMES,
                name + CATEGORY + EXPENSES, name + MAIN_SETTINGS
        };

        String zipFileName = c.getFilesDir() + "/" + name + "Export.zip";

        String password = "pass";

        try {
            ZipParameters parameters = new ZipParameters();
            parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
            parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);

            parameters.setEncryptFiles(true);
            parameters.setEncryptionMethod(Zip4jConstants.ENC_METHOD_AES);
            parameters.setAesKeyStrength(Zip4jConstants.AES_STRENGTH_256);
            parameters.setPassword(password);

            ZipFile zipFile = new ZipFile(zipFileName);

            for (String targetPath : files) {
                File targetFile = new File(c.getFilesDir().getParent() + "/shared_prefs/", targetPath + ".xml");
                if (targetFile.isFile()) {
                    zipFile.addFile(targetFile, parameters);
                } else if (targetFile.isDirectory()) {
                    zipFile.addFolder(targetFile, parameters);
                }
            }
        } catch (Exception ignored) {
        }
    }
}
