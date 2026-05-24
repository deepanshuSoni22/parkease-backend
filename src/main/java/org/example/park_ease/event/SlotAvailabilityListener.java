package org.example.park_ease.event;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class SlotAvailabilityListener {

    private final SimpMessagingTemplate messagingTemplate;

    public SlotAvailabilityListener(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @EventListener
    public void handleSlotAvailable(ParkingSlotAvailableEvent event) {
        SlotAvailableMessage message = new SlotAvailableMessage(
                event.getSlotId(),
                event.getSlotNumber(),
                event.getParkingLotId()
        );

        // Broadcast to all clients listening to this parking lot
        messagingTemplate.convertAndSend("/topic/slot-available", message);
    }
}
