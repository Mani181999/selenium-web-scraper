package com.browserstack.scraper;

import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;

import java.io.IOException;

public class GoogleTranslate {

    public static String translateText(String text, String sourceLang, String targetLang) throws IOException {
        // Replace with your actual Google Cloud credentials
        String projectId = "browserstack-448817"; 

        // Instantiates a client
        Translate translate = TranslateOptions.newBuilder().setProjectId(projectId).build().getService();

        // The text to translate
        String textToTranslate = text;

        // The target language
        String targetLanguageCode = targetLang;

        Translate translate1 = TranslateOptions.newBuilder()
                .setApiKey("AIzaSyBNoEwM8L7VfOdQ4bkazVzFFQDd6rcIBn8") // Replace with your API key
                .build()
                .getService();
        
        // Translates the text
        Translation translation =
                translate1.translate(
                        textToTranslate,
                        Translate.TranslateOption.targetLanguage(targetLanguageCode));

        return translation.getTranslatedText();
    }
}
