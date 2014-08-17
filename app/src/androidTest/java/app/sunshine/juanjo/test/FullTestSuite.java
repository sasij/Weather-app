package app.sunshine.juanjo.test;

import android.test.suitebuilder.TestSuiteBuilder;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Created by juanjo on 17/08/14.
 */
public class FullTestSuite extends TestSuite {
	public static Test suite() {
		return new TestSuiteBuilder(FullTestSuite.class).includeAllPackagesUnderHere().build();
	}

	public FullTestSuite() {
		super();
	}
}
