package com.navis.consumerlagmonitoring.controller;

import com.navis.consumerlagmonitoring.service.IConsumerMonitoringService;
import com.navis.consumerlagmonitoring.vo.ConsumerGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/monitor")
public class ConsumerMonitoringController {

    @Autowired
    private IConsumerMonitoringService consumerMonitoringService;

    @GetMapping(value = "", produces = "application/json")
    public List<ConsumerGroup> getConsumerGroupInfo() {
        return consumerMonitoringService.getConsumerGroupsDescription();
    }
}
