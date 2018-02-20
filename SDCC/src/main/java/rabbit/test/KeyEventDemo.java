package rabbit.test;

/*
* KeyEventDemo
*/

import rabbit.controllers.SemaphoreController;
import rabbit.entities.Semaphore;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;

public class KeyEventDemo extends JFrame implements KeyListener, ActionListener
{

    private static Integer ID;
    private static ArrayList<SemaphoreController> semaphores;
    private JTextArea displayArea;
    private JTextField typingArea;
    private static final String newline = System.getProperty("line.separator");

    public static void main(String[] args) {
        ID = 0;
        semaphores = new ArrayList<SemaphoreController>();
        /* Use an appropriate Look and Feel */
        try {
            //UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            //UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
            UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
        } catch (UnsupportedLookAndFeelException ex) {
            ex.printStackTrace();
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        } catch (InstantiationException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        /* Turn off metal's use of bold fonts */
        UIManager.put("swing.boldMetal", Boolean.FALSE);

        //Schedule a job for event dispatch thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }

    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private static void createAndShowGUI() {
        //Create and set up the window.
        KeyEventDemo frame = new KeyEventDemo("KeyEventDemo");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        //Set up the content pane.
        frame.addComponentsToPane();


        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    private void addComponentsToPane() {

        JButton button = new JButton("Clear");
        button.addActionListener(this);

        typingArea = new JTextField(20);
        typingArea.addKeyListener(this);

        //Uncomment this if you wish to turn off focus
        //traversal.  The focus subsystem consumes
        //focus traversal keys, such as Tab and Shift Tab.
        //If you uncomment the following line of code, this
        //disables focus traversal and the Tab events will
        //become available to the key event listener.
        //typingArea.setFocusTraversalKeysEnabled(false);

        displayArea = new JTextArea();
        displayArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(displayArea);
        scrollPane.setPreferredSize(new Dimension(375, 125));

        getContentPane().add(typingArea, BorderLayout.PAGE_START);
        getContentPane().add(scrollPane, BorderLayout.CENTER);
        getContentPane().add(button, BorderLayout.PAGE_END);
    }

    private KeyEventDemo(String name) {
        super(name);
    }


    /** Handle the key typed event from the text field. */
    public void keyTyped(KeyEvent e) {
        displayInfo(e);
    }

    /** Handle the key pressed event from the text field. */
    public void keyPressed(KeyEvent e) {
        //displayInfo(e, "KEY PRESSED: ");
    }

    /** Handle the key released event from the text field. */
    public void keyReleased(KeyEvent e) {
        //displayInfo(e, "KEY RELEASED: ");
    }

    /** Handle the button click. */
    public void actionPerformed(ActionEvent e) {
        //Clear the text components.
        displayArea.setText("");
        typingArea.setText("");

        //Return the focus to the typing area.
        typingArea.requestFocusInWindow();
    }

    /*
     * We have to jump through some hoops to avoid
     * trying to print non-printing characters
     * such as Shift.  (Not only do they not print,
     * but if you put them in a String, the characters
     * afterward won't show up in the text area.)
     */
    private void displayInfo(KeyEvent e){

        //You should only rely on the key char if the event
        //is a key typed event.
        String keyString;

        int keyCode = e.getKeyChar();

        if(keyCode == 97){ //a
            keyString = "adding semaphore with ID: " + ID;
            SemaphoreController s = new SemaphoreController(ID);
            semaphores.add(s);
            s.addCrossroad("incrocio");
            ID++;
        }
        else if(keyCode >= 49 && keyCode <= 57){
            if(keyCode - 49 < ID) {
                keyString = "sent message by the semaphore with ID: " + semaphores.get(keyCode - 49).getID();
                semaphores.get(keyCode - 49).sendRequest("traffic", "incrocio");
            }
            else
                keyString = "";
        }
        else if(keyCode == 104){ //h
            keyString = "letter to press - action \n\t'a' - add semaphore \n\t '0' - send message from the first semaphore \n\t '1' - send message from the second semaphore\n\t ...";
        }
        else {
            keyString = "keycode = " + keyCode;
        }

        displayArea.append(keyString + newline);
        displayArea.setCaretPosition(displayArea.getDocument().getLength());
    }
}