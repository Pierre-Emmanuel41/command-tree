package fr.pederobien.commandtree.impl;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import fr.pederobien.commandtree.interfaces.ICommandNode;
import fr.pederobien.commandtree.interfaces.INode;

public class CommandNode<T> extends Node<T> implements ICommandNode<T> {

	/**
	 * Creates a node specified by the given parameters.
	 * 
	 * @param label       The primary node name.
	 * @param explanation The explanation associated to this node.
	 * @param isAvailable True if this node is available, false otherwise.
	 */
	protected CommandNode(String label, T explanation, Supplier<Boolean> isAvailable) {
		super(label, explanation, isAvailable);
	}

	/**
	 * Creates a node specified by the given parameters.
	 * 
	 * @param label       The primary node name.
	 * @param explanation The explanation associated to this node.
	 */
	protected CommandNode(String label, T explanation) {
		this(label, explanation, () -> false);
	}

	@Override
	public List<String> onTabComplete(String[] args) {
		if (!isAvailable())
			return emptyList();

		try {
			String argument = args[0];
			ICommandNode<T> node = getChildren().get(argument);

			// Node not recognized, display all available children nodes.
			if (node == null)
				return filter(getAvailableChildren().map(e -> e.getLabel()), argument);

			// Node not available, display nothing.
			if (!node.isAvailable())
				return emptyList();

			return node.onTabComplete(extract(args, 1));
		} catch (IndexOutOfBoundsException e) {
			// When args is empty -> args[0] throw an IndexOutOfBoundsException
			return emptyList();
		}
	}

	@Override
	public boolean onCommand(String[] args) {
		try {
			String argument = args[0];
			ICommandNode<T> node = getChildren().get(argument);

			if (node == null)
				throw new NodeNotFoundException(getLabel(), argument, args);

			if (!node.isAvailable())
				throw new NotAvailableArgumentException(node.getLabel(), argument);

			return node.onCommand(extract(args, 1));
		} catch (IndexOutOfBoundsException e) {
			// Do nothing
		}
		return true;
	}

	@Override
	public void setParent(INode<T> parent) {
		super.setParent((ICommandNode<T>) parent);
	}

	@Override
	public ICommandNode<T> getParent() {
		return (ICommandNode<T>) super.getParent();
	}

	@Override
	public ICommandNode<T> getRoot() {
		return (ICommandNode<T>) super.getRoot();
	}

	@Override
	public void add(INode<T> node) {
		super.add((ICommandNode<T>) node);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, ICommandNode<T>> getChildren() {
		return (Map<String, ICommandNode<T>>) super.getChildren();
	}
}
