package fr.pederobien.commandtree.impl;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import fr.pederobien.commandtree.interfaces.INode;
import fr.pederobien.commandtree.interfaces.INodeBuilder;
import fr.pederobien.commandtree.interfaces.IResult;
import fr.pederobien.commandtree.interfaces.ITree;

public class NodeBuilder<T> implements INodeBuilder<T> {
	private final String name;
	private final String explanation;
	private WrappedNode<T> node;
	private Function<T, Boolean> availability;
	private BiFunction<ITree<T>, String[], List<String>> completion;
	private BiFunction<ITree<T>, String[], IResult> execution;

	/**
	 * Creates a node builder.
	 * 
	 * @param name        The node's name.
	 * @param explanation The node's explanation.
	 */
	public NodeBuilder(String name, String explanation) {
		this.name = name;
		this.explanation = explanation;

		availability = _ -> true;
	}

	@Override
	public INodeBuilder<T> withAvailability(Function<T, Boolean> availability) {
		this.availability = availability;
		return this;
	}

	@Override
	public INodeBuilder<T> withCompletions(BiFunction<ITree<T>, String[], List<String>> completion) {
		this.completion = completion;
		return this;
	}

	@Override
	public INodeBuilder<T> withExecution(BiFunction<ITree<T>, String[], IResult> onExecution) {
		this.execution = onExecution;
		return this;
	}

	@Override
	public INode<T> build() {
		node = new WrappedNode<T>(name, explanation, availability);
		node.setCompletion(completion);
		node.setExecution(execution);

		return node;
	}

	private class WrappedNode<U> extends Node<U> {
		private BiFunction<ITree<U>, String[], List<String>> completion;
		private BiFunction<ITree<U>, String[], IResult> execution;

		private WrappedNode(String name, String explanation, Function<U, Boolean> isAvailable) {
			super(name, explanation, isAvailable);
		}

		/**
		 * Set the behavior of the node when the onTabComplete function is called.
		 * 
		 * @param completion The function implementation.
		 */
		public void setCompletion(BiFunction<ITree<U>, String[], List<String>> completion) {
			this.completion = completion;
		}

		/**
		 * Set the behavior of the node when the onCommand function is called.
		 * 
		 * @param execution The function implementation.
		 */
		public void setExecution(BiFunction<ITree<U>, String[], IResult> execution) {
			this.execution = execution;
		}

		@Override
		public List<String> getCompletions(ITree<U> tree, String[] args) {
			return completion == null ? super.getCompletions(tree, args) : completion.apply(tree, args);
		}

		@Override
		public IResult execute(ITree<U> tree, String[] args) {
			return execution == null ? super.execute(tree, args) : execution.apply(tree, args);
		}
	}
}
