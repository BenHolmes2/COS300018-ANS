import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.commons.future.IFuture;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import javax.swing.*;

public class MarketUserAgentGUI extends JFrame {

    File orderFile = null;

    public MarketUserAgentGUI(final IExternalAccess agent, final MarketUserAgent mkAgent) {

        JFrame frame = new JFrame(); //creating instance of JFrame

        //creating title label
        JLabel titleLabel = new JLabel("User: " + agent.getComponentIdentifier().getName());
        titleLabel.setFont(new Font("Default", Font.BOLD, 20));
        titleLabel.setBounds(50, 0, 500, 80);

        // Creation of non-editable textfield to preview pending orders
        JLabel pendingLabel = new JLabel("Pending Orders");
        pendingLabel.setFont(new Font("Default", Font.BOLD, 14));
        pendingLabel.setBounds(50, 60, 150, 20);

        JTextArea pendingOrderTextArea = new JTextArea();
        pendingOrderTextArea.setEditable(false);
        JScrollPane pendingOrdersScroll = new JScrollPane(pendingOrderTextArea);
        pendingOrdersScroll.setPreferredSize(new Dimension(400, 200));
        pendingOrdersScroll.setBounds(50, 90, 400, 200);

        // The File input button
        JLabel orderFileLabel = new JLabel("Order file : ");
        orderFileLabel.setBounds(50, 290, 200, 30);
        JButton fileButton = new JButton("Load Orders From File");
        fileButton.setBounds(50, 320, 150, 30);

        // the create button creation and location setting
        JButton readOrderFromFileButton = new JButton("Send Orders");
        readOrderFromFileButton.setBounds(350, 320, 80, 30);

        // Creation of non-editable textArea to display Catalogue
        JLabel catLabel = new JLabel("Catalogue");
        catLabel.setFont(new Font("Default", Font.BOLD, 14));
        catLabel.setBounds(50, 360, 150, 20);

        JTextArea catTextArea = new JTextArea();
        catTextArea.setEditable(false);
        JScrollPane catScroll = new JScrollPane(catTextArea);
        catScroll.setPreferredSize(new Dimension(400, 200));
        catScroll.setBounds(50, 400, 400, 100);

        // Creation of non-editable textArea to display Settlements
        JLabel settleLabel = new JLabel("Settlement Log");
        settleLabel.setFont(new Font("Default", Font.BOLD, 14));
        settleLabel.setBounds(50, 500, 150, 20);

        JTextArea settleTextArea = new JTextArea();
        settleTextArea.setEditable(false);
        JScrollPane settleScroll = new JScrollPane(settleTextArea);
        settleScroll.setPreferredSize(new Dimension(400, 200));
        settleScroll.setBounds(50, 540, 400, 100);


        //adding all parts to frame
        frame.add(titleLabel);
        frame.add(pendingLabel);
        frame.add(pendingOrdersScroll);
        frame.add(fileButton);
        frame.add(orderFileLabel);
        frame.add(readOrderFromFileButton);

        frame.add(catLabel);
        frame.add(catScroll);

        frame.add(settleLabel);
        frame.add(settleScroll);

        //frame settings
        frame.setSize(512, 720);//400 width and 500 height
        frame.setLayout(null);//using no layout managers
        frame.setVisible(true);//making the frame visible

        fileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser orderFileChooser = new JFileChooser(".");
                int option = orderFileChooser.showOpenDialog(frame);
                if (option == JFileChooser.APPROVE_OPTION) {
                    orderFile = orderFileChooser.getSelectedFile();
                    orderFileLabel.setText("Order file : " + orderFile.getName());
                    try {
                        pendingOrderTextArea.append(Files.readString(Paths.get(String.valueOf(orderFile))));
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        readOrderFromFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // schedule step
                // Send order function called within GUI action          (SendOrders(String[] orders))
                //  mkAgent.SendOrder(null);
                agent.scheduleStep(new IComponentStep<Void>() {
                    public IFuture<Void> execute(IInternalAccess ia) {
                        if (orderFile == null) {
                            orderFileLabel.setText("Order file : not selected");
                        } else {
                            try {
                                mkAgent.SendOrder(mkAgent.ReadOrders(orderFile.getPath()));
                                // mkAgent.ReadOrders(orderFile.getPath());
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                        }
                        return IFuture.DONE;
                    }
                });
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

    }
}
