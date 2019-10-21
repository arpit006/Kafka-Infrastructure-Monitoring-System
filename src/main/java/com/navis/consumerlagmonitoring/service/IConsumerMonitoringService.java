package com.navis.consumerlagmonitoring.service;

import com.navis.consumerlagmonitoring.vo.ConsumerGroup;

import java.util.List;

public interface IConsumerMonitoringService {

    /**
     * Gets the description of all consumer groups
     * @return List of ConsumerGroup
     */
    List<ConsumerGroup> getConsumerGroupsDescription();
}
