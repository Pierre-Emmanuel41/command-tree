package fr.pederobien.commandtree.exceptions;

public class NotAvailableCommandException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	private String label;

	public NotAvailableCommandException(String label) {
		super("The command " + label + " is not available (yet ?)");
		this.label = label;
	}

	/**
	 * @return The label of the node.
	 */
	public String getLabel() {
		return label;
	}
}
