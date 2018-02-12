package com.agilerules.iotled.controller

import com.google.common.collect.Maps
import groovy.transform.CompileStatic
import org.apache.camel.ProducerTemplate
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@CompileStatic
@RestController
class MessageResource {

	Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	ProducerTemplate producerTemplate

	@RequestMapping("/message")
	void pushMessage(@RequestParam("body") body){
		producerTemplate.sendBodyAndHeaders("direct:googlePubsubStart", body, Maps.newHashMap());
		log.info("Successfully published message to Pubsub.");
	}

}
