package project;

import org.kohsuke.args4j.Option;

import samples.AbstractProblem;
import solver.Solver;
import solver.constraints.ICF;
import solver.constraints.IntConstraintFactory;
import solver.constraints.nary.automata.FA.FiniteAutomaton;
import solver.constraints.nary.automata.FA.IAutomaton;
import solver.explanations.ExplanationEngine;
import solver.explanations.ExplanationFactory;
import solver.search.strategy.IntStrategyFactory;
import solver.variables.BoolVar;
import solver.variables.VariableFactory;
import util.tools.ArrayUtils;

import javax.swing.*;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.ImageObserver;
import java.awt.image.PixelGrabber;
import java.io.IOException;
import java.util.Collection;
import java.util.logging.Logger;

/**
 * CSPLib prob012:<br/>
 * "Nonograms are a popular puzzles, which goes by different names in different
 * countries. Solvers have to shade in squares in a grid so that blocks of
 * consecutive shaded squares satisfy constraints given for each row and column.
 * Constraints typically indicate the sequence of shaded blocks (e.g. 3,1,2
 * means that there is a block of 3, then a gap of unspecified size, a block of
 * length 1, another gap, and then a block of length 2)." <br/>
 * 
 * @author Charles Prud'homme
 * @since 08/08/11
 */
public class NonogramAutomata extends AbstractNonogrammProblem {


	BoolVar[][] vars;
	
	public NonogramAutomata(String pathNono, String pathCsv) {
		super(pathNono, pathCsv);
	}
	public NonogramAutomata(int line, int column) {
		super(line, column);
	}

	@Override
	public void createSolver() {
		solver = new Solver("Nonogram");
	}

	@Override
	protected void buildConstraint() {

		vars = new BoolVar[line][column];
		for (int i = 0; i < line; i++) {
			for (int j = 0; j < column; j++) {
				vars[i][j] = VariableFactory.bool(
						String.format("B_%d_%d", i, j), solver);
			}
		}
		for (int i = 0; i < line; i++) {
			dfa(vars[i], toIntegerarray(lineData.get(i)), solver);
		}
		for (int j = 0; j < column; j++) {
			dfa(ArrayUtils.getColumn(vars, j),
					toIntegerarray(columnData.get(j)), solver);
		}

		for (int j = 0; j < line; j++) {
			solver.post(ICF.sum(vars[j], VariableFactory.fixed(this.sumLine.get(j), solver)));	
		}
		for (int i = 0; i < column; i++) {
			BoolVar[] col = ArrayUtils.getColumn(vars, i);
			solver.post(ICF.sum(col,  VariableFactory.fixed(this.sumColumn.get(i), solver)));
		}
		

	}

	private Integer[] toIntegerarray(Collection<Integer> collection) {
		Integer arrayInteger[] = new Integer[collection.size()];
		int i = 0;
		for (Integer integer : collection) {
			arrayInteger[i] = integer;
			i++;
		}
		return arrayInteger;

	}

	private void dfa(BoolVar[] cells, Integer[] rest, Solver solver) {
		StringBuilder regexp = new StringBuilder("0*");
		int m = rest.length;
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < rest[i]; j++) {
				regexp.append('1');
			}
			regexp.append('0');
				regexp.append(i == m - 1 ? '*' : '+');
			
		}
		IAutomaton auto = new FiniteAutomaton(regexp.toString());
		solver.post(IntConstraintFactory.regular(cells, auto));
	}

	@Override
	public void configureSearch() {
		solver.set(IntStrategyFactory.minDom_LB(ArrayUtils.flatten(vars)));
	}

	@Override
	public void solve() {
		level=level.SILENT;
		solver.findAllSolutions();
		writeResult();
		System.out.println(solver.getNbCstrs());
	}

	@Override
	public void prettyOut() {
		System.out.println(String.format("Nonogram -- "));
		StringBuilder st = new StringBuilder();
		for (int i = 0; i < vars.length; i++) {
			st.append("\t");
			for (int j = 0; j < vars[i].length; j++) {
				st.append(vars[i][j].getValue() == 1 ? '1' : '0');
			}
			st.append("\n");
		}
		System.out.println(st.toString());

	}

	public static void main(String[] args) {
		System.out.println(args[0]);
		new NonogramAutomata(args[1],args[3]).execute(args);
	}
}
