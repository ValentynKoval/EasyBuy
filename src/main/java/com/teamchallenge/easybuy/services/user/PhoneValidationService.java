package com.teamchallenge.easybuy.services.user;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import org.springframework.stereotype.Service;

@Service
public class PhoneValidationService {
    private final PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();

    public String formatToE164(String phoneNumber) {
        try {
            PhoneNumber number = phoneNumberUtil.parse(phoneNumber, null);
            return phoneNumberUtil.format(number, PhoneNumberUtil.PhoneNumberFormat.E164);
        } catch (NumberParseException e) {
            throw new IllegalArgumentException("Invalid phone number: " + e.getMessage());
        }
    }
}
