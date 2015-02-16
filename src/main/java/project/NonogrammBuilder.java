package project;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NonogrammBuilder extends AbstractNonogramProvider implements NonogramProvider{
	private double density = Math.random() / 2;
	

	public NonogrammBuilder(int line,int column) {
		this.line = line;
		this.column = column;
		nonogramm = new int[line][column];
		Random rand = new Random();

		for (int i = 0; i < line; i++) {
			lineData.add(new ArrayList<Integer>());
			
			for (int j = 0; j < column; j++) {
				if(i==0){
					columnData.add(new ArrayList<Integer>());	
				}
				
				if (rand.nextDouble() <= this.density) {
					nonogramm[i][j] = 1;
				} else {
					nonogramm[i][j] = 0;
				}
			}
		}
		{
			int comptLine = 0;
			int sum = 0;
			for (int i = 0; i < line; i++) {
				sum = 0;
				comptLine = 0;
				for (int j = 0; j < column; j++) {
					if (nonogramm[i][j] == 1) {
						comptLine++;
						sum++;
						if (j == column - 1) {
							this.lineData.get(i).add(new Integer(comptLine));
						}
					} else if (comptLine != 0) {
						this.lineData.get(i).add(new Integer(comptLine));
						comptLine = 0;
					}
				}
				this.sumLine.add(sum);
			}
		}
		{
			int sum = 0;
			int comptColumn = 0;
			for (int j = 0; j < column; j++) {
				sum = 0;
				comptColumn = 0;
				for (int i = 0; i < line; i++) {
					if (nonogramm[i][j] == 1) {
						comptColumn++;
						sum++;
						if (i == line - 1) {
							this.columnData.get(j).add(new Integer(comptColumn));
						}
					} else if (comptColumn != 0) {
						this.columnData.get(j).add(comptColumn);
						comptColumn = 0;
					}
				}
				this.sumColumn.add(sum);
			}
		}
	}
	@Override
	public void printNonoGramm() {
		for (int j = 0; j < line; j++) {
		for (int i = 0; i < column; i++) {
			System.out.print(nonogramm[j][i] + ", ");
		}
		System.out.println("=" + this.sumLine.get(j));
	}
		super.printNonoGramm();
	}

		public static void main(String[] args) {
		NonogrammBuilder nno = new NonogrammBuilder(6,8);
		nno.printNonoGramm();

	}
}
