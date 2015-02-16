package project;

import java.util.ArrayList;
import java.util.List;

public abstract class   AbstractNonogramProvider implements NonogramProvider {

	protected List<List<Integer>> lineData = new ArrayList<List<Integer>>();
	protected List<List<Integer>> columnData = new ArrayList<List<Integer>>();
	protected List<Integer> sumLine = new ArrayList<Integer>();
	protected List<Integer> sumColumn = new ArrayList<Integer>();
	protected int nonogramm[][];
	protected int line;
	protected int column;
	
	
	public void printNonoGramm() {


		for (int j = 0; j < this.sumColumn.size(); j++) {
			System.out.print(sumColumn.get(j) + ", ");
		}

		System.out.println();
		System.out.println("description of block in line");
		for (List<Integer> listblock : lineData) {
			for (Integer block : listblock) {
				System.out.print(block + ", ");
			}
			System.out.print("\n");
		}
		System.out.println("description of block in column");
		for (List<Integer> listblock : columnData) {
			for (Integer block : listblock) {
				System.out.print(block + ", ");
			}
			System.out.print("\n");
		}
	}

	public List<List<Integer>> getColumnData() {
		return columnData;
	}
	public int[][] getNonogramm() {
		return nonogramm;
	}

	public List<Integer> getSumLine() {
		return sumLine;
	}

	public List<Integer> getSumColumn() {
		return sumColumn;
	}

	public List<List<Integer>> getLineData() {
		return lineData;
	}


}