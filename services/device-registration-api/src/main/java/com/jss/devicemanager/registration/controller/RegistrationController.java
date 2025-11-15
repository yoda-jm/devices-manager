package com.jss.devicemanager.registration.controller;

import com.jss.devicemanager.common.security.RequireApiKey;
import com.jss.devicemanager.registration.api.RegistrationApi;
import com.jss.devicemanager.registration.model.RegisterRequest;
import com.jss.devicemanager.registration.model.RegisterResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequireApiKey
public class RegistrationController implements RegistrationApi {

    @Override
    public ResponseEntity<RegisterResponse> registerDevice(RegisterRequest registerRequest) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
