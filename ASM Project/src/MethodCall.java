
public class MethodCall {
String caller;  //calling method - represented as a string
String callee;  //called method - represented as a string
int count;		//records how many times the call was done

public MethodCall(String caller, String callee) {
	this.caller = caller;
	this.callee = callee;
	this.count = 1;
}

public MethodCall(String str) {
	String [] parts = str.split(" -> ");
	caller = parts[0];
	callee = parts[1];
	count = 1;
}

public void incrementCount() {
	count++;
}

@Override
public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((callee == null) ? 0 : callee.hashCode());
	result = prime * result + ((caller == null) ? 0 : caller.hashCode());
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
	MethodCall other = (MethodCall) obj;
	if (callee == null) {
		if (other.callee != null)
			return false;
	} else if (!callee.equals(other.callee))
		return false;
	if (caller == null) {
		if (other.caller != null)
			return false;
	} else if (!caller.equals(other.caller))
		return false;
	return true;
}

@Override
public String toString() {
	return caller + " -> " + callee;
}

public String asDOTEntry() {
	String [] A = caller.split("\\|");
	String [] B = callee.split("\\|");
	return A[1] + " -> " + B[1] + " [label=" + count + "]";
}

}
