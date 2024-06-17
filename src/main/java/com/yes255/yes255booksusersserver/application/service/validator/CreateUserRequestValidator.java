package com.yes255.yes255booksusersserver.application.service.validator;

import com.yes255.yes255booksusersserver.common.exception.UserEmailNotAvailableException;
import com.yes255.yes255booksusersserver.common.exception.ValidationFailedException;
import com.yes255.yes255booksusersserver.presentation.dto.request.CreateUserRequest;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component
public class CreateUserRequestValidator implements Validator {

    private static final String EMAIL_PATTERN = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
    private static final String PHONE_PATTERN = "^010-\\d{4}-\\d{4}$";

    @Override
    public boolean supports(Class<?> clazz) {
        return CreateUserRequest.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {

        CreateUserRequest createUserRequest = (CreateUserRequest) target;

        String userPhone = createUserRequest.getUserPhone();
        String userEmail = createUserRequest.getUserEmail();

        if (userEmail == null || !userEmail.matches(EMAIL_PATTERN)) {
            throw new UserEmailNotAvailableException("유효한 이메일 형식이 아닙니다. ex) shopping@gmail.com");
        }

//        if (userPhone == null || !userPhone.matches(PHONE_PATTERN)) {
//            throw new ValidationException("유효한 전화번호 형식이 아닙니다. ex) 010-XXXX-XXXX");
//        }
    }
}
