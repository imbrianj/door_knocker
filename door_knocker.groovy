/**
 *  Door Knocker
 *
 *  Author: brian@bevey.org
 *  Date: 9/10/13
 *
 *  Let me know when someone knocks on the door, but ignore
 *  when someone is opening the door.
 */

preferences {
  section("When Someone Knocks?") {
    input name: "knockSensor", type: "capability.accelerationSensor", title: "Where?"
  }

  section("But not when they open this door?") {
    input name: "openSensor", type: "capability.contactSensor", title: "Where?"
  }

  section("Knock Delay (defaults to 5s)?") {
    input name: "knockDelay", type: "number", title: "How Long?", required: false
  }
}

def installed() {
  init()
}

def updated() {
  unsubscribe()
  init()
}

def init() {
  state.lastClosed = now()
  subscribe(knockSensor, "acceleration.active", handleEvent)
  subscribe(openSensor, "contact.closed", doorClosed)
}

def doorClosed(evt) {
  state.lastClosed = now()
}

def doorKnock() {
  if((openSensor.latestValue("contact") == "closed") &&
     (now() - (60 * 1000) > state.lastClosed)) {
    log.debug("${knockSensor.label ?: knockSensor.name} detected a knock.")
    sendPush("${knockSensor.label ?: knockSensor.name} detected a knock.")
  }

  else {
    log.debug("${knockSensor.label ?: knockSensor.name} knocked, but looks like it was just someone opening the door.")
  }
}

def handleEvent(evt) {
  def delay = knockDelay ?: 5
  runIn(delay, "doorKnock")
}