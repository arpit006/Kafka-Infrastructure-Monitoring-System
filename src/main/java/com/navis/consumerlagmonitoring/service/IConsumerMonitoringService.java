package com.navis.consumerlagmonitoring.service;

import com.navis.consumerlagmonitoring.vo.ConsumerGroup;

import java.util.List;

public interface IConsumerMonitoringService {

    List<ConsumerGroup> getConsumerGroupsDescription();
}
