package tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ SinglePlayerTests.class, YahtzeeMathTest.class, GameLogicTest.class, PersistenceTest.class, MultiPlayerTest.class})
public class AllTests {

}
