import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CoverageHelper {

	Set<String> coveredMethods;
	Set<MethodCall> coveredMethodCalls;
	
	Set<String> coveredBasicBlocks;
	Set<BasicBlockEdge> coveredBasicBlockEdges;
	
	
	public CoverageHelper() {
		coveredMethods = new HashSet<>();
		coveredMethodCalls = new HashSet<>();
		
		coveredBasicBlocks = new HashSet<>();
		coveredBasicBlockEdges = new HashSet<>();
	}


	public void coverMethod(String m) {
		coveredMethods.add(m);
	}
	

	public void coverMethodCall(MethodCall mc) {
		for(MethodCall m:coveredMethodCalls) {
			if(m.equals(mc)) {
				m.incrementCount();
				return;
			}
		}
		coveredMethodCalls.add(mc);
	}
	
	public void coverBasicBlock(String bb) {
		coveredBasicBlocks.add(bb);
	}
	

	public void coverBasicBlockEdge(BasicBlockEdge bbe) {
		for(BasicBlockEdge bb: coveredBasicBlockEdges) {
			if(bb.equals(bbe)) {
				bb.increment();
				return;
			}
		}
		coveredBasicBlockEdges.add(bbe);
	}

	public Set<String> getCoveredMethods() { return coveredMethods;}
	public Set<MethodCall> getCoveredMethodCalls() { return coveredMethodCalls;}
	public Set<String> getCoveredBasicBlocks() { return coveredBasicBlocks;}
	public Set<BasicBlockEdge> getCoveredBasicBlockEdges() { return coveredBasicBlockEdges;}
}
