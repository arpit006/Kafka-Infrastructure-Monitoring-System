package com.navis.consumerlagmonitoring.vo;

import lombok.Data;

import java.util.List;

@Data
public class Topic {

    private String topicName;

    private long totalLag;

    private List<Partition> partition;
}
