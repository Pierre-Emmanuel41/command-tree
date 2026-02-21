package fr.pederobien.commandtree.impl;

import java.util.List;

import fr.pederobien.commandtree.interfaces.INode;
import fr.pederobien.commandtree.interfaces.INodeBuilder;
import fr.pederobien.commandtree.interfaces.IResult;
import fr.pederobien.commandtree.interfaces.ITree;

public class Tree<T> implements ITree<T> {
	private T seed;
	private INode<T> root;
	private Helper<T> helper;

	/**
	 * Creates a command tree with a seed.
	 * 
	 * @param seed The seed than can be modified by tree's nodes.
	 */
	public Tree(T seed) {
		this.seed = seed;

		root = new Node<T>("", "", _ -> true);
		helper = new Helper<T>(root);
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

		if (args.length > 0 && args[0].equals(helper.getName()))
			return helper.getCompletions(NodeHelper.extract(args, 1));

		List<String> completions = NodeHelper.emptyList();
		if (args[0].length() == 0 || NodeHelper.containsIgnoreCase(helper.getName(), args[0]))
			completions.add(helper.getName());

		// The returned list might be not modifiable
		for (String completion : root.getCompletions(this, args))
			completions.add(completion);

		return completions;
	}

	@Override
	public IResult execute(String argument) {
		String[] args = argument.trim().split(" ");

		if (args.length > 0 && args[0].equals(helper.getName()))
			return helper.execute(NodeHelper.extract(args, 1));

		return root.execute(this, args);
	}
}
