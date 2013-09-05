/**
 *  Let me know when someone knocks on the door, but ignore
 *  when someone is opening the door.
 *
 *  Author: brian@bevey.org
 *  Date: 9/5/13
 */

preferences {
  section("When Someone Knocks?") {
    input name: "multi", type: "device.SmartSenseMulti", title: "Where?"
  }

  section("Knock Delay (defaults to 5s)?") {
    input name: "knockDelay", type: "decimal", title: "How Long?", required: false
  }
}

def installed() {
  state.lastOpen = now()
  subscribe(multi, "acceleration.active", handleEvent)
  subscribe(multi, "contact.open", doorOpen)
}

def updated() {
  unsubscribe()
  doorOpen()
  subscribe(multi, "acceleration.active", handleEvent)
  subscribe(multi, "contact.open", doorOpen)
}

def doorOpen(evt) {
  state.lastOpen = now()
}

def doorKnock() {
  def now = now()

  if((multi.latestValue("contact") == "closed") ||
     (now - (60 * 1000) > state.lastOpen)) {
    log.debug "${multi.label ?: multi.name} detected a knocked."
    sendPush("${multi.label ?: multi.name} detected a knock")
  }

  else {
    log.debug "${multi.label ?: multi.name} knocked, but looks like it was just someone opening the door."
  }
}

def handleEvent(evt) {
  def delay = knockDelay ?: 5
  runIn(delay, "doorKnock")
}