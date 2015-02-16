package project;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class NonogramFileReader extends AbstractNonogramProvider {

	public NonogramFileReader(String pathSrc) {

		BufferedReader br = null;
		try {
			String sCurrentLine;
			br = new BufferedReader(new FileReader(new File(pathSrc.replace(
					"\\", "/"))));
			while ((sCurrentLine = br.readLine()) != null) {
				if (sCurrentLine.startsWith("width")) {
					this.column = Integer.parseInt(sCurrentLine.split(" ")[1]);
				}
				if (sCurrentLine.startsWith("height")) {
					this.line = Integer.parseInt(sCurrentLine.split(" ")[1]);
				}
				if (sCurrentLine.startsWith("rows")) {
					int line = 0;
					// read all line
					while (!(sCurrentLine = br.readLine()).equals("columns")) {
						int cmpt = 0;
						lineData.add(new ArrayList<Integer>());
						if (!sCurrentLine.equals("")) {
							for (String element : sCurrentLine.split(",")) {
								int parseInt = Integer.parseInt(element);
								cmpt += parseInt;
								this.lineData.get(line).add(parseInt);
							}
						}
						this.sumLine.add(cmpt);
						line++;

					}
					// read all column
					int column = 0;
					while ((sCurrentLine = br.readLine()) != null) {
						int cmpt = 0;
						columnData.add(new ArrayList<Integer>());
						if (!sCurrentLine.equals("")) {
							for (String element : sCurrentLine.split(",")) {
								int parseInt = Integer.parseInt(element);
								cmpt += parseInt;
								this.columnData.get(column).add(parseInt);
							}
						}
						this.sumColumn.add(cmpt);
						column++;

					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		NonogramFileReader nf = new NonogramFileReader(
				"C:\\Users\\Anthony\\workspaces\\workspacePPC\\Nonogram-DataSet\\autostereogram.txt");
		nf.printNonoGramm();
	}

}
