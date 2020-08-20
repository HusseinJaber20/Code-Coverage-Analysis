import java.util.ArrayList;

public class Profiler {
	private static CoverageHelper helper = new CoverageHelper();
	static String lastInserted=null;
	static int BasicBlocks=0;
	static int Methods=0;
	// EECE 334: INSERT CODE HERE
	
	static Thread shutdownHook;
	
	static class ProfilerShutdownHandler extends Thread implements Runnable {	
        public ProfilerShutdownHandler() {
	    super();
        }
	
        @SuppressWarnings("unlikely-arg-type")
		public void run() {
		    System.out.println("Shutdown hook running");
		    
		    //Generating the DOT code
		    System.out.println("\n--- Generating DOT code ----\n");
		    System.out.println("digraph{");
		    for(MethodCall mc:helper.coveredMethodCalls)
		    	if(helper.coveredMethods.contains(mc.caller) && helper.coveredMethods.contains(mc.callee) && mc.toString().indexOf("<init>")<0)
		    		System.out.println(mc.asDOTEntry());
	        System.out.println("}");
	        
	        
	        System.out.println("------------------------------------------");
	        
	        
	        
	        // Getting the percentage of covered methods:
	        double a=helper.coveredMethods.size();
	        System.out.println("Total number of methods in Class1 is "+ Methods);
	        System.out.println("Total number of methods actually called is "+(a+1));
	        System.out.println("Percentage = "+((a+1)/Methods)*100); // I added one because it calls the constructor init too. 
	        
	        System.out.println("------------------------------------------");
	        
	        
	        
	        //Getting the percentage of covered BasicBlocks:
	        a=helper.coveredBasicBlocks.size();
	        System.out.println("Total number of BasicBlocks in Class1 is "+ BasicBlocks);
	        System.out.println("Total number of BasicBlocks actually called is "+a);
	        System.out.println("Percentage = "+(a/BasicBlocks)*100);
	        
	        
	        System.out.println("------------------------------------------");

	        //Dynamic CFG
	        System.out.println("\n--- Generating DOT code ----\n");
		    System.out.println("digraph{");
		    for(BasicBlockEdge bb:helper.coveredBasicBlockEdges) {
		    	if(helper.coveredBasicBlocks.contains(bb.source) && helper.coveredBasicBlocks.contains(bb.target)) {
		    		System.out.println(bb.asDOTEntry());
		    	}
		    }
	        System.out.println("}");
	        
	        
	        
	        System.out.println("------------------------------------------");
        }
	}
	
	public static void simulateStart() {
		System.out.println("Starting Profiler");
		Runtime.getRuntime().addShutdownHook(shutdownHook = new ProfilerShutdownHandler());
	}
	
	static {
		simulateStart();
    }
	
	public static CoverageHelper getCoverageHelper() {return helper;}
	
	public static void handleMethodEntry(String className, String methodName, String methodSignature) {
		// EECE 334: INSERT CODE HERE
		String result=className+"|"+methodName+"|"+methodSignature;
		//function to call
		helper.coverMethod(result);
		
	}
	
	public static void handleMethodInvoke(
			String srcClassName,
			String srcMethodName,
			String srcMethodSignature,
			String dstClassName,
			String dstMethodName,
			String dstMethodSignature
			) 
	{
		// EECE 334: INSERT CODE HERE
		System.out.println("From " + srcClassName + "." + srcMethodName + "." + srcMethodSignature);
		System.out.println("...Calling method " + dstClassName + "." + dstMethodName + "." + dstMethodSignature);
		
		String method = srcClassName + "|" + srcMethodName + "|" + srcMethodSignature + " -> " + dstClassName + "|" + dstMethodName + "|" + dstMethodSignature;
		MethodCall a= new MethodCall(method);
		helper.coverMethodCall(a);
		//Done
	}
	public static void handleBasicBlockEntry(String className, String methodName, String methodSignature, String leaderIndex) {
		String result=className+"|"+methodName+"|"+methodSignature+"|"+leaderIndex;
		if(lastInserted==null) {
			helper.coverBasicBlock(result);
			lastInserted=result;
		}
		else {
			helper.coverBasicBlock(result);
			String edge= lastInserted+" -> "+result;
			BasicBlockEdge myedge= new BasicBlockEdge(edge);
			helper.coverBasicBlockEdge(myedge);
			lastInserted=result;
		}
	}
	
	
	public static void handleLineNumber(String className, String methodName, String methodSignature, String line) {
		//System.out.println("---------- Reached line " + line);
	}
	
}
