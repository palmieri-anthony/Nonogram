package project;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import solver.Solver;
import solver.constraints.Constraint;
import solver.constraints.ICF;
import solver.constraints.IntConstraintFactory;
import solver.constraints.LCF;
import solver.constraints.nary.automata.FA.FiniteAutomaton;
import solver.constraints.nary.automata.FA.IAutomaton;
import solver.search.strategy.IntStrategyFactory;
import solver.variables.BoolVar;
import solver.variables.IntVar;
import solver.variables.VariableFactory;
import util.tools.ArrayUtils;

public class MixApproach extends AbstractNonogrammProblem {

	public MixApproach(int line, int column) {
		super(line, column);
	}

	public MixApproach(String pathNono, String pathCsv) {
		super(pathNono, pathCsv);
	}

	/**
	 * representing the nonogramm form square[i][j]=1 <=> the j pixel of row i
	 * is painted . 0 otherwise.
	 */
	BoolVar[][] square;
	/**
	 * placement of row cluster. rowCluster[i][t]=k <=> the cluster t has the
	 * leftmost pixel on row i, column k. Remark, the second dimension will be
	 * adjust according to the number of block
	 */
	IntVar[][] rowCluster;
	/**
	 * placement of column cluster. columnCluster[i][t]=k <=> the cluster t has
	 * the leftmost pixel on column i, row k. Remark, the second dimension will
	 * be adjust according to the number of block
	 */
	IntVar[][] columnCluster;

	@Override
	protected void buildConstraint() {
		square = new BoolVar[line][column];
		rowCluster = new IntVar[line][1];
		columnCluster = new IntVar[this.column][1];
		square = VariableFactory
				.boolMatrix("c", this.line, this.column, solver);
		buildCluster(rowCluster, lineData, column, sumLine, "row");
		buildCluster(columnCluster, columnData, line, sumColumn, "column");
		// sens xij => cluster ik <=> j
		initInblockVarRow();
		initInblockVarColumn();
		
		for (int j = 0; j < column; j++) {
			dfa(ArrayUtils.getColumn(square, j),
					toIntegerarray(columnData.get(j)), solver);
		}
		for (int i = 0; i < line; i++) {
			dfa(square[i], toIntegerarray(lineData.get(i)), solver);
		}
		
		
		
		
		for (int j = 0; j < line; j++) {
			solver.post(ICF.sum(square[j],
					VariableFactory.fixed(this.sumLine.get(j), solver)));
		}
		for (int i = 0; i < column; i++) {
			BoolVar[] col = ArrayUtils.getColumn(square, i);
			solver.post(ICF.sum(col,
					VariableFactory.fixed(this.sumColumn.get(i), solver)));
		}
	}
	
	private void initInblockVarColumn() {
		// pour chaque ligne
		BoolVar[][] transpose = ArrayUtils.transpose(square);
		for (int i = 0; i < transpose.length; i++) {
			// pour chaque cluster
			for (int j = 0; j < transpose[i].length; j++) {
				// si il existe un cluster > 0
				if (columnData.get(i).size() > 0&&sumColumn.get(i)>0 && sumColumn.get(i) != line) {
					// si x[i][j]=1 alors .... sinon ...
					solver.post(LCF.ifThenElse(transpose[i][j], LCF
							.or(createOrConstraintXij1(i, j, transpose[i][j],
									columnCluster[i], this.columnData,
									this.sumColumn)), LCF
							.and(createOrConstraintXij0(i, j, transpose[i][j],
									columnCluster[i], this.columnData))));
				}
			}
		}
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

	
	private Integer[] toIntegerarray(Collection<Integer> collection) {
		Integer arrayInteger[] = new Integer[collection.size()];
		int i = 0;
		for (Integer integer : collection) {
			arrayInteger[i] = integer;
			i++;
		}
		return arrayInteger;

	}
	
	
	private void initInblockVarRow() {
		// pour chaque ligne
		for (int i = 0; i < square.length; i++) {
			// pour chaque element de la ligne
			for (int j = 0; j < square[i].length; j++) {
				// si il existe un cluster > 0
				if (sumLine.get(i) > 0 && rowCluster[i].length > 0
						&& sumLine.get(i) != column) {
					// si x[i][j]=1 alors .... sinon ...
					solver.post(LCF.ifThenElse(
							square[i][j],
							LCF.or(createOrConstraintXij1(i, j, square[i][j],
									rowCluster[i], this.lineData, this.sumLine)),
							LCF.and(createOrConstraintXij0(i, j, square[i][j],
									rowCluster[i], this.lineData))));
				}
			}
		}
	}


	private Constraint[] createOrConstraintXij1(int line, int column,
			BoolVar boolVar, IntVar[] cluster, List<List<Integer>> data,
			List<Integer> sum) {
		// need as much as number of row cluster to ensure that the variable is
		// in a block.
		ArrayList<Constraint> tabConstraint = new ArrayList<Constraint>();
		for (int k = 0; k < cluster.length; k++) {
			// si le cluster est concerné <=> j in [min (dom ( cluster) ),
			// max(dom (cluster))]
			if (cluster[k].getLB() <= column && cluster[k].getUB() +data.get(line).get(k)>= column) {
				Constraint supConstraint = ICF.arithm(cluster[k], "<=", column);
				Constraint lessConstraint = ICF.arithm(cluster[k], ">", column
						- data.get(line).get(k));
				tabConstraint.add(LCF.and(supConstraint, lessConstraint));
			}
		}
		return tabConstraint.toArray(new Constraint[tabConstraint.size()]);
	}

	private Constraint[] createOrConstraintXij0(int line, int column,
			BoolVar boolVar, IntVar[] cluster, List<List<Integer>> data) {
		// need as much as number of row cluster to ensure that the variable is
		// in a block.
		ArrayList<Constraint> tabConstraint = new ArrayList<Constraint>();
		for (int k = 0; k < cluster.length; k++) {
			if (cluster[k].getLB() <= column && cluster[k].getUB() +data.get(line).get(k)>= column) {
				Constraint supConstraint = ICF.arithm(cluster[k], ">", column);
				Constraint lessConstraint = ICF.arithm(cluster[k], "<=", column
						- data.get(line).get(k));
				tabConstraint.add(LCF.or(supConstraint, lessConstraint));
			}
		}
		return tabConstraint.toArray(new Constraint[tabConstraint.size()]);
	}

	private void buildCluster(IntVar[][] cluster, List<List<Integer>> data,
			int n, List<Integer> sum, String qualifier) {
		for (int i = 0; i < cluster.length; i++) {
			// creation du tableau contenant les clusters
			cluster[i] = new IntVar[data.get(i).size()];
			// pour chaque cluster de chaque ensemble(ligne colonne)
			int previousSum = 0;
//			if (sum.get(i) != 0) {
				
				for (int j = 0; j < data.get(i).size(); j++) {
					// create (left|top)most cluster variable
					cluster[i][j] = VariableFactory.integer(qualifier
							+ "cluster[" + i + "][" + j + "]", previousSum + j,
							n - (sum.get(i) - previousSum)
									- (data.get(i).size() - j)+1, solver);
					previousSum += data.get(i).get(j);
					// si il existe plusieurs cluster alors on introduit un
					// ordre

					if (j > 0/* && cluster[i][j-1].getUB()<cluster[i][j].getLB() */) {
						// get a blank
						// ensure position of next cluster is next the previous
						// +
						// size of previous + 1 for a blank.
						solver.post(ICF.arithm(cluster[i][j], "-",
								cluster[i][j - 1], ">",
								(data.get(i).get(j - 1))));
					}
				}
//			}
		}
	}

	@Override
	public void configureSearch() {
		for (int i = 0; i < rowCluster.length; i++) {
			solver.set(IntStrategyFactory.minDom_LB(this.rowCluster[i]));
		}
		for (int i = 0; i < columnCluster.length; i++) {
			solver.set(IntStrategyFactory.minDom_LB(this.columnCluster[i]));
		}
	}

	@Override
	public void solve() {
		level = level.SILENT;
		solver.findSolution();
		writeResult();

	}

	@Override
	public void prettyOut() {

	}

	public static void main(String[] args) {
		new MixApproach(args[1], args[3]).execute(args);
	}
}


