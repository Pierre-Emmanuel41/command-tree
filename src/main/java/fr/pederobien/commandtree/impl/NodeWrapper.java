package fr.pederobien.commandtree.impl;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Supplier;

import fr.pederobien.commandtree.interfaces.INode;

public class NodeWrapper<T> implements INode<T> {
	private INode<T> source;

	/**
	 * Wraps the given node.
	 * 
	 * @param source The node source of this wrapper.
	 */
	protected NodeWrapper(INode<T> source) {
		this.source = source;
	}

	@Override
	public Iterator<Entry<String, INode<T>>> iterator() {
		return source.iterator();
	}

	@Override
	public String getLabel() {
		return source.getLabel();
	}

	@Override
	public T getExplanation() {
		return source.getExplanation();
	}

	@Override
	public void setParent(INode<T> parent) {
		source.setParent(parent);
	}

	@Override
	public INode<T> getParent() {
		return source.getParent();
	}

	@Override
	public INode<T> getRoot() {
		return source.getRoot();
	}

	@Override
	public void add(INode<T> node) {
		source.add(node);
	}

	@Override
	public void remove(String label) {
		source.remove(label);
	}

	@Override
	public Map<String, ? extends INode<T>> getChildren() {
		return source.getChildren();
	}

	@Override
	public List<? extends INode<T>> getChildrenByLabel(String label) {
		return source.getChildrenByLabel(label);
	}

	@Override
	public boolean isAvailable() {
		return source.isAvailable();
	}

	@Override
	public void setAvailable(Supplier<Boolean> isAvailable) {
		source.setAvailable(isAvailable);
	}
}
