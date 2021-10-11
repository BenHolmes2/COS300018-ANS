import java.awt.Font;
import javax.swing.*;
public class AgentCreateGUI {
    public static void main(String[] args) {
        JFrame frame=new JFrame();//creating instance of JFrame

        //creating title label
        JLabel titleLabel = new JLabel("User Creation");
        titleLabel.setFont(new Font("Default", Font.BOLD, 20));
        titleLabel.setBounds( 100, 0, 200, 80);

        //creation of the agent name input box and its label as well as setting their location within the frame.
        JTextField agentName = new JTextField();
        JLabel agentNameLabel = new JLabel("Agent Name:");
        agentName.setBounds( 90, 60, 100, 20);
        agentNameLabel.setBounds( 10, 60, 80, 20);

        //creation of inventory inputs and its labels as well as setting their location within the frame
        JLabel inventoryLabel = new JLabel("Initial Inventory");
        inventoryLabel.setFont(new Font("Default", Font.BOLD, 14));
        inventoryLabel.setBounds( 50, 80, 150, 40);
        //item 1
        JTextField item1 = new JTextField();
        JLabel item1Label = new JLabel("Item 1:");
        item1.setBounds( 90, 120, 100, 20);
        item1Label.setBounds( 10, 120, 80, 20);
        JTextField itemAmount1 = new JTextField();
        JLabel itemAmount1Label = new JLabel("Amount:");
        itemAmount1.setBounds( 250, 120, 100, 20);
        itemAmount1Label.setBounds( 200, 120, 80, 20);
        //item 2
        JTextField item2 = new JTextField();
        JLabel item2Label = new JLabel("Item 2:");
        item2.setBounds( 90, 140, 100, 20);
        item2Label.setBounds( 10, 140, 80, 20);
        JTextField itemAmount2 = new JTextField();
        JLabel itemAmount2Label = new JLabel("Amount:");
        itemAmount2.setBounds( 250, 140, 100, 20);
        itemAmount2Label.setBounds( 200, 140, 80, 20);
        /*
        will add more items as necessary
         */

        //creation of order inputs and their labels as well as setting location within the frame
        JLabel ordersLabel = new JLabel("Initial Orders");
        ordersLabel.setFont(new Font("Default", Font.BOLD, 14));
        ordersLabel.setBounds( 50, 160, 150, 40);
        //order 1
        JTextField orderItem1 = new JTextField();
        JLabel orderItem1Label = new JLabel("Order Item 1:");
        orderItem1.setBounds( 90, 200, 100, 20);
        orderItem1Label.setBounds( 10, 200, 80, 20);
        JTextField orderAmount1 = new JTextField();
        JLabel orderAmount1Label = new JLabel("Amount:");
        orderAmount1.setBounds( 250, 200, 100, 20);
        orderAmount1Label.setBounds( 200, 200, 80, 20);
        //order 2
        JTextField orderItem2 = new JTextField();
        JLabel orderItem2Label = new JLabel("Order Item 2:");
        orderItem2.setBounds( 90, 220, 100, 20);
        orderItem2Label.setBounds( 10, 220, 80, 20);
        JTextField orderAmount2 = new JTextField();
        JLabel orderAmount2Label = new JLabel("Amount:");
        orderAmount2.setBounds( 250, 220, 100, 20);
        orderAmount2Label.setBounds( 200, 220, 80, 20);
        /*
        will add more orders as necessary
         */


        // the create button creation and location setting
        JButton createButton=new JButton("Create");
        createButton.setBounds(300,300,80, 30);

        // The File input button
        JButton fileButton=new JButton("File Input");
        fileButton.setBounds(10,300,100, 30);

        //adding all parts to frame
        frame.add(titleLabel);
        frame.add(agentNameLabel);
        frame.add(agentName);
        frame.add(inventoryLabel);
        frame.add(item1);
        frame.add(item1Label);
        frame.add(itemAmount1);
        frame.add(itemAmount1Label);
        frame.add(item2);
        frame.add(item2Label);
        frame.add(itemAmount2);
        frame.add(itemAmount2Label);
        frame.add(ordersLabel);
        frame.add(orderItem1);
        frame.add(orderItem1Label);
        frame.add(orderAmount1);
        frame.add(orderAmount1Label);
        frame.add(orderItem2);
        frame.add(orderItem2Label);
        frame.add(orderAmount2);
        frame.add(orderAmount2Label);
        frame.add(createButton);
        frame.add(fileButton);

        //frame settings
        frame.setSize(400,500);//400 width and 500 height
        frame.setLayout(null);//using no layout managers
        frame.setVisible(true);//making the frame visible

        /*
        I need options to add name, inventory and orders.
         */
    }
}
