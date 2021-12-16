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

		try {
			String argument = args[0];
			ICommandNode<T> node = getChildren().get(argument);

			// Node not recognized, display all available children nodes.
			if (node == null)
				return filter(concat(getAvailableChildren().map(e -> e.getLabel()), Stream.of(getHelper().getLabel())), argument);

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
		if (!isAvailable())
			throw new NotAvailableCommandException(getLabel());

		try {
			String argument = args[0];
			ICommandNode<T> node = getChildren().get(argument);

			if (argument.equals(getHelper().getLabel()))
				return getHelper().onCommand(extract(args, 1));

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
