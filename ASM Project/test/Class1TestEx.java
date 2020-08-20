import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;


public class Class1TestEx {

	public static CoverageHelper helper;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		new File("output/Class1.class").delete();
		new File("output/output.txt").delete();
		
		Instrumenter.main(new String[] {"-i" , "Class1"});
		Class1.main(null);

		helper = Profiler.getCoverageHelper();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public void testMethodCoverage() {
		Set<String> coveredMethods = helper.getCoveredMethods();

		Set<String> reference = new HashSet<>();
		reference.add("Class1|factorial|(I)I");
		reference.add("Class1|power|(DI)D");
		reference.add("Class1|main|([Ljava/lang/String;)V");
		reference.add("Class1|estimateExp|(DI)D");
		
		assertTrue("Method coverage doesn't match", coveredMethods.equals(reference));
	}
	
	@Test
	public void testMethodCallCoverage() {
		Set<MethodCall> coveredMethodCalls = helper.getCoveredMethodCalls();

		Set<MethodCall> reference = new HashSet<>();
		reference.add(new MethodCall("Class1|factorial|(I)I -> Class1|factorial|(I)I"));
		reference.add(new MethodCall("Class1|estimateExp|(DI)D -> Class1|factorial|(I)I"));
		reference.add(new MethodCall("Class1|main|([Ljava/lang/String;)V -> Class1|estimateExp|(DI)D"));
		reference.add(new MethodCall("Class1|estimateExp|(DI)D -> Class1|power|(DI)D"));
		reference.add(new MethodCall("Class1|main|([Ljava/lang/String;)V -> java/io/PrintStream|println|(D)V"));

		assertTrue("Method call coverage doesn't match", coveredMethodCalls.equals(reference));
	}
	@Test
	public void testBasicBlockCoverage() {
		Set<String> coveredBasicBlocks = helper.getCoveredBasicBlocks(); 

		Set<String> reference = new HashSet<>();
		reference.add("Class1|power|(DI)D|27");
		reference.add("Class1|estimateExp|(DI)D|2");
		reference.add("Class1|power|(DI)D|13");
		reference.add("Class1|estimateExp|(DI)D|41");
		reference.add("Class1|power|(DI)D|2");
		reference.add("Class1|factorial|(I)I|2");
		reference.add("Class1|main|([Ljava/lang/String;)V|2");
		reference.add("Class1|factorial|(I)I|11");
		reference.add("Class1|power|(DI)D|22");
		reference.add("Class1|estimateExp|(DI)D|36");
		reference.add("Class1|factorial|(I)I|6");
		reference.add("Class1|estimateExp|(DI)D|13");

		assertTrue("Basic block coverage doesn't match", coveredBasicBlocks.equals(reference));
	}
	@Test
	public void testBasicBlockEdgeCoverage() {
		Set<BasicBlockEdge> coveredBasicBlockEdges = helper.getCoveredBasicBlockEdges();

		Set<BasicBlockEdge> reference = new HashSet<>();
		reference.add(new BasicBlockEdge("Class1|factorial|(I)I|11 -> Class1|factorial|(I)I|2"));
		reference.add(new BasicBlockEdge("Class1|estimateExp|(DI)D|2 -> Class1|estimateExp|(DI)D|36"));
		reference.add(new BasicBlockEdge("Class1|power|(DI)D|22 -> Class1|power|(DI)D|13"));
		reference.add(new BasicBlockEdge("Class1|estimateExp|(DI)D|13 -> Class1|power|(DI)D|2"));
		reference.add(new BasicBlockEdge("Class1|power|(DI)D|22 -> Class1|power|(DI)D|27"));
		reference.add(new BasicBlockEdge("Class1|estimateExp|(DI)D|36 -> Class1|estimateExp|(DI)D|13"));
		reference.add(new BasicBlockEdge("Class1|power|(DI)D|13 -> Class1|power|(DI)D|22"));
		reference.add(new BasicBlockEdge("Class1|factorial|(I)I|2 -> Class1|factorial|(I)I|11"));
		reference.add(new BasicBlockEdge("Class1|estimateExp|(DI)D|36 -> Class1|estimateExp|(DI)D|41"));
		reference.add(new BasicBlockEdge("Class1|main|([Ljava/lang/String;)V|2 -> Class1|estimateExp|(DI)D|2"));
		reference.add(new BasicBlockEdge("Class1|factorial|(I)I|2 -> Class1|factorial|(I)I|6"));
		reference.add(new BasicBlockEdge("Class1|factorial|(I)I|6 -> Class1|estimateExp|(DI)D|36"));
		reference.add(new BasicBlockEdge("Class1|power|(DI)D|2 -> Class1|power|(DI)D|22"));
		reference.add(new BasicBlockEdge("Class1|power|(DI)D|27 -> Class1|factorial|(I)I|2"));
		
		System.out.println("HEY"+coveredBasicBlockEdges);
		assertTrue("Basic block edge coverage doesn't match", coveredBasicBlockEdges.equals(reference));
	}
}
