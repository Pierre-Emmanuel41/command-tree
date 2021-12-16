package fr.pederobien.commandtree.exceptions;

public class NotAvailableArgumentException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	private String argument;
	private String label;

	public NotAvailableArgumentException(String label, String argument) {
		super("The argument \"" + argument + "\" associated to the command \"" + label + "\" is not available.");
		this.label = label;
		this.argument = argument;
	}

	/**
	 * @return The label of the node.
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @return The not available argument
	 */
	public String getArgument() {
		return argument;
	}
}
