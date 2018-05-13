package fa;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import fa.NFA.NfaNode;


public class NFA {
	static class NfaNode extends FaNode{
		private HashMap<Character, HashSet<NfaNode>> next;
		private HashSet<NfaNode> epSet;
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
		public HashSet<NfaNode> getNext(Character c) {
			return next.get(c);
		}
		public Set<Character> keySet() {
			return next.keySet();
		}
		public Set<Map.Entry<Character, HashSet<NfaNode>>>  edgeEntry() {
			return next.entrySet();
		}
		/*
		 * judge if nfanode can transform through given char @{c}
		 */
		public boolean canTrans(char c) {
			return next.containsKey(c);
		}
		/*
		 * eps_closure(s) = delta(s,eps) UNION eps(delta(e,eps))
		 */
		private HashSet<NfaNode> epsClosure() {
			HashSet<NfaNode> ret = new HashSet<>();
			LinkedList<NfaNode> Q = new LinkedList<>();
			Q.add(this);
			while (!Q.isEmpty()) {
				NfaNode now = Q.removeFirst();
				if(now == null|| !now.canTrans(' '))continue;
				for(NfaNode no: now.getNext(' ')){
					if(no!=null && ret.add(no))Q.add(no);
				}
			}
			return ret;
		}
		/*
		 * cache eps_closure
		 */
		public HashSet<NfaNode> memepsClosure() {
			if(epSet==null)epSet = epsClosure();
			return epSet;
		}
		public void updateEpset() {
			epSet = null;
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
	
	public HashSet<NfaNode> travelNodes() {
		HashSet<NfaNode> ret = new HashSet<>();
		LinkedList<NfaNode> Q = new LinkedList<>();
		ret.add(start);
		Q.add(start);
		while (!Q.isEmpty()) {
			NfaNode now = Q.removeFirst();
			for(HashSet<NfaNode> nodes: now.next.values()){
				for(NfaNode no: nodes)
					if(ret.add(no))Q.add(no);
			}
		}
		return ret;
	}
	
	public Graph toGraph() {
		Graph G = new Graph();
		int num =0;
		HashMap<NfaNode, Integer> label = new HashMap<>();
		HashSet<NfaNode> vis = new  HashSet<>();
		LinkedList<NfaNode> Q = new LinkedList<>();
		Q.add(start);
		label.put(start, num++);
		while (!Q.isEmpty()) {
			NfaNode now = Q.removeFirst();
			if(!vis.add(now))continue;
			for(Character character : now.next.keySet()){
				HashSet<NfaNode> nebor = now.next.get(character);
				for(NfaNode node: nebor){
					if(!label.containsKey(node))label.put(node, num++);
					G.addEdge(label.get(now), label.get(node), character);
					if(!vis.contains(node))Q.addLast(node);
				}
			}
		}
		G.setStart(label.get(start));
		G.setEnd(label.get(end));
		return G;
	}
	
	public static void main(String[] args) {
		
	}
	
}
