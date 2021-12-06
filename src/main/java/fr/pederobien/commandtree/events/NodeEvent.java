package fr.pederobien.commandtree.events;

import fr.pederobien.commandtree.interfaces.INode;
import fr.pederobien.utils.event.Event;

public class NodeEvent extends Event {
	private INode<?> node;

	/**
	 * Creates a node event.
	 * 
	 * @param node The node source involved in this event.
	 */
	public NodeEvent(INode<?> node) {
		this.node = node;
	}

	/**
	 * @return The node involved in this event.
	 */
	public INode<?> getNode() {
		return node;
	}
}
