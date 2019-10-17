package com.navis.consumerlagmonitoring.service;

import com.navis.consumerlagmonitoring.bean.Offsets;
import com.navis.consumerlagmonitoring.config.ConsumerGroupsConfig;
import com.navis.consumerlagmonitoring.vo.ConsumerGroup;
import com.navis.consumerlagmonitoring.vo.Partition;
import com.navis.consumerlagmonitoring.vo.Topic;
import com.omarsmak.kafka.consumer.lag.monitoring.client.KafkaConsumerLagClient;
import com.omarsmak.kafka.consumer.lag.monitoring.client.KafkaConsumerLagClientFactory;
import com.omarsmak.kafka.consumer.lag.monitoring.client.data.Lag;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

@Service
public class ConsumerMonitoringService implements IConsumerMonitoringService {

    Comparator<ConsumerGroup> comparator = Comparator.comparing(t -> t.getTopic().get(0).getPartition().get(0).getPartition());
    @Value("${kafka.servers}")
    private String kafkaServer;
    private List<ConsumerGroup> consumerGroupList = new ArrayList<>();
    @Autowired
    private ConsumerGroupsConfig consumerGroupsConfig;
    private Map<String, Map<String, Map<Integer, Offsets>>> groupTopicPartitionOffsetMap = new HashMap<>();

    private long count = 0L;

    @Scheduled(fixedRate = 60000)
    public void getConsumerDescription() {
        ++count;
        Set<String> set = consumerGroupsConfig.getConsumerGroupSet();
        List<ConsumerGroup> result = new ArrayList<>();
        for (String cgs : set) {
            Map<String, Map<Integer, Offsets>> topicPartitionOffsetMap = groupTopicPartitionOffsetMap.computeIfAbsent(cgs, k -> new HashMap<>());
            result.add(getConsumerDescriptionForEachConsumerGroup(cgs, topicPartitionOffsetMap));
        }
        consumerGroupList = new ArrayList<>();
        consumerGroupList.addAll(result);
    }

    private ConsumerGroup getConsumerDescriptionForEachConsumerGroup(String inConsumerGroupName, Map<String, Map<Integer, Offsets>> topicPartitionOffsetMap) {
        Properties properties = new Properties();
        properties.setProperty(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServer);
        final KafkaConsumerLagClient kafkaConsumerLagClient = KafkaConsumerLagClientFactory.create(properties);
        List<Lag> consumerLag = kafkaConsumerLagClient.getConsumerLag(inConsumerGroupName);
        ConsumerGroup cg = new ConsumerGroup();
        cg.setConsumerGroupName(inConsumerGroupName);
        List<Topic> topics = new ArrayList<>();
        boolean isProducerActive = false;
        boolean isConsumerActive = false;
        long totalProducerSpeed = 0L;
        long totalConsumerSpeed = 0L;
        for (Lag l : consumerLag) {
            Map<Integer, Long> latestConsumerOffsetMap = l.getLatestConsumerOffsets();
            Map<Integer, Long> latestPartitionOffsetMap = l.getLatestTopicOffsets();
            Map<Integer, Long> lagPerPartitionMap = l.getLagPerPartition();
            Set<Integer> keys = latestConsumerOffsetMap.keySet();
            Topic topic = new Topic();
            topic.setTopicName(l.getTopicName());
            List<Partition> partitions = new ArrayList<>();
            Comparator<Partition> comparator = Comparator.comparing(Partition::getPartition);
            Map<Integer, Offsets> partitionOffsetMap = topicPartitionOffsetMap.computeIfAbsent(l.getTopicName(), k -> new HashMap<>());
            for (Integer key : keys) {
                Partition partition = new Partition();
                Offsets offset;
                if (!partitionOffsetMap.containsKey(key)) {
                    Offsets offsets = new Offsets(0, 0, null, null, 0, 0);
                    partitionOffsetMap.put(key, offsets);
                }
                offset = partitionOffsetMap.get(key);
                partition.setPartition(key);
                partition.setPreviousPartitionOffset(offset.getPartitionOffset());
                partition.setLatestPartitionOffset(latestPartitionOffsetMap.get(key));
                partition.setPreviousProducerWriteTime(offset.getProducerTime());
                partition.setLastProducerWriteTime(new Date());
                partition.setPreviousConsumerOffset(offset.getConsumerOffset());
                partition.setLatestConsumerOffset(latestConsumerOffsetMap.get(key));
                partition.setPreviousConsumerReadTime(offset.getConsumerTime());
                partition.setLastConsumerReadTime(new Date());
                partition.setLag(lagPerPartitionMap.get(key));

                long diffProducerOffset = latestPartitionOffsetMap.get(key) - offset.getPartitionOffset();
                long diffConsumerOffset = latestConsumerOffsetMap.get(key) - offset.getConsumerOffset();
                long producerPrevReadTime = offset.getProducerTime() == null ? 0 : offset.getProducerTime().getTime();
                long consumerPrevReadTime = offset.getConsumerTime() == null ? 0 : offset.getConsumerTime().getTime();
                long latestTimeInMillis = System.currentTimeMillis();

                if (diffProducerOffset == 0L) {
                    isProducerActive = isProducerActive || false;
                    partition.setIsProducerActive("INACTIVE");
                } else {
                    isProducerActive = isProducerActive || true;
                    partition.setIsProducerActive("ACTIVE");
                }
                if (diffConsumerOffset == 0L) {
                    isConsumerActive = isConsumerActive || false;
                    partition.setIsConsumerActive("INACTIVE");
                } else {
                    isConsumerActive = isConsumerActive || true;
                    partition.setIsConsumerActive("ACTIVE");
                }

                long writeSpeed = (long) ((diffProducerOffset * 60000) / (latestTimeInMillis - producerPrevReadTime));
                long readSpeed = (long) ((diffConsumerOffset * 60000) / (latestTimeInMillis - consumerPrevReadTime));

                totalProducerSpeed += writeSpeed;
                totalConsumerSpeed += readSpeed;

                partition.setWriteSpeed(writeSpeed);
                partition.setReadSpeed(readSpeed);

                long avgProducerSpeed = (writeSpeed + offset.getPreviousAvgProducerSpeed() * (count - 1)) / count;
                long avgConsumerSpeed = (readSpeed + offset.getPreviousAverageConsumerSpeed() * (count - 1)) / count;

                partition.setAverageWriteSpeed(avgProducerSpeed);
                partition.setAverageReadSpeed(avgConsumerSpeed);
                partitions.add(partition);
                Offsets newOffset = new Offsets(latestConsumerOffsetMap.get(key), latestPartitionOffsetMap.get(key), new Date(), new Date(), avgProducerSpeed, avgConsumerSpeed);
                partitionOffsetMap.put(key, newOffset);
            }
            topicPartitionOffsetMap.put(l.getTopicName(), partitionOffsetMap);
            partitions.sort(comparator);
            topic.setPartition(partitions);
            topic.setTotalLag(l.getTotalLag());
            topics.add(topic);
        }
        cg.setTopic(topics);
        String consumerStatus;
        String producerStatus;
        if (!isConsumerActive) { //false
            consumerStatus = "INACTIVE!";
        } else {
            consumerStatus = "ACTIVE!";
        }
        if (!isProducerActive) {
            producerStatus = "INACTIVE";
        } else {
            producerStatus = "ACTIVE!";
        }
        cg.setIsConsumerActive(consumerStatus);
        cg.setIsProducerActive(producerStatus);
        cg.setTotalConsumerSpeed(totalConsumerSpeed);
        cg.setTotalProducerSpeed(totalProducerSpeed);
        return cg;
    }

    @Override
    public List<ConsumerGroup> getConsumerGroupsDescription() {
        return consumerGroupList;
    }
}
