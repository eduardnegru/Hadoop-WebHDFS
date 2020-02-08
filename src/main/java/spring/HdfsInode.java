package spring;

import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import org.apache.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import spring.body.INode;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public class HdfsInode {

    private String path;
    private INodeType inodeType;

    public HdfsInode(String path, INodeType inodeType) {
        this.path = path;
        this.inodeType = inodeType;
    }

    public HashMap<String, String> upload(String hostPath) throws IOException {

        WebHdfsUrl webHdfsUrl = new WebHdfsUrl.Builder()
                .setHost(Config.NAMENODE_IP_ADDRESS)
                .setPort(Config.WEBHDFS_PORT)
                .setMethod(Config.METHOD_CREATE)
                .setHomeFolder(Config.WEBHDFS_HOME_DIRECTORY_DEFAULT)
                .setPath(this.path)
                .build();

        long start = System.currentTimeMillis();

        HttpResponse<JsonNode> response = Unirest.put(webHdfsUrl.getUrl()).asJson();
        HashMap<String, String> stats = new HashMap<>();

        if(response.getStatus() == HttpStatus.SC_TEMPORARY_REDIRECT) {
            String location = response.getHeaders().get("location").get(0);
            FileReader fileReader = new FileReader(new File(hostPath));
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;StringBuffer stringBuffer = new StringBuffer();

            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line + "\n");
            }

            response = Unirest.put(location).body(stringBuffer.toString()).asJson();

            long end = System.currentTimeMillis();

            long duration = end - start;
            int length = stringBuffer.length();
            float speed = ((float)length) / duration;

            System.out.println("D=" + duration);
            System.out.println("L=" + length);
            System.out.println("S=" + speed);

            stats.put("duration", Long.toString(duration));
            stats.put("length", Integer.toString(length));
            stats.put("speed", Float.toString(speed));

        }

        return stats;
    }

    public HashMap<String, String> uploadDirectory(String hostPath) {

        float bandwidth = 0;
        org.springframework.http.HttpStatus httpStatus = org.springframework.http.HttpStatus.OK;
        HashMap<String, String> uploadStats = new HashMap<>();

        try {
            HttpResponse<JsonNode> response =  this.mkdir();

            if(response.getStatus() != org.springframework.http.HttpStatus.OK.value()) {
                throw new Exception("Failed to create directory on HDFS");
            }

            File dir = new File(hostPath);
            for(File f : dir.listFiles()) {
                if (f.isFile()) {
                    try {
                        HdfsInode hdfsFile = new HdfsInode(this.path + "/" + f.getName(), INodeType.FILE);
                        uploadStats = hdfsFile.upload(f.getAbsolutePath());
                        float speed = Float.parseFloat(uploadStats.get("speed"));
                        System.out.println(speed);
                        if(speed > bandwidth) {
                            bandwidth = speed;
                        }
                    } catch (Exception exception) {
                        System.out.println(exception.getMessage());
                    }
                }
            }

            uploadStats.put("bandwidth", Float.toString(bandwidth));
        } catch (Exception exception) {
            exception.printStackTrace();
            uploadStats.put("message", exception.getMessage());
            httpStatus = org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
        }

        return uploadStats;
    }

    public HttpResponse<JsonNode> mkdir() throws IOException {

        WebHdfsUrl webHdfsUrl = new WebHdfsUrl.Builder()
                .setHost(Config.NAMENODE_IP_ADDRESS)
                .setPort(Config.WEBHDFS_PORT)
                .setMethod(Config.METHOD_MKDIR)
                .setHomeFolder(Config.WEBHDFS_HOME_DIRECTORY_DEFAULT)
                .setPath(this.path)
                .build();
        System.out.println(webHdfsUrl.getUrl());
        HttpResponse<JsonNode> response = Unirest.put(webHdfsUrl.getUrl()).asJson();
        return response;
    }

    public String read() throws IOException {

        WebHdfsUrl webHdfsUrl = new WebHdfsUrl.Builder()
                .setHost(Config.NAMENODE_IP_ADDRESS)
                .setPort(Config.WEBHDFS_PORT)
                .setMethod(Config.METHOD_OPEN)
                .setHomeFolder(Config.WEBHDFS_HOME_DIRECTORY_DEFAULT)
                .setPath(this.path)
                .build();

        Unirest.config().reset();
        Unirest.config().followRedirects(false);
        HttpResponse<JsonNode> response = Unirest.get(webHdfsUrl.getUrl()).asJson();

        if(response.getStatus() == HttpStatus.SC_TEMPORARY_REDIRECT) {
            String location = response.getHeaders().get("location").get(0);
            String readLine;
            URL url = new URL(location);
            HttpURLConnection conection = (HttpURLConnection) url.openConnection();
            conection.setRequestMethod("GET");

            int responseCode = conection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conection.getInputStream()));
                StringBuffer res = new StringBuffer();

                while ((readLine = in.readLine()) != null) {
                    res.append(readLine + "\n");
                }
                in.close();
                return res.toString();
            }
        }

        HashMap<String, String>  errorMessage = new HashMap<>();
        errorMessage.put("error", "File redirect does not work");
        return errorMessage.toString();
    }

    public  HttpResponse<JsonNode>  delete(boolean recursive) {

        WebHdfsUrl webHdfsUrl = new WebHdfsUrl.Builder()
                .setHost(Config.NAMENODE_IP_ADDRESS)
                .setPort(Config.WEBHDFS_PORT)
                .setMethod(Config.METHOD_DELETE)
                .setHomeFolder(Config.WEBHDFS_HOME_DIRECTORY_DEFAULT)
                .setQueryParameters("&recursive=" + recursive)
                .setPath(this.path)
                .build();
        System.out.println(webHdfsUrl.getUrl());
        HttpResponse<JsonNode> response = Unirest.delete(webHdfsUrl.getUrl()).asJson();

        return response;
    }

    public HttpResponse<JsonNode> listStatus() {

        WebHdfsUrl webHdfsUrl = new WebHdfsUrl.Builder()
                .setHost(Config.NAMENODE_IP_ADDRESS)
                .setPort(Config.WEBHDFS_PORT)
                .setMethod(Config.METHOD_LIST_STATUS)
                .setHomeFolder(Config.WEBHDFS_HOME_DIRECTORY_DEFAULT)
                .setPath(this.path)
                .build();

        HttpResponse<JsonNode> response = Unirest.get(webHdfsUrl.getUrl()).asJson();

        return response;
    }

    public HashMap<String, String> download(String hostPath) throws IOException {
        WebHdfsUrl webHdfsUrl = new WebHdfsUrl.Builder()
                .setHost(Config.NAMENODE_IP_ADDRESS)
                .setPort(Config.WEBHDFS_PORT)
                .setMethod(Config.METHOD_OPEN)
                .setHomeFolder(Config.WEBHDFS_HOME_DIRECTORY_DEFAULT)
                .setPath(this.path)
                .build();

        long start = System.currentTimeMillis();
        HashMap<String, String>  errorMessage = new HashMap<>();
        Unirest.config().reset();
        Unirest.config().followRedirects(false);
        HttpResponse<JsonNode> response = Unirest.get(webHdfsUrl.getUrl()).asJson();

        if(response.getStatus() == HttpStatus.SC_TEMPORARY_REDIRECT) {
            String location = response.getHeaders().get("location").get(0);
            String readLine;
            URL url = new URL(location);
            HttpURLConnection conection = (HttpURLConnection) url.openConnection();
            conection.setRequestMethod("GET");

            int responseCode = conection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conection.getInputStream()));
                StringBuffer res = new StringBuffer();

                while ((readLine = in.readLine()) != null) {
                    res.append(readLine + "\n");
                }
                in.close();

                FileWriter fileWriter = new FileWriter(hostPath);
                fileWriter.write(res.toString());
                fileWriter.close();

                long end = System.currentTimeMillis();

                long duration = end - start;
                int length = res.length();
                float speed = ((float)length) / duration;

                HashMap<String, String> stats = new HashMap<>();
                stats.put("duration", Long.toString(duration));
                stats.put("length", Integer.toString(length));
                stats.put("speed", Float.toString(speed));
                return stats;
            } else {
                errorMessage.put("message", conection.getResponseMessage());
            }
        } else {
            errorMessage.put("message", response.getBody().toPrettyString());
        }


        return errorMessage;
    }
}
