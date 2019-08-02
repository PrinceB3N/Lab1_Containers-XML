/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Midterm1Review;
import java.util.*;
/**
 *
 * @author benja
 */
public class Review2 {
    public static ArrayList<Integer> getIndices(BitSet bSet){
        ArrayList<Integer> test=new ArrayList<Integer>(0);
        for(int i=0;i<bSet.size();i++){
            if(bSet.get(i)==true)
                test.add(i);
        }
        return test;
    }
    public static void main(String[] args){
        BitSet bSet=new BitSet(9);
        bSet.set(8);
        System.out.println(bSet.toString());
        //Either print out BitSet.toString() to get all indices of ON
        //OR reinvent the wheel and loop through the bitSet and check BitSet.get(i) is 1 and store that index somewhere.
        System.out.println(getIndices(bSet));
    }
}
