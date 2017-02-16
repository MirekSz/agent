package pl;

import java.util.ArrayList;
import java.util.List;

public class CallTree {
	public CallTree parent;

	public CallTree(String name, long currentTimeMillis, CallTree parent) {
		this.method = name;
		this.start = currentTimeMillis;
		this.parent = parent;
	}

	public String method;
	public long start;
	public long stop;
	public long time;
	public List<CallTree> children = new ArrayList();
}
