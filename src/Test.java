import AI.ID3;

import java.io.*;
import java.util.Scanner;


public class Test {
    static String[][] parseCSV(String fileName)
            throws FileNotFoundException, IOException {
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        String s = br.readLine();
        int fields = 1;
        int index = 0;
        while ((index = s.indexOf(',', index) + 1) > 0)
            fields++;
        int lines = 1;
        while (br.readLine() != null)
            lines++;
        br.close();
        String[][] data = new String[lines][fields];
        Scanner sc = new Scanner(new File(fileName));
        sc.useDelimiter("[,\n]");
        for (int l = 0; l < lines; l++)
            for (int f = 0; f < fields; f++)
                if (sc.hasNext())
                    data[l][f] = sc.next();
        sc.close();
        return data;
    } // parseCSV()

    public static void main(String[] args) throws FileNotFoundException,
            IOException {

        String[][] trainingData = parseCSV(args[0]);
        String[][] testData = parseCSV(args[1]);
        ID3 classifier = new ID3();
        classifier.train(trainingData);
        classifier.getClassID(trainingData);
        // System.out.println(classifier.sameClass(trainingData));
        // classifier.train(trainingData);
    } // main()
}
