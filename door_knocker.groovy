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
    input name: "multi", type: "device.SmartSenseMulti", title: "Where?"
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
  subscribe(multi, "acceleration.active", handleEvent)
  subscribe(multi, "contact.closed", doorClosed)
}

def doorClosed(evt) {
  state.lastClosed = now()
}

def doorKnock() {
  if((multi.latestValue("contact") == "closed") &&
     (now() - (60 * 1000) > state.lastClosed)) {
    log.debug("${multi.label ?: multi.name} detected a knock.")
    sendPush("${multi.label ?: multi.name} detected a knock.")
  }

  else {
    log.debug("${multi.label ?: multi.name} knocked, but looks like it was just someone opening the door.")
  }
}

def handleEvent(evt) {
  def delay = knockDelay ?: 5
  runIn(delay, "doorKnock")
}