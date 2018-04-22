package fa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class NFA {
	class NfaNode extends FaNode{
		HashMap<Character, HashSet<NfaNode>> next;
		
		public NfaNode() {
			super();
			next = new HashMap<>();
		}

		public void addNextNode(char c, FaNode node) {
			NfaNode nd = (NfaNode) node;//if not NfaNode ,there will be a Exception;
			if(next.containsKey(c)) {
				HashSet<NfaNode> tmp = next.get(c);
				tmp.add(nd);
			}else {
				HashSet<NfaNode> tmp = new HashSet<>();
				tmp.add(nd);
				next.put(c, tmp);
			}
		}
	}
	
	public NfaNode start,end;
	
	public NFA(NfaNode start, NfaNode end) {
		super();
		this.start = start;
		this.end = end;
	}
	
	public NFA() {
		super();
		start = new NfaNode();
		end = new NfaNode();
		start.addNextNode(end);
	}
	
	public NFA(char c) {
		start = new NfaNode();
		end = new NfaNode();
		start.addNextNode(c,end);
	}

	/*
	 * link arithmic
	 */
	public NFA link(NFA other) {
		this.end.addNextNode(other.start);
		return new NFA(this.start, other.end);
	}
	/*
	 * star arithimic, re*
	 */
	public NFA star() {
		NfaNode st = new NfaNode();
		NfaNode ed = new NfaNode();
		st.addNextNode(ed);
		st.addNextNode(start);
		end.addNextNode(ed);
		end.addNextNode(start);
		return new NFA(st,ed);
	}
	/*
	 * re1 | re2
	 */
	public NFA or(NFA other) {
		NfaNode st = new NfaNode();
		NfaNode ed = new NfaNode();
		st.addNextNode(other.start);
		st.addNextNode(start);
		this.end.addNextNode(ed);
		other.end.addNextNode(ed);
		return new NFA(st,ed);
	}
	public static void main(String[] args) {
		NFA nfa = new NFA();
		NfaNode st = nfa.start;
		for(Character eCharacter : st.next.keySet()) {
			System.out.println(st.next.get(eCharacter));
		}
		System.out.println(nfa.end);
	}
}
