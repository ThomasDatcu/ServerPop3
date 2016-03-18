/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Tests;

import ServerPop3.ServerPop3.User;

/**
 *
 * @author gaetan
 */
public class UserTest {
    public static void main(String[] args){
        User u = new User(1,"tata", "yolo");
        u.connect("yolo");
        int n = u.getNumberOfMessageInMaildrop();
        System.out.println("lenght of maildrop =" + u.getLengthOfMailDrop());
        System.out.println("number of messages = " + n);
        for(int i = 0; i<n; i++)
            System.out.println("Message " + i + " : \n" +  u.getMessage(i).getText());
        u.setMarkDeleted(0);
        u.disconnect();
        
    }
}
