public class BasicBlockEdge {
String source; //source basic block - represented as a string: className|methodName|methodSignature|leaderIndex
String target; //target basic block - represented as a string: className|methodName|methodSignature|leaderIndex
int count;
public BasicBlockEdge(String source, String target) {
	this.source = source;
	this.target = target;
	count=1;
}

public BasicBlockEdge(String str) {
	String [] parts = str.split(" -> ");
	source = parts[0];
	target = parts[1];
	count=1;
}
public void increment() {
	count=count+1;
}
public String asDOTEntry() {
	String [] A = source.split("\\|");
	String [] B = target.split("\\|");
	return A[1] + A[3]+ " -> " + B[1]+B[3]+ " [label=" + count + "]";
}
@Override
public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((target == null) ? 0 : target.hashCode());
	result = prime * result + ((source == null) ? 0 : source.hashCode());
	return result;
}

@Override
public boolean equals(Object obj) {
	if (this == obj)
		return true;
	if (obj == null)
		return false;
	if (getClass() != obj.getClass())
		return false;
	BasicBlockEdge other = (BasicBlockEdge) obj;
	if (target == null) {
		if (other.target != null)
			return false;
	} else if (!target.equals(other.target))
		return false;
	if (source == null) {
		if (other.source != null)
			return false;
	} else if (!source.equals(other.source))
		return false;
	return true;
}

@Override
public String toString() {
	return source + " -> " + target;
}

}
