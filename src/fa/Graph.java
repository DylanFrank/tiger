package fa;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

public final class Graph {
	private final static String curDir = System.getProperty("user.dir")+System.getProperty("file.separator")+"output";
	private final static String sysLineSep = System.getProperty("line.separator");
	private final static byte[] lsByteArr = sysLineSep.getBytes();
	private ArrayList<String> edges = new ArrayList<>();
	private ArrayList<String> nodes = new ArrayList<>();

	public Graph() {
		super();
	}
	
	public void addEdge(int from,int to,Character c) {
		edges.add(String.valueOf(from)+" -> "+String.valueOf(to)+String.format("[label=\"%c\"]", c));
	}
	private File writeDotFile(String name) {
		File dotFile = new File(curDir, name+".dot");
		
		// write dot file
		try(FileOutputStream fos = new FileOutputStream(dotFile)){
			fos.write(String.valueOf("digraph tmp{").getBytes());
			fos.write(lsByteArr);
			writeNodes(fos);
			writeEdges(fos);
			fos.write(String.valueOf("}").getBytes());
			fos.write(lsByteArr);
		}catch (IOException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return dotFile;
	}
	private void writeEdges(OutputStream os) throws IOException {
		for(String e: edges){
			os.write(e.getBytes());
			os.write(lsByteArr);
		}
	}
	private void writeNodes(OutputStream os) throws IOException {
		for(String e: nodes){
			os.write(e.getBytes());
			os.write(lsByteArr);
		}
	}
	public void draw(String name) {
		File dotFile = writeDotFile(name);
		File png = new File(curDir,name+".png");
		String cmd = String.format("dot %s -T png -o %s", dotFile.getAbsolutePath(),png.getAbsolutePath());
		try {
			Runtime.getRuntime().exec(cmd);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void draw() {
		String name = "graph";
		draw(name);
	}
	public void setStart(int node) {
		nodes.add(String.format("%d [color=blue]", node));
	}
	public void setEnd(int node) {
		nodes.add(String.format("%d [color=red]", node));
	}
	public static void main(String[] args) {
		Graph G = new Graph();
		G.addEdge(1, 2, 'a');
		G.addEdge(2, 4, 'b');
		G.addEdge(2, 3, 'e');
		G.addEdge(3, 4, ' ');
		G.setStart(1);
		G.setEnd(4);
		G.draw();
	}
}
