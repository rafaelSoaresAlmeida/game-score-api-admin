package com.gamescore.gamescoreapiadmin.exception;

import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@Component
public class CustomAttributes extends DefaultErrorAttributes {

    @Override
    public Map<String, Object> getErrorAttributes(final ServerRequest serverRequest, final ErrorAttributeOptions errorAttributeOptions) {
        Map<String, Object> errorAttributesMap = super.getErrorAttributes(serverRequest, errorAttributeOptions);
        Throwable throwable = getError(serverRequest);
        if (throwable instanceof ResponseStatusException) {
            ResponseStatusException responseStatusException = (ResponseStatusException) throwable;
            errorAttributesMap.put("message", responseStatusException.getMessage());
        }

        return errorAttributesMap;
    }
}
