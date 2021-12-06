package fr.pederobien.commandtree.impl;

import java.util.List;
import java.util.function.Supplier;

import fr.pederobien.commandtree.interfaces.ICommandHelperNode;
import fr.pederobien.commandtree.interfaces.ICommandNode;
import fr.pederobien.commandtree.interfaces.ICompletor;
import fr.pederobien.commandtree.interfaces.IExecutor;

public class Tree {

	/**
	 * Creates a root node based on the given parameters with an empty list of aliases.
	 * 
	 * @param label       The primary node name.
	 * @param explanation The explanation associated to this node.
	 * @param isAvailable True if this node is available, false otherwise.
	 * @param helperNode  The helper associated to this root.
	 * 
	 * @return A tree builder.
	 */
	public static <T> TreeBuilder<T> create(String label, T explanation, Supplier<Boolean> isAvailable, ICommandHelperNode<T> helperNode) {
		return new TreeBuilder<T>(label, explanation, isAvailable, helperNode);
	}

	/**
	 * Creates a root node specified by the given parameters with an empty list of aliases.
	 * 
	 * @param label       The primary node name.
	 * @param explanation The explanation associated to this node.
	 * @param isAvailable True if this node is available, false otherwise.
	 * 
	 * @return A tree builder.
	 */
	public static <T> TreeBuilder<T> create(String label, T explanation, Supplier<Boolean> isAvailable) {
		return new TreeBuilder<T>(label, explanation, isAvailable);
	}

	/**
	 * Creates a root node specified by the given parameters with an empty list of aliases.
	 * 
	 * @param label       The primary node name.
	 * @param explanation The explanation associated to this node.
	 * 
	 * @return A tree builder.
	 */
	public static <T> TreeBuilder<T> create(String label, T explanation) {
		return new TreeBuilder<T>(label, explanation);
	}

	public static class TreeBuilder<T> {
		private ICommandNode<T> root;

		/**
		 * Creates a root node based on the given parameters with an empty list of aliases.
		 * 
		 * @param label       The primary node name.
		 * @param explanation The explanation associated to this node.
		 * @param isAvailable True if this node is available, false otherwise.
		 * @param helperNode  The helper associated to this root.
		 */
		private TreeBuilder(String label, T explanation, Supplier<Boolean> isAvailable, ICommandHelperNode<T> helperNode) {
			root = new CommandRootNode<T>(label, explanation, isAvailable, helperNode);
		}

		/**
		 * Creates a root node specified by the given parameters with an empty list of aliases.
		 * 
		 * @param label       The primary node name.
		 * @param explanation The explanation associated to this node.
		 * @param isAvailable True if this node is available, false otherwise.
		 */
		private TreeBuilder(String label, T explanation, Supplier<Boolean> isAvailable) {
			root = new CommandRootNode<T>(label, explanation, isAvailable);
		}

		/**
		 * Creates a root node specified by the given parameters with an empty list of aliases.
		 * 
		 * @param label       The primary node name.
		 * @param explanation The explanation associated to this node.
		 */
		private TreeBuilder(String label, T explanation) {
			root = new CommandRootNode<T>(label, explanation);
		}

		/**
		 * Creates a node specified by the given parameters.
		 * 
		 * @param label       The primary node name.
		 * @param explanation The explanation associated to this node.
		 * @param isAvailable True if this node is available, false otherwise.
		 * 
		 * @return A node builder to set the completor and executor of the underlying node.
		 */
		public NodeBuilder<T> addNode(String label, T explanation, Supplier<Boolean> isAvailable) {
			return new NodeBuilder<T>(root, label, explanation, isAvailable);
		}

		/**
		 * Creates a node specified by the given parameters.
		 * 
		 * @param label       The primary node name.
		 * @param explanation The explanation associated to this node.
		 * 
		 * @return A node builder to set the completor and executor of the underlying node.
		 */
		public NodeBuilder<T> addNode(String label, T explanation) {
			return new NodeBuilder<T>(root, label, explanation);
		}

		/**
		 * @return The root of this tree.
		 */
		public ICommandNode<T> build() {
			return root;
		}
	}

	public static class NodeBuilder<T> {
		private ICommandNode<T> parent;
		private SetupNode<T> node;

		/**
		 * Creates a node specified by the given parameters.
		 * 
		 * @param parent      The parent node associated to the underlying node.
		 * @param label       The primary node name.
		 * @param explanation The explanation associated to this node.
		 * @param isAvailable True if this node is available, false otherwise.
		 */
		private NodeBuilder(ICommandNode<T> parent, String label, T explanation, Supplier<Boolean> isAvailable) {
			this.parent = parent;
			node = new SetupNode<T>(new CommandNode<T>(label, explanation, isAvailable));
		}

		/**
		 * Creates a node specified by the given parameters.
		 * 
		 * @param parent      The parent node associated to the underlying node.
		 * @param label       The primary node name.
		 * @param explanation The explanation associated to this node.
		 */
		private NodeBuilder(ICommandNode<T> parent, String label, T explanation) {
			this.parent = parent;
			node = new SetupNode<T>(new CommandNode<T>(label, explanation));
		}

		/**
		 * Set the action to perform when method onTabComplete is called.
		 * 
		 * @param executor The action to perform.
		 * 
		 * @return this node builder.
		 */
		public NodeBuilder<T> withCompleter(ICompletor completor) {
			node.setCompletor(completor);
			return this;
		}

		/**
		 * Set the action to perform when method onCommand is called.
		 * 
		 * @param completor The action to perform.
		 */
		public NodeBuilder<T> withExecutor(IExecutor executor) {
			node.setExecutor(executor);
			return this;
		}

		/**
		 * Creates a node specified by the given parameters.
		 * 
		 * @param label       The primary node name.
		 * @param explanation The explanation associated to this node.
		 * @param isAvailable True if this node is available, false otherwise.
		 */
		public NodeBuilder<T> addNode(String label, T explanation, Supplier<Boolean> isAvailable) {
			return new NodeBuilder<T>(node, label, explanation, isAvailable);
		}

		/**
		 * Creates a node specified by the given parameters.
		 * 
		 * @param label       The primary node name.
		 * @param explanation The explanation associated to this node.
		 */
		public NodeBuilder<T> addNode(String label, T explanation) {
			return new NodeBuilder<T>(node, label, explanation);
		}

		/**
		 * Add the constructed node to the parent node associated to this builder.
		 * 
		 * @return this builder.
		 */
		public NodeBuilder<T> append() {
			parent.add(node);
			return this;
		}

		/**
		 * Add the constructed node to the parent node associated to this builder.
		 * 
		 * @return the constructed node.
		 */
		public ICommandNode<T> addAndGet() {
			parent.add(node);
			return node;
		}
	}

	private static class SetupNode<T> extends CommandNodeWrapper<T> {
		private ICompletor completor;
		private IExecutor executor;

		protected SetupNode(ICommandNode<T> source) {
			super(source);
		}

		@Override
		public List<String> onTabComplete(String[] args) {
			return completor == null ? super.onTabComplete(args) : completor.onTabComplete(args);
		}

		@Override
		public boolean onCommand(String[] args) {
			return executor == null ? super.onCommand(args) : executor.onCommand(args);
		}

		/**
		 * Set the action to perform when method onTabComplete is called.
		 * 
		 * @param executor The action to perform.
		 */
		public void setCompletor(ICompletor completor) {
			this.completor = completor;
		}

		/**
		 * Set the action to perform when method onCommand is called.
		 * 
		 * @param completor The action to perform.
		 */
		public void setExecutor(IExecutor executor) {
			this.executor = executor;
		}
	}
}
