package com.example.owner.googlemapsgoogleplaces.DemoMap.MapManipulation;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import com.example.owner.googlemapsgoogleplaces.DemoMap.DatabaseManipulation.SQLHelper;
import com.example.owner.googlemapsgoogleplaces.DemoMap.MapActivity;

public class Dijkstra {

    SQLHelper dbHelper;
    Cursor cursor;

    public String[][] graph = new String[100][100];
    public String shortest_path1 = "";
    public String status = "none";

    MapActivity mapActivity ;

    public void shortestPath(String[][] arg_graph, int SourceVertex, int DestinationVertex){

        System.out.println("sa : " + SourceVertex + " & st : " + DestinationVertex);
        if(SourceVertex == DestinationVertex){
            status = "die";
            return;
            //System.exit(0);
        }


        graph = arg_graph;
        int source_vertex = SourceVertex;//ex:0
        int advanced_vertex = SourceVertex;
        int destination_vertex = DestinationVertex;

        if(advanced_vertex != destination_vertex){
            //connect all the nodes
            int vertex_count = 0;
            for(String[] array : graph){
                if(array[0] != null){
                    vertex_count += 1;
                }
            }
            //System.out.println("Number of Nodes : "+vertex_count);
            //System.out.println("==============================================");
            //System.out.println();

            //Add the vertices along the path
            List<Integer> ProgressingVertex = new ArrayList<Integer>();
            List<Integer> workingVertex= new ArrayList<Integer>();

            double initialVertexWeight = 0;
            double global_initialVertexWeight = 0;

            //i HANDLE
            for(int i = 0; i < 1; i++){


                //minimum weight of specific vertex
                List<Double> minVertexWeight = new ArrayList<Double>();

                //ADD the vertices the are included in the whole path
                if(!ProgressingVertex.contains(advanced_vertex)){
                    ProgressingVertex.add(advanced_vertex);
                }

                //fetching the minimum weight from the paths of the vertex added into ProgressingVertex
                for(int iVertex = 0; iVertex < ProgressingVertex.size(); iVertex++){
                    //COUNT NUMBER OF paths starting from the specified vertex
                    int fix_path_count = 0;
                    for(int path_num = 0; path_num < 100; path_num++){
                        if(graph[ProgressingVertex.get(iVertex)][path_num] != null){
                            fix_path_count += 1;
                        }
                    }


                    List<Double> weight = new ArrayList<Double>();
                    int line_status = 0;

                    //getting the minimum weight path of the specified vertex
                    for(int iPathNum = 0; iPathNum < fix_path_count; iPathNum++){
                        String weight_and_destinationVertex = graph[ProgressingVertex.get(iVertex)][iPathNum];//must be in order [0][0],[0][1] etc.//ex: graph[10][0]=4->432.23
                        //print line contents[0][0],[0][1],[0][2] etc.
                        //System.out.println("weight_and_destinationVertex : "+weight_and_destinationVertex);

                        String[] explode;
                        explode = weight_and_destinationVertex.split("->");
                        //System.out.println("weight_ : "+explode[1]);

                        //search for not worked vertex(without the '->true' extension)
                        if(explode.length == 2){
                            line_status += 1; //no added extension

                            //check if the vertex is included in the ProgressingVertex then do not re-add the initial weight
                            //else add the initial weight
                            if(!workingVertex.isEmpty()){
                                if(workingVertex.contains(ProgressingVertex.get(iVertex))){
                                    initialVertexWeight = 0;
                                }else{
                                    initialVertexWeight =global_initialVertexWeight;
                                }
                            }
                            //add the weight fetched from graph +initialVertexWeight into  weight
                            weight.add((Double.parseDouble(explode[1])+initialVertexWeight));

                            //modify the new weight in the graph
                            graph[ProgressingVertex.get(iVertex)][iPathNum] =
                                    String.valueOf(explode[0]+"->"+(Double.parseDouble(explode[1])+initialVertexWeight));    //graph[10][0]=5->432.23
                        }
                    }

                    //if no extension is added to the graph index
                    if(line_status > 0){

                        //GET MINIMUM weight
                        for(int index_weight = 0; index_weight < weight.size(); index_weight++){
                            if(weight.get(index_weight) <= weight.get(0)){
                                weight.set(0, weight.get(index_weight));
                            }
                        }

                        //add minimum weight into minVertexWeight
                        minVertexWeight.add(weight.get(0));
                    }//end if if ->true or ->blocked not all done
                    else{
                        //System.out.println("Vertex already exists");
                    }

                    //add the vertex advanced_vertex into workingVertex
                    if(!workingVertex.contains(ProgressingVertex.get(iVertex))){
                        workingVertex.add(ProgressingVertex.get(iVertex));
                    }
                }//end for iVertex

                //get the minimum wight btw vertices
                for(int iMinWeights = 0; iMinWeights < minVertexWeight.size(); iMinWeights++){
                    if(minVertexWeight.get(iMinWeights) <= minVertexWeight.get(0)){
                        minVertexWeight.set(0, minVertexWeight.get(iMinWeights));
                    }
                }

                //get the new cumulative weight and the next vertex(advanced_vertex)
                int startOrigIndex = 0;
                int line_status1 = 0;
                int MinWeightPathIndex = 0;
                int old_node = 0;
                //searching for the next vertex from the graph[][]
                for(Integer origIndex_weight : ProgressingVertex){
                    for(int iPathNum = 0; iPathNum < 100; iPathNum++){
                        if(graph[ProgressingVertex.get(startOrigIndex)][iPathNum] != null){
                            String weight_and_destinationVertex1 = graph[ProgressingVertex.get(startOrigIndex)][iPathNum];//ex: graph [10][0] 4->35.24
                            //System.out.println(weight_and_destinationVertex1);
                            String[] explode1;
                            explode1 = weight_and_destinationVertex1.split("->");
                            if(explode1.length == 2){
                                // System.out.println(+explode1[1]);
                                //fetching the node with the minimum cumulative weight
                                if(minVertexWeight.get(0) == Double.parseDouble(explode1[1])){
                                    MinWeightPathIndex = iPathNum;
                                    old_node = ProgressingVertex.get(startOrigIndex);//10
                                    advanced_vertex = Integer.parseInt(explode1[0]);//4
                                    line_status1 += 1;
                                }
                            }//end if check ->true or ->blocked

                        }//end if check line! = null

                    }//end for line limit = 100
                    startOrigIndex++; //next Vertex
                }//end search

                //adding the extensions
                if(line_status1 > 0){
                    //add the extension for the reserved path
                    Log.i("array1",graph[old_node][MinWeightPathIndex]);
                    graph[old_node][MinWeightPathIndex] +="->true";//4->244.34->true
                    Log.i("array2",graph[old_node][MinWeightPathIndex]);
                    //Block other paths to the advanced vertex
                    for(int iPath = 0; iPath < vertex_count; iPath++){
                        for(int min_line = 0; min_line < 100; min_line++){
                            //search for the value in graph that contains the new node as its destination and add "->blocked"
                            if(graph[iPath][min_line] != null){
                                String segmentWillBeDeleted = graph[iPath][min_line];
                                String[] explode3 = segmentWillBeDeleted.split("->");
                                if(explode3.length == 2){
                                    if(explode3[0].equals(String.valueOf(advanced_vertex))){
                                        graph[iPath][min_line] +="->blocked";
                                    }
                                }//will if if check ->true or ->blocked
                            }//end if check line! = null
                        }//end for line
                    }//end for iPath
                }

                //renew the initial weight
                global_initialVertexWeight = minVertexWeight.get(0);


                if(advanced_vertex != destination_vertex){
                    --i;
                }
                else{
                    break; //end i
                }
            }//end for handle i


            //combine all src and dst vertices that contain "->true" in their graph ex 1-11,4-1,4-9,10-4
            List<String> joinVertexOption = new ArrayList<>();
            for(int h = 0; h < vertex_count; h++){
                for(int n = 0; n < 100; n++){
                    if(graph[h][n] != null){
                        String str_graph = graph[h][n];
                        String[] explode6=str_graph.split("->");
                        if(explode6.length==3)
                            if(explode6[2].equals("true")){
                                Log.i("array5",graph[h][n]);
                                String[] explode4 = graph[h][n].split("->");
                                String NodeJoin = h+"-"+explode4[0];

                                joinVertexOption.add(NodeJoin);
                            }
                    }//end if check the contents of graph! = null
                }//end for looping line

            }//end h loop

            String f=new String();
            for(int k=0;k<joinVertexOption.size();k++)
                f+=joinVertexOption.get(k)+",";
            Log.i("array1",f+",");

            List<Integer> VertexPath = new ArrayList<Integer>();
            //first enter the destination vertex
            VertexPath.add(destination_vertex);

            int NODE_explode = destination_vertex;

            //get the path ex: 11,1,4,10 and store them in VertexPath
            for(int v = 0; v < 1; v++){
                Log.i("srcyydst","1");
                for(int w = 0; w < joinVertexOption.size(); w++){
                    String explode_vertex = joinVertexOption.get(w);
                    String[] explode5 = explode_vertex.split("-");
                    if(NODE_explode == Integer.parseInt(explode5[1])){
                        VertexPath.add(Integer.parseInt(explode5[0]));
                        NODE_explode = Integer.parseInt(explode5[0]);
                    }
                    if(NODE_explode == source_vertex){
                        break;
                    }
                }

                if(source_vertex != NODE_explode){
                    --v;
                }else{
                    break;
                }
            }//end v loop


            //reverse the path in VertexPath and store them in shortest_path
            //ex:10->4->1->11
            Collections.reverse(VertexPath);
            String shortest_path = "";
            for(int x = 0; x < VertexPath.size(); x++){
                if(x == VertexPath.size()-1){
                    shortest_path += VertexPath.get(x);
                }else{
                    shortest_path += VertexPath.get(x)+"->";
                }
            }

            //System.out.println("... "+shortest_path);
            shortest_path1 = shortest_path;
        }//end if start != finish

    }
}