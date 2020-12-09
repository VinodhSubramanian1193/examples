package com.vinteck.example.kafkacustombinder;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Payload;

@Slf4j
@EnableBinding(StreamBindings.class)
@SpringBootApplication
public class KafkaCustomBinderApplication {

	public static void main(String[] args) {
		SpringApplication.run(KafkaCustomBinderApplication.class, args);
	}

	@StreamListener(StreamBindings.INPUT_1)
	public void process_input_test1(@Payload Message<?> message){
		log.info("Message received in input-1 {}", message.getPayload());
	}

	@StreamListener(StreamBindings.INPUT_2)
	public void process_input_test2(@Payload Message<?> message){
		log.info("Message received in input-2 {}", message.getPayload());
	}
}
