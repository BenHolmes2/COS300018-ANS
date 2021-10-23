import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
public class NegotiationGUI {

    private static String offer = "test";

    public NegotiationGUI(String offerInput) {
        offer = offerInput;
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame();//creating instance of JFrame

        //creating title label
        JLabel titleLabel = new JLabel("Offer Received");
        titleLabel.setFont(new Font("Default", Font.BOLD, 30));
        titleLabel.setBounds(80, 0, 300, 80);

        //creation of the agent name input box and its label as well as setting their location within the frame.
        JTextField counterOffer = new JTextField();
        JLabel counterOfferLabel = new JLabel("Counter offer:");
        counterOffer.setBounds(95, 200, 100, 20);
        counterOfferLabel.setBounds(10, 200, 80, 20);

        //creation of inventory inputs and its labels as well as setting their location within the frame
        JLabel offerLabel = new JLabel("Offer:");
        offerLabel.setFont(new Font("Default", Font.BOLD, 20));
        offerLabel.setBounds(100, 80, 80, 40);
        JLabel offerAmount = new JLabel(offer);
        offerAmount.setFont(new Font("Default", Font.BOLD, 20));
        offerAmount.setBounds(180, 80, 150, 40);


        // the create button creation and location setting
        JButton counterButton=new JButton("Counter offer");
        counterButton.setBounds(200,200,120, 20);

        // The File input button
        JButton acceptButton=new JButton("Accept offer");
        acceptButton.setBounds(200,300,150, 30);

        // The File input button
        JButton refuseButton=new JButton("Refuse offer");
        refuseButton.setBounds(10,300,150, 30);

        frame.add(titleLabel);
        frame.add(offerLabel);
        frame.add(offerAmount);
        frame.add(counterOffer);
        frame.add(counterOfferLabel);
        frame.add(counterButton);
        frame.add(acceptButton);
        frame.add(refuseButton);


        frame.setSize(400,400);//400 width and 500 height
        frame.setLayout(null);//using no layout managers
        frame.setVisible(true);//making the frame visible

        acceptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //TODO: accept offer code
            }
        });

        refuseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //TODO: refuse offer code
            }
        });

        counterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String counter = counterOffer.getText();
                //TODO: counter offer code
            }
        });
    }
}
