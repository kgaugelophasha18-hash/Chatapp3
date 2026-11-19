package com.mycompany.chatapp;

import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author Student
 */
public class Message {

    private String messageID;
    private int messageNumber;
    private String recipient;
    private String messageText;

    private static int totalMessages = 0;
    private static String sentMessages = "";

    private static String jsonFile = "messages.json";

    public Message(String messageID, int messageNumber, String recipient, String messageText) {
        this.messageID = messageID;
        this.messageNumber = messageNumber;
        this.recipient = recipient;
        this.messageText = messageText;
    }

    public String getMessageID() {
        return messageID;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getMessageText() {
        return messageText;
    }

    public static String getJsonFile() {
        return jsonFile;
    }

    public static void setJsonFile(String fileName) {
        jsonFile = fileName;
    }

    public static String generateMessageID() {
        String id = "";
        for (int i = 0; i < 10; i++) {
            int digit = (int) (Math.random() * 10);
            id = id + digit;
        }
        return id;
    }
    
    public String displayMessageID() {
        return "Message ID generated: " + messageID;
    }

    public boolean checkMessageID() {
        return messageID.length() <= 10;
    }

    public String checkRecipientCell() {
        if (recipient.matches("\\+27[0-9]{9}")) {
            return "Cell phone number successfully captured.";
        } else {
            return "Cell phone number is incorrectly formatted or does not contain "
                 + "an international code. Please correct the number and try again.";
        }
    }

    public String checkMessageLength() {
        if (messageText.length() <= 250) {
            return "Message ready to send.";
        } else {
            int tooMany = messageText.length() - 250;
            return "Message exceeds 250 characters by " + tooMany
                 + "; please reduce the size.";
        }
    }

    public String createMessageHash() {
        String firstTwo = messageID.substring(0, 2);

        String[] words = messageText.split(" "); 
        String firstWord = words[0];
        String lastWord = words[words.length - 1];

        String hash = firstTwo + ":" + messageNumber + ":" + firstWord + lastWord;

        hash = hash.toUpperCase();
        hash = hash.replace("?", "").replace(",", "").replace("!", "").replace(".", "");
        return hash;
    }
    
    public String messageDetails() {
        return "Message ID: " + messageID + "\n"
             + "Message Hash: " + createMessageHash() + "\n"
             + "Recipient: " + recipient + "\n"
             + "Message: " + messageText;
    }

    public String sentMessage(int choice) {
        if (choice == 1) {
            totalMessages = totalMessages + 1;
            sentMessages = sentMessages + messageDetails() + "\n\n";
            return "Message successfully sent.";
        } else if (choice == 2) {
            return "Press 0 to delete the message.";
        } else if (choice == 3) {
            storeMessage();
            return "Message successfully stored.";
        } else {
            return "Invalid option.";
        }
    }

    public static String printMessages() {
        return sentMessages;
    }

    public static int returnTotalMessages() {
        return totalMessages;
    }

    public String toJson() {
        String text = messageText.replace("\"", "'");  

        return "{\"messageID\": \"" + messageID + "\", "
             + "\"messageNumber\": \"" + messageNumber + "\", "
             + "\"messageHash\": \"" + createMessageHash() + "\", "
             + "\"recipient\": \"" + recipient + "\", "
             + "\"message\": \"" + text + "\"}";
    }

    public void storeMessage() {
        try {
            
            FileWriter writer = new FileWriter(jsonFile, true);
            writer.write(toJson() + "\n");
            writer.close();
        } catch (IOException e) {
            System.out.println("Could not save the file.");
        }
    }
}