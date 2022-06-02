/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lk.ijse.thogakade.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import lk.ijse.thogakade.db.DBConnection;
import lk.ijse.thogakade.model.Order;


/**
 *
 * @author niroth
 */
public class OrderController {
    
    public static String getLastOrderId() throws SQLException, ClassNotFoundException {
        Connection connection = DBConnection.getInstance().getConnection();
        Statement stm = connection.createStatement();
        ResultSet rst = stm.executeQuery("SELECT id FROM Orders ORDER BY id DESC LIMIT 1");
        return rst.next() ? rst.getString("id") : null;
    }

    public static boolean addOrder(Order order) throws ClassNotFoundException, SQLException {
        //1. insert into Order [D001, 2022-01-30, C001]
        Connection connection = DBConnection.getInstance().getConnection();
        PreparedStatement stm = connection.prepareStatement("Insert into Orders values(?,?,?)");
        stm.setObject(1, order.getId());
        stm.setObject(2, order.getDate());
        stm.setObject(3, order.getCustomerId());
        boolean orderIsAdded=stm.executeUpdate()>0;
        //2. insert into OrderDetails[]
        if(orderIsAdded){
            boolean orderDetailsAdded=OrderDetailController.addOrderDetails(order.getOrderDetailList());
            //3. Update item stock
            if(orderDetailsAdded){
                boolean isUpdate=ItemController.updateStock(order.getOrderDetailList());
                if(isUpdate){
                    return true;
                }
            }
        }
        return false;
    }
    
    
}
