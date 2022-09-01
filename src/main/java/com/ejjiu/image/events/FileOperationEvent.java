package com.ejjiu.image.events;

import java.io.File;

import javafx.event.Event;
import javafx.event.EventType;

/**
 *
 * 创建人  liangsong
 * 创建时间 2021/06/10 11:55
 */
public class FileOperationEvent extends Event {
    public static final EventType<FileOperationEvent> REMOVE = new EventType<>("REMOVE");
    public final File file;

    public FileOperationEvent(EventType<? extends Event> eventType, File file) {
        super(eventType);
        this.file = file;
    }
}
