package fr.pederobien.commandtree.interfaces;

import java.util.List;
import java.util.Optional;

public interface INode<T> {

	/**
	 * @return The name of this node. The name is used to navigate from one node to another one.
	 */
	String getName();

	/**
	 * @return An explanation used to explain what this node does for the main command.
	 */
	String getExplanation();

	/**
	 * Adds a node in the list of children of this node.
	 * 
	 * @param node The node to add.
	 * @return True if the node has been added, false otherwise.
	 */
	boolean add(INode<T> node);

	/**
	 * Remove a node from this node.
	 * 
	 * @param name The name of the node to remove.
	 * @return True if there is no node registered for the given name or if it has been removed successfully, false if the node could
	 *         not be removed.
	 */
	Optional<INode<T>> remove(String name);

	/**
	 * @return A copy of the list that contains node's children.
	 */
	List<INode<T>> getChildren();

	/**
	 * A node is available means that is can be used as argument.
	 * 
	 * @param seed The seed used to determine if this node is available or not.
	 * @return True if this edition is available, false otherwise.
	 */
	boolean isAvailable(T seed);

	/**
	 * Requests a list of possible completions for a command argument.
	 * 
	 * @param tree The tree to which a command is dispatched.
	 * @param args The arguments passed to the command, including final partial argument to be completed and command alias.
	 * 
	 * @return A List of possible completions for the final argument, or empty.
	 */
	List<String> getCompletions(ITree<T> tree, String[] args);

	/**
	 * Executes a command and returns its success.
	 * 
	 * @param tree The tree to which a command is dispatched.
	 * @param args Passed command arguments.
	 * 
	 * @return The command result.
	 */
	IResult execute(ITree<T> tree, String[] args);
}
