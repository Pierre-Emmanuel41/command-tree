package fr.pederobien.commandtree.events;

import java.util.StringJoiner;

import fr.pederobien.commandtree.interfaces.INode;

public class NodeAddPostEvent extends NodeEvent {
	private INode<?> source;

	/**
	 * Creates an event thrown when a node has been added to another node.
	 * 
	 * @param node   The added node.
	 * @param source The node to which a node has been added.
	 */
	public NodeAddPostEvent(INode<?> node, INode<?> source) {
		super(node);
		this.source = source;
	}

	/**
	 * @return The node to which a node has been added.
	 */
	public INode<?> getSource() {
		return source;
	}

	@Override
	public String toString() {
		StringJoiner joiner = new StringJoiner(", ", "{", "}");
		joiner.add("source=" + source.getLabel());
		joiner.add("add=" + getNode().getLabel());
		return String.format("%s_%s", getName(), joiner);
	}
}
