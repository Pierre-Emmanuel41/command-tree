package fr.pederobien.commandtree.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import fr.pederobien.commandtree.interfaces.ICommandHelperNode;
import fr.pederobien.commandtree.interfaces.INode;

public class CommandHelperNode<T> extends HelperNode<T> implements ICommandHelperNode<T> {
	private Consumer<INode<T>> displayer;

	/**
	 * Creates an helper responsible to display the explanation of one or several children of the given source node. The default
	 * behavior can be overridden the protected method {@link #displayExplanation(INode)}.
	 * 
	 * @param source    The node source in order to display the explanation of one or several children.
	 * @param displayer The consumer that specifies how to display the node explanation.
	 */
	public CommandHelperNode(INode<T> source, Consumer<INode<T>> displayer) {
		super(source);
		this.displayer = displayer;
	}

	/**
	 * Creates an helper responsible to display the explanation of one or several children of the given source node. The default
	 * behavior can be overridden the protected method {@link #displayExplanation(INode)}.
	 * 
	 * @param source The node source in order to display the explanation of one or several children.
	 */
	public CommandHelperNode(INode<T> source) {
		super(source);
	}

	@Override
	public List<String> onTabComplete(String[] args) {
		try {
			Collection<? extends INode<T>> values = getSource().getChildren().values();
			INode<T> edition = getSource().getChildren().get(args[0]);

			for (int i = 1; i < args.length; i++) {
				if (edition != null) {
					values = edition.getChildren().values();
					edition = edition.getChildren().get(args[i]);
				}
			}
			return filter(values.stream()).filter(str -> str.contains(args[args.length - 1])).collect(Collectors.toList());
		} catch (IndexOutOfBoundsException | NullPointerException e) {
			return new ArrayList<String>();
		}
	}

	@Override
	public boolean onCommand(String[] args) {
		try {
			INode<T> child = getSource().getChildren().get(args[0]);
			for (int i = 1; i < args.length; i++)
				if (child != null)
					child = child.getChildren().get(args[i]);
			displayExplanation(child);
		} catch (IndexOutOfBoundsException e) {
			getSource().getChildren().values().stream().filter(node -> node.isAvailable()).forEach(node -> display(node));
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	/**
	 * Display the explanation of the given node. This method should be overridden in order to change the default behavior.
	 * 
	 * @param node The node whose the explanation should be displayed.
	 */
	protected void display(INode<T> node) {
		if (displayer != null) {
			displayer.accept(node);
			return;
		}

		displayExplanation(node);
	}
}
