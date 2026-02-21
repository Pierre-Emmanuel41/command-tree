package fr.pederobien.commandtree.impl;

import java.util.ArrayList;
import java.util.List;

import fr.pederobien.commandtree.interfaces.INode;
import fr.pederobien.commandtree.interfaces.IResult;

public class Helper<T> {
	private INode<T> source;

	/**
	 * Creates an helper responsible to display the explanation of a node.
	 * 
	 * @param source The node source in order to display the explanation of one or several children.
	 */
	public Helper(INode<T> source) {
		this.source = source;
	}

	/**
	 * @return The node name: "help"
	 */
	public String getName() {
		return "help";
	}

	public List<String> getCompletions(String[] args) {
		// Retrieving the last filtered child
		INode<T> node = source;
		int index;
		for (index = 0; index < args.length; index++) {
			INode<T> next = getChildNodeByName(node, args[index]);
			if (next == null)
				break;

			// Navigating to the next node
			node = next;
		}

		List<String> completions = getChildrenNames(node);
		if (args.length - index == 0)
			return completions;

		return NodeHelper.filter(completions.stream(), args[args.length - 1]);
	}

	public IResult execute(String[] args) {
		// Retrieving the last filtered child
		INode<T> node = source;
		int index;
		for (index = 0; index < args.length; index++) {
			INode<T> next = getChildNodeByName(node, args[index]);
			if (next == null)
				break;

			node = next;
		}

		IResult result = node == source ? NodeHelper.result(true, "") : NodeHelper.result(true, "%s - %s", node.getName(), node.getExplanation());

		for (INode<T> child : node.getChildren())
			result.getFeedbacks().add(String.format("%s - %s", child.getName(), child.getExplanation()));

		return result;
	}

	/**
	 * Get the node associated to the given name.
	 * 
	 * @param name The name of the node the return.
	 * @return Null if no node is registered for the given name, the node otherwise.
	 */
	private INode<T> getChildNodeByName(INode<T> source, String name) {
		for (INode<T> node : source.getChildren())
			if (node.getName().equals(name))
				return node;

		return null;
	}

	/**
	 * Get a list that contains the name of each child of the given source.
	 * 
	 * @param source The node from which children names shall be retrieved.
	 * @return A list that contains children name.
	 */
	private List<String> getChildrenNames(INode<T> source) {
		List<String> names = new ArrayList<String>();
		for (INode<T> node : source.getChildren())
			names.add(node.getName());

		return names;
	}
}
