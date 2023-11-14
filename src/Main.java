/**
 * WORKED ON WITH:
 * MACY MCKENNEY
 * DEVINN SEGURA
 */

import java.io.*;
import java.util.*;

public class Main {
    public static void main(String[] args) throws IOException {
        BufferedReader stdin = new BufferedReader(new FileReader(new File("input4.txt")));
        ArrayList<String[]> initalTable = populateTable(stdin);
        System.out.println("INITIAL TABLE: ");
        printTable(initalTable);
        System.out.println("_________________________________");

        ArrayList<String[]> tablePairs = populateTablePairs(initalTable);
        System.out.println("TABLE PAIRS: ");
        printTable(tablePairs);
        System.out.println("_________________________________");

        ArrayList<String[]> iteratedTable = iterateList(tablePairs);
        System.out.println("MARKED OFF BAD TABLE PAIRS: ");
        printTable(iteratedTable);
        System.out.println("_________________________________");

        finishIterating(iteratedTable, initalTable);
        System.out.println("FINISHED ITERATING TABLE: ");
        printTable(iteratedTable);
        System.out.println("_________________________________");

        finishList(iteratedTable, initalTable);
    }

    private static void finishList(ArrayList<String[]> iteratedTable, ArrayList<String[]> initalTable) {
        Map<String, Set<String>> elementMap = new HashMap<>();
        Set<String> visited = new HashSet<>();

        for (String[] pair : iteratedTable) {
            Set<String> pairSet = new HashSet<>(Arrays.asList(pair)); // Convert the pair to a set

            for (String element : pair) {
                if (!elementMap.containsKey(element)) {
                    elementMap.put(element, new HashSet<>());
                }
                elementMap.get(element).addAll(pairSet); // Use pairSet instead of Arrays.asList(pair)
            }
        }

        ArrayList<String[]> resultList = new ArrayList<>();

        for (String element : elementMap.keySet()) {
            if (!visited.contains(element)) {
                Set<String> currentSet = elementMap.get(element);
                resultList.add(currentSet.toArray(new String[0]));
                visited.addAll(currentSet);
            }
        }

        // Sort each String[] in the ArrayList
        for (String[] array : resultList) {
            Arrays.sort(array);
        }
        System.out.println("EQUAL STATES: ");
        printTable(resultList);
        System.out.println("_________________________________");

        //Now change the inital table
        initalTable = fillInList(initalTable, resultList);
        System.out.println("EXPANDED LIST");
        printTable(initalTable);
        System.out.println("_________________________________");
        removeDuplicateRows(initalTable);
        System.out.println("FINISHED COLLAPSING");
        printTable(initalTable);
        System.out.println("_________________________________");

    }
    private static void removeDuplicateRows(ArrayList<String[]> list) {
        HashSet<List<String>> uniqueRows = new HashSet<>();
        Iterator<String[]> iterator = list.iterator();

        while (iterator.hasNext()) {
            String[] row = iterator.next();
            List<String> rowAsList = Arrays.asList(row);

            if (!uniqueRows.add(rowAsList)) {
                // Duplicate row found, remove it
                iterator.remove();
            }
        }
    }
    private static ArrayList<String[]> fillInList(ArrayList<String[]> matrix, ArrayList<String[]> equalElementsList) {
        ArrayList<String[]> updatedMatrix = new ArrayList<>();
        Map<String, String> mapping = new HashMap<>();

        // Create a mapping for each set of equal elements
        for (String[] equalElements : equalElementsList) {
            String firstElement = equalElements[0];
            for (String element : equalElements) {
                mapping.put(element, firstElement);
            }
        }

        // Update the matrix with the mapped values
        for (String[] row : matrix) {
            String[] updatedRow = new String[row.length];
            for (int i = 0; i < row.length; i++) {
                updatedRow[i] = mapping.getOrDefault(row[i], row[i]);
            }
            updatedMatrix.add(updatedRow);
        }

        return updatedMatrix;
    }

    private static void finishIterating(ArrayList<String[]> iteratedTable, ArrayList<String[]> initalTable) {
        //TODO: IF it doesnt delete from the arraylist make a temp return list
        int count = 1;
        while(count > 0){
            count = 0;
            Iterator<String[]> iterator = iteratedTable.iterator();
            while(iterator.hasNext()) {
                String[] arr = iterator.next();
                String aX = null, aY = null, bX = null, bY = null, x = null, y = null;

                y = arr[1];
                x = arr[0];

                //Find X & Y in data
                for (String[] tableRow : initalTable) {
                    if (tableRow[0].equals(x)) {
                        aX = tableRow[1];
                        bX = tableRow[2];
                    } else if (tableRow[0].equals(y)) {
                        aY = tableRow[1];
                        bY = tableRow[2];
                    }
                }
                System.out.println("{" + x + ", " + y + "}");
                System.out.println("a -> {" + aX + ", " + aY + "}");
                System.out.println("b -> {" + bX + ", " + bY + "}");

                boolean delete = false;
                boolean aViolates = true;
                boolean bViolates = true;
                int violationCount = 0;

                if(aX.length() != aY.length() || bX.length() != bY.length()){
                    delete = true;
                }

                for (String[] row : iteratedTable) { //Check that states match
                    //Add the S on the end of the 0
                    if(aX.equals("0")){
                        aX = "0S";
                    }else if(aY.equals("0")){
                        aY = "0S";
                    }else if(bX.equals("0")){
                        bX = "0S";
                    }else if(bY.equals("0")){
                        bY = "0S";
                    }

                    //CHECK the A
                    if(aX.equals(aY)){ //If it is equal break that iteration
                        aViolates = false;
                    } else if ((aX.equals(row[0]) || aX.equals(row[1])) && (aY.equals(row[0]) || aY.equals(row[1]))) {
                        aViolates = false;
                    }

                    //CHECK the B
                    if(bX.equals(bY)){ //If it is equal break that iteration
                        bViolates = false;
                    } else if ((bX.equals(row[0]) || bX.equals(row[1])) && (bY.equals(row[0]) || bY.equals(row[1]))) {
                        bViolates = false;
                    }

                    if(bViolates || aViolates){
                        violationCount++;
                    }

                }
                if(delete || violationCount >= iteratedTable.size()){
                    iterator.remove();
                    count++;
                }
            }
        }

    }

    private static ArrayList<String[]> iterateList(ArrayList<String[]> tablePairs) {
        ArrayList<String[]> returnList = tablePairs;
        Iterator<String[]> iterator = returnList.iterator();
        while(iterator.hasNext()){
            String[] arr = iterator.next();
            String startValue;
            //Check if it is a start State
            if(arr[0].length() >= 2 && arr[0].charAt(arr[0].length() - 1) == 'S'){
                startValue = arr[0].substring(0, arr[0].length()-1);
            }else{
                startValue = arr[0];
            }

            //Check if the two states have the same size
            if(startValue.length() !=  arr[1].length()){
                iterator.remove();
            }
        }
        return returnList;
    }

    private static ArrayList<String[]> populateTablePairs(ArrayList<String[]> initalTable) {
        ArrayList<String[]> returnList = new ArrayList<>();

        for(int i = 1; i < initalTable.size() -  1; i++){ //TODO: Delete equal if loop doenst work
            String x = initalTable.get(i)[0];
            for(int j = 1 + i; j <= (initalTable.size()) - 1; j++ ){
                String y = initalTable.get(j)[0];
                String[] strAdd = {x, y};
                returnList.add(strAdd);
            }
        }

        return returnList;
    }

    private static ArrayList<String[]> populateTable(BufferedReader stdin) throws IOException {
        ArrayList<String[]> returnArray = new ArrayList<>();
        String s;
        while ((s = stdin.readLine()) != null){
            String[] oneArr = s.split(" ");
            returnArray.add(oneArr);
        }
        return returnArray;
    }

    public static void printTable(ArrayList<String[]> table){
        for(String[] oneArr :  table){
            for (String x : oneArr){
                System.out.print(x + " ");
            }
            System.out.println();
        }
    }
}