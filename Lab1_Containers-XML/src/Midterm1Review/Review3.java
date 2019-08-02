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
public class Review3 {

    public static void main(String[] args){
        ArrayList<IceCream> shop=new ArrayList<IceCream>(0);
        shop.add(new IceCream("yum_yum",11.00f));
        shop.add(new IceCream("asdbasdhaskd",6.00f));
        shop.add(new IceCream("strawberry",7.00f));
        shop.add(new IceCream("vanilla",3.00f));
        shop.add(new IceCream("yumberry",11.00f));        
        Collections.sort(shop,new IceCreamCompare());
        shop.sort(new IceCreamCompare());
        for(IceCream x:shop){
            System.out.println("Name: "+x.name+" Price: "+x.price);
        }
        
    }
}
    class IceCream{
        String name;
        float price;
        IceCream(String name, float price){
            this.name=name;
            this.price=price;
        }
    }
    class IceCreamCompare implements Comparator{
        public int compare(Object ice_1, Object ice_2){
            System.out.println(((IceCream)ice_1).name.compareTo(((IceCream)ice_2).name));
            return ((IceCream)ice_1).name.compareTo(((IceCream)ice_2).name);  
        }
        
    }
