package project;

import java.util.List;

public interface NonogramProvider {

	public int[][] getNonogramm();
	public List<Integer> getSumLine();
	public List<Integer> getSumColumn();
	public List<List<Integer>> getLineData();
	public List<List<Integer>> getColumnData();
	void printNonoGramm();
}
