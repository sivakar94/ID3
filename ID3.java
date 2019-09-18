// STUDENT_ID= 180798364
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import java.util.Arrays;
import java.util.ArrayList;

class ID3 {

	/** Each node of the tree contains either the Attribute number (for non-leaf
	 *  nodes) or class number (for leaf nodes) in <b>value</b>, and an array of
	 *  tree nodes in <b>children</b> containing each of the children of the
	 *  node (for non-leaf nodes).
	 *  The Attribute number corresponds to the column number in the training
	 *  and test files. The children are ordered in the same order as the
	 *  Strings in strings[][]. E.g., if value == 3, then the array of
	 *  children correspond to the branches for Attribute 3 (named data[0][3]):
	 *      children[0] is the branch for Attribute 3 == strings[3][0]
	 *      children[1] is the branch for Attribute 3 == strings[3][1]
	 *      children[2] is the branch for Attribute 3 == strings[3][2]
	 *      etc.
	 *  The class number (leaf nodes) also corresponds to the order of classes
	 *  in strings[][]. For example, a leaf with value == 3 corresponds
	 *  to the class label strings[attributes-1][3].
	 **/
	class Tree {

		Tree[] children;
		int value;

		public Tree(Tree[] ch, int val) {
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

	} // inner class Tree

	private int attributes; 	// Number of attributes (including the class)
	private int examples;		// Number of training examples
	private Tree decisionTree;	// Tree learnt in training, used for classifying
	private String[][] data;	// Training data indexed by example, Attribute
	private String[][] strings; // Unique strings for each Attribute
	private int[] stringCount;  // Number of unique strings for each Attribute
	
	
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
			error("Attempted to print null Tree");
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
		
		int Number_of_attributes= (attributes-1);
		for(int row=1; row<testData.length; row++){
			int result = classify_tree(testData[row], decisionTree);
		}
	} 

	private int classify_tree(String[] testData, Tree tree){ // uses the tree avilabe from the training to classifiy test data
		if(tree.children == null){
			return tree.value;
		}
		//get string of selected Attribute for this row
		String string_Attr = testData[tree.value];		
		for(int branch=0; branch<stringCount[tree.value]; branch++){
			if(string_Attr.equals(strings[tree.value][branch])){
				return classify_tree(testData, tree.children[branch]); //do it recursively
			}
		}
		return 0; //would return 0 if training examples didn't have a string that was in the test set
	}



	public void train(String[][] trainingData) {       // training of the ID3
		indexStrings(trainingData);
		decisionTree = train(new ArrayList<Integer>(), new ArrayList<Integer>());	// calls the method below	
	} 
	
	private Tree train(ArrayList<Integer> col_attributes, ArrayList<Integer> raw_sample){

		int all_members = istheSameclass(raw_sample);
		int Number_of_attributes= (attributes-1);
		
		if(all_members != -1){ 	//if all members are same class
			return new Tree(null, all_members);} // single node tree with label all_members
		else
			if(Number_of_attributes <=col_attributes.size()) { //if there are no more attributes!
				return new Tree(null, MostCommon(raw_sample));}
				
		int Best_Attribute = getBestAttribute(raw_sample,col_attributes); //Best_Attribute IS THE Attribute that maximizes information Gain
						
		ArrayList<Integer> BRANCH = new ArrayList<Integer>(col_attributes);  // COME BACK
		BRANCH.add(Best_Attribute); 
		
		Tree[] SUB_SET = new Tree[stringCount[Best_Attribute]]; // CREATE A SUBSET number of unique strings for each Attribute!!!
		
		for(int i=0; i<SUB_SET.length; i++)
		{ 
			ArrayList<Integer> NOT_SEEN_TRAIN_EXAMPLES = Getting_not_seen_data(Best_Attribute, i, raw_sample); // get an arrAY with not seen training_examples	
			SUB_SET[i] = train(BRANCH, NOT_SEEN_TRAIN_EXAMPLES); // train recursively attributes with non-seen training examples
			if(SUB_SET[i] == null)
			{
				SUB_SET[i] = new Tree(null, MostCommon(raw_sample)); // add leaf with the most common value of target Attribute in the training example 
			}
		}
		return new Tree(SUB_SET, Best_Attribute);
	}


	private boolean hasbeenseen_before(int number, ArrayList<Integer> Values){ // it will return TRUE OR FALSE if the number is equal to values
		for(Integer value : Values)
		{    
			if(number == value) 
				return true;
		}
		return false;
	}
	
	
	private ArrayList<Integer> Getting_not_seen_data(int Attribute, int J, ArrayList<Integer> raw_sample){ // GEtting_not_seen_data (raw_examples)
		
		String check_string = strings[Attribute][J]; // Training examaple INDEXED BY EXAMPLE , ATTRIBUTE
		if(check_string == null) 
			return raw_sample; // if there is no training samples it will return the same raw
		ArrayList<Integer> NotSeen = new ArrayList<Integer>(raw_sample);
		for(int row=1; row<examples; row++)
		{
			if(!check_string.equals(data[row][Attribute]) && !hasbeenseen_before(row, raw_sample))
			{     
			    NotSeen.add(row);     // IF THE CHECK_STRING IS NOT EQUAL it is added to NotSeen!!!                                           
			}
		}
		return NotSeen;
	}


	private int istheSameclass(ArrayList<Integer> raw_sample){ //RETURN -1 if the members of the ALL the training_examples  are of the same class)
		
		int all_members = -1;
		int Number_of_attributes= (attributes-1);	
		for(int row=1; row<examples; row++)
		{
			if(hasbeenseen_before(row, raw_sample))
				continue; //find the not seen ignored row	
			String all_membersString = data[row][Number_of_attributes]; //set class string as class of this row
			for(int index=0; index<stringCount[Number_of_attributes]; index++)
			{//
				if(all_membersString.equals(strings[Number_of_attributes][index]))
				{
					all_members = index;
					break;
				}
			}
		}
		String all_membersString = strings[Number_of_attributes][all_members]; 	//all_membersString be reused for comparison
		for(int row=1; row<examples; row++){ //check  if the not-seen trainging_exapmple  are the same
			if(hasbeenseen_before(row, raw_sample)) 
				continue;
			if(!all_membersString.equals(data[row][Number_of_attributes])){
				return -1;//if class string of this row is different
			}
		}
	
		return all_members;
	}


	private int MostCommon(ArrayList<Integer> raw_sample){  // returns if the most common attribute in the training example
		
		int most_common_attr = 0;	
		int Number_of_attributes= (attributes-1);
		int[] all_membersCount = new int[stringCount[Number_of_attributes]];//how many string of training examaple of each class is not seen data!!
		
		for(int row=1; row<examples; row++)
		{	
			if(hasbeenseen_before(row, raw_sample)) 
				continue;
			String data_value_cell = data[row][Number_of_attributes];			
			for(int all_members=0; all_members<all_membersCount.length; all_members++)
			{
				if(data_value_cell.equals(strings[Number_of_attributes][all_members]))
				{//find class index
					all_membersCount[all_members]++;
				}
			}
		}
		for(int all_members=1; all_members<all_membersCount.length; all_members++)
		{
			if(all_membersCount[all_members] > all_membersCount[most_common_attr])
			{				
				most_common_attr = all_members;
			}
		}
		return most_common_attr;
	}



	private int getBestAttribute( ArrayList<Integer> raw_sample, ArrayList<Integer> col_attributes){
		
		double Best_Gain = -1;
		int Best_Attribute = -1;
		double s = ENTR_TEMP(raw_sample);
		int[][][] class_count = Class_Count(raw_sample,col_attributes);
		int Total_number_training_examples = (examples - 1);
		
		for(int Attribute=0; Attribute < class_count.length; Attribute++)// goes through every single attribute of the class_count
		{ 
			if(hasbeenseen_before(Attribute, col_attributes)) 
				continue; // check if every Attribute has been seen in column attributes list!!!!
			double information_gain = s;	
			for(int[] string : class_count[Attribute])
			{
				int Total_class_sum = 0; //total number of those strings in data
				for(int classSum : string)
				{
					Total_class_sum += classSum;
				}
				double entropy_H_s = 0.0;
				for(int classSum : string)
				{
					double param = (double)classSum/(double)Total_class_sum;
					entropy_H_s -= xlogx(param); // calculating Entropyyy!!! 
				}
				
				double ratio = ((double)Total_class_sum/(double)Total_number_training_examples);
				information_gain -= ratio * entropy_H_s;
			}
			if(Best_Gain <= information_gain) //if information_gain for this Attribute is better than previous - set as new best Attribute
			{                                 
				Best_Gain = information_gain;
				Best_Attribute = Attribute;
			}
		}
		return Best_Attribute;
	}



	private int[][][] Class_Count( ArrayList<Integer> raw_sample, ArrayList<Integer> col_attributes){

		int Number_of_attributes = attributes-1; 
		
		int classes = stringCount[Number_of_attributes];
		int[][][] class_count = new int[Number_of_attributes][][];
		for(int Attribute=0; Attribute<Number_of_attributes; Attribute++)
		{		
			int AttributeStrings = stringCount[Attribute];
			class_count[Attribute] = new int[AttributeStrings][classes];//check each row, without title row (1st row)
			for(int row=1; row<examples; row++)
			{		
				if(hasbeenseen_before(row, raw_sample)) 
					continue;//check if the row if the row has already been seen!
				int ID_STRING = 0;
				String value_cell = data[row][Attribute];
				String class_row = data[row][Number_of_attributes];
				
				for(String val : strings[Attribute])
				{
					if(value_cell.equals(val))
					{ //  
						int ID_CLASS = 0;
						for(String all_members : strings[Number_of_attributes])
						{
							if(class_row.equals(all_members))
							{
								class_count[Attribute][ID_STRING][ID_CLASS]++;// it will increment the count of the three paramters
							}
							ID_CLASS++;
						}
					}
					ID_STRING++;
				}
			}
		}
		return class_count;
	}

	private double ENTR_TEMP(ArrayList<Integer> raw_sample){ // GET ENTR_TEMP AT THE PARENT NODE
	
		int Number_of_attributes= (attributes-1);
		int[] Unique_String_Count = new int[stringCount[Number_of_attributes]];  //stringCount : number if uniques strings for each Attribute!!!!! 
		double S = 0.0; //for Entropy Calculation
		
		for(int row=1; row<examples; row++)
		{

			if(hasbeenseen_before(row, raw_sample)) 
				continue; 
			String row_String = data[row][Number_of_attributes];
			for(int row2=0; row2 <Unique_String_Count.length; row2++)
			{  					
				if(row_String.equals(strings[Number_of_attributes][row2]))
				{
					Unique_String_Count[row2]++;
				}
			}
		}
		int total_number_Rows = examples -1; // total number of training samples		
		for(Integer sum : Unique_String_Count)
		{			
			float VARIABLE = ((float)sum/total_number_Rows);			
			S -= xlogx(VARIABLE);
		}
		return S;
	}
	





	/** Given a 2-dimensional array containing the training data, numbers each
	 *  unique value that each Attribute has, and stores these Strings in
	 *  instance variables; for example, for Attribute 2, its first value
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
		for (int Attribute = 0; Attribute < attributes; Attribute++) {
			stringCount[Attribute] = 0;
			for (int ex = 1; ex < examples; ex++) {
				for (index = 0; index < stringCount[Attribute]; index++)
					if (data[ex][Attribute].equals(strings[Attribute][index]))
						break;	// we've seen this String before
				if (index == stringCount[Attribute])		// if new String found
					strings[Attribute][stringCount[Attribute]++] = data[ex][Attribute];
			} // for each example
		} // for each Attribute
	} // indexStrings()

	/** For debugging: prints the list of Attribute values for each Attribute
	 *  and their index values.
	 **/
	void printStrings() {
		for (int Attribute = 0; Attribute < attributes; Attribute++)
			for (int index = 0; index < stringCount[Attribute]; index++)
				System.out.println(data[0][Attribute] + " value " + index +
									" = " + strings[Attribute][index]);
	} // printStrings()            n
	/** Reads a text file containing a fixed number of comma-separated values
	 *  on each line, and returns a two dimensional array of these values,
	 *  indexed by line number and position in line.
	 **/
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
				else
					error("Scan error in " + fileName + " at " + l + ":" + f);
		sc.close();
		return data;
	} // parseCSV()

	public static void main(String[] args) throws FileNotFoundException,
												  IOException {
		if (args.length != 2)
			error("Expected 2 arguments: file names of training and test data");
		String[][] trainingData = parseCSV(args[0]);
		String[][] testData = parseCSV(args[1]);
		ID3 classifier = new ID3();
		classifier.train(trainingData);
		classifier.printTree();
		classifier.classify(testData);
	} // main()

} // class ID3