package fr.pederobien.commandtree.interfaces;

import java.util.List;

public interface ITree<T> {

	/**
	 * @return The seed associated to this tree. The seed is usually the object where the properties that can be modified by each node
	 *         of the tree.
	 */
	T getSeed();

	/**
	 * Set the seed of this tree. The seed is usually the object where the properties that can be modified by each node of the tree.
	 * 
	 * @param seed The new tree's seed.
	 */
	void setSeed(T seed);

	/**
	 * Creates a node builder associated to the given name and explanation.
	 * 
	 * @param name        The node's name.
	 * @param explanation The node's explanation.
	 * @return The node builder use to define node's implementation.
	 */
	INodeBuilder<T> getNodeBuilder(String name, String explanation);

	/**
	 * Adds a node in the list of children of the root.
	 * 
	 * @param node The node to add.
	 * @return True if the node has been added, false otherwise.
	 */
	boolean add(INode<T> node);

	/**
	 * Extract a list of arguments from the given arguments parameter. Each argument shall be separated by a space character. Then
	 * dispatch the list of arguments to each node and get the possible completions.
	 * 
	 * @param arguments The arguments line to use to get the associated completion.
	 * 
	 * @return A List of possible completions for the final argument, or empty.
	 */
	List<String> getCompletions(String arguments);

	/**
	 * Extract a list of arguments from the given arguments parameter. Each argument shall be separated by a space character. Then
	 * dispatch the list of arguments to each node and get the command result.
	 * 
	 * @param argument Passed command arguments, all separated by a space character.
	 * @return The command result composed of a success state and a feedback.
	 */
	IResult execute(String arguments);
}
