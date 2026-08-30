package fr.pederobien.commandtree.impl;

import java.util.List;
import java.util.function.Function;

import fr.pederobien.commandtree.interfaces.INode;
import fr.pederobien.commandtree.interfaces.INodeBuilder;
import fr.pederobien.commandtree.interfaces.IResult;
import fr.pederobien.commandtree.interfaces.ITree;

public class Tree<T> implements ITree<T> {
	private final INode<T> root;
	private final Helper<T> helper;
	private T seed;

	/**
	 * Creates a tree associated to a specific seed. The seed is available to each node in the tree.
	 * 
	 * @param seed         The seed to interact with.
	 * @param name         The name of the root node.
	 * @param explanation  The explanation of the root node.
	 * @param availability A function that indicates if this node is available or not, depending on the seed properties.
	 */
	public Tree(T seed, String name, String explanation, Function<T, Boolean> availability) {
		this.seed = seed;

		root = new Node<T>(name, explanation, availability);
		helper = new Helper<T>(root);
	}

	/**
	 * Creates a tree associated to a specific seed. The seed is available to each node in the tree.
	 * 
	 * @param seed        The seed to interact with.
	 * @param name        The name of the root node.
	 * @param explanation The explanation of the root node.
	 */
	public Tree(T seed, String name, String explanation) {
		this(seed, name, explanation, treeSeed -> true);
	}

	/**
	 * Creates a command tree with a seed.
	 * 
	 * @param seed The seed than can be modified by tree's nodes.
	 */
	public Tree(T seed) {
		this(seed, "", "", treeSeed -> true);
	}

	/**
	 * Creates a command tree with no seed.
	 * 
	 * @param seed The seed than can be modified by tree's nodes.
	 */
	public Tree() {
		this(null);
	}

	@Override
	public T getSeed() {
		return seed;
	}

	@Override
	public void setSeed(T seed) {
		this.seed = seed;
	}

	@Override
	public INodeBuilder<T> getNodeBuilder(String name, String explanation) {
		return new NodeBuilder<T>(name, explanation);
	}

	@Override
	public boolean add(INode<T> node) {
		return root.add(node);
	}

	@Override
	public List<String> getCompletions(String argument) {
		String[] args = argument.split(" ");

		// Last character is space, then it needs to be added to the args list
		if (argument.lastIndexOf(" ") == argument.length() - 1) {
			String[] intermediate = new String[args.length + 1];
			System.arraycopy(args, 0, intermediate, 0, args.length);
			intermediate[intermediate.length - 1] = "";

			args = intermediate;
		}

		return getCompletions(args);
	}

	@Override
	public List<String> getCompletions(String[] arguments) {
		if (arguments.length > 0 && arguments[0].equals(helper.getName()))
			return helper.getCompletions(NodeHelper.extract(arguments, 1));

		List<String> completions = NodeHelper.emptyList();
		if (arguments[0].length() == 0 || NodeHelper.containsIgnoreCase(helper.getName(), arguments[0]))
			completions.add(helper.getName());

		// The returned list might be not modifiable
		for (String completion : root.getCompletions(this, arguments))
			completions.add(completion);

		return completions;
	}

	@Override
	public IResult execute(String argument) {
		return execute(argument.trim().split(" "));
	}

	@Override
	public IResult execute(String[] arguments) {
		if (arguments.length > 0 && arguments[0].equals(helper.getName()))
			return helper.execute(NodeHelper.extract(arguments, 1));

		return root.execute(this, arguments);
	}
}
