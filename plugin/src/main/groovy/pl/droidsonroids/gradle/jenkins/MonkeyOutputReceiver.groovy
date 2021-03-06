package pl.droidsonroids.gradle.jenkins

import com.android.ddmlib.MultiLineReceiver

class MonkeyOutputReceiver extends MultiLineReceiver {

	private final PrintWriter printWriter
	private boolean isCancelled

	MonkeyOutputReceiver(File outputFile) throws IOException {
		printWriter = new PrintWriter(new BufferedWriter(new FileWriter(outputFile, true), 16 * 1024))
	}

	@Override
	public void processNewLines(String[] lines) {
		lines.each { printWriter.println(it) }
	}

	@Override
	public void done() {
		printWriter.flush()
		printWriter.close()
	}

	@Override
	public boolean isCancelled() {
		isCancelled
	}

	public void cancel() {
		isCancelled = true
	}
}