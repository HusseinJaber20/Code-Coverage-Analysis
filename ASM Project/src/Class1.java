public class Class1 {
	
	static int field1;
	static int staticField;
	
	public static void neverCalled() {
		int x = staticField;
	} 
	
	static int factorial(int N) { //assuming N is non-negative
		if(N == 0)
			return 1;
		else
			return N*factorial(N-1);
	}
	
	static double power(double x, int N) {//assuming N is non-negative
		double result = 1;
		for(int i=1;i<=N;i++)
			result *= x;
		return result;
	}
	
	public static double estimateExp(double x, int N) {
		double result = 0;
		for(int i=0;i<=N;i++)
		{
			double numerator = power(x, i);
			int denomenator = factorial(i);
			result += numerator/denomenator;
		}
		return result;
	}
	
	public static void main(String[] args) {
		System.out.println(estimateExp(1, 10));
	}
}