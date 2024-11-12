package al_hiro.com.Mkoba.Management.System.utils;

import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class Utils {
    public static String getExceptionMessage(Exception e) {
        String msg = e.getMessage();
        if (e.getCause() != null) {
            Throwable cause = e.getCause();
            while (cause.getCause() != null && cause.getCause() != cause) {
                if (cause.getMessage() != null)
                    msg = cause.getMessage();
                cause = cause.getCause();
            }
            if (cause.getMessage() != null)
                return cause.getMessage();
        }
        return msg != null ? msg : "";
    }

    public static String camelCaseToSnakeCase(String camelCase) {
        if (camelCase == null || camelCase.isEmpty()) {
            return "";
        }

        Pattern pattern = Pattern.compile("([a-z])([A-Z]+)");
        Matcher matcher = pattern.matcher(camelCase);
        StringBuilder snakeCase = new StringBuilder();

        while (matcher.find()) {
            matcher.appendReplacement(snakeCase, "$1_" + matcher.group(2).toLowerCase());
        }

        matcher.appendTail(snakeCase);

        // Handle the case where the first letter is uppercase
        if (Character.isUpperCase(camelCase.charAt(0))) {
            snakeCase.setCharAt(0, Character.toLowerCase(snakeCase.charAt(0)));
        }

        return snakeCase.toString();
    }
}
