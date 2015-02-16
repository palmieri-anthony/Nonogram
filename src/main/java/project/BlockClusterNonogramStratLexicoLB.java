package project;

import solver.search.strategy.IntStrategyFactory;

public class BlockClusterNonogramStratLexicoLB extends BlockClusterNonogramMinDom_LB {



	public BlockClusterNonogramStratLexicoLB(String pathNono, String pathCsv) {
		super(pathNono, pathCsv);
	}
	public BlockClusterNonogramStratLexicoLB(int line, int column) {
		super(line, column);
	}

	
	@Override
	public void configureSearch() {
	for(int i=0;i<rowCluster.length;i++){
		 solver.set(IntStrategyFactory.lexico_LB(rowCluster[i]));
//				 (this.rowCluster[i]));
	}
//	for(int i=0;i<columnCluster.length;i++){
//		 solver.set(IntStrategyFactory.lexico_LB(columnCluster[i]));
////				 (this.rowCluster[i]));
//	}
	}
	
	public static void main(String[] args) {
		System.out.println(args[0]);
		new BlockClusterNonogramStratLexicoLB(args[1],args[3]).execute(args);
	}
}
