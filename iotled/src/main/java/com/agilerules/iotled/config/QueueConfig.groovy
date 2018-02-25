
package com.agilerules.iotled.config

import com.google.api.client.googleapis.json.GoogleJsonResponseException
import com.google.api.services.pubsub.Pubsub
import com.google.api.services.pubsub.model.Subscription
import com.google.api.services.pubsub.model.Topic
import groovy.transform.CompileStatic
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.component.google.pubsub.GooglePubsubComponent
import org.apache.camel.component.google.pubsub.GooglePubsubConnectionFactory
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@CompileStatic
public class QueueConfig {

    Logger log = LoggerFactory.getLogger(QueueConfig.class)

    @Value('${pubsub.projectId}')
    String pubsubProjectId
    @Value('${pubsub.topic}')
    String pubsubTopic
    @Value('${pubsub.subscription}')
    String pubsubSubscription
    @Value('${GOOGLE_APPLICATION_CREDENTIALS}')
    String credentialsFileLocation

    @Bean
    GooglePubsubComponent gPubSub() {
        return createComponent()
    }

    @Bean
    RouteBuilder googlePubSubConsumer() {
        createTopicSubscriptionPair();
        return new RouteBuilder() {
            void configure() {
                log.info("About to start route: Google Pubsub -> Log ")

                from("gPubSub:${pubsubProjectId}:${pubsubSubscription}?maxMessagesPerPoll=1&concurrentConsumers=1")
                        .routeId("FromGooglePubsub")
                        .log('Got a message from queue: ${body}')
            }
        }
    }

    @Bean
    RouteBuilder googlePubSubProducer() {
        return new RouteBuilder() {
            void configure() {

                from("direct:googlePubsubStart")
                        .routeId("DirectToGooglePubsub")
                        .to("gPubSub:${pubsubProjectId}:${pubsubTopic}")
                        .log('${headers}')
            }
        }
    }



    GooglePubsubComponent createComponent() {
        GooglePubsubComponent component = new GooglePubsubComponent()
        GooglePubsubConnectionFactory connectionFactory = createConnectionFactory()
        component.setConnectionFactory(connectionFactory)
        return component
    }

    GooglePubsubConnectionFactory createConnectionFactory() {
        GooglePubsubConnectionFactory connectionFactory = new GooglePubsubConnectionFactory()
        connectionFactory.setCredentialsFileLocation(credentialsFileLocation)
        return connectionFactory
    }




    void createTopicSubscriptionPair()  {
        String topicFullName = "projects/${pubsubProjectId}/topics/${pubsubTopic}"
        String subscriptionFullName = "projects/${pubsubProjectId}/subscriptions/${pubsubSubscription}"

        Pubsub pubsub = createConnectionFactory().getDefaultClient()

        try {
            pubsub.projects()
                    .topics()
                    .create(topicFullName, new Topic())
                    .execute()
        } catch (GoogleJsonResponseException e) {
            // 409 indicates that the resource is available already
            if (409 == e.getStatusCode()) {
                log.info("Topic " + pubsubTopic + " already exist")
            } else {
                throw e
            }
        }

        try {
            Subscription subscription = new Subscription()
                    .setTopic(topicFullName)
                    .setAckDeadlineSeconds(15)

            pubsub.projects()
                    .subscriptions()
                    .create(subscriptionFullName, subscription)
                    .execute()
        } catch (GoogleJsonResponseException e) {
            // 409 indicates that the resource is available already
            if (409 == e.getStatusCode()) {
                log.info("Subscription " + pubsubSubscription + " already exist")
            } else {
                throw e
            }
        }
    }


}
