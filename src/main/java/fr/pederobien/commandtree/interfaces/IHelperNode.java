package fr.pederobien.commandtree.interfaces;

public interface IHelperNode<T> {

	/**
	 * @return The label of this helper. It returns "help".
	 */
	String getLabel();

	/**
	 * @return The source node associated to this helper.
	 */
	INode<T> getSource();

	/**
	 * Sets the source node associated to this helper.
	 * 
	 * @param source The source node.
	 */
	void setSource(INode<T> source);
}
