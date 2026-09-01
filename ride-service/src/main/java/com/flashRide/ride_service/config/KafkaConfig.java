package com.flashRide.ride_service.config;


import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.internals.Topic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

import java.awt.*;

@Configuration
public class KafkaConfig {

    @Bean
   public NewTopic rideRequestTopic(){
       return TopicBuilder
               .name("ride.requested")
               .partitions(3)
               .replicas(1)
               .build();
   }

   @Bean
    public NewTopic rideMatchedTopic(){
        return TopicBuilder
                .name("ride.matched")
                .partitions(3)
                .replicas(1)
                .build();
   }
}
