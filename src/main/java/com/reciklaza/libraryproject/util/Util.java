package com.reciklaza.libraryproject.util;

import com.reciklaza.libraryproject.exception.NotValidUserSubmissionException;

public class Util {

    /**
     * Validates that a string value is not blank.
     * If the value is null or blank, a {@code NotValidUserSubmissionException} is thrown.
     *
     * @param value     the string value to validate
     * @param fieldName the name of the field being validated
     * @throws NotValidUserSubmissionException if the value is null or blank
     */
    public static void validateNotBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new NotValidUserSubmissionException(fieldName + " can't be blank");
        }
    }

}
