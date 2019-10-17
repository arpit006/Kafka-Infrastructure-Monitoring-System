package com.navis.consumerlagmonitoring.vo;

import lombok.Data;

import java.util.List;

@Data
public class ConsumerGroup {

    private String consumerGroupName;

    private List<Topic> topic;

    private String isProducerActive;

    private String isConsumerActive;

    private long totalConsumerSpeed;

    private long totalProducerSpeed;

    private long averageProducerSpeed;

    private long averageConsumerSpeed;
}
