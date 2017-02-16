package pl;

import java.util.List;

public class Informer {
	private static boolean ENABLED = false;
	static CallTree root = new CallTree("root", 0, null);
	static CallTree last = root;

	public static void start(String name) {
		if (!ENABLED) {
			return;
		}
		CallTree callTree = new CallTree(name, System.currentTimeMillis(), last);
		last.children.add(callTree);
		last = callTree;
	}

	public static void stop(String name) {
		if (!ENABLED) {
			return;
		}
		last.stop = System.currentTimeMillis();
		last.time = last.stop - last.start;
		last = last.parent;
		if (last == root) {
			System.out.println("koniec");
			print(root.children, 1);
		}
	}

	public static void main(String[] args) throws Exception {
		start("login");
		start("a");
		stop("a");
		start("a");
		start("b");
		Thread.sleep(20);
		stop("b");

		stop("a");
		start("login");
		Thread.sleep(20);
		stop("login");
		stop("login");
		print(root.children, 1);
		// for (CallTree tree1 : root.children) {
		// System.out.println(" " + tree1.method);
		// for (CallTree tree2 : tree1.children) {
		// System.out.println(" " + tree2.method);
		// for (CallTree tree3 : tree2.children) {
		// System.out.println(" " + tree3.method);
		//
		// }
		// }
		// }
	}

	private static void print(List<CallTree> children, int level) {
		String levelPrefix = " ";
		for (int i = 0; i < level; i++) {
			levelPrefix += "  ";

		}
		for (CallTree callTree : children) {
			System.out.println(levelPrefix + callTree.method + ": " + callTree.time);
			if (!callTree.children.isEmpty()) {
				print(callTree.children, ++level);
			}
		}
	}
}
