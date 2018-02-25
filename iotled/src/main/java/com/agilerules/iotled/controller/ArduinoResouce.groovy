package com.agilerules.iotled.controller

import com.fazecast.jSerialComm.SerialPort
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@CompileStatic
@RestController
class ArduinoResouce {

    Logger log = LoggerFactory.getLogger(getClass())

    private SerialPort serialPort;

    @RequestMapping("/sendSerialData")
    synchronized void sendSerialData(@RequestParam("data") String data) {
        serialPort = getSerialPort()
        log.info("Using SerialPort ${serialPort.descriptivePortName}, ${serialPort.systemPortName} and sending data '${data}'")

        serialPort.getOutputStream().write(
        """
        ${data}
        ?
        """.bytes)
        serialPort.getOutputStream().flush();

        byte[] readBuffer = new byte[serialPort.bytesAvailable()];
        serialPort.readBytes(readBuffer, readBuffer.length);
        serialPort.getInputStream()
        log.info(new String(readBuffer));
    }

    private SerialPort getSerialPort() {
        if(serialPort && serialPort.isOpen()) {
            return serialPort
        }

        SerialPort[] comPorts = SerialPort.getCommPorts();
        log.info("Found ${comPorts*.descriptivePortName}")
        serialPort = SerialPort.getCommPort("/dev/ttyACM0");  // TODO: move to config
        serialPort.setBaudRate(115200)
        serialPort.setParity(SerialPort.NO_PARITY)
        serialPort.setNumStopBits(SerialPort.ONE_STOP_BIT)
        serialPort.openPort();
        serialPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 1000, 0);
        return serialPort
    }


}
