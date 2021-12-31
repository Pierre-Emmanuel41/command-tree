package fr.pederobien.commandtree.events;

import java.util.StringJoiner;

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

	@Override
	public String toString() {
		StringJoiner joiner = new StringJoiner(", ", "{", "}");
		joiner.add("node=" + getNode().getLabel());
		joiner.add("oldAvailability=" + !getNode().isAvailable());
		joiner.add("newAvailability=" + getNode().isAvailable());
		return String.format("%s_%s", getName(), joiner);
	}
}
