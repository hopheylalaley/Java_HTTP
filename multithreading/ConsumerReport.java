package multiThreading;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import report.ActiveHostsReport;
import report.MaxReplyBytesReport;
import report.TotalReplySizeReport;
import main.Host;
import main.LogRecord;

public class ConsumerReport implements Runnable {

	private int count = 0;
	private Map<String, Integer> dict;
	private LogRecord maxSize;
	private long totalSize = 0;
	private final int amountOfLines;
	private BlockingQueue<LogRecord> queueLogs;
	
	public static ActiveHostsReport activeHosts;
	public static TotalReplySizeReport totalReplySize;
	public static MaxReplyBytesReport maxReplyBytes;

	ConsumerReport(int amountOfLines, BlockingQueue<LogRecord> queueLogs) {
		dict = new HashMap<String, Integer>();
		maxSize = new LogRecord();
		this.amountOfLines = amountOfLines;
		this.queueLogs = queueLogs;
	}

	@Override
	public void run() {
		try {
			while (count < amountOfLines) {
				consume(queueLogs.take());
			}

			List<Map.Entry<String, Integer>> list = new ArrayList<>(
					dict.entrySet());
			Collections.sort(list,
					(a, b) -> b.getValue().compareTo(a.getValue()));
			if (ActiveHostsReport.numberOfHosts > list.size()) {
				activeHosts = new ActiveHostsReport(list);
			} else {
				activeHosts = new ActiveHostsReport(list.subList(0,
						ActiveHostsReport.numberOfHosts));
			}
			
			totalReplySize = new TotalReplySizeReport(totalSize);
			
			maxReplyBytes = new MaxReplyBytesReport(maxSize);
		} catch (InterruptedException ex) {
		}
	}

	private void consume(LogRecord log) throws InterruptedException {
		int temp = log.getReplyBytes();
		if (temp > maxSize.getReplyBytes()) {
			maxSize = log;
		}
		totalSize += temp;
		++count;

		Host host = log.getHost();
		if (dict.containsKey(host.toString())) {
			dict.put(host.toString(), dict.get(host.toString()) + 1);
		} else {
			dict.put(host.toString(), 1);
		}
	}
}