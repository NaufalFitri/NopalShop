package org.Nopal.nopalShop.Data;

public class TranslateColor {

    public static String text(char altColorChar, String textToTranslate) {
        return textToTranslate.replace(altColorChar, '§');
    }

}
