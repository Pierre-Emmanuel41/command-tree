package fr.pederobien.commandtree.events;

import fr.pederobien.commandtree.interfaces.INode;

public class NodeAvailableChangePostEvent extends NodeEvent {

	/**
	 * Creates an event thrown when a node availability has changed.
	 * 
	 * @param node The node whose the availability has changed.
	 */
	public NodeAvailableChangePostEvent(INode<?> node) {
		super(node);
	}
}
