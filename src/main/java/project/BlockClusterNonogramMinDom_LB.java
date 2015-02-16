package project;

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

public class BlockClusterNonogramMinDom_LB extends AbstractNonogrammProblem {

	public BlockClusterNonogramMinDom_LB(int line, int column) {
		super(line, column);
	}
	public BlockClusterNonogramMinDom_LB(String pathNono, String pathCsv) {
		super(pathNono, pathCsv);
	}
	/**
	 * representing the nonogramm form square[i][j]=1 <=> the j pixel of row i
	 * is painted . 0 otherwise.
	 */
	BoolVar[][] square;
	/**
	 * placement of row cluster. rowCluster[i][t]=k <=> the cluster t has the
	 * leftmost pixel on row i, column k.
	 * Remark, the second dimension will be adjust according to the number of block
	 */
	IntVar [][] rowCluster ;
	/**
	 * placement of column cluster. columnCluster[i][t]=k <=> the cluster t has the
	 * leftmost pixel on column i, row k.
	 * Remark, the second dimension will be adjust according to the number of block
	 */
	IntVar [][]  columnCluster ;
	@Override
	protected void buildConstraint() {
		square= new BoolVar[line][column];
		 rowCluster = new IntVar[line][1];
		 columnCluster = new IntVar [this.column][1] ;
		square = VariableFactory
				.boolMatrix("c", this.line, this.column, solver);
		buildCluster(rowCluster,lineData,column,"row");
		buildCluster(columnCluster,columnData,line,"column");
		//sens xij => cluster ik <=> j 
		initInblockVarRow();
		initInblockVarColumn();
		
		for (int j = 0; j < line; j++) {
			solver.post(ICF.sum(square[j], VariableFactory.fixed(this.sumLine.get(j), solver)));	
		}
		for (int i = 0; i < column; i++) {
			BoolVar[] col = ArrayUtils.getColumn(square, i);
			solver.post(ICF.sum(col,  VariableFactory.fixed(this.sumColumn.get(i), solver)));
		}
	}

	private void initInblockVarRow() {
		//pour chaque ligne
		for (int i = 0; i < square.length; i++) {
			//pour chaque element de la ligne
			for (int j = 0; j < square[i].length; j++) {
				//si il existe un cluster > 0
				if(lineData.get(i).size()>0){
					//si x[i][j]=1 alors .... sinon ...
					solver.post(LCF.ifThenElse(square[i][j], LCF.or(createOrConstraintXij1(i,j,square[i][j],rowCluster,this.lineData)),
							LCF.and(createOrConstraintXij0(i,j,square[i][j],rowCluster,this.lineData))));
				}
			}
		}
	}

	private void initInblockVarColumn() {
		//pour chaque ligne
		BoolVar[][] transpose = ArrayUtils.transpose(square);
		for (int i = 0; i < transpose.length; i++) {
			//pour chaque cluster
			for (int j = 0; j < transpose[i].length; j++) {
				//si il existe un cluster > 0
				if(columnData.get(i).size()>0){
					//si x[i][j]=1 alors .... sinon ...
					solver.post(LCF.ifThenElse(transpose[i][j], LCF.or(createOrConstraintXij1(i,j,transpose[i][j],columnCluster,this.columnData)),
							LCF.and(createOrConstraintXij0(i,j,transpose[i][j],columnCluster,this.columnData))));
				}
			}
		}	
	}
	
	
	private Constraint[] createOrConstraintXij1(int line, int column, BoolVar boolVar, IntVar[][] cluster,
			List<List<Integer>> data) {
			//need as much as number of row cluster to ensure that the variable is in a block.
			Constraint[] tabConstraint = new Constraint[cluster[line].length];
			for (int k = 0; k < cluster[line].length; k++) {
				Constraint supConstraint = ICF.arithm(cluster[line][k],"<=", column);
				Constraint lessConstraint = ICF.arithm(cluster[line][k],">", column-data.get(line).get(k));
				tabConstraint[k]=supConstraint;
				tabConstraint[k]=LCF.and(supConstraint,lessConstraint);
			}
			return tabConstraint;
	}
	
	private Constraint[] createOrConstraintXij0(int line, int column, BoolVar boolVar, IntVar[][] cluster,
			List<List<Integer>> lineData) {
			//need as much as number of row cluster to ensure that the variable is in a block.
			Constraint[] tabConstraint = new Constraint[cluster[line].length];
			for (int k = 0; k < cluster[line].length; k++) {
				Constraint supConstraint = ICF.arithm(cluster[line][k],">", column);
				Constraint lessConstraint = ICF.arithm(cluster[line][k],"<=", column-lineData.get(line).get(k));
				tabConstraint[k]=LCF.or(supConstraint,lessConstraint);
			}
			return tabConstraint;
	}
	
	private void buildCluster(IntVar[][] cluster, List<List<Integer>> data, int n,String qualifier) {
		for(int i=0;i<cluster.length;i++){
			//creation du tableau contenant les clusters
			cluster[i]= new IntVar[data.get(i).size()]; 
			//pour chaque cluster de chaque ensemble(ligne colonne)
			for (int j = 0; j < data.get(i).size(); j++) {
				//create (left|top)most cluster variable
				cluster[i][j]= VariableFactory.integer(qualifier+"cluster["+i+"]["+j+"]", 0, n-data.get(i).get(j), solver);
				//si il existe plusieurs cluster alors on introduit un ordre
				if(j>0){
					//get a blank
					//ensure position of next cluster is next the previous + size of previous + 1 for a blank.
					solver.post(ICF.arithm(cluster[i][j], "-", cluster[i][j-1], ">", (data.get(i).get(j-1))));
				}
			}
		}
	}
	
	@Override
	public void configureSearch() {
		for(int i=0;i<rowCluster.length;i++){
			 solver.set(IntStrategyFactory.minDom_LB(this.rowCluster[i]));
		}
		for (int i = 0; i < columnCluster.length; i++) {
			 solver.set(IntStrategyFactory.minDom_LB(this.columnCluster[i]));
		}
	}

	@Override
	public void prettyOut() {
//		for (int i = 0; i < line; i++) {
//			for (int j = 0; j < column; j++) {
//				System.out.print(square[i][j].getValue() + ", ");
//
//			}
//			System.out.println();
//		}
//		
//		
//		for (int i = 0; i < square.length; i++) {
//			checkSequence(square[i],lineData.get(i));
//		}
//		BoolVar[][] transpose= ArrayUtils.transpose(square);
//		for (int j = 0; j< transpose.length; j++) {
//			checkSequence(transpose[j],columnData.get(j));
//		}
		
	}
//	private void checkSequence(BoolVar[] boolVars, List<Integer> list) {
//		int cmptContigous=0;
//		int cmptCluster=0;
//		for (BoolVar var : boolVars) {
//			cmptContigous+=var.getValue();
//			if(v+(n-k) -1ar.getValue()==0){
//				if (cmptContigous>0) {
//					assert list.get(cmptCluster)==cmptContigous;
//					cmptContigous=0;
//					cmptCluster++;
//				}
//			}
//		}
//		if (cmptContigous>0) {
//			assert list.get(cmptCluster)==cmptContigous;
//		}
//		
//	}

	@Override
	public void solve() {
		level=level.SILENT;
		solver.findSolution();
		writeResult();
		
	}

	public static void main(String[] args) {
		new BlockClusterNonogramMinDom_LB(args[1],args[3]).execute(args);
	}
	

}
