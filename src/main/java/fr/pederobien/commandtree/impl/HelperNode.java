package fr.pederobien.commandtree.impl;

import java.util.stream.Stream;

import fr.pederobien.commandtree.interfaces.IHelperNode;
import fr.pederobien.commandtree.interfaces.INode;

public class HelperNode<T> implements IHelperNode<T> {
	private INode<T> source;

	/**
	 * Creates an helper responsible to display the explanation of one or several children of the given source node. The default
	 * behavior can be overridden the protected method {@link #displayExplanation(INode)}.
	 * 
	 * @param source The node source in order to display the explanation of one or several children.
	 */
	protected HelperNode(INode<T> source) {
		this.source = source;
	}

	@Override
	public String getLabel() {
		return "help";
	}

	@Override
	public INode<T> getSource() {
		return source;
	}

	@Override
	public void setSource(INode<T> source) {
		this.source = source;
	}

	/**
	 * Default behavior to display the explanation of the given node.
	 * 
	 * @param node The node whose the explanation should be displayed.
	 */
	protected void displayExplanation(INode<T> node) {
		System.out.println(String.format("%s - %s", node.getLabel(), node.getExplanation()));
	}

	/**
	 * Filter the element of the given stream in order to return only available element without the element with label "help".
	 * 
	 * @param stream The stream to filter.
	 * @param args   The array whose the last element is used as filter.
	 * 
	 * @return A filtered stream.
	 */
	protected Stream<String> filter(Stream<? extends INode<T>> stream, String... args) {
		return stream.filter(e -> e.isAvailable()).map(e -> e.getLabel()).filter(str -> str.contains(args[args.length - 1]));
	}
}
