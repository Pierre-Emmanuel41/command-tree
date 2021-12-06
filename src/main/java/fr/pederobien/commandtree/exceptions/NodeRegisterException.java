package fr.pederobien.commandtree.exceptions;

import fr.pederobien.commandtree.interfaces.INode;

public class NodeRegisterException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	private INode<?> node;

	public NodeRegisterException(INode<?> node) {
		super("A node is already registered for label " + node.getLabel());
		this.node = node;
	}

	/**
	 * @return The already registered node.
	 */
	public INode<?> getNode() {
		return node;
	}
}
