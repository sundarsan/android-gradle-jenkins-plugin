package pl.droidsonroids.gradle.jenkins

import com.android.utils.ILogger
import groovy.transform.TupleConstructor

@TupleConstructor
class LoggerBasedOutputReceiver extends BaseOutputReceiver {
	ILogger logger

	@Override
	void processNewLines(String[] lines) {
		lines.each { logger.println(it) }
	}
}