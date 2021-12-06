package fr.pederobien.commandtree.impl;

import java.util.List;

import fr.pederobien.commandtree.interfaces.ICommandNode;

public class CommandNodeWrapper<T> extends NodeWrapper<T> implements ICommandNode<T> {
	private ICommandNode<T> source;

	protected CommandNodeWrapper(ICommandNode<T> source) {
		super(source);
		this.source = source;
	}

	@Override
	public List<String> onTabComplete(String[] args) {
		return source.onTabComplete(args);
	}

	@Override
	public boolean onCommand(String[] args) {
		return source.onCommand(args);
	}
}
