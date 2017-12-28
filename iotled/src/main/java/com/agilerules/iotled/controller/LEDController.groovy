package com.agilerules.iotled.controller

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

	static final Map<String,GpioPinDigitalOutput> pins = new HashMap<String, GpioPinDigitalOutput>();

	@RequestMapping("/")
	String greeting(){
		return "Hello World!";
	}

	@RequestMapping("/status")
	Map<String,GpioPinDigitalOutput> status(){
		return pins;
	}

	@RequestMapping("/light")
	Map light(){
		return getPinTogleAndReturnState(RaspiPin.GPIO_08, "light")
	}

	Map getPinTogleAndReturnState(Pin pinNumber, String pinName) {
		GpioPinDigitalOutput pin = pins.get(pinName);
		if(pin==null){
			GpioController gpio = GpioFactory.getInstance();
			pin = gpio.provisionDigitalOutputPin(pinNumber,pinName, PinState.LOW);
			pins.put(pinName, pin);
		}
		pin.toggle();
		return [
		        pinState : pin.state,
				pinMode : pin.mode,
				pinName : pin.name,
				pinProperties : pin.properties
		]
	}
}
