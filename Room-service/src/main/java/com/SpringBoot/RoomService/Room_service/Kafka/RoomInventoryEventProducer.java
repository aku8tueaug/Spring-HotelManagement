package com.SpringBoot.RoomService.Room_service.Kafka;

import com.SpringBoot.RoomService.Room_service.DTO.RoomInventoryEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoomInventoryEventProducer {

    private final KafkaTemplate<String, RoomInventoryEvent> kafkaTemplate;

    private static final String TOPIC =
            "room-inventory-events";

    public void publish(RoomInventoryEvent event)
    {
        kafkaTemplate.send(
                TOPIC,
                event.operationId(),
                event
        );
    }
}
