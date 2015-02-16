package project;

import solver.search.strategy.IntStrategyFactory;

public class BlockClusterNonogramStratmaxDom_Split extends BlockClusterNonogramMinDom_LB {

	public BlockClusterNonogramStratmaxDom_Split(String pathNono, String pathCsv) {
		super(pathNono, pathCsv);
	}

	public BlockClusterNonogramStratmaxDom_Split(int line, int column) {
		super(line, column);
	}

	@Override
	public void configureSearch() {
		for (int i = 0; i < rowCluster.length; i++) {
			solver.set(IntStrategyFactory.maxDom_Split(rowCluster[i]));
		}
		for (int i = 0; i < columnCluster.length; i++) {
			solver.set(IntStrategyFactory.maxDom_Split(columnCluster[i]));
			// (this.rowCluster[i]));
		}
	}

	public static void main(String[] args) {
		new BlockClusterNonogramStratmaxDom_Split(args[1], args[3]).execute(args);
	}
}
