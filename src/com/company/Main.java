package com.company;

import java.io.*;
import java.util.*;

public class Main {

    public static void main(String[] args) {
        ArrayList<String> userInput = setSrcAndDest();
        String src = userInput.get(0) == null ? "" : userInput.get(0);
        String dest = userInput.get(1) == null ? "" : userInput.get(1);

        if(src.isEmpty() || dest.isEmpty()) {
            System.err.println("You did provide the correct station name(s).");
            System.err.println("Exiting the program now.");
            return;
        }

        HashMap<String, ArrayList<String>> hashMap = parseFileIntoMap();
        //System.out.println(hashMap);
        ArrayList<ArrayList<String>> breadthFirstSearchGraph = extendLink(src, hashMap);
        boolean cont = true;
        while(cont) {
            ArrayList<ArrayList<String>> breadthFirstSearchGraphTmp = new ArrayList<ArrayList<String>>();
            int routesWithDeadEnd=0;
            for(ArrayList<String> aRoute : breadthFirstSearchGraph) {
                String lastCity = aRoute.get(aRoute.size()-1);
                ArrayList<ArrayList<String>> moreBranches = extendLink(lastCity, hashMap);
                for(ArrayList<String> aBranch : moreBranches) {
                    ArrayList<String> extendedRoute = new ArrayList<String>();
                    extendedRoute.addAll(aRoute);
                    extendedRoute.addAll(aBranch);
                    String lastCityInTheCurrentRoute = extendedRoute.get(extendedRoute.size()-1);
                    if(lastCityInTheCurrentRoute.equals(dest) || lastCityInTheCurrentRoute.equals("null")) {
                        routesWithDeadEnd++;
                    }
                    breadthFirstSearchGraphTmp.add(extendedRoute);
                }
            }
            cleanUp(breadthFirstSearchGraph);
            cleanUp(breadthFirstSearchGraphTmp);
            if(breadthFirstSearchGraph.equals(breadthFirstSearchGraphTmp)) { // we are done finding all routes and are just looping now
                cont = false;
            } else {
                breadthFirstSearchGraph = breadthFirstSearchGraphTmp;
                if(routesWithDeadEnd == breadthFirstSearchGraph.size()) {
                    cont = false;
                }
            }
        }
        //System.out.println(breadthFirstSearchGraph);
        ArrayList<ArrayList<String>> targetRoutes = routesToFrom(dest, src, breadthFirstSearchGraph);
        printRoutes(targetRoutes);
    }

    private static ArrayList<String> setSrcAndDest() {
        String src=null;
        String dest=null;
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        try {
            System.out.print("enter start station>");
            src = br.readLine();
            System.out.print("enter end station>");
            dest = br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ArrayList<String> result = new ArrayList<String>();
        result.add(src);
        result.add(dest);
        return result;
    }

    private static HashMap<String, ArrayList<String>> parseFileIntoMap() {
        File file = new File("/home/nabass/Desktop/test/stations.csv");
        BufferedReader reader;
        HashMap<String, ArrayList<String>> hashMap = new HashMap<String, ArrayList<String>>();
        try {
            reader = new BufferedReader(new FileReader(file));
            String text;
            while((text = reader.readLine()) !=  null) {
                String[] cities = text.split(",");
                String cityA = cities[0];
                String cityB = cities[1];
                ArrayList<String> destination = hashMap.get(cityA);
                if(destination == null) {
                    destination = new ArrayList<String>();
                }
                destination.add(cityB);
                hashMap.put(cityA, destination);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return hashMap;
    }

    private static ArrayList<ArrayList<String>> extendLink(String src, HashMap<String, ArrayList<String>> map) {
        ArrayList<ArrayList<String>> allRoutes = new ArrayList<ArrayList<String>>();
        ArrayList<String> routes = map.get(src);
        if(routes == null) {
            ArrayList<String> aRoute = new ArrayList<String>();
            aRoute.add(src);
            aRoute.add("null");
            allRoutes.add(aRoute);
        } else {
            for (String city : routes) {
                ArrayList<String> aRoute = new ArrayList<String>();
                aRoute.add(src);
                aRoute.add(city);
                allRoutes.add(aRoute);
            }
        }
        return allRoutes;
    }

    private static void cleanUp(ArrayList<ArrayList<String>> arrayLists) {
        for(ArrayList<String> arrayList : arrayLists) {
            Set<String> linkedHashSet = new LinkedHashSet<String>();
            linkedHashSet.addAll(arrayList);
            arrayList.clear();
            arrayList.addAll(linkedHashSet);
        }
    }

    private static ArrayList<ArrayList<String>> routesToFrom(String to, String from, ArrayList<ArrayList<String>> allRoutes) {
        ArrayList<ArrayList<String>> routes = new ArrayList<ArrayList<String>>();
        for(ArrayList<String> aRoute : allRoutes) {
            if(aRoute.get(0).equals(from) && aRoute.get(aRoute.size()-1).equals(to)) {
                routes.add(aRoute);
            }
        }
        return routes;
    }

    private static void printRoutes(ArrayList<ArrayList<String>> routes) {
        System.out.println("Available routes are:");
        if(routes.isEmpty()) {
            System.out.println("None found.");
            return;
        }
        for(ArrayList<String> route : routes) {
            String printableRoute = "";
            for(String city : route) {
                printableRoute = printableRoute.concat(city).concat("->");
            }
            // remove the -> at the end
            printableRoute = printableRoute.substring(0, printableRoute.length()-2);
            System.out.println(printableRoute);
        }
    }
}
