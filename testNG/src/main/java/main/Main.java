package main;

import lang.CheckRecords;
import org.springframework.cloud.openfeign.EnableFeignClients;

import java.io.IOException;

@EnableFeignClients
public class Main {
    public static void main(String[] args) throws IOException {
        CheckRecords cr = new CheckRecords();
        cr.getRecordDetail("937bc70d-0bf7-4714-abc3-32339797edb4");
        cr.getRecordDetailsHttpClient();
        String str = "Hello World";
        String hstr = "שלום עולם,  Aדוד מלך ישראל";


        System.out.println(str.matches("[\\u0590-\\u05FF]"));
        System.out.println(hstr.matches("[.* \\u0000-~\\u0590-\\u05fe\\u2000-\\u206e]"));
        System.out.println(hstr.matches( "[.* \\u0590-\\u05FF\u0000-\u0041]*+"));

        char ch = 'é';
        char hch = 'ñ';
        int ascii = ch;
        // You can also cast char to int
        int castAscii = hch;

        System.out.println("The ASCII value of " + ch + " is: " + ascii);
        System.out.println("The ASCII value of " + ch + " is: " + castAscii);

//        TextToLang ttl = new TextToLang();
//        System.out.println(ttl.getLanguage("los idiomas son impresionantes los idiomas son impresionantes los idiomas son impresionantes los idiomas son impresionantes los idiomas son impresionantes los idiomas son impres שלום ומה נשמע שלום עולם שלום כולם שביל הבריחה"));
    }
}
