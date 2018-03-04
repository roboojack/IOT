package com.agilerules.iotled.model

import groovy.transform.Canonical
import groovy.transform.CompileStatic

@CompileStatic
@Canonical
class MessageDTO {

    static final enum Command {
        GO_LEFT,
        GO_RIGHT
    }

    Command command
    LinkedHashMap data

}
