package mx.tec.jobbify_enterprise.validators;

import android.util.Patterns;

import java.util.regex.Pattern;

public class FormValidator {

    public static boolean isValidEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean isValidName(String name){
        return Pattern.compile("^[\\p{L} .'-]+$", Pattern.CASE_INSENSITIVE).matcher(name).find();
    }

    public static boolean isValidPhone(String phone){
        return Patterns.PHONE.matcher(phone).matches();
    }
}
