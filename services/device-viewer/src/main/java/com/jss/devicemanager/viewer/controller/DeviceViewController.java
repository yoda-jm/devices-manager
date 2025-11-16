package com.jss.devicemanager.viewer.controller;

import com.jss.devicemanager.viewer.service.DeviceViewerService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class DeviceViewController {

    private final DeviceViewerService deviceViewerService;

    public DeviceViewController(DeviceViewerService deviceViewerService) {
        this.deviceViewerService = deviceViewerService;
    }

    @GetMapping("/")
    public String index() {
        return "index.html";
    }

    @GetMapping("/api/devices")
    @ResponseBody
    public ResponseEntity<List<DeviceDTO>> getDevices(
            @RequestParam(required = false) String search) {

        List<DeviceDTO> devices = deviceViewerService.searchDevices(search)
                .stream()
                .map(DeviceDTO::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(devices);
    }

    @GetMapping("/api/statistics")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", deviceViewerService.getTotalCount());
        stats.put("byType", deviceViewerService.getDeviceStatistics()
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        e -> e.getKey().name(),
                        Map.Entry::getValue
                )));

        return ResponseEntity.ok(stats);
    }
}
