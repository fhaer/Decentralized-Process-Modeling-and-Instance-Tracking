package de.fha.dpmi.modeling.bpmn.elements;

/*
 * License: GNU General Public License v3.0, Copyright: (c) 2018 Felix Härer
 *
 * Under this license, copyright and license notices are required to be preserved
 * and included in any partial or full copies of this software.
 */

/**
 * Implementing class for an Event
 */
public class FlowObjectEventImpl extends FlowObjectImpl {

	/**
	 * trigger of this Event
	 *
	 * @see Event
	 */
	protected Trigger trigger;

	/**
	 * type of this Event
	 *
	 * @see Event
	 */
	final protected Type type;

	/**
	 * true if throwing, false if catching
	 *
	 * @see Event
	 */
	protected Boolean isThrowing;

	/**
	 * To create an Event, the lane, type is required and trigger are required.
	 *
	 * @param type
	 *            type of event (see Event interface)
	 */
	public FlowObjectEventImpl(Type type) {
		this.type = type;
	}

	/**
	 * Type of the event. A StartEvent is the event of a pool, an EndEvent is
	 * the last. Events in between are called IntermediateEvents.
	 *
	 */
	public static enum Type {
		StartEvent, IntermediateThrowEvent, IntermediateCatchEvent, EndEvent, Unknown
	}

	/**
	 * Defines how the event is triggered. E.g. through a Message or Timer.
	 *
	 * @author Felix Härer
	 */
	public static enum Trigger {
		/**
		 * event triggered by a message
		 */
		Message,
		/**
		 * event triggered by a timer
		 */
		Timer,
		/**
		 * event triggered by a rule
		 */
		Rule,
		/**
		 * links the trigger to another event
		 */
		Link,
		/**
		 * trigger not known
		 */
		Unknown
	}

	/**
	 * Returns how the event is triggered, e.g. through a Message or a Timer.
	 *
	 * @return trigger of this event
	 */
	public Trigger getTrigger() {
		return trigger;
	}

	/**
	 * Returns the type of this event. A StartEvent is the event of a pool, an
	 * EndEvent is the last. Events in between are called IntermediateEvents.
	 *
	 * @return type of this event
	 */
	public Type getType() {
		return type;
	}

	/**
	 * Returns if an event is throwing. E.g. a message event is throwing if it
	 * sends messages and catching if it receives messages. Null if undefined.
	 *
	 * @return true, if the event is throwing, false if the event is catching,
	 *         null if undefined.
	 */
	public Boolean isThrowingEvent() {
		return isThrowing;
	}

	/**
	 * Sets this event to a catching event. E.g. a message event is throwing if
	 * it sends messages and catching if it receives messages.
	 */
	public void setCatchingEvent() {
		isThrowing = false;
	}

	/**
	 * Sets this event to a throwing event. E.g. a message event is throwing if
	 * it sends messages and catching if it receives messages.
	 */
	public void setThrowingEvent() {
		isThrowing = true;
	}
}
