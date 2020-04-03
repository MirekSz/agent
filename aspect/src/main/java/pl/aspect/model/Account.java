
package pl.aspect.model;

public class Account {

	public int balance = 20;

	public boolean withdraw(final int amount) {
		if (balance < amount) {
			return false;
		}
		balance = balance - amount;
		return true;
	}

}
