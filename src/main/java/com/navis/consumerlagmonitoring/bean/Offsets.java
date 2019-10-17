package com.navis.consumerlagmonitoring.bean;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class Offsets {

    private long consumerOffset;

    private long partitionOffset;

    private Date consumerTime;

    private Date producerTime;

    private long previousAvgProducerSpeed;

    private long previousAverageConsumerSpeed;
}
