package fr.pederobien.commandtree.impl;

import java.util.Scanner;
import java.util.function.Function;

import fr.pederobien.commandtree.interfaces.IResult;
import fr.pederobien.commandtree.interfaces.ITree;

public class CLI {

	/**
	 * Creates a simple command line interface that waits for user input and dispatch the user content to the given tree.
	 * 
	 * @param prompt The text to display when waiting for user input.
	 * @param exit   The condition to use to exit the start method.
	 * @param tree   The tree that performs the user command.
	 * @return A simple command line interface to interact with the given tree.
	 */
	public static <T> Runnable simpleInterface(String prompt, Function<String, Boolean> exit, ITree<T> tree) {
		return new SimpleCommandLineInterface(prompt, exit, tree);
	}

	public static class SimpleCommandLineInterface implements Runnable {
		private final String prompt;
		private final Function<String, Boolean> exit;
		private final ITree<?> tree;

		/**
		 * Creates a simple command line interface that waits for user input and dispatch the user content to the given tree.
		 * 
		 * @param prompt The text to display when waiting for user input.
		 * @param exit   The condition to use to exit the start method.
		 * @param tree   The tree that performs the user command.
		 */
		public SimpleCommandLineInterface(String prompt, Function<String, Boolean> exit, ITree<?> tree) {
			this.prompt = prompt;
			this.tree = tree;
			this.exit = exit;
		}

		/**
		 * Waits for user arguments. Each argument shall be separated by a " " space character. The command is executed once the user
		 * pressed Enter.
		 * 
		 * To exit this method, the user
		 */
		public void run() {
			Scanner scanner = new Scanner(System.in);

			while (true) {
				System.out.print(prompt);
				String arguments = scanner.nextLine();

				// Checking is user exit console
				if (exit.apply(arguments))
					break;

				print(tree.execute(arguments));
			}

			System.out.println("Exiting program...");
			scanner.close();
		}

		private void print(IResult result) {
			if (result.isSuccess())
				for (String feedback : result.getFeedbacks())
					System.out.println(feedback);
			else
				for (String feedback : result.getFeedbacks())
					System.err.println(feedback);
		}
	}
}
