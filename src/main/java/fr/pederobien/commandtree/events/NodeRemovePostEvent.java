package fr.pederobien.commandtree.events;

import java.util.StringJoiner;

import fr.pederobien.commandtree.interfaces.INode;

public class NodeRemovePostEvent extends NodeEvent {
	private INode<?> source;

	/**
	 * Creates an event thrown when a node has been removed from another node.
	 * 
	 * @param node   The removed node.
	 * @param source The node from which a node has been removed.
	 */
	public NodeRemovePostEvent(INode<?> node, INode<?> source) {
		super(node);
		this.source = source;
	}

	/**
	 * @return The node from which a node has been removed.
	 */
	public INode<?> getSource() {
		return source;
	}

	@Override
	public String toString() {
		StringJoiner joiner = new StringJoiner(", ", "{", "}");
		joiner.add("source=" + source.getLabel());
		joiner.add("remove=" + getNode().getLabel());
		return String.format("%s_%s", getName(), joiner);
	}
}
