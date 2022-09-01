package com.ejjiu.image.events;



import javafx.event.Event;
import javafx.event.EventType;

/**
 *
 * 创建人  liangsong
 * 创建时间 2021/04/30 14:39
 */
public class ConfigListEvent<T> extends Event {
    public static final EventType<ConfigListEvent> APPLY_EVENT = new EventType<>("APPLY_EVENT");
    public final T data;

    public ConfigListEvent(EventType<? extends Event> eventType, T data) {
        super(eventType);
        this.data = data;
    }
}
