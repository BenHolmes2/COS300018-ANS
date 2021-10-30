import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.commons.future.IFuture;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class AgentCreateGUI extends JFrame{

    public AgentCreateGUI(final IExternalAccess agent, final MarketUserAgent mkAgent) {
        JFrame frame= new JFrame();//creating instance of JFrame

        //creating title label
        JLabel titleLabel = new JLabel("User: " + agent.getComponentIdentifier().getName());
        titleLabel.setFont(new Font("Default", Font.BOLD, 20));
        titleLabel.setBounds( 100, 0, 500, 80);

        //creation of the agent name input box and its label as well as setting their location within the frame.
        JTextField agentName = new JTextField();
        JLabel agentNameLabel = new JLabel("Agent Name:");
        agentName.setBounds( 90, 60, 100, 20);
        agentNameLabel.setBounds( 10, 60, 80, 20);

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
        frame.add(ordersLabel);
        frame.add(orderItem1);
        frame.add(orderItem1Label);
        frame.add(orderAmount1);
        frame.add(orderAmount1Label);
        frame.add(fileButton);
        frame.add(createButton);


        //frame settings
        frame.setSize(400,500);//400 width and 500 height
        frame.setLayout(null);//using no layout managers
        frame.setVisible(true);//making the frame visible

        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //This is the code to grab the text from each field
                String agentNametext = agentName.getText();
                String orderItem1text = orderItem1.getText();

            }
        });

        fileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FileIOGUI fileGUI = new FileIOGUI();
                frame.dispose();
            }
        });

        /* --------------- TEST METHODS ---------- */

        // schedule step
        // Catalogue request function called within GUI action   (RequestCatalogue())
        //  mkAgent.RequestCatalogue();
        agent.scheduleStep(new IComponentStep<Void>() {
            public IFuture<Void> execute(IInternalAccess ia) {
                mkAgent.RequestCatalogue();
                return IFuture.DONE;
            }
        });
        // schedule step
        // Send order function called within GUI action          (SendOrders(String[] orders))
        //  mkAgent.SendOrder(null);
        agent.scheduleStep(new IComponentStep<Void>() {
            public IFuture<Void> execute(IInternalAccess ia) {
                mkAgent.SendOrder(null);
                return IFuture.DONE;
            }
        });
    }
}
