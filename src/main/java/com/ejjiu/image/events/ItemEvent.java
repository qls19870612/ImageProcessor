package com.ejjiu.image.events;

import javafx.event.Event;
import javafx.event.EventType;

/**
 *
 * 创建人  liangsong
 * 创建时间 2022/08/10 15:17
 */
public class ItemEvent<T> extends Event {
    public static final EventType<ItemEvent> ITEM_SELECT = new EventType<>("ITEM_SELECT");
    public static final EventType<ItemEvent> ITEM_DELETE = new EventType<>("ITEM_DELETE");
    public static final EventType<ItemEvent> ITEM_SORT = new EventType<>("ITEM_SORT");
    public final T info;
    
    public ItemEvent(EventType<? extends Event> eventType, T info) {
        super(eventType);
        this.info = info;
    }
}
