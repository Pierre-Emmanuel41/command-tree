package fr.pederobien.commandtree.interfaces;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public interface INodeBuilder<T> {

	/**
	 * Defines the availability of the node to create. If the node availability depends on the tree's seed properties then the node
	 * availability shall be defined.
	 * 
	 * @param availability The function to apply to determine if the node is available or not.
	 * @return The builder.
	 */
	INodeBuilder<T> withAvailability(Function<T, Boolean> availability);

	/**
	 * Defines the behavior of the node to create regarding command completion.
	 * 
	 * @param completion The implementation of the function.
	 * @return this builder.
	 */
	INodeBuilder<T> withCompletions(BiFunction<ITree<T>, String[], List<String>> completion);

	/**
	 * Defines the behavior of the node to create regarding the command to execute.
	 * 
	 * @param execution The implementation of the function.
	 * @return This builder.
	 */
	INodeBuilder<T> withExecution(BiFunction<ITree<T>, String[], IResult> execution);

	/**
	 * @return The created node. Once created the node's behavior cannot be modified.
	 */
	INode<T> build();
}
