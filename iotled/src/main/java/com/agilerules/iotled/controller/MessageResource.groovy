package com.agilerules.iotled.controller

import com.agilerules.iotled.model.MessageDTO
import com.google.common.collect.Maps
import com.google.gson.Gson
import groovy.transform.CompileStatic
import org.apache.camel.ProducerTemplate
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@CompileStatic
@RestController
class MessageResource {

	Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	ProducerTemplate producerTemplate

	@RequestMapping(method = RequestMethod.POST, path = "/message")
	void pushMessage(@RequestBody MessageDTO body){
		producerTemplate.sendBodyAndHeaders("direct:googlePubsubStart", new Gson().toJson(body), Maps.newHashMap());
		log.info("Successfully published message to Pubsub.");
	}

}
