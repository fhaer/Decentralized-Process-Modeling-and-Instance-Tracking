package de.fha.dpmi.modeling.bpmn.elements;

/*
 * License: GNU General Public License v3.0, Copyright: (c) 2018 Felix Härer
 *
 * Under this license, copyright and license notices are required to be preserved
 * and included in any partial or full copies of this software.
 */

/**
 * Implementing class for a Gateway
 */
public class FlowObjectGatewayImpl extends FlowObjectImpl {

	protected Type type;

	public FlowObjectGatewayImpl(Type type) {
		this.type = type;
	}

	/**
	 * Defines the split behavior of this Gateway. For an exclusive gateways,
	 * only one outgoing sequence flow is used (XOR). For an inclusive gateway,
	 * all are used which have a condition that is true (OR). For a parallel
	 * gateway, all outgoing sequence flows are executed in parallel with no
	 * conditions (AND). A Complex gateway defines a custom rule to select
	 * outgoing sequence flows.
	 *
	 */
	public static enum Type {
		/**
		 * exclusive gateway (semantics: xor)
		 */
		Exclusive,
		/**
		 * inclusive gateway (semantics: or)
		 */
		Inclusive,
		/**
		 * parallel gateway (semantics: and)
		 */
		Parallel,
		/**
		 * complex gateway
		 */
		Complex,
		/**
		 * gateway type not known
		 */
		Unknown
	}

	/**
	 * Returns the type of an Event. It defines the split behavior of this
	 * Gateway. For an exclusive gateways, only one outgoing sequence flow is
	 * used (XOR). For an inclusive gateway, all are used which have a condition
	 * that is true (OR). For a parallel gateway, all outgoing sequence flows
	 * are executed in parallel with no conditions (AND). A Complex gateway
	 * defines a custom rule to select outgoing sequence flows.
	 *
	 * @return type of this gateway
	 */
	public Type getType() {
		return type;
	}

}
