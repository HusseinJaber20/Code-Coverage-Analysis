import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;
import org.objectweb.asm.util.Printer;
import org.objectweb.asm.util.Textifier;
import org.objectweb.asm.util.TraceMethodVisitor;

abstract class BasicBlockMethodVisitor extends MethodVisitor{
	public BasicBlockMethodVisitor(int api, MethodVisitor methodVisitor) {
		super(api, methodVisitor);
	}

	public abstract void visitBasicBlock(int count);
}

	

public class BasicBlockGeneratorMethodVisitor extends MethodVisitor{
	private BasicBlockMethodVisitor m_methodVisitor;
	private MethodNode m_methodNode;
	private Map<Integer, String> locals;
	private List<Integer> instructionNumber;
	
	public BasicBlockGeneratorMethodVisitor(int api, BasicBlockMethodVisitor methodVisitor) {
		super(api,methodVisitor); // Ignore methodVisitor - don't forward calls
		m_methodNode = new MethodNode();
		this.m_methodVisitor = methodVisitor;
	}

	
	@Override
	public void visitCode() {
		//m_methodVisitor.visitCode(); 
		// stop redirecting to next method visitor
		// redirect to method node instead
		mv = m_methodNode; 
		mv.visitCode();
	}
	
	@Override
	public void visitEnd() {
		m_methodNode.visitEnd();
		m_methodVisitor.visitCode();		 
		
		buildMethodBasicBlockDesignators();
		AbstractInsnNode[] arr = m_methodNode.instructions.toArray();
		for (int i = 0 ; i < arr.length ; i++) {
			if (instructionNumber.contains(i))
				m_methodVisitor.visitBasicBlock(i);
			arr[i].accept(m_methodVisitor);
		}
		m_methodVisitor.visitMaxs(0, 0);
		m_methodVisitor.visitEnd();
	}
	
	public static Map<String, List<String>> basicBlockMap = new HashMap<String, List<String>>();
		
	void storeLocals() {
		locals = new HashMap<Integer, String>();
		List<LocalVariableNode> locVariables = (List<LocalVariableNode>)m_methodNode.localVariables;
		if(locVariables != null){
			for(final LocalVariableNode local : locVariables) {
				locals.put(local.index, local.name);
            }
		}
	}

	
	public void goOverCode(MethodNode methodNode){
		printer = new Textifier();
		mp = new TraceMethodVisitor(printer);

		for(AbstractInsnNode insn: methodNode.instructions.toArray()){
			String instructionName = insnToString(insn);
		}
	}
	
	
	
	public void buildMethodBasicBlockDesignators(){
		storeLocals();
		
		
		printer = new Textifier();
		mp = new TraceMethodVisitor(printer);
		List<Integer> basicBlockLeaders = new ArrayList<Integer>();
		instructionNumber = new ArrayList<Integer>();
		
		final List<TryCatchBlockNode> trycatchBlock = m_methodNode.tryCatchBlocks;
		List<Integer> targetLabels = new ArrayList<Integer>();
		List<LabelNode> trycatches = new ArrayList<LabelNode>();

		if (trycatchBlock != null) {
			for(TryCatchBlockNode tcb: trycatchBlock){
				trycatches.add(tcb.start);
				trycatches.add(tcb.handler);
				trycatches.add(tcb.end);
			}	
		}
		
		printer = new Textifier();
		mp = new TraceMethodVisitor(printer);
		boolean isLeader = true;
		int i = 0;
		for(AbstractInsnNode insn: m_methodNode.instructions.toArray()){
			String instructionName = insnToString(insn);
			if(isLeader){
				isLeader = false;
				if(insn instanceof LabelNode){
					if(insn.getNext() != null){
						Integer label = Integer.valueOf(instructionName.split(" L")[1].trim());
						if(!basicBlockLeaders.contains(label)){
							basicBlockLeaders.add(label);
					}
				}
				}else{
					if(!instructionNumber.contains(i)){
						instructionNumber.add(i);
					}
				}
			}

			if(insn instanceof JumpInsnNode){
				isLeader = true;
				Integer targetLabelNumber= Integer.valueOf(instructionName.split(" L")[1].trim()); 
				if(!targetLabels.contains(targetLabelNumber)){
					targetLabels.add(targetLabelNumber);
				}
			}else if(insn instanceof LookupSwitchInsnNode ){  
				LookupSwitchInsnNode lookupNode = (LookupSwitchInsnNode) insn;
				Integer targetLabelNumber;
				if(lookupNode.labels.size() > 0){
					targetLabelNumber= Integer.valueOf(insnToString(lookupNode.labels.get(0)).split(" L")[1].trim());
				}else{
					targetLabelNumber= Integer.valueOf(insnToString(lookupNode.dflt).split(" L")[1].trim());
				}
				if(!targetLabels.contains(targetLabelNumber)){ 
					targetLabels.add(targetLabelNumber);
				}
			}else if(insn instanceof TableSwitchInsnNode){ 
				TableSwitchInsnNode tableSwitchNode = (TableSwitchInsnNode) insn;
				Integer targetLabelNumber;
				if(tableSwitchNode.labels.size() > 0){
					targetLabelNumber= Integer.valueOf(insnToString(tableSwitchNode.labels.get(0)).split(" L")[1].trim());
				}else{
					targetLabelNumber= Integer.valueOf(insnToString(tableSwitchNode.dflt).split(" L")[1].trim());
				}
				if(!targetLabels.contains(targetLabelNumber)){
					targetLabels.add(targetLabelNumber);
				}
			}else if((insn.getOpcode() >= Opcodes.IRETURN && insn.getOpcode() <= Opcodes.RETURN)
					|| insn.getOpcode() == Opcodes.ATHROW){
				isLeader = true;
			}
			i++;
		}


		printer = new Textifier();
		mp = new TraceMethodVisitor(printer);

		for(AbstractInsnNode insn : m_methodNode.instructions.toArray()){
			String instructionName = insnToString(insn);	
			if(insn.getType() == AbstractInsnNode.LABEL){
				Integer label = Integer.valueOf(instructionName.split("L")[1].trim());
				if(targetLabels.contains(label)){
					if(!basicBlockLeaders.contains(label)){
						basicBlockLeaders.add(label);
					}
				}
				if(trycatches.contains((LabelNode)insn)){
					if(!basicBlockLeaders.contains(label)){
						basicBlockLeaders.add(label);
					}
				}
			}
		}
		
		int count = 0;
		for(AbstractInsnNode insn : m_methodNode.instructions.toArray()){
			String instructionName = insnToString(insn);	
			if(insn instanceof LabelNode){
				Integer label = Integer.valueOf(instructionName.split("L")[1].trim());
				if(basicBlockLeaders.contains(label)){
					int tempCount = count;
					AbstractInsnNode nextNode = insn;
					boolean findInstruction = (nextNode instanceof LabelNode) ||
												(nextNode instanceof LineNumberNode) ||
												(nextNode instanceof FrameNode);
					while(findInstruction){
						nextNode = nextNode.getNext();
						tempCount++;
						findInstruction = (nextNode instanceof LabelNode) ||
								(nextNode instanceof LineNumberNode) ||
								(nextNode instanceof FrameNode);
					}
					
					if(!instructionNumber.contains(tempCount)){
						instructionNumber.add(tempCount);
					}
				}
			}
			count++;
		}

		printer = new Textifier();
		mp = new TraceMethodVisitor(printer);
		count = 0;
		int lineNumber = 0;

		for(AbstractInsnNode insn : m_methodNode.instructions.toArray()){
			String instructionName = insnToString(insn);
			if(insn instanceof LineNumberNode){
				LineNumberNode ln = (LineNumberNode) insn;
				lineNumber = ln.line;
			}

			else if(insn instanceof LabelNode){
				// do nothing
			}

			else if(instructionNumber.contains(count)){ //Basic block leader
				//System.out.println(className+ " " + methodName+ " " + methodNode.desc+ " " + count+ " " + lineNumber);
			}	
			count++;
		}
		
		
	}


	public static String insnToString(AbstractInsnNode insn){
		insn.accept(mp);
		StringWriter sw = new StringWriter();
		printer.print(new PrintWriter(sw));
		printer.getText().clear();
		return sw.toString();
	}

	public static void output(String filename, byte[] data) throws IOException {
		FileOutputStream out=new FileOutputStream(filename);
		out.write(data);
		out.close();
	}

	private static Printer printer = new Textifier();
	private static TraceMethodVisitor mp = new TraceMethodVisitor(printer); 



}
