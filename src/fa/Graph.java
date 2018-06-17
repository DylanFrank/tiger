package fa;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public final class Graph {
	private Path curPath = Paths.get("./output");
	private final static String sysLineSep = System.getProperty("line.separator");
	private ArrayList<String> edges = new ArrayList<>();
	private ArrayList<String> nodes = new ArrayList<>();
	public Graph() {
		super();
	}
	
	public Graph(Path outputPath) {
		super();
		this.curPath = outputPath;
	}

	public void addEdge(int from,int to,Character c) {
		edges.add(String.valueOf(from)+" -> "+String.valueOf(to)+String.format("[label=\"%c\"]", c));
	}
	private Path writeDotFile(String name) {
		name = name+".dot";
		Path dotFile = curPath.resolve(name);
		// write dot file
		try(BufferedWriter fw = Files.newBufferedWriter(dotFile, Charset.forName("US-ASCII"))){
			fw.write(String.valueOf("digraph tmp{"));
			fw.write(sysLineSep);
			writeNodes(fw);
			writeEdges(fw);
			fw.write(String.valueOf("}"));
			fw.write(sysLineSep);
		}catch (IOException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return dotFile;
	}
	private void writeEdges(Writer writer) throws IOException {
		for(String e: edges){
			writer.write(e);
			writer.write(sysLineSep);
		}
	}
	private void writeNodes(Writer writer) throws IOException {
		for(String e: nodes){
			writer.write(e);
			writer.write(sysLineSep);
		}
	}
	public void draw(String name) {
		Path dotFile = writeDotFile(name);
		Path png = curPath.resolve(name+".png");
		String cmd = String.format("dot %s -T png -o %s", dotFile.toAbsolutePath(),png.toAbsolutePath());
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
