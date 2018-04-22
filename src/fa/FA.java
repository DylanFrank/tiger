package fa;

import java.util.Stack;


public final class FA {

	private static Stack<NFA> nfaStack = new Stack<>();
	private static Stack<Character> symStack = new Stack<>();// only have ['|','(']
	private static void popSymStack() {
		while (!symStack.empty()) {
			char c = symStack.pop();
			switch (c) {
			case '|':
				NFA f1  = nfaStack.pop();
				NFA f2 = nfaStack.pop();
				nfaStack.push(f1.or(f2));
				break;

			default:
				break;
			}
		}
	}
	public static NFA toNFA(String re) {
		for(int i=0 ; i<re.length() ; ++i) {
			char tmp = re.charAt(i);
			switch (re.charAt(i)) {
			case '(':
				symStack.push(tmp);
				break;
			case ')':
				popSymStack();
				break;
			case '|':
				symStack.push(tmp);
				break;
			case '*':
				NFA nfa = nfaStack.pop();
				nfaStack.push(nfa.star());
				break;
			default:
				NFA nfa2 = new NFA(tmp);	// build a new NFA with a char
				nfa = nfaStack.pop();
				nfaStack.push(nfa.link(nfa2));	// link nfa2
				break;
			}
		}
		while (!symStack.empty()) {
			popSymStack();
		}
		return new NFA();
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
	}

}
