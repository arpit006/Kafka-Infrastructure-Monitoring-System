package com.navis.consumerlagmonitoring.vo;

import lombok.Data;

import java.util.Date;

@Data
public class Partition {

    private int partition;

    private long latestPartitionOffset;

    private long latestConsumerOffset;

    private long previousConsumerOffset;

    private long previousPartitionOffset;

    private Date previousProducerWriteTime;

    private Date lastProducerWriteTime;

    private Date previousConsumerReadTime;

    private Date lastConsumerReadTime;

    private long readSpeed;

    private long writeSpeed;

    private long lag;

    private long averageReadSpeed;

    private long averageWriteSpeed;

    private String isProducerActive;

    private String isConsumerActive;
}
