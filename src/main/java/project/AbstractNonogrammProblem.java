package project;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import org.kohsuke.args4j.Option;

import samples.AbstractProblem;
import solver.Solver;

public abstract class AbstractNonogrammProblem extends AbstractProblem {
	int line ;
	int column ;
	@Option(name = "-p", usage = "Nonogram.", required = true)
	String path;
	
	@Option(name = "-s", usage = "Nonogram.", required = true)
	String targetPath;
	
	public AbstractNonogrammProblem(int line, int column) {
		this.line=line;
		this.column=column;
		nb  = new NonogrammBuilder(line,column);
	}
	public AbstractNonogrammProblem(String path,String target) {
		nb= new NonogramFileReader(path);
		this.targetPath=target;
	}
	
	protected List<List<Integer>> lineData ;
	protected List<List<Integer>> columnData ;
	protected List<Integer> sumLine ;
	protected List<Integer> sumColumn ;
	protected NonogramProvider nb;
	@Override
	public void createSolver() {
		solver = new Solver("Nonogramm Problem");
	}

	@Override
	public void buildModel() {
		this.line=nb.getLineData().size();
		this.column=nb.getColumnData().size();
		lineData= nb.getLineData();
		columnData=nb.getColumnData();
		this.sumLine= nb.getSumLine();
		this.sumColumn=nb.getSumColumn();
		buildConstraint();
//		nb.printNonoGramm();
	}
	
	public void writeResult(){
		try {
			PrintWriter out = new PrintWriter(new FileWriter(new File(targetPath), true));
			out.write(solver.getMeasures().toCSV()+";constraint:"+solver.getNbCstrs()+" variable: "+solver.getNbVars()+"\n");
			out.flush();
			 out.close();
			} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	protected abstract void buildConstraint() ;

	


	@Override
	public void solve() {
		 solver.findAllSolutions();
//		 prettyOut();

	}

}
