package fr.pederobien.commandtree.interfaces;

public interface IRootNode<T> extends INode<T> {

	/**
	 * Add each children of this root to the returned new root. The children are not cloned, only the reference to their parent is
	 * updated.
	 * 
	 * @return A simple node that contains all children of this root.
	 */
	INode<T> export();

	/**
	 * Add each children of this node to the children of the given node.
	 * 
	 * @param root The new root.
	 */
	void export(INode<T> root);
}
