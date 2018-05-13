package fa;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Stack;

import fa.NFA.NfaNode;


public final class FA {

	private static Stack<NFA> nfaStack ;
	private static Stack<Character> symStack;// only have ['|','(']
	private static void popSymStack() {
		while (!symStack.empty()) {
			char c = symStack.pop();
			if(c == '(')break;
			NFA f1  = nfaStack.pop();
			NFA f2 = nfaStack.pop();
			nfaStack.push(f1.or(f2));
		}
	}
	/*
	 * Thompson Algo
	 */
	
	public static NFA toNFA(String re) {
		nfaStack = new Stack<>();
		symStack = new Stack<>();
		boolean needLink = false;
		for(int i=0 ; i<re.length() ; ++i) {
			char tmp = re.charAt(i);
			switch (tmp) {
			case '(':
				symStack.push(tmp);
				needLink = false;
				break;
			case ')':
				popSymStack();
				needLink = false;
				break;
			case '|':
				symStack.push(tmp);
				needLink = false;
				break;
			case '*':
				needLink = true;
				NFA nfa = nfaStack.pop();
				nfaStack.push(nfa.star());
				break;
			default:
				NFA nfaChar = new NFA(tmp);
				if(needLink){
					NFA top =nfaStack.pop();
					nfaChar = top.link(nfaChar);
				}
				nfaStack.push(nfaChar);
				needLink = true;
				break;
			}
		}
		while (!symStack.empty()) {
			popSymStack();
		}
		while (nfaStack.size()>1) {
			NFA top1 = nfaStack.pop();
			NFA top2 = nfaStack.pop();
			nfaStack.push(top2.link(top1));
		}
		return nfaStack.pop();
	}
	
	/*
	 * construct sub-set Algorithm :NFA -> DFA
	 */
	public static DFA NFA2DFA(NFA nfa) {
		HashMap<HashSet<NfaNode>, Integer> num= new HashMap<>();
		DFA ret = new DFA();
		int cnt =0;
		HashSet<NfaNode> start = nfa.start.memepsClosure();//eps_closure
		start.add(nfa.start);
		LinkedList<HashSet<NfaNode>> Q = new LinkedList<>();
		num.put(start, cnt++);
		Q.add(start);
		while (!Q.isEmpty()) {
			HashSet<NfaNode> now = Q.removeFirst();
			HashMap<Character, HashSet<NfaNode>> delta = new HashMap<>();//delta(c,node) transformed table
			// 计算 delta matrix
			for(NfaNode node: now){
				for(Map.Entry<Character, HashSet<NfaNode>> entry: node.edgeEntry()){
					if(entry.getKey().equals(' '))continue;
					if(!delta.containsKey(entry.getKey()))delta.put(entry.getKey(), new HashSet<>());
					HashSet<NfaNode> next = delta.get(entry.getKey());
					for(NfaNode no: entry.getValue()){
						next.add(no);
						next.addAll(no.memepsClosure());//到达点 + 传递闭包
					}
				}
			}
			int from = num.get(now);
			for(Map.Entry<Character,HashSet<NfaNode>> entry: delta.entrySet()){
				if(!num.containsKey(entry.getValue())){//new Set Node
					num.put(entry.getValue(), cnt++);
					Q.add(entry.getValue());
				}
				ret.addEdge(from, num.get(entry.getValue()), entry.getKey());
			}
		}
		// set start state and end state
		for(Map.Entry<HashSet<NfaNode>, Integer> entry : num.entrySet()){
			HashSet<NfaNode> tmp = entry.getKey();
			if(tmp.contains(nfa.start))ret.addStart(entry.getValue());
			if(tmp.contains(nfa.end))ret.addEnd(entry.getValue());
		}
		return ret;
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		NFA tmp = FA.toNFA("a(befe|bcce)*");
		tmp.toGraph().draw("nfa");
		DFA dfa = NFA2DFA(tmp);
		dfa.toGraph().draw("dfa");
		DFA min = dfa.hopcroft();
		min.toGraph().draw("hopcroft");
	}

}
