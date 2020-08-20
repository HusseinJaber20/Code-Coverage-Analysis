import java.io.*;
import org.objectweb.asm.*;

public class Instrumenter implements Opcodes{
	
	public static void main(String[] args) throws Exception {
		if (args.length != 2 || !args[0].equals("-i")) {
			System.out.println("Usage: Instrumenter -i ClassName");
		}
		
		String className = args[1];
		String inputPath = "bin/" + className + ".class";
		String outputPath = "output/" + className + ".class";
		
		InputStream in = new FileInputStream(inputPath);
		ClassReader cr = new ClassReader(in);
		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
		ClassVisitor cv = new InstrumenterClassVisitor(ASM7, cw) { };
		cr.accept(cv, ClassReader.EXPAND_FRAMES);
		
		byte[] b2 = cw.toByteArray();
		FileOutputStream fos = new FileOutputStream(outputPath);
		fos.write(b2);
		fos.close();
		System.out.println("Instrumentation Complete!");
	}
}

