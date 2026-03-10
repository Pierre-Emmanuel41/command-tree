package fr.pederobien.commandtree.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.function.Function;

import fr.pederobien.commandtree.interfaces.INode;
import fr.pederobien.commandtree.interfaces.IResult;
import fr.pederobien.commandtree.interfaces.ITree;

public class Node<T> implements INode<T> {
	private String name;
	private String explanation;
	private Function<T, Boolean> isAvailable;
	private List<INode<T>> nodes;

	/**
	 * Creates a node specified by the given parameters.
	 * 
	 * @param name        The node name.
	 * @param explanation The explanation associated to this node.
	 * @param isAvailable A function that indicates if this node is available or not, depending on the seed properties.
	 */
	public Node(String name, String explanation, Function<T, Boolean> isAvailable) {
		this.name = name;
		this.explanation = explanation;
		this.isAvailable = isAvailable;

		this.nodes = new ArrayList<INode<T>>();
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getExplanation() {
		return explanation;
	}

	@Override
	public boolean add(INode<T> node) {
		INode<T> registered = getNodeByName(node.getName());
		if (registered != null)
			return false;

		nodes.add(node);
		return true;
	}

	@Override
	public Optional<INode<T>> remove(String label) {
		INode<T> node = getNodeByName(label);
		nodes.remove(node);

		return Optional.ofNullable(node);
	}

	@Override
	public List<INode<T>> getChildren() {
		return new ArrayList<INode<T>>(nodes);
	}

	@Override
	public boolean isAvailable(T seed) {
		return isAvailable.apply(seed);
	}

	@Override
	public List<String> getCompletions(ITree<T> tree, String[] args) {
		T seed = tree.getSeed();

		if (!isAvailable(seed) || args.length == 0)
			return NodeHelper.emptyList();

		// Getting node associated to the first argument
		INode<T> node = getNodeByName(args[0]);

		// Node not recognized, display all available children nodes
		if (node == null)
			return NodeHelper.filter(NodeHelper.getAvailableChildren(this, seed).map(e -> e.getName()), args[0]);

		// Node not available, display nothing
		if (!node.isAvailable(seed))
			return NodeHelper.emptyList();

		// Navigating to the next node in the tree
		return node.getCompletions(tree, NodeHelper.extract(args, 1));
	}

	@Override
	public IResult execute(ITree<T> tree, String[] args) {
		T seed = tree.getSeed();

		if (args.length == 0) {
			StringJoiner joiner = new StringJoiner(", ");
			for (INode<T> child : getChildren())
				joiner.add(child.getName());

			return NodeHelper.result(false, "Input arguments array is empty, possible argument(s): %s", joiner);
		}

		INode<T> node = getNodeByName(args[0]);
		if (node == null)
			return NodeHelper.result(false, "There is no node associated to argument \"%s\"", args[0]);

		if (!node.isAvailable(seed))
			return NodeHelper.result(false, "The node %s is not available", args[0]);

		return node.execute(tree, NodeHelper.extract(args, 1));
	}

	/**
	 * Get the node associated to the given name.
	 * 
	 * @param name The name of the node the return.
	 * @return Null if no node is registered for the given name, the node otherwise.
	 */
	private INode<T> getNodeByName(String name) {
		for (INode<T> node : nodes)
			if (node.getName().equals(name))
				return node;

		return null;
	}
}
