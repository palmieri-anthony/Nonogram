package project;

import solver.search.strategy.IntStrategyFactory;

public class BlockClusterNonogramStratMaxRegret extends BlockClusterNonogramMinDom_LB {
	public BlockClusterNonogramStratMaxRegret(String pathNono, String pathCsv) {
		super(pathNono, pathCsv);
	}

	public BlockClusterNonogramStratMaxRegret(int line, int column) {
		super(line, column);
	}

	@Override
	public void configureSearch() {
		for (int i = 0; i < columnCluster.length; i++) {
			solver.set(IntStrategyFactory.maxReg_LB(columnCluster[i]));
		}
		for (int i = 0; i < rowCluster.length; i++) {
			solver.set(IntStrategyFactory.maxReg_LB(rowCluster[i]));
			// (this.rowCluster[i]));
		}
	}

	public static void main(String[] args) {
		new BlockClusterNonogramStratMaxRegret(args[1], args[3]).execute(args);
	}
}
