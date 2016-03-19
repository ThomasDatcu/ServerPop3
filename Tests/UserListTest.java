/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Tests;

import ServerPop3.ServerPop3.User;
import ServerPop3.ServerPop3.UserList;

/**
 *
 * @author gaetan
 */
public class UserListTest {
    public static void main(String[] args){
        UserList u = new UserList();
        String res1 = u.chechUser("pute");
        System.out.println(res1);
        User res2 = u.connect("tata", "yoyo");
        if(res2 == null)
            System.out.println("failed to connect");
        else
            System.out.println(res2.getLengthOfMailDrop());
        
    }
}

