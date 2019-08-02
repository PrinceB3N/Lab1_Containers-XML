/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Midterm1Review;
import javax.swing.*;
import java.awt.event.*;
/**
 *
 * @author benja
 */
public class Review1 {
    public void clickLambda(){
        JFrame j = new JFrame();
        JPanel pane = new JPanel();
        JButton button = new JButton();
        j.add(pane.add(button));
        button.addActionListener((e)->jButton1Clicked(e));
        j.setVisible(true);
    }
    public static void main(String[] args){

        Review1 r=new Review1();
        r.clickLambda();
    }
    void jButton1Clicked(ActionEvent e){
        ((JButton)e.getSource()).setText("helo");
    }
}
