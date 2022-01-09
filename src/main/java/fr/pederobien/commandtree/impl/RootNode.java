package fr.pederobien.commandtree.impl;

import java.util.function.Supplier;

import fr.pederobien.commandtree.interfaces.IHelperNode;
import fr.pederobien.commandtree.interfaces.INode;
import fr.pederobien.commandtree.interfaces.IRootNode;

public abstract class RootNode<T> extends Node<T> implements IRootNode<T> {
	private IHelperNode<T> helperNode;

	/**
	 * Creates a root node based on the given parameters with an empty list of aliases.
	 * 
	 * @param label       The primary node name.
	 * @param explanation The explanation associated to this node.
	 * @param isAvailable True if this node is available, false otherwise.
	 * @param helperNode  The helper associated to this root.
	 */
	protected RootNode(String label, T explanation, Supplier<Boolean> isAvailable, IHelperNode<T> helperNode) {
		super(label, explanation, isAvailable);
		this.helperNode = helperNode;
	}

	/**
	 * Creates a root node specified by the given parameters with an empty list of aliases.
	 * 
	 * @param label       The primary node name.
	 * @param explanation The explanation associated to this node.
	 * @param isAvailable True if this node is available, false otherwise.
	 */
	protected RootNode(String label, T explanation, Supplier<Boolean> isAvailable) {
		super(label, explanation, isAvailable);
		helperNode = new HelperNode<T>(this);
	}

	/**
	 * Creates a root node specified by the given parameters with an empty list of aliases.
	 * 
	 * @param label       The primary node name.
	 * @param explanation The explanation associated to this node.
	 * @param isAvailable True if this node is available, false otherwise.
	 */
	protected RootNode(String label, T explanation) {
		this(label, explanation, () -> false);
	}

	@Override
	public void setParent(INode<T> parent) {
		throw new IllegalStateException("A root node has no parent");
	}

	/**
	 * @return The helper associated to this root node.
	 */
	protected IHelperNode<T> getHelper() {
		return helperNode;
	}

	/**
	 * Set the helper associated to this root node. An helper is responsible to display the explanation of one or several children
	 * associated to this root.
	 * 
	 * @return The helper associated to this root node.
	 */
	protected void setHelperNode(IHelperNode<T> helperNode) {
		this.helperNode = helperNode;
	}

	/**
	 * Adds each children of this root to the node returned by the given supplier.
	 * 
	 * @param supplier The supplier used to get the new root of the children.
	 * 
	 * @return The created node.
	 */
	protected INode<T> export(Supplier<INode<T>> supplier) {
		INode<T> root = supplier.get();
		for (INode<T> child : getChildren().values())
			root.add(child);
		return root;
	}
}
