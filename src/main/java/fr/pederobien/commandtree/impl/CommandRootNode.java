package fr.pederobien.commandtree.impl;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

import fr.pederobien.commandtree.exceptions.NodeNotFoundException;
import fr.pederobien.commandtree.exceptions.NotAvailableArgumentException;
import fr.pederobien.commandtree.exceptions.NotAvailableCommandException;
import fr.pederobien.commandtree.interfaces.ICommandHelperNode;
import fr.pederobien.commandtree.interfaces.ICommandNode;
import fr.pederobien.commandtree.interfaces.IHelperNode;
import fr.pederobien.commandtree.interfaces.INode;

public class CommandRootNode<T> extends RootNode<T> implements ICommandNode<T> {

	/**
	 * Creates a root node based on the given parameters with an empty list of aliases.
	 * 
	 * @param label       The primary node name.
	 * @param explanation The explanation associated to this node.
	 * @param isAvailable True if this node is available, false otherwise.
	 * @param helperNode  The helper associated to this root.
	 */
	public CommandRootNode(String label, T explanation, Supplier<Boolean> isAvailable, ICommandHelperNode<T> helperNode) {
		super(label, explanation, isAvailable, helperNode);
	}

	/**
	 * Creates a root node based on the given parameters with an empty list of aliases.
	 * 
	 * @param label       The primary node name.
	 * @param explanation The explanation associated to this node.
	 * @param isAvailable True if this node is available, false otherwise.
	 * @param helperNode  The helper associated to this root.
	 */
	public CommandRootNode(String label, T explanation, Supplier<Boolean> isAvailable, Consumer<INode<T>> displayer) {
		super(label, explanation, isAvailable);
		setHelperNode(new CommandHelperNode<T>(this, displayer));
	}

	/**
	 * Creates a root node specified by the given parameters with an empty list of aliases.
	 * 
	 * @param label       The primary node name.
	 * @param explanation The explanation associated to this node.
	 * @param isAvailable True if this node is available, false otherwise.
	 */
	public CommandRootNode(String label, T explanation, Supplier<Boolean> isAvailable) {
		super(label, explanation, isAvailable);
		setHelperNode(new CommandHelperNode<T>(this));
	}

	/**
	 * Creates a root node specified by the given parameters with an empty list of aliases.
	 * 
	 * @param label       The primary node name.
	 * @param explanation The explanation associated to this node.
	 * @param isAvailable True if this node is available, false otherwise.
	 */
	public CommandRootNode(String label, T explanation) {
		this(label, explanation, () -> false);
	}

	@Override
	public List<String> onTabComplete(String[] args) {
		if (!isAvailable())
			return emptyList();

		String label;
		try {
			label = args[0];
		} catch (IndexOutOfBoundsException e) {
			return emptyList();
		}

		if (label.equals(getHelper().getLabel()))
			return getHelper().onTabComplete(args);

		ICommandNode<T> node = getChildren().get(label);

		// Node not recognized, display all available children nodes.
		if (node == null)
			return filter(concat(getAvailableChildren().map(e -> e.getLabel()), Stream.of(getHelper().getLabel())), label);

		// Node not available, display nothing.
		if (!node.isAvailable())
			return emptyList();

		return node.onTabComplete(extract(args, 1));
	}

	@Override
	public boolean onCommand(String[] args) {
		if (!isAvailable())
			throw new NotAvailableCommandException(getLabel());

		String label;
		try {
			label = args[0];
		} catch (IndexOutOfBoundsException e) {
			return false;
		}

		if (label.equals(getHelper().getLabel()))
			return getHelper().onCommand(args);

		ICommandNode<T> node = getChildren().get(label);

		if (node == null)
			throw new NodeNotFoundException(getLabel(), label, args);

		if (!node.isAvailable())
			throw new NotAvailableArgumentException(node.getLabel(), label);

		return node.onCommand(extract(args, 1));
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

	@Override
	protected ICommandHelperNode<T> getHelper() {
		return (ICommandHelperNode<T>) super.getHelper();
	}

	@Override
	protected void setHelperNode(IHelperNode<T> helperNode) {
		super.setHelperNode((ICommandHelperNode<T>) helperNode);
	}
}
