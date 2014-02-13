package tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ SinglePlayerTests.class, YahtzeeMathTest.class, GameLogicTest.class })
public class AllTests {

}
