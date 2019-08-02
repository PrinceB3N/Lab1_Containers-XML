/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Lab2;
import java.util.*;
/**
 *
 * @author benja
 */

public class Product{
    public class BarCode{
        private BitSet barcode;
        private static final int BARCODE_LENGTH=45;
        private BitSet fromString(String binary) {
            BitSet bitset = new BitSet(binary.length());
                for (int i = 0; i < binary.length(); i++) {
                    if (binary.charAt(i) == '1') {
                        bitset.set(i);
                    }
                }
                return bitset;
        }
        private String toBinaryString(BitSet bSet){
                StringBuilder sBuild = new StringBuilder();

                for( int i = 0; i < bSet.length();  i++ )
                {
                    sBuild.append( bSet.get( i ) == true ? 1: 0 );
                }
                for(int x=sBuild.length();x<BARCODE_LENGTH;x++){
                    sBuild.append(0);
                }

                return sBuild.toString();
        }
        BarCode(String binary){
            barcode = fromString(binary);
        }
        public String getBarCode(){
            return toBinaryString(barcode);
        }
}
    BarCode barcode;
    float price;
    String name;
    
    Product(String binary,float price,String name){
        this.barcode=new BarCode(binary);
        this.price=price;
        this.name=name;
    }
    public Product shallowClone(){
        Product foo = this;
        return foo;
    }
    public Product deepClone(){
        Product foo = new Product(this.barcode.getBarCode(),this.price,this.name);
        return foo;
    }
    public void printInfo(){
        System.out.println("Barcode:"+barcode.getBarCode()+", Price:"+price+", Name:"+name);
    }
    public void setBarCode(String binary){
        this.barcode=new BarCode(binary);
    }
public static void main(String[] args){
    String test1 = "1010101010101010101010101010101010101010101010101010101010101010101010";
    String test2 = "0000000000000000000000000000000000000000000000000000000000000000000000";
    
    Product original = new Product(test1, 11.00f, "APRIC");
    
    Product shallowCopy = original.shallowClone();
    shallowCopy.setBarCode(test2);
    
    Product deepCopy = original.deepClone();
    deepCopy.setBarCode(test2); deepCopy.price=3.33f; deepCopy.name="WALNU";
    
    original.printInfo(); shallowCopy.printInfo(); deepCopy.printInfo();
    
    //
}
}
