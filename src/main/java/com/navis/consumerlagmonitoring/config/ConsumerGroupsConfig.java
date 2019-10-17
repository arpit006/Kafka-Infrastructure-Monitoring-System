package com.navis.consumerlagmonitoring.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Configuration
@Data
public class ConsumerGroupsConfig {

    @Value("#{'${consumer.group.names}'.replaceAll('\\s+','').split(',')}")
    private List<String> consumerGroupsWhiteList;

    private Set<String> consumerGroupSet;

    @PostConstruct
    public void initialize() {
        consumerGroupSet = new HashSet<>(consumerGroupsWhiteList);
    }
}
