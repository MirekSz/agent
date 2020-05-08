
package pl.com;

public class Main {

	public static void main(final String[] args) {
		Customer customer = new Customer();
		customer.setName("MIrek");
		System.out.println(customer.getName());

		Runnable child = (Runnable) new Child();
		child.run();
	}
}
