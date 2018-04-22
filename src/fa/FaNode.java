package fa;


public abstract class FaNode {	
	public abstract void addNextNode(char c, FaNode node);
	public void addNextNode(FaNode node) {
		addNextNode(' ',node);
	}
}
