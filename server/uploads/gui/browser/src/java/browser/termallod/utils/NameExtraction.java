/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package browser.termallod.utils;


import browser.termallod.constants.Symbols;
import java.io.File;

/**
 *
 * @author elahi
 */
public class NameExtraction implements Symbols{

    public static String getCategoryName(String browser, File categoryFile, String modelFileExtension) {
        String categoryName = categoryFile.getName();
        String[] info = categoryName.split(UNDERSCORE);
        categoryName = info[0] + UNDERSCORE + info[1] + modelFileExtension;
        categoryName = categoryName.replace(modelFileExtension.trim(), "");
        return categoryName;
    }
    
    public static String getPairName(File categoryFile,String categoryName, String langCode,String modelFileExtension) {
        String pair=categoryFile.getName().replace(categoryName, "");
        pair=pair.replace(UNDERSCORE+langCode+UNDERSCORE, "");
        pair=pair.replace(modelFileExtension, "");
        return pair;
    }

    public static String getLanCode(File file, String MODEL_EXTENSION) {
        String fileName = file.getName();
        fileName = fileName.replace(MODEL_EXTENSION, "");
        String[] info = fileName.split("_");
        return info[2];
    }

}
