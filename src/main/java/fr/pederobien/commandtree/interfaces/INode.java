package fr.pederobien.commandtree.interfaces;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import fr.pederobien.commandtree.events.NodeAddPostEvent;
import fr.pederobien.commandtree.events.NodeRemovePostEvent;
import fr.pederobien.commandtree.exceptions.NodeRegisterException;

public interface INode<T> extends Iterable<Map.Entry<String, INode<T>>> {

	/**
	 * @return The label of this node. It is a minecraft command argument.
	 */
	String getLabel();

	/**
	 * @param T A generic parameter used to get different type of explanation. In most case, the explanation is a String. But it could
	 *          append that developers need more than a simple String.
	 * @return An explanation used to explain what this argument does for the main command.
	 */
	T getExplanation();

	/**
	 * Set the parent of this node. The given parent can contains several informations needed by this children.
	 * 
	 * @param parent The parent of this child.
	 */
	void setParent(INode<T> parent);

	/**
	 * @return the parent of this node. If this node has no parent then it returns itself.
	 */
	INode<T> getParent();

	/**
	 * @return The root that has not parent.
	 */
	INode<T> getRoot();

	/**
	 * Appends a node to this node. This element is stored into a Map with key is {@link INode#getLabel()} and the value is itself. Be
	 * careful, if two nodes have the same label then the first node is removed in order to add the second one. This method should
	 * throw a {@link NodeAddPostEvent}.
	 * 
	 * @param node The node to add.
	 * 
	 * @throws NodeRegisterException If a node is already registered for the label of the given node.
	 */
	void add(INode<T> node);

	/**
	 * Remove a node from this node. This method should throw a {@link NodeRemovePostEvent}.
	 * 
	 * @param label The label of the node to remove.
	 */
	void remove(String label);

	/**
	 * @return An unmodifiable view as map of all children of this node.
	 */
	Map<String, ? extends INode<T>> getChildren();

	/**
	 * Get a list of all descendants matching on the given label. If label correspond to "*" then it returns a list that contains all
	 * descendants for this node.
	 * 
	 * @param label The label to match on.
	 * 
	 * @return A list of all descendants.
	 */
	List<? extends INode<T>> getChildrenByLabel(String label);

	/**
	 * A node is available means that is can be used as minecraft argument.
	 * 
	 * @return True if this edition is available, false otherwise.
	 */
	boolean isAvailable();

	/**
	 * Set the availability of this edition. A node is available means that is can be used as argument. It may be possible the node
	 * availability could change according to the properties of an external object.
	 * 
	 * @param isAvailable The new value that represents the availability of this edition.
	 */
	void setAvailable(Supplier<Boolean> isAvailable);
}
