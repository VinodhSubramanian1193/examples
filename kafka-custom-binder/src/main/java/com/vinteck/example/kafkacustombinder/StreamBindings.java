package com.vinteck.example.kafkacustombinder;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface StreamBindings {
    public static final String INPUT_1 = "input1";
    public static final String INPUT_2 = "input2";

    @Input(StreamBindings.INPUT_1)
    SubscribableChannel input1();

    @Input(StreamBindings.INPUT_2)
    SubscribableChannel input2();
}
