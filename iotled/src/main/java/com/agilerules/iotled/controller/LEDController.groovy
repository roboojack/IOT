package com.agilerules.iotled.controller

import com.google.common.collect.Maps
import com.pi4j.io.gpio.Pin
import groovy.transform.CompileStatic;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

import java.util.HashMap;
import java.util.Map;

@CompileStatic
@RestController
class LEDController {


	static final Map<String,GpioPinDigitalOutput> pins = [
            "light" : GpioFactory.getInstance().provisionDigitalOutputPin(RaspiPin.GPIO_08,"light", PinState.LOW)
    ]

	@RequestMapping("/status")
	Map<String,Object> status(){
		Map<String,Object> result = Maps.newHashMap()
		pins.each {
			def value = [
					pinState : it.value.state,
					pinMode : it.value.mode,
					pinName : it.value.name,
					pinProperties : it.value.properties,
			]
			result.put(it.key, value)
		}

		return result
	}

	@RequestMapping("/light")
	Map light(){
		return getPinTogleAndReturnState("light")
	}

	Map getPinTogleAndReturnState(String pinName) {
		GpioPinDigitalOutput pin = pins[pinName]
        assert pin != null : "${pinName} is not a known device."
		pin.toggle();
		return [
		        pinState : pin.state,
				pinMode : pin.mode,
				pinName : pin.name,
				pinProperties : pin.properties
		]
	}
}
