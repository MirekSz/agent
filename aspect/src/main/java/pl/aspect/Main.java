
package pl.aspect;

import pl.aspect.model.Account;

public class Main {

	public static void main(final String[] args) {
		Account account = new Account();
		account.withdraw(8);

		account.balance = 500;

		account.balance = 1123;
	}
}
