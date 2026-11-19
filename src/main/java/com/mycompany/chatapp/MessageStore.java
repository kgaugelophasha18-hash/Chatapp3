package com.mycompany.chatapp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author Student
 */

public class MessageStore {

    private ArrayList<Message> sentMessages = new ArrayList<Message>();
    private ArrayList<Message> disregardedMessages = new ArrayList<Message>();
    private ArrayList<Message> storedMessages = new ArrayList<Message>();
    private ArrayList<String> messageHashes = new ArrayList<String>();
    private ArrayList<String> messageIDs = new ArrayList<String>();

    public ArrayList<Message> getSentMessages() {
        return sentMessages;
    }

    public ArrayList<Message> getDisregardedMessages() {
        return disregardedMessages;
    }

    public ArrayList<Message> getStoredMessages() {
        return storedMessages;
    }

    public ArrayList<String> getMessageHashes() {
        return messageHashes;
    }

    public ArrayList<String> getMessageIDs() {
        return messageIDs;
    }

    public void addMessage(Message msg, String flag) {
        if (flag.equals("Sent")) {
            sentMessages.add(msg);
        } else if (flag.equals("Disregard")) {
            disregardedMessages.add(msg);
        } else if (flag.equals("Stored")) {
            storedMessages.add(msg);
        }

        messageHashes.add(msg.createMessageHash());
        messageIDs.add(msg.getMessageID());
    }

    public void loadStoredMessages() {
        File file = new File(Message.getJsonFile());
        if (file.exists() == false) {
            return;  
        }

        try {
            Scanner reader = new Scanner(file);
            while (reader.hasNextLine()) {
                String line = reader.nextLine();

                try {
                    Message msg = messageFromJson(line);
                    storedMessages.add(msg);
                    messageHashes.add(msg.createMessageHash());
                    messageIDs.add(msg.getMessageID());
                } catch (Exception e) {
                    
                }
            }
            reader.close();
        } catch (FileNotFoundException e) {
            System.out.println("Could not read the file.");
        }
    }

    public void saveStoredMessages() {
        try {
            FileWriter writer = new FileWriter(Message.getJsonFile());   
            for (int i = 0; i < storedMessages.size(); i++) {
                writer.write(storedMessages.get(i).toJson() + "\n");
            }
            writer.close();
        } catch (IOException e) {
            System.out.println("Could not save the file.");
        }
    }

    public static Message messageFromJson(String line) {
        String id = getJsonValue(line, "messageID");
        int number = Integer.parseInt(getJsonValue(line, "messageNumber"));
        String recipient = getJsonValue(line, "recipient");
        String text = getJsonValue(line, "message");
        return new Message(id, number, recipient, text);
    }

    private static String getJsonValue(String line, String name) {
        String start = "\"" + name + "\": \"";
        int from = line.indexOf(start) + start.length();
        int to = line.indexOf("\"", from);
        return line.substring(from, to);
    }

    public String displaySenderAndRecipient(String sender) {
        if (storedMessages.size() == 0) {
            return "No stored messages.";
        }

        String result = "";
        for (int i = 0; i < storedMessages.size(); i++) {
            result = result + "Sender: " + sender
                   + " | Recipient: " + storedMessages.get(i).getRecipient() + "\n";
        }
        return result;
    }

    public String longestStoredMessage() {
        if (storedMessages.size() == 0) {
            return "No stored messages.";
        }

        Message longest = storedMessages.get(0);
        for (int i = 1; i < storedMessages.size(); i++) {
            if (storedMessages.get(i).getMessageText().length() > longest.getMessageText().length()) {
                longest = storedMessages.get(i);
            }
        }
        return longest.getMessageText();
    }

    public Message findMessageByID(String id) {
        Message found = searchList(sentMessages, id);
        if (found == null) {
            found = searchList(disregardedMessages, id);
        }
        if (found == null) {
            found = searchList(storedMessages, id);
        }
        return found;
    }

    private Message searchList(ArrayList<Message> list, String id) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getMessageID().equals(id)) {
                return list.get(i);
            }
        }
        return null;
    }

    public ArrayList<String> searchByRecipient(String recipient) {
        ArrayList<String> results = new ArrayList<String>();

        for (int i = 0; i < sentMessages.size(); i++) {
            if (sentMessages.get(i).getRecipient().equals(recipient)) {
                results.add(sentMessages.get(i).getMessageText());
            }
        }
        for (int i = 0; i < storedMessages.size(); i++) {
            if (storedMessages.get(i).getRecipient().equals(recipient)) {
                results.add(storedMessages.get(i).getMessageText());
            }
        }
        return results;
    }

    public String deleteByHash(String hash) {
        for (int i = 0; i < storedMessages.size(); i++) {
            if (storedMessages.get(i).createMessageHash().equalsIgnoreCase(hash)) {
                String text = storedMessages.get(i).getMessageText();
                storedMessages.remove(i);
                return "Message: \"" + text + "\" successfully deleted.";
            }
        }
        return "Message hash not found.";
    }

    public String displayReport(ArrayList<Message> messages) {
        if (messages.size() == 0) {
            return "No messages to display.";
        }

        String report = "";
        for (int i = 0; i < messages.size(); i++) {
            report = report + messages.get(i).messageDetails() + "\n\n";
        }
        return report;
    }
}