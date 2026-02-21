package fr.pederobien.commandtree.testing;

import java.util.List;
import java.util.StringJoiner;

import fr.pederobien.commandtree.interfaces.IResult;
import fr.pederobien.utils.event.Logger;

public class ModelCommandTreeTestApp {

	public static void main(String[] args) {
		Logger.instance().colorized(true).debug(true);

		ModelCommandTree tree = new ModelCommandTree();

		print(tree.getCompletions(""));
		print(tree.execute(""));

		print(tree.getCompletions("h"));
		print(tree.getCompletions("in"));

		print(tree.getCompletions("help"));
		print(tree.execute("help"));

		print(tree.getCompletions("help mo"));
		print(tree.getCompletions("help modify"));
		print(tree.execute("help modify"));

		print(tree.getCompletions("help modify z"));
		print(tree.execute("help modify z"));

		print(tree.getCompletions("init"));
		print(tree.getCompletions("init Harr"));
		print(tree.getCompletions("init Harry "));
		print(tree.getCompletions("init Harry 2"));
		print(tree.getCompletions("init Harry 20 "));
		print(tree.getCompletions("init Harry 20 Hog"));
		print(tree.execute("init Harry 20 Hogwarts"));

		print(tree.execute("init Harry twenty Hogwarts"));

		print(tree.getCompletions("modify na"));
		print(tree.getCompletions("modify name "));
		print(tree.execute("modify name "));
		print(tree.execute("modify name Ronald"));

		print(tree.getCompletions("modify a"));
		print(tree.getCompletions("modify ag "));
		print(tree.getCompletions("modify age "));
		print(tree.execute("modify age "));
		print(tree.execute("modify age thirty"));
		print(tree.execute("modify age 30"));

		print(tree.getCompletions("modify c"));
		print(tree.getCompletions("modify city "));
		print(tree.execute("modify city "));
		print(tree.execute("modify city Forbidden_forest"));
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
