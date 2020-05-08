
package pl;

import pl.server.CustomerDto;

public class Main {

	public static void main(final String[] args) {
		CustomerDto dto = new CustomerDto();
		CustomerDto dto2 = new CustomerDto();
		int size = dto.mai.size();
		dto.name = "asdsa";
		dto.id = 456L;
		System.out.println(dto.name);
	}

}
