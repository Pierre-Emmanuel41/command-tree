package fr.pederobien.commandtree.impl;

import java.util.StringJoiner;

public class NodeNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	private String label, notFoundArgument;
	private String[] arguments;
	private StringJoiner joiner;

	public NodeNotFoundException(String label, String notFoundArgument, String[] arguments) {
		this.label = label;
		this.notFoundArgument = notFoundArgument;
		this.arguments = arguments;

		joiner = new StringJoiner(" ");
		joiner.add("\nArgument \"" + getNotFoundArgument() + "\" not found for command : " + getLabel() + ".");
		joiner.add("\nGiven arguments :");
		for (String cmd : getArguments())
			joiner.add(cmd);
	}

	/**
	 * @return The label of the command.
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @return The not found argument.
	 */
	public String getNotFoundArgument() {
		return notFoundArgument;
	}

	public String[] getArguments() {
		return arguments;
	}

	@Override
	public String getMessage() {
		return joiner.toString();
	}
}
