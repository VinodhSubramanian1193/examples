package com.vinteck.example.kafkacustombinder;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.cloud.stream.binder.ConsumerProperties;
import org.springframework.cloud.stream.binder.kafka.properties.KafkaBinderConfigurationProperties;
import org.springframework.cloud.stream.config.BindingProperties;
import org.springframework.cloud.stream.config.BindingServiceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

@Configuration
@Slf4j
public class SpringCloudStreamConfiguration {

    @Primary
    @Bean
    public KafkaBinderConfigurationProperties getKafkaBinderProps(){
        KafkaBinderConfigurationProperties kafkaBinderConfigurationProperties
                = new KafkaBinderConfigurationProperties(new KafkaProperties());

        String brokers = "localhost:9095";
        kafkaBinderConfigurationProperties.setBrokers(brokers);
        kafkaBinderConfigurationProperties.setDefaultBrokerPort("9095");
        Map<String, String> configuration = new HashMap<>();
        kafkaBinderConfigurationProperties.setConfiguration(configuration);

        return kafkaBinderConfigurationProperties;
    }

    @Primary
    @Bean
    public BindingServiceProperties bindingServiceProperties() {
        BindingServiceProperties bindingServiceProperties = new BindingServiceProperties();

        BindingProperties input1BindingProps = getInput1BindingProperties();
        BindingProperties input2BindingProps = getInput2BindingProperties();

        Map<String, BindingProperties> bindingProperties = new HashMap<>();
        bindingProperties.put(StreamBindings.INPUT_1, input1BindingProps);
        bindingProperties.put(StreamBindings.INPUT_2, input2BindingProps);
        bindingServiceProperties.setBindings(bindingProperties);

        return bindingServiceProperties;
    }

    private BindingProperties getInput1BindingProperties() {
        ConsumerProperties consumerProperties = new ConsumerProperties();
        consumerProperties.setMaxAttempts(1);
        consumerProperties.setDefaultRetryable(false);

        BindingProperties props = new BindingProperties();
        props.setDestination("test1");
        props.setContentType(MediaType.APPLICATION_JSON_VALUE);
        props.setGroup("test-group");
        props.setConsumer(consumerProperties);
        return props;
    }

    private BindingProperties getInput2BindingProperties() {
        ConsumerProperties consumerProperties = new ConsumerProperties();
        consumerProperties.setMaxAttempts(1);
        consumerProperties.setDefaultRetryable(false);

        BindingProperties props = new BindingProperties();
        props.setDestination("test2");
        props.setContentType(MediaType.APPLICATION_JSON_VALUE);
        props.setGroup("test-group");
        props.setConsumer(consumerProperties);
        return props;
    }
}
