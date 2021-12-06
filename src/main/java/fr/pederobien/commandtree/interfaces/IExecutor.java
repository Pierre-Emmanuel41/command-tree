package fr.pederobien.commandtree.interfaces;

@FunctionalInterface
public interface IExecutor {

	/**
	 * Executes a command and returns its success.
	 *
	 * @param args Passed command arguments.
	 * 
	 * @return true if a valid command, otherwise false.
	 */
	boolean onCommand(String[] args);
}
