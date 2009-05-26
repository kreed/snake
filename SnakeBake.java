
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.ArrayList;

public class SnakeBake {
	public static void main(String[] args) throws Exception
	{
		if (args.length == 0) {
			System.err.println("Usage: SnakeBake map.sm");
			System.exit(1);
		}

		BufferedReader in = new BufferedReader(new FileReader(args[0]));
		ArrayList<String> rows = new ArrayList<String>();
		while (in.ready())
			rows.add(in.readLine());
		in.close();

		final int magic = 42;
		int width = rows.get(0).length();
		int height = rows.size();

		if (width > 255 || height > 255) {
			System.err.println("Snake maps cannot be larger than 255x255 blocks");
			System.exit(1);
		}

		FileOutputStream out = new FileOutputStream(args[0] + "g");
		out.write(magic);
		out.write(width);
		out.write(height);
		for (String row : rows) {
			char[] list = row.toCharArray();
			int block = 0;
			int i;
			for (i = 0; i != list.length; ++i) {
				switch (list[i]) {
				case 'x':
					block |= 1;
					break;
				case 'p':
					block |= 2;
					break;
				}
				if (i % 4 == 3) {
					out.write(block);
					block = 0;
				} else
					block <<= 2;
			}
			if (i % 4 != 0)
				out.write(block >> 2);
		}
		out.close();
	}
}
