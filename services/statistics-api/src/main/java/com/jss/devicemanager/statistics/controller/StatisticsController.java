package com.jss.devicemanager.statistics.controller;

import com.jss.devicemanager.statistics.api.StatisticsApi;
import com.jss.devicemanager.statistics.model.AuthRequest;
import com.jss.devicemanager.statistics.model.AuthResponse;
import com.jss.devicemanager.statistics.model.StatisticsResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatisticsController implements StatisticsApi {

    @Override
    public ResponseEntity<StatisticsResponse> getStatistics(String deviceType) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public ResponseEntity<AuthResponse> logAuth(AuthRequest authRequest) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
