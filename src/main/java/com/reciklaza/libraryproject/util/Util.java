package com.reciklaza.libraryproject.util;

import com.reciklaza.libraryproject.exception.NotValidUserSubmissionException;

public class Util {

    public static void validateNotBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new NotValidUserSubmissionException(fieldName + " can't be blank");
        }
    }
}
