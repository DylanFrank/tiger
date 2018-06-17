package cfg;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;
import java.util.Stack;


/*默认 grammer  N-> str, 用 HashMap存放 non-Terminal map to str
*1.默认大写变量是 non-terminal
*2.非大写变量是terminal
*3.允许epsilon,用@表示epsilon
*4.必须是LR(1)型文法
*5. 语法形式  N -> a | bc| ....以 |分割
*/

public class CFG {
	private static final char epsilon = '@';
	private static final char endSymbol = '$';
	private Map<Character, List<String>> grammer = new HashMap<>();
	private Set<Character> nullable = new HashSet<>();// nullable集合，表示能产生空串的字符
	private Map<Character, Set<Character>> follow = new HashMap<>();// follow 集合，follow(c)表示跟随在 non-Terminal Char
																	//后面的 Terminal字符
	private Map<Character, Set<Character>> first = new HashMap<>();// first集合
	private Map<Character,Map<Character, String>> predictTable = new HashMap<>();// 预测分析表
	private Character start;
	public CFG(Character start) {
		super();
		this.start = start;
	}
	
	public void addGram(final Character nonTer,final String str) {
		if(!grammer.containsKey(nonTer))grammer.put(nonTer, new ArrayList<>());
		List<String> tmp = grammer.get(nonTer);
		tmp.add(str);
	}
	public void addGram(final Character nonTer, final List<String> productions) {
		if(!grammer.containsKey(nonTer))grammer.put(nonTer, new ArrayList<>());
		List<String> tmp = grammer.get(nonTer);
		tmp.addAll(productions);
	}
	
	private void initPredictTable()throws UnsupportedOperationException {
		/*
		 * 构建预测分析表，龙书（第二版） Algorithm 4.31
		 */
		for(Character c: grammer.keySet())predictTable.put(c, new HashMap<>());
		for(Entry<Character, List<String>> entry : grammer.entrySet()){
			Character key = entry.getKey();
			Map<Character, String> predict = predictTable.get(key);
			for(String production : entry.getValue()){
				Set<Character> firsts = getFirstSetOfProduction(key, production);
				for(Character a: firsts){
					if(predict.containsKey(a))throw new UnsupportedOperationException("not LL(1)grammer");
					predict.put(a, production);
				}
			}
		}
	}
	public void init() {
		computeNullable();
		computeFirst();
		computeFollow();
		initPredictTable();
	}
	public List<Map.Entry<Character, String>> predictAnalysis(String clause) throws UnsupportedOperationException {
		/*
		 * 输入一个子句 {@code clause}, 输出一个最左推导，或者报错
		 * 龙书(第二版)algorithm 4.34
		 */
		if(!clause.endsWith(new String(new char[]{endSymbol})))clause = clause+endSymbol;
		List<Map.Entry<Character, String>> ret = new ArrayList<>();
		Deque<Character> symStack = new LinkedList<>(Arrays.asList(start,endSymbol));//反人类的deque
		int idx = 0;
		Character top = symStack.peek();
		Character now = clause.charAt(idx);
		while (top != endSymbol) {//栈非空
			if(isTerminal(top)){
				if(top == now){
					now = clause.charAt(++idx);
					symStack.pop();
				}
				else {
					throw new UnsupportedOperationException(String.format("expect %c, but get a %c", top,now));
				}
			}else{
				String production = predictTable.get(top).get(now);
				if(production==null)throw new UnsupportedOperationException(String.format("expect %c, but get a %c", top,now));
				else{
					ret.add(new AbstractMap.SimpleEntry<Character, String>(top, production));
					symStack.pop();
					if(!isEpsilong(production)){
						for(int i=production.length() -1; i >=0 ; --i )
							symStack.push(production.charAt(i));
					}
				}
			}
			top = symStack.peek();
		}
		return ret;
	}
	
	private void computeNullable() {
		boolean isUpdate = true;// nullable 集合是否更新
		while (isUpdate) {
			isUpdate = false;
			for(Map.Entry<Character, List<String>> entry : grammer.entrySet()){
				if(nullable.contains(entry.getKey()))continue;// 已经进入nullable 集合
				for(String production: entry.getValue()){
					int i=0,n = production.length();
					for( ; i< n ; ++i){
						char now = production.charAt(i);
						if(isTerminal(now) || !nullable.contains(now))break;
					}
					if(i==n || isEpsilong(production)){//nullable
						nullable.add(entry.getKey());
						isUpdate = true;
						break;
					}
				}
			}
		}
	}
	private void computeFirst() {
		for(Character key : grammer.keySet())
			first.put(key, new HashSet<>());
		boolean isUpdate = true;
		while (isUpdate) {
			isUpdate = false;
			for(Map.Entry<Character, List<String>> entry : grammer.entrySet()){
				Character key = entry.getKey();
				for(String production : entry.getValue()){
					if(isEpsilong(production))continue;
					for(int i=0,n = production.length() ; i < n ; ++i){
						char now = production.charAt(i);
						if(isTerminal(now)){//终结符下一条产生式
							if(first.get(key).add(now))isUpdate = true;
							break;
						}else {
							if(first.get(key).addAll(first.get(now)))isUpdate = true;
							if(!nullable.contains(now))break;//非nullable， 下一条产生式
						}
					}
				}
			}
		}
	}
	
	private void computeFollow() {
		/*
		 * 计算Follow集合，加入终止符号"$",
		 */
		for(Character key: grammer.keySet())
			follow.put(key, new HashSet<>());
		follow.get(start).add(endSymbol);
		boolean isUpdate = true;
		while (isUpdate) {
			isUpdate = false;
			for(Map.Entry<Character, List<String>> entry : grammer.entrySet()){
				Character key = entry.getKey();
				for(String production : entry.getValue()){
					if(isEpsilong(production))continue;
					Set<Character> tmp = new HashSet<>(follow.get(key));
					for(int i = production.length()-1 ; i >=0 ; --i){
						Character now = production.charAt(i);
						if(isTerminal(now))tmp = new HashSet<>(now);
						else {
							if(follow.get(now).addAll(tmp))isUpdate =true;//follow(now) U= tmp;
							if(!nullable.contains(now))tmp.clear();//now is not a nullable
							tmp.addAll(first.get(now));
						}
					}
				}
			}
		}
	}
	
	private Set<Character> getFirstSetOfProduction(Character nonTerminal,String production){
		/*
		 * get first_s(alpha) : A -> alpha, alpha is a production
		 * if first(alpha) -> epsilon ,than, first_s U= follow(alpha)
		 */
		Set<Character> ret = new HashSet<>();
		if(isEpsilong(production)){
			ret.addAll(follow.get(nonTerminal));
			return ret;
		}
		int i=0,n = production.length() ;
		for(; i < n ; ++i){
			char now = production.charAt(i);
			if(isTerminal(now)){
				ret.add(now);
				break;
			}else{
				ret.addAll(first.get(now));
				if(!nullable.contains(now))break;
			}
		}
		if(i==n)ret.addAll(follow.get(nonTerminal));// alpha -> epsilon
		return ret;
	}
	private static boolean isTerminal(char c) {
		return !Character.isUpperCase(c) && c!=epsilon;
	}
	private static boolean isEpsilong(String s) {
		return s.length() ==1 && s.charAt(0)==epsilon;
	}
	
	public static CFG construcCFG(Path grammerFile) {
		/*
		 * construc a CFG from a given grammer file, default first Character is Start{@code start} Symbol
		 */
		try(Scanner in = new Scanner(Files.newBufferedReader(grammerFile));){
			String grammer = in.nextLine().replaceAll("[\\s,->]","");
			CFG cfg = new CFG(grammer.charAt(0));
			cfg.addGram(grammer.charAt(0), Arrays.asList(grammer.substring(1).split("\\|")));
			while (in.hasNextLine()) {
				String production = in.nextLine().replaceAll("[\\s,->]","");
				cfg.addGram(production.charAt(0), Arrays.asList(production.substring(1).split("\\|")));
			}
			for(Entry<Character, List<String>> entry : cfg.grammer.entrySet())
				System.out.println(entry.getKey() +" -> "+ entry.getValue().toString());
			cfg.init();
			return cfg;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	public static void main(String[] args) {
		CFG cfg = CFG.construcCFG(Paths.get("./input", "grammer.txt"));
		List<Map.Entry<Character, String>> productions = cfg.predictAnalysis("i+i*i$");
		for (Entry<Character, String> entry : productions) {
			System.out.println(entry.getKey() + " -> " +entry.getValue());
		}
//		String string = "A -> +TA|@";
//		String s2 = string.replaceAll("[\\s,->]", "");
//		System.out.println(s2);
//		String[] s3 = s2.substring(1).split("\\|");
//		for(int i=0 ; i< s3.length ; ++i)
//			System.out.println(s3[i]);
	}
}
