package fr.pederobien.commandtree.testing;

import java.util.List;
import java.util.StringJoiner;

import fr.pederobien.commandtree.impl.CLI;
import fr.pederobien.commandtree.interfaces.IResult;
import fr.pederobien.utils.event.Logger;

public class ModelCommandTreeTestApp {

	public static void main(String[] args) {
		Logger.setPrintInColor(true);
		Logger.setPrintTimeStamp(false);

		testTree();
		testCLI();
	}

	private static void testTree() {
		ModelCommandTree modelTree = new ModelCommandTree();

		print(modelTree.getTree().getCompletions(""));
		print(modelTree.getTree().execute(""));

		print(modelTree.getTree().getCompletions("h"));
		print(modelTree.getTree().getCompletions("in"));

		print(modelTree.getTree().getCompletions("help"));
		print(modelTree.getTree().execute("help"));

		print(modelTree.getTree().getCompletions("help mo"));
		print(modelTree.getTree().getCompletions("help modify"));
		print(modelTree.getTree().execute("help modify"));

		print(modelTree.getTree().getCompletions("help modify z"));
		print(modelTree.getTree().execute("help modify z"));

		print(modelTree.getTree().getCompletions("init"));
		print(modelTree.getTree().getCompletions("init Harr"));
		print(modelTree.getTree().getCompletions("init Harry "));
		print(modelTree.getTree().getCompletions("init Harry 2"));
		print(modelTree.getTree().getCompletions("init Harry 20 "));
		print(modelTree.getTree().getCompletions("init Harry 20 Hog"));
		print(modelTree.getTree().execute("init Harry 20 Hogwarts"));

		print(modelTree.getTree().execute("init Harry twenty Hogwarts"));

		print(modelTree.getTree().getCompletions("modify na"));
		print(modelTree.getTree().getCompletions("modify name "));
		print(modelTree.getTree().execute("modify name "));
		print(modelTree.getTree().execute("modify name Ronald"));

		print(modelTree.getTree().getCompletions("modify a"));
		print(modelTree.getTree().getCompletions("modify ag "));
		print(modelTree.getTree().getCompletions("modify age "));
		print(modelTree.getTree().execute("modify age "));
		print(modelTree.getTree().execute("modify age thirty"));
		print(modelTree.getTree().execute("modify age 30"));

		print(modelTree.getTree().getCompletions("modify c"));
		print(modelTree.getTree().getCompletions("modify city "));
		print(modelTree.getTree().execute("modify city "));
		print(modelTree.getTree().execute("modify city Forbidden_forest"));
	}

	private static void testCLI() {
		ModelCommandTree modelTree = new ModelCommandTree();

		Runnable cli = CLI.simpleInterface("test>", arg -> arg.equals("q"), modelTree.getTree());
		cli.run();
	}

	private static void print(List<String> completions) {
		if (completions.isEmpty())
			Logger.warning("No completion available");
		else {
			StringJoiner joiner = new StringJoiner("\t");
			for (String completion : completions)
				joiner.add(completion);

			Logger.info(joiner.toString());
		}
	}

	private static void print(IResult result) {
		if (result.isSuccess())
			for (String feedback : result.getFeedbacks())
				Logger.info(feedback);
		else
			for (String feedback : result.getFeedbacks())
				Logger.error(feedback);
	}
}
