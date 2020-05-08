
package pl.server;

import java.util.HashSet;
import java.util.Set;

import pl.Main;

public class CustomerDto {

	public Long id;
	public String name;
	public Set<Main> mai = new HashSet<Main>();

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString();
	}
}
