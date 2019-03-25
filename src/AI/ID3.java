package AI;

import java.util.HashMap;
import java.util.ArrayList;

public class ID3 {

    /** Each node of the tree contains either the attribute number (for non-leaf
     *  nodes) or class number (for leaf nodes) in <b>value</b>, and an array of
     *  tree nodes in <b>children</b> containing each of the children of the
     *  node (for non-leaf nodes).
     *  The attribute number corresponds to the column number in the training
     *  and test files. The children are ordered in the same order as the
     *  Strings in strings[][]. E.g., if value == 3, then the array of
     *  children correspond to the branches for attribute 3 (named data[0][3]):
     *      children[0] is the branch for attribute 3 == strings[3][0]
     *      children[1] is the branch for attribute 3 == strings[3][1]
     *      children[2] is the branch for attribute 3 == strings[3][2]
     *      etc.
     *  The class number (leaf nodes) also corresponds to the order of classes
     *  in strings[][]. For example, a leaf with value == 3 corresponds
     *  to the class label strings[attributes-1][3].
     **/
    class NodeTree {

        NodeTree[] children;
        int value;

        public NodeTree(NodeTree[] ch, int val) {
            value = val;
            children = ch;
        } // constructor

        public String toString() {
            return toString("");
        } // toString()

        String toString(String indent) {
            if (children != null) {
                String s = "";
                for (int i = 0; i < children.length; i++)
                    s += indent + data[0][value] + "=" +
                            strings[value][i] + "\n" +
                            children[i].toString(indent + '\t');
                return s;
            } else
                return indent + "Class: " + strings[attributes-1][value] + "\n";
        } // toString(String)

    } // inner class NodeTree

    private int attributes; 	// Number of attributes (including the class)
    private int examples;		// Number of training examples
    private NodeTree decisionTree;	// NodeTree learnt in training, used for classifying
    private String[][] data;	// Training data indexed by example, attribute
    private String[][] strings; // Unique strings for each attribute
    private int[] stringCount;  // Number of unique strings for each attribute

    public ID3() {
        attributes = 0;
        examples = 0;
        decisionTree = null;
        data = null;
        strings = null;
        stringCount = null;
    } // constructor

    public void printTree() {
        if (decisionTree == null)
            error("Attempted to print null NodeTree");
        else
            System.out.println(decisionTree);
    } // printTree()

    /** Print error message and exit. **/
    static void error(String msg) {
        System.err.println("Error: " + msg);
        System.exit(1);
    } // error()

    static final double LOG2 = Math.log(2.0);

    static double xlogx(double x) {
        return x == 0? 0: x * Math.log(x) / LOG2;
    } // xlogx()

    /** Execute the decision tree on the given examples in testData, and print
     *  the resulting class names, one to a line, for each example in testData.
     **/
    public void classify(String[][] testData) {
        if (decisionTree == null)
            error("Please run training phase before classification");
        // PUT  YOUR CODE HERE FOR CLASSIFICATION
        for(int row=1; row<testData.length; row++){
            int result = classify(testData[row], decisionTree);
            System.out.println(strings[attributes-1][result]);
        }
    } // classify()

    private int classify(String[] testData, NodeTree tree){
        //only row is passed to this method
        //if reached tree leaf node
        if(tree.children == null){
            return tree.value;
        }
        //get string of selected attribute for this row
        String string = testData[tree.value];
        //find string number, to follow right branch
        for(int branch=0; branch<stringCount[tree.value]; branch++){
            if(string.equals(strings[tree.value][branch])){
                return classify(testData, tree.children[branch]);
            }
        }
		/*
		this would happen if training data didn't have a string that was
		in the test data
		*/
        return 0;
    }

    public void train(String[][] trainingData) {
        indexStrings(trainingData);

        decisionTree = ID3(trainingData, new ArrayList<Integer>());
        System.out.println(decisionTree);
    }

    /**
     * ================================
     * # ID3 ALGORITHM IMPLEMENTATION #
     * ================================
     */
    private NodeTree ID3(String[][] data, ArrayList<Integer> questionsAsked){
        /**
         * The ID3 algorithm works as follows:
         *
         * in this implementation ... the member function ``indexStrings`` has taken care of
         * creating a variable called `strings` which stores the unique string values per attribute,
         * such that for example:
         *                  [["outlook", "temperature", "windy"],
         *                  =======================================
         *                  ["sunny", "28", "yes"],
         *                  ["sunny", "25", "no"],
         *                  ["windy", "28", "yes"],
         *                  [...]]
         * becomes:
         *                  [["sunny", "windy"], ["28", "25"], ["yes", "no"]] ---> `strings`
         *
         * In this implementation of ID3, numerical values are considered categorical - meaning that we
         * do not have do partition the feature space into a range of numerical values, hence we consider
         * numbers as categories. In the example above, "28" and "25" are separate attributes that do not have a scale.
         *
         * NODETREE CLASS:
         * The NodeTree class' main idea is that it requires an int value that corresponds to the index of the attribute
         * that was chosen. In the example above, this would be 0 for "outlook", 1 for "temperature" and 2 for "windy"
         *
         * This should be created as follows:
         *                  new NodeTree(null, int)
         * and appended to a list. This step is recursively
         * executed and lastly, at the end:
         *
         *                  new NodeTree(list, int);
         *
         * is returned.
         *
         *
         * ID3 PSEUDO-CODE:
         *
         *              ID3(String[][], Array<> questionsAsked){
         *                  if all the points have the same class:
         *                      return new NodeTree(null, class);
         *
         *                  else if no questions remain:
         *                      return new NodeTree(null, majorityClass);
         *
         *
         *                  question = findBestQuestion(String[][] trainingSet);
         *                  Sets = split(TrainingSet, question);
         *                  subset[]
         *                  for set in Sets:
         *                      questionAsked->question
         *                      subset[] -> ID3(set, questionAsked);
         *                  return new NodeTree(subset, val);
         *
         *              }
         *
         */
        if (sameClass(data)){
            return new NodeTree(null, getClassID(data));
            // TODO: attributes - 1 could be wrong
        } else if (questionsAsked.size() == (attributes - 1)){
            return new NodeTree(null, getClassID(data));
        }

        int question = findBestQuestion(data);
        ArrayList<String[][]> sets =  split(data, question);
        NodeTree[] subsets = new NodeTree[stringCount[question]];

        for (int i = 0; i < sets.size(); i++){
            questionsAsked = addQuestion(questionsAsked, question);
            subsets[i] = ID3(sets.get(i), questionsAsked);
        }

        return new NodeTree(subsets, question);
    }

    public ArrayList<Integer> addQuestion(ArrayList<Integer> questionsAsked, int question){
        int index = questionsAsked.size();
        questionsAsked.add(index, question);
        return questionsAsked;
    }

    public ArrayList<String[][]> split(String[][] data, int question){
        ArrayList<String[][]> out = new ArrayList<String[][]>();
        out.add(0, data);

        return out;
    }

    /**
     * Mimmicking Python's Counter dictionary.
     * @param dataArray string[]
     * @return {"string": frequency (int)}
     */
    HashMap<String, Integer> Counter(ArrayList<String> dataArray){
        HashMap<String, Integer> hashMap = new HashMap<>();

        for (String s: dataArray){
            if (!hashMap.containsKey(s)){
                hashMap.put(s, 1);
            } else {
                int currentValue = hashMap.get(s);
                hashMap.put(s, currentValue + 1);
            }
        }

        return hashMap;
    }

    double getEntropy(double probability){
        return - xlogx(probability);
    }


    double calculateEntropy(String[][] data){
        int lastIndex = data[0].length - 1;
        ArrayList<String> classLabels = new ArrayList<>();
        double entropy = 0.0;

        int index = 0;
        for (String[] s: data){
            classLabels.add(index, s[lastIndex]);
            index++;
        }

        HashMap<String, Integer> labelCount = Counter(classLabels);
        int labelTotal = labelCount.size();

        for (String key: labelCount.keySet()){
            double value = labelCount.get(key);
            double prob = (value / labelTotal);
            // slightly redundant function... but it's staying.
            entropy += getEntropy(prob);
        }
        return entropy;
    }

    ArrayList<Integer> identifyQuestion(String question){
        ArrayList<Integer> questionPos = new ArrayList<>();

        int i =0;
        int j = 0;

        for (String[] st: strings){
            for (String s: st){
                if (s.equals(question)){
                    questionPos.add(0, i);
                    questionPos.add(1, j);
                    return questionPos;
                }
                j++;
            }
            i++;
            j=0;
        }

        return questionPos;
    }


    // TODO: finish this
    String[][] makeSubSet(String[][] data, String question){

        String[][] newSet = new String[data[0].length][data.length];

        int index = 0;
        for (int i = 0; i < data[0].length; i++){
            for (String[] datum : data) {
                if (i == identifyQuestion(question).get(0)) {
                    if (datum[i].equals(question)) {
                        newSet[index] = datum;
                        index++;
                    }
                }
            }
        }

        return newSet;
    }


    int countQuestion(String[][] data, String question){
        int counter = 0;
        for (String[] S: data){
            for (String s: S){
                if (s.equals(question)){
                    counter += 1;
                }
            }
        }

        return counter;
    }

    int indexOfLowestPositiveValue(ArrayList<Double> a){
        int index = 0;
        double minValue = 9999999.0;

        for (Double i: a){
            if (i > 0){
                if (i < minValue){
                    minValue = i;
                }
            }
        }

        for (Double i: a){
            if (i == minValue){
                return index;
            }
            index++;
        }
        // hopefully never reached
        System.out.println("WARNING LOWEST POSITIVE INT DID NOT TRIGGER IF");
        return index;
    }

    /**
     *
     * @param data
     * @return
     */
    public int findBestQuestion(String[][] data){

        int lenSet = data.length;
        double originalEntropy = calculateEntropy(data);
        ArrayList<Double> entropies = new ArrayList<Double>();
        int index = 0;
        for (String[] string: strings){
            double sum_ = 0;
            for (String s: string){
                String[][] subSet = makeSubSet(data, s);
                int subSetFreq = countQuestion(subSet, s);
                double ent = calculateEntropy(subSet);
                sum_ += (subSetFreq / lenSet) * ent;
            }
            entropies.add(index, originalEntropy - sum_);
            index++;
        }
        return indexOfLowestPositiveValue(entropies);
    }

    /**
     * checks if the class is the same
     *
     * @param data string[][] contaning or data
     * @return boolean indicating whether it is the same class
     */
    public boolean sameClass(String[][] data){
        boolean isSameClass = true; // until otherwise
        int lastIndex = data[1].length - 1;
        String lastClass = data[1][lastIndex];

        for (String[] rows: data){
            if (!rows[lastIndex].equals(lastClass)){
                isSameClass = false;
                break;
            }
        }

        return isSameClass;
    }

    /**
     * returns the id of the class both in case of a majority class and in case of the same class.
     *
     * @param data string[][] containing the data
     * @return int indicating the id of the class.
     */
    public int getClassID(String[][] data){
        boolean isSameClass = sameClass(data);
        int lastIndex = data[0].length - 1;
        int classIndex = 0;
        HashMap<String, Integer> mp = new HashMap<>();
        int maxCount = 0;

        if (isSameClass){
            String className = data[1][lastIndex];

            for (int i=0; i<stringCount[lastIndex]; i++) {
                if (strings[lastIndex][i].equals(className)){
                    // System.out.println(i);
                    return i;
                }
            }
        } else {
            // is not the same class.....
            for (int i = 0; i<stringCount[lastIndex]; i++){
                if (mp.containsKey(strings[lastIndex][i])){
                    int currentValue = mp.get(strings[lastIndex][i]);
                    mp.put(strings[lastIndex][i], currentValue + 1);
                } else {
                    mp.put(strings[lastIndex][i], 1);
                }
            }

            for (String key: mp.keySet()){
                if (mp.get(key) > maxCount){
                    maxCount = mp.get(key);
                    classIndex = getClassID(key);
                }
            }
        }
        return classIndex;
    }

    private int getClassID(String className){
        int lastIndex = data[1].length - 1;
        for (int i=0; i<stringCount[lastIndex]; i++) {
            if (strings[lastIndex][i].equals(className)){
                // System.out.println(i);
                return i;
            }
        }
        return -1;
    }


    /** Given a 2-dimensional array containing the training data, numbers each
     *  unique value that each attribute has, and stores these Strings in
     *  instance variables; for example, for attribute 2, its first value
     *  would be stored in strings[2][0], its second value in strings[2][1],
     *  and so on; and the number of different values in stringCount[2].
     **/
    void indexStrings(String[][] inputData) {
        data = inputData;
        examples = data.length;
        attributes = data[0].length;
        stringCount = new int[attributes];
        strings = new String[attributes][examples];// might not need all columns
        int index = 0;
        for (int attr = 0; attr < attributes; attr++) {
            stringCount[attr] = 0;
            for (int ex = 1; ex < examples; ex++) {
                for (index = 0; index < stringCount[attr]; index++)
                    if (data[ex][attr].equals(strings[attr][index]))
                        break;	// we've seen this String before
                if (index == stringCount[attr])		// if new String found
                    strings[attr][stringCount[attr]++] = data[ex][attr];
            } // for each example
        } // for each attribute
    } // indexStrings()

    /** For debugging: prints the list of attribute values for each attribute
     *  and their index values.
     **/
    void printStrings() {
        for (int attr = 0; attr < attributes; attr++)
            for (int index = 0; index < stringCount[attr]; index++)
                System.out.println(data[0][attr] + " value " + index +
                        " = " + strings[attr][index]);
    } // printStrings()

} // class ID3