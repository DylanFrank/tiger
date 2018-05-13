package fa;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class DFA {
	static class DFANode extends FaNode{
		private HashMap<Character, DFANode> edges;
		int label;
		public DFANode(int lab) {
			super();
			this.label = lab;
			edges = new HashMap<>();
		}
		
		@Override
		public void addNextNode(char c, FaNode node) {
			edges.put(c, (DFANode)node);
		}
		@Override
		public boolean equals(Object obj) {
			if(obj==null)return false;
			if(obj.getClass()!=this.getClass())return false;
			DFANode cmpObj = (DFANode) obj;
			return (cmpObj.label==this.label);
		}
		@Override
		public int hashCode() {
			return Integer.hashCode(label);
		}
		public Set<Map.Entry<Character, DFANode>> edgeSet() {
			return edges.entrySet();
		}
		public DFANode neighbor(Character c) {
			return edges.get(c);
		}
	}
	private class HopcroftAlgo{
		Map<DFANode, Set<DFANode>> belong;
		public HopcroftAlgo() {
			super();
			this.belong = new HashMap<>();
			Set<DFANode> acSet = new HashSet<>();
			Set<DFANode> nacSet = new HashSet<>();
			//O(n)
			for(DFANode node: nodes.values()){
				if(endSet.contains(node)){
					acSet.add(node);
					belong.put(node, acSet);
				}else{
					nacSet.add(node);
					belong.put(node, nacSet);
				}
			}
		}
		private List<Set<DFANode>> split(Set<DFANode> set, Character c) {
			Set<DFANode> yes = new HashSet<>();
			Set<DFANode> no= new HashSet<>();
			for(DFANode node: set){
				if(node.edges.containsKey(c)){
					yes.add(node);
				}
				else{
					no.add(node);
				}
			}
			return Arrays.asList(yes,no);
		}
		private List<Set<DFANode>> split(Set<DFANode> splitSet) {
			if(splitSet.size()<=1)Arrays.asList(splitSet);
			Set<Character> sigma = splitSet.iterator().next().edges.keySet();
			// select min char set
			for(DFANode node: splitSet){
				if(node.edges.keySet().size() <sigma.size())sigma = node.edges.keySet();
			}
			for(DFANode node: splitSet){
				for(Character c: node.edges.keySet()){
					if(!sigma.contains(c)){
						//ok split the Set
						return split(splitSet, c);
					}
				}
			}
			
			for(Character c: sigma){
				Map<Set<DFANode>, Set<DFANode>> map = new HashMap<>();
				for(DFANode node: splitSet){
					DFANode near = node.neighbor(c);
					Set<DFANode> toSet= belong.get(near);
					if(!map.containsKey(toSet))map.put(toSet, new HashSet<>());
					map.get(toSet).add(node);
				}
				if(map.size() >1){
					return new LinkedList<>(map.values());
				}
			}
			return Arrays.asList(splitSet);
		}
		private DFA construct(Map<Set<DFANode>, Integer> map) {
			DFA ret = new DFA();
			for(Map.Entry<Set<DFANode>, Integer> entry: map.entrySet()){
				DFANode from = entry.getKey().iterator().next();
				int label = entry.getValue();
				for(Map.Entry<Character, DFANode> e2: from.edgeSet()){
					ret.addEdge(label, map.get(belong.get(e2.getValue())), e2.getKey());
				}
				for(DFANode node: startSet)
					if(entry.getKey().contains(node)){
						ret.addStart(label);
						break;
					}
				for(DFANode node: endSet)
					if(entry.getKey().contains(node)){
						ret.addEnd(label);
						break;
					}
			}
			
			return ret;
		}
		public DFA hopcroft() {
			int cnt =0;
			Map<Set<DFANode>, Integer> map = new HashMap<>();
			LinkedList<Set<DFANode>> Q = new LinkedList<>(belong.values());
			while (!Q.isEmpty()) {
				Set<DFANode> candidate = Q.removeFirst();
				if(map.containsKey(candidate))continue;
				List<Set<DFANode>> ret = split(candidate);
				if(ret.size()>1){
					for(Set<DFANode> set: ret){
						Q.add(set);
						for(DFANode node: set)
							belong.replace(node, set);
					}
				}else {
					map.put(ret.iterator().next(), cnt++);
				}
			}
			return construct(map);
		}
	}
	private HashMap<Integer, DFANode> nodes;
	private HashSet<DFANode> startSet = new HashSet<>();//start state
	private HashSet<DFANode> endSet = new HashSet<>();//end state
	public DFA() {
		super();
		nodes = new HashMap<>();
	}
	private boolean addNode(int label) {
		boolean ret = true;
		if(!nodes.containsKey(label)){
			nodes.put(label, new DFANode(label));
			ret = false;
		}
		return ret;
	}
	public void addEdge(int from,int to,char c) {
		addNode(from);
		addNode(to);
		DFANode fNode = nodes.get(from);
		DFANode t= nodes.get(to);
		fNode.addNextNode(c, t);
	}
	
	public Graph toGraph() {
		Graph G=  new Graph();
		for(DFANode node: nodes.values()){
			for(Map.Entry<Character, DFANode> entry : node.edgeSet()){
				G.addEdge(node.label, entry.getValue().label, entry.getKey());
			}
		}
		for(DFANode node: startSet)G.setStart(node.label);
		for(DFANode node: endSet) G.setEnd(node.label);
		return G;
	}
	public boolean addStart(int u) {
		if(nodes.containsKey(u))return startSet.add(nodes.get(u));
		return false;
	}
	public boolean addEnd(int u) {
		if(nodes.containsKey(u))return endSet.add(nodes.get(u));
		return false;
	}
	
	public DFA hopcroft() {
		return new HopcroftAlgo().hopcroft();
	}
	public static void main(String[] args) {
		
	}

}
