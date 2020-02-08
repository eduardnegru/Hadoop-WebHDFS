import com.google.gson.JsonObject;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;

import java.io.InputStream;
import java.util.Scanner;
import java.util.TreeMap;

public class Cli {

    public static void displayHelp() {
        System.out.println("Allowed commands");
        System.out.println("* help");
        System.out.println("* set [NAMENODE_IP] [NAMENODE_PORT]");
        System.out.println("* listStatus [HDFS_PATH]");
        System.out.println("* upload [INODE_TYPE] [HDFS_PATH] [LOCAL_PATH]");
        System.out.println("* download [HDFS_PATH] [LOCAL_PATH]");
        System.out.println("* delete [HDFS_PATH] [RECURSIVE]");
        System.out.println("* quit");
    }

    public static String getPath(String namenodeIp, String namenodePort) {
        return "http://" + namenodeIp + ":" + namenodePort;
    }

    public static void main(String[] args) {
        boolean namenodeSet = false;
        String namenodeIp = "localhost", namenodePort = "9870";

        displayHelp();

        while (true) {
            try {
                Scanner scanner = new Scanner(System.in);

                if(!scanner.hasNextLine()) {
                    continue;
                }

                String command = scanner.nextLine();
                String[] commandParts = command.split(" ");

                if (commandParts[0].toLowerCase().equals("quit") || commandParts[0].toLowerCase().equals("exit")) {
                    System.out.println("exiting ...");
                    break;
                } else if(commandParts[0].toLowerCase().equals("set")) {
                    if(commandParts.length != 3) {
                        System.out.println(commandParts);
                        System.out.println("Set command invalid. Expected: set [NAMENODE_IP] [NAMENODE_PORT]");
                    } else {
                        namenodeIp = commandParts[1];
                        namenodePort = commandParts[2];
                        namenodeSet = true;
                        System.out.println("Namenode set successfully. You are good to go.");
                    }
                } else if(commandParts[0].toLowerCase().equals("help")) {
                    displayHelp();
                } else if (!namenodeSet) {
                    System.out.println("Please set the namenode ip and port first using the set command.");
                    System.out.println("Use help for more information.");
                }
                else if(commandParts[0].equals("listStatus")) {
                    if(commandParts.length != 2) {
                        System.out.println("listStatus command invalid. Expected: listStatus [HDFS_PATH]");
                    } else {
                        String path = getPath(namenodeIp, namenodePort) + "/" + commandParts[0];
                        JsonObject node = new JsonObject();
                        node.addProperty("hdfsPath", commandParts[1]);
                        System.out.println(path);
                        HttpResponse<JsonNode> response = Unirest.post(path).header("Content-Type", "application/json").body(node).asJson();
                        System.out.println(response.getBody().getObject().toString());
                    }
                } else if(commandParts[0].toLowerCase().equals("upload")) {
                    JsonObject node = new JsonObject();
                    if(commandParts[1].toUpperCase().equals("FILE")) {
                        node.addProperty("hostPath", commandParts[3]);
                    }
                    node.addProperty("inodeType", commandParts[1]);
                    node.addProperty("hdfsPath", commandParts[2]);
                    String path = getPath(namenodeIp, namenodePort) + "/" + commandParts[0];
                    System.out.println(path);
                    HttpResponse<JsonNode> response = Unirest.put(path).header("Content-Type", "application/json").body(node).asJson();
                    System.out.println(response.getBody().getObject().get("message").toString());
                } else if(commandParts[0].toLowerCase().equals("download")) {
                    if(commandParts.length != 3) {
                        System.out.println("download command invalid. Expected: download [HDFS_PATH] [LOCAL_PATH]");
                    } else {
                        JsonObject node = new JsonObject();
                        node.addProperty("hdfsPath", commandParts[1]);
                        node.addProperty("hostPath", commandParts[2]);
                        String path = getPath(namenodeIp, namenodePort) + "/" + commandParts[0];
                        System.out.println(path);
                        HttpResponse<JsonNode> response = Unirest
                                .post(path)
                                .header("Content-Type", "application/json")
                                .body(node)
                                .asJson();
                        System.out.println(response.getBody().getObject().get("message").toString());
                    }
                } else if(commandParts[0].toLowerCase().equals("delete")) {
                    if(commandParts.length != 3) {
                        System.out.println("delete command invalid. Expected: delete [HDFS_PATH] [LOCAL_PATH]");
                    } else {
                        JsonObject node = new JsonObject();
                        node.addProperty("hdfsPath", commandParts[1]);
                        String path = getPath(namenodeIp, namenodePort) + "/" + commandParts[0] + "?recursive=" +  commandParts[2];
                        System.out.println(path);
                        HttpResponse<JsonNode> response = Unirest.delete(path)
                                .queryString("recursive", commandParts[2])
                                .header("Content-Type", "application/json")
                                .body(node)
                                .asJson();
                        System.out.println(response.getBody().getObject().get("message").toString());
                    }
                } else if(commandParts[0].equals("test")) {
                    HttpResponse<String> res = Unirest.get(getPath(namenodeIp, namenodePort)).asString();
                    System.out.println(res.getBody());

                }  else {
                    System.out.println("Command unknown. Please type help for the list of the allowed commands");
                }
            } catch (Exception exception) {
                System.out.println(exception.getMessage());
            }
        }
    }
}