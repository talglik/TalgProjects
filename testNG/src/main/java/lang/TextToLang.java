package lang;

import com.github.pemistahl.lingua.api.Language;
import com.github.pemistahl.lingua.api.LanguageDetector;
import com.github.pemistahl.lingua.api.LanguageDetectorBuilder;

import java.io.IOException;

import static com.github.pemistahl.lingua.api.Language.*;

public class TextToLang {
    public String getLanguage(String text)
    {
        final LanguageDetector detector = LanguageDetectorBuilder.fromLanguages(ENGLISH, FRENCH, GERMAN, SPANISH).withMinimumRelativeDistance(text.length() > 150 ? 0.1 : 0.05).build();
        final Language detectedLanguage = detector.detectLanguageOf(text);
        return detectedLanguage.name();
    }

    public String getLanguage2(String text) throws IOException {
        System.out.println(text.matches("[\\u0590-\\u05FF]"));
        System.out.println(text.matches("[.* \\u0000-~\\u0590-\\u05fe\\u2000-\\u206e]"));
        System.out.println(text.matches( "[.* \\u0590-\\u05FF\u0000-\u0041]*+"));

        return "sdf";
    }

}
