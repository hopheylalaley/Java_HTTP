import java.io.IOException;
import java.util.List;

public interface ILogFileParser {

	public List<String> getLines(String path, int numberOfStartLine,
			int amountOfLines) throws IOException;
}