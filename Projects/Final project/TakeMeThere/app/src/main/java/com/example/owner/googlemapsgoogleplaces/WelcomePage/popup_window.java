package com.example.owner.googlemapsgoogleplaces.WelcomePage;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.owner.googlemapsgoogleplaces.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EmptyStackException;
import java.util.Random;
import java.util.Stack;

public class popup_window extends AppCompatActivity {

    ArrayList<String> facts = new ArrayList<>(
            Arrays.asList("Dijkstra's algorithm is a graph algorithm.",
                    "Dijkstra is used to solve single-source shortest path problem for non-negative edge costs.",
                    "Dijkstra takes a source vertex and finds the path with lowest cost to reach a destination vertex.",
                    "Dijkstra's time complexity varies with data structure used.",
                    "Dijkstra's implementation based on min-priority queue has running time O(|E|+|V|log|V|).")
    );
    Stack<String> stackPre = new Stack<>();
    TextView info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup_window);
        info = (TextView) findViewById(R.id.information);
        overridePendingTransition(R.anim.go_in, R.anim.go_out);

        fixMetrics();
        show(facts);

    }
    public void show(final ArrayList<String> facts){
        Random rand = new Random();
        int value = rand.nextInt(facts.size());

        stackPre.push(facts.get(value));
        info.setText(facts.get(value));

        //When button next is pressed
        Button next = findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               show(facts);
            }
        });

        //When button previous is pressed
        Button previous = findViewById(R.id.previous);
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fact="" ;
                try{
                    fact = stackPre.pop();
                    info.setText(fact);
                }catch (EmptyStackException e){
                    Random rand = new Random();
                    int value = rand.nextInt(facts.size());
                    info.setText(facts.get(value));
                    stackPre.push(facts.get(value));
                }
            }
        });
    }


    public void fixMetrics(){
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*0.98),(int)(height*0.3));
    }
}
